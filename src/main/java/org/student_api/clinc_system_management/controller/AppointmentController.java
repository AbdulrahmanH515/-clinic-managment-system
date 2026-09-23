package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Request.AppointmentRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.role.AppointmentStatus;
import org.student_api.clinc_system_management.role.Role;
import org.student_api.clinc_system_management.security.AuthenticatedUser;
import org.student_api.clinc_system_management.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public ResponseEntity<AppointmentResponseDto> bookAppointment(
            @Valid @RequestBody AppointmentRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        if (currentUser.getRole() == Role.PATIENT
                && !request.getPatientId().equals(currentUser.getLinkedProfileId())) {
            throw new AccessDeniedException("Patients can only book appointments for themselves");
        }

        AppointmentResponseDto response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        AppointmentResponseDto appointment = appointmentService.getAppointmentById(id);
        assertCanAccessAppointment(currentUser, appointment);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AppointmentResponseDto>> getAllAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(status, date, pageable));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<AppointmentResponseDto> confirmAppointment(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        assertDoctorOwnsOrAdmin(currentUser, appointmentService.getAppointmentById(id));
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        assertCanAccessAppointment(currentUser, appointmentService.getAppointmentById(id));
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        assertDoctorOwnsOrAdmin(currentUser, appointmentService.getAppointmentById(id));
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    @PatchMapping("/{id}/no-show")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<AppointmentResponseDto> markNoShow(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        assertDoctorOwnsOrAdmin(currentUser, appointmentService.getAppointmentById(id));
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }

    // ADMIN: full access. PATIENT: own appointments only. DOCTOR: own appointments only.
    private void assertCanAccessAppointment(AuthenticatedUser currentUser, AppointmentResponseDto appointment) {
        if (currentUser.getRole() == Role.ADMIN) return;

        if (currentUser.getRole() == Role.PATIENT
                && appointment.getPatientId().equals(currentUser.getLinkedProfileId())) {
            return;
        }
        if (currentUser.getRole() == Role.DOCTOR
                && appointment.getDoctorId().equals(currentUser.getLinkedProfileId())) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to access this appointment");
    }

    // ADMIN: full access. DOCTOR: own appointments only (PATIENT never reaches here).
    private void assertDoctorOwnsOrAdmin(AuthenticatedUser currentUser, AppointmentResponseDto appointment) {
        if (currentUser.getRole() == Role.ADMIN) return;

        if (!appointment.getDoctorId().equals(currentUser.getLinkedProfileId())) {
            throw new AccessDeniedException("Doctors can only manage their own appointments");
        }
    }
}