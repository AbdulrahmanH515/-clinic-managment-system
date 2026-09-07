package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.student_api.clinc_system_management.dto.Request.SpecializationRequestDto;
import org.student_api.clinc_system_management.dto.Response.DoctorResponseDto;
import org.student_api.clinc_system_management.dto.Response.SpecializationResponseDto;
import org.student_api.clinc_system_management.service.SpecializationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PostMapping
    public ResponseEntity<SpecializationResponseDto> registerSpecialization(
            @Valid @RequestBody SpecializationRequestDto request) {

        SpecializationResponseDto response = specializationService.registerSpecialization(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SpecializationResponseDto>> getAllSpecializations() {

        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecializationResponseDto> getSpecializationById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(specializationService.getSpecializationById(id));
    }

    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorsBySpecialization(
            @PathVariable UUID id) {

        return ResponseEntity.ok(specializationService.getDoctorsBySpecialization(id));
    }
}
