package org.student_api.clinc_system_management.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.student_api.clinc_system_management.dto.Request.PatientRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.dto.Response.PatientResponseDto;
import  org.student_api.clinc_system_management.service.AppointmentService;
import  org.student_api.clinc_system_management.service.PatientService;
import org.student_api.clinc_system_management.role.AppointmentStatus;
import org.student_api.clinc_system_management.role.Role;
import org.student_api.clinc_system_management.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService, AppointmentService appointmentService) {

        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponseDto> registerPatient(@Valid @RequestBody PatientRequestDto request) {
        PatientResponseDto response = patientService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<Page<PatientResponseDto>> getAllPatients(Pageable pageable) {
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        assertCanAccessPatient(currentUser, id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        assertIsAdminOrOwner(currentUser, id);
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {

        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getPatientAppointments(
            @PathVariable UUID id,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        assertCanAccessPatient(currentUser, id);
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(id, status, date));
    }

    // ADMIN: full access. DOCTOR: can view any patient. PATIENT: own record only.
    private void assertCanAccessPatient(AuthenticatedUser currentUser, UUID patientId) {
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.DOCTOR) return;

        if (currentUser.getRole() == Role.PATIENT && patientId.equals(currentUser.getLinkedProfileId())) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to access this patient's data");
    }

    // ADMIN: full access. PATIENT: own record only (DOCTOR cannot edit patient profiles).
    private void assertIsAdminOrOwner(AuthenticatedUser currentUser, UUID patientId) {
        if (currentUser.getRole() == Role.ADMIN) return;

        if (currentUser.getRole() == Role.PATIENT && patientId.equals(currentUser.getLinkedProfileId())) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to modify this patient's data");
    }
}