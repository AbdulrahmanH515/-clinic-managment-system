package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Request.MedicalRecordRequestDto;
import org.student_api.clinc_system_management.dto.Response.MedicalRecordResponseDto;
import org.student_api.clinc_system_management.service.MedicalRecordService;

import java.util.List;
import java.util.UUID;

@RestController
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping("/api/appointments/{appointmentId}/medical-record")
    public ResponseEntity<MedicalRecordResponseDto> createMedicalRecord(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody MedicalRecordRequestDto request) {
        MedicalRecordResponseDto response = medicalRecordService.createMedicalRecord(appointmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/patients/{patientId}/medical-records")
    public ResponseEntity<List<MedicalRecordResponseDto>> getMedicalRecordsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByPatient(patientId));
    }

    @GetMapping("/api/medical-records/{id}")
    public ResponseEntity<MedicalRecordResponseDto> getMedicalRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(id));
    }
}