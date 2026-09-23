package org.student_api.clinc_system_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.student_api.clinc_system_management.dto.Request.*;
import org.student_api.clinc_system_management.dto.Response.AuthResponseDto;
import org.student_api.clinc_system_management.dto.Response.DoctorResponseDto;
import org.student_api.clinc_system_management.dto.Response.PatientResponseDto;
import org.student_api.clinc_system_management.exception.DuplicateUserException;
import org.student_api.clinc_system_management.exception.InvalidCredentialsException;
import org.student_api.clinc_system_management.model.User;
import org.student_api.clinc_system_management.repository.UserRepository;
import org.student_api.clinc_system_management.role.Role;
import org.student_api.clinc_system_management.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    // ADMIN only — no linked profile
    public AuthResponseDto register(RegisterRequestDto request) {
        assertEmailNotTaken(request.getEmail());

        User user = createUser(request.getEmail(), request.getPassword(), Role.ADMIN, null);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto registerPatient(PatientRegisterRequestDto request) {
        assertEmailNotTaken(request.getEmail());

        PatientRequestDto patientRequest = new PatientRequestDto(
                request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getDateOfBirth(), request.getGender(), null
        );
        PatientResponseDto patient = patientService.registerPatient(patientRequest);

        User user = createUser(request.getEmail(), request.getPassword(), Role.PATIENT, patient.getId());
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto registerDoctor(DoctorRegisterRequestDto request) {
        assertEmailNotTaken(request.getEmail());

        DoctorRequestDto doctorRequest = new DoctorRequestDto(
                request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone(),
                request.getMedicalLicenseNumber(), request.getYearsOfExperience(),
                request.getConsultationFee(), null
        );
        doctorRequest.setSpecializationId(request.getSpecializationId());
        DoctorResponseDto doctor = doctorService.registerDoctor(doctorRequest);

        User user = createUser(request.getEmail(), request.getPassword(), Role.DOCTOR, doctor.getId());
        return buildAuthResponse(user);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    private void assertEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("Email already registered: " + email);
        }
    }

    private User createUser(String email, String rawPassword, Role role, java.util.UUID linkedProfileId) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setLinkedProfileId(linkedProfileId);
        return userRepository.save(user);
    }

    private AuthResponseDto buildAuthResponse(User user) {
        String linkedId = user.getLinkedProfileId() != null ? user.getLinkedProfileId().toString() : null;
        String token = jwtService.generateToken(
                user.getEmail(), user.getRole().name(), user.getId().toString(), linkedId
        );
        return new AuthResponseDto(token, user.getId(), user.getEmail(), user.getRole());
    }
}