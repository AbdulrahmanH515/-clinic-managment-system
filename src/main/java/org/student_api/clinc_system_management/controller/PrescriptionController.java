package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Request.PrescriptionRequestDto;
import org.student_api.clinc_system_management.dto.Response.PrescriptionResponseDto;
import org.student_api.clinc_system_management.service.PrescriptionService;

import java.util.List;
import java.util.UUID;

@RestController
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/api/medical-records/{recordId}/prescriptions")
    public ResponseEntity<PrescriptionResponseDto> createPrescription(
            @PathVariable UUID recordId,
            @Valid @RequestBody PrescriptionRequestDto request) {
        PrescriptionResponseDto response = prescriptionService.createPrescription(recordId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/medical-records/{recordId}/prescriptions")
    public ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByRecord(@PathVariable UUID recordId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByRecord(recordId));
    }

    @GetMapping("/api/patients/{patientId}/prescriptions")
    public ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(patientId));
    }
}