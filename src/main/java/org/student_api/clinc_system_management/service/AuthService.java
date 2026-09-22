package org.student_api.clinc_system_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.student_api.clinc_system_management.dto.Request.LoginRequestDto;
import org.student_api.clinc_system_management.dto.Request.RegisterRequestDto;
import org.student_api.clinc_system_management.dto.Response.AuthResponseDto;
import org.student_api.clinc_system_management.exception.DuplicateUserException;
import org.student_api.clinc_system_management.exception.InvalidCredentialsException;
import org.student_api.clinc_system_management.model.User;
import org.student_api.clinc_system_management.repository.UserRepository;
import org.student_api.clinc_system_management.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLinkedProfileId(null); // هنربطها بـ Patient/Doctor لاحقًا لو احتجنا

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(
                saved.getEmail(),
                saved.getRole().name(),
                saved.getId().toString()
        );

        return new AuthResponseDto(token, saved.getId(), saved.getEmail(), saved.getRole());
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId().toString()
        );

        return new AuthResponseDto(token, user.getId(), user.getEmail(), user.getRole());
    }
}