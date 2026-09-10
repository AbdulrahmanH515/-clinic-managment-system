package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Request.DoctorAvailabilityRequestDto;
import org.student_api.clinc_system_management.dto.Response.DoctorAvailabilityResponseDto;
import org.student_api.clinc_system_management.model.DoctorAvailability;
import org.student_api.clinc_system_management.service.DoctorAvailabilityService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors/{doctorId}/availability")
public class DoctorAvailabilityController {
    private final DoctorAvailabilityService availabilityService ;

    public DoctorAvailabilityController(DoctorAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }
    @PostMapping
    public ResponseEntity<DoctorAvailabilityResponseDto> addAvailability(@PathVariable UUID doctorId , @Valid @RequestBody DoctorAvailabilityRequestDto request){
        DoctorAvailabilityResponseDto response = availabilityService.addAvailability(doctorId , request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<DoctorAvailabilityResponseDto>> getDoctorAvailability(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(availabilityService.getDoctorAvailability(doctorId));
    }
}
