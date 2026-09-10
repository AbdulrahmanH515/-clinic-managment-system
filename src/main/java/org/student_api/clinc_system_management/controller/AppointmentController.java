package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Request.AppointmentRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.service.AppointmentService;

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
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable UUID id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }
}
