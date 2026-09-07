package org.student_api.clinc_system_management.service;


import org.student_api.clinc_system_management.dto.Request.PatientRequestDto;
import org.student_api.clinc_system_management.dto.Response.PatientResponseDto;
import org.student_api.clinc_system_management.exception.*;
import org.student_api.clinc_system_management.model.*;
import org.student_api.clinc_system_management.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    public PatientResponseDto updatePatient(UUID id, PatientRequestDto request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        if (patientRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateEmailException(request.getEmail());
        }

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        Patient updated = patientRepository.save(patient);
        return toResponseDto(updated);
    }
    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }

    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public PatientResponseDto getPatientById(UUID id) {
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