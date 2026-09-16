package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.student_api.clinc_system_management.dto.Request.AssignSpecializationRequestDto;
import org.student_api.clinc_system_management.dto.Request.DoctorRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.dto.Response.DoctorResponseDto;
import org.student_api.clinc_system_management.role.AppointmentStatus;
import org.student_api.clinc_system_management.service.AppointmentService;
import org.student_api.clinc_system_management.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDto> registerDoctor( @Valid @RequestBody DoctorRequestDto request) {
        DoctorResponseDto response = doctorService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponseDto>> getAllDoctors(@RequestParam(required = false) String name, @RequestParam(required = false) String specialization, Pageable pageable) {

        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(doctorService.searchDoctorsByName(name, pageable));
        }
        if (specialization != null && !specialization.isBlank()) {
            return ResponseEntity.ok(doctorService.searchDoctorsBySpecialization(specialization, pageable));
        }
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctor(@PathVariable UUID id, @Valid @RequestBody DoctorRequestDto request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/specialization")
    public ResponseEntity<DoctorResponseDto> assignSpecialization(@PathVariable UUID id, @Valid @RequestBody AssignSpecializationRequestDto request) {
        return ResponseEntity.ok(
                doctorService.assignSpecialization(id, request.getSpecializationId()));
    }


    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getDoctorAppointments(
            @PathVariable UUID id,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(id, status, date));
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(@PathVariable UUID id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(id, date));
    }
}