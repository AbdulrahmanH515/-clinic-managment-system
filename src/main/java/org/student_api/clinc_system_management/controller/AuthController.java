package org.student_api.clinc_system_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.student_api.clinc_system_management.dto.Response.*;
import org.student_api.clinc_system_management.dto.Request.*;
import org.student_api.clinc_system_management.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponseDto> registerPatient(@Valid @RequestBody PatientRegisterRequestDto request) {
        return new ResponseEntity<>(authService.registerPatient(request), HttpStatus.CREATED);
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<AuthResponseDto> registerDoctor(@Valid @RequestBody DoctorRegisterRequestDto request) {
        return new ResponseEntity<>(authService.registerDoctor(request), HttpStatus.CREATED);
    }
}