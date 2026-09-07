package org.student_api.clinc_system_management.service;

import org.student_api.clinc_system_management.dto.Request.SpecializationRequestDto;
import org.student_api.clinc_system_management.dto.Response.DoctorResponseDto;
import org.student_api.clinc_system_management.dto.Response.SpecializationResponseDto;
import org.student_api.clinc_system_management.exception.DuplicateSpecializationException;
import org.student_api.clinc_system_management.exception.SpecializationNotFoundException;
import org.student_api.clinc_system_management.model.Specialization;
import org.student_api.clinc_system_management.repository.DoctorRepository;
import org.student_api.clinc_system_management.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    public SpecializationService(SpecializationRepository specializationRepository,
                                 DoctorRepository doctorRepository,
                                 DoctorService doctorService) {
        this.specializationRepository = specializationRepository;
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
    }

    public SpecializationResponseDto registerSpecialization(SpecializationRequestDto request) {

        if (specializationRepository.existsByName(request.getName())) {

            throw new DuplicateSpecializationException(request.getName());
        }

        Specialization specialization = new Specialization(
                request.getName(),
                request.getDescription()
        );

        Specialization saved = specializationRepository.save(specialization);

        return toResponseDto(saved);
    }

    public List<SpecializationResponseDto> getAllSpecializations() {

        return specializationRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public SpecializationResponseDto getSpecializationById(UUID id) {

        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new SpecializationNotFoundException(id));

        return toResponseDto(specialization);
    }

    public List<DoctorResponseDto> getDoctorsBySpecialization(UUID id) {

        if (!specializationRepository.existsById(id)) {
            throw new SpecializationNotFoundException(id);
        }

        return doctorRepository.findBySpecializationId(id).stream()
                .map(doctorService::toResponseDto)
                .toList();
    }

    private SpecializationResponseDto toResponseDto(Specialization specialization) {

        return new SpecializationResponseDto(
                specialization.getId(),
                specialization.getName(),
                specialization.getDescription(),
                specialization.getDoctors().size()
        );
    }
}
