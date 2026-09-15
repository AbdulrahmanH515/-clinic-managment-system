package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Request.AppointmentRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.role.AppointmentStatus;
import org.student_api.clinc_system_management.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService ;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> bookAppointment(@Valid @RequestBody AppointmentRequestDto request) {
        AppointmentResponseDto response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(status, date));
    }


    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDto> confirmAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    @PatchMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponseDto> markNoShow(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }


}