package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.student_api.clinc_system_management.dto.Request.AssignSpecializationRequestDto;
import org.student_api.clinc_system_management.dto.Request.DoctorRequestDto;
import org.student_api.clinc_system_management.dto.Response.DoctorResponseDto;
import org.student_api.clinc_system_management.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDto> registerDoctor(
            @Valid @RequestBody DoctorRequestDto request) {

        DoctorResponseDto response = doctorService.registerDoctor(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {

        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctorById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctor(
            @PathVariable UUID id,
            @Valid @RequestBody DoctorRequestDto request) {

        return ResponseEntity.ok(
                doctorService.updateDoctor(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable UUID id) {

        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/specialization")
    public ResponseEntity<DoctorResponseDto> assignSpecialization(
            @PathVariable UUID id,
            @Valid @RequestBody AssignSpecializationRequestDto request) {

        return ResponseEntity.ok(
                doctorService.assignSpecialization(id, request.getSpecializationId())
        );
    }
}