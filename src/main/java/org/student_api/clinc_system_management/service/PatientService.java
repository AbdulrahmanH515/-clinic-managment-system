package org.student_api.clinc_system_management.service;


import org.student_api.clinc_system_management.DTO.*;
import org.student_api.clinc_system_management.exception.*;
import org.student_api.clinc_system_management.model.*;
import org.student_api.clinc_system_management.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponseDto registerPatient(PatientRequestDto request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Patient patient = new Patient(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getDateOfBirth(),
                request.getGender(),
                LocalDate.now()
        );

        Patient saved = patientRepository.save(patient);
        return toResponseDto(saved);
    }

    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public PatientResponseDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        return toResponseDto(patient);
    }

    private PatientResponseDto toResponseDto(Patient patient) {
        return new PatientResponseDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getRegistrationDate()
        );
    }
}