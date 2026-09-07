package org.student_api.clinc_system_management.service;

import org.student_api.clinc_system_management.dto.Request.DoctorRequestDto;
import org.student_api.clinc_system_management.dto.Response.DoctorResponseDto;
import org.student_api.clinc_system_management.exception.DoctorNotFoundException;
import org.student_api.clinc_system_management.exception.DuplicateLicenseException;
import org.student_api.clinc_system_management.model.Doctor;
import org.student_api.clinc_system_management.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponseDto registerDoctor(DoctorRequestDto request) {

        if (doctorRepository.existsByMedicalLicenseNumber(
                request.getMedicalLicenseNumber())) {

            throw new DuplicateLicenseException(
                    request.getMedicalLicenseNumber());
        }

        Doctor doctor = new Doctor(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getMedicalLicenseNumber(),
                request.getYearsOfExperience(),
                request.getConsultationFee(),
                request.getSpecialization()
        );

        Doctor saved = doctorRepository.save(doctor);

        return toResponseDto(saved);
    }

    public List<DoctorResponseDto> getAllDoctors() {

        return doctorRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public DoctorResponseDto getDoctorById(UUID id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));

        return toResponseDto(doctor);
    }

    public DoctorResponseDto updateDoctor(
            UUID id,
            DoctorRequestDto request) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));

        if (doctorRepository.existsByMedicalLicenseNumber(
                request.getMedicalLicenseNumber())
                && !doctor.getMedicalLicenseNumber()
                .equals(request.getMedicalLicenseNumber())) {

            throw new DuplicateLicenseException(
                    request.getMedicalLicenseNumber());
        }

        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setMedicalLicenseNumber(
                request.getMedicalLicenseNumber());
        doctor.setYearsOfExperience(
                request.getYearsOfExperience());
        doctor.setConsultationFee(
                request.getConsultationFee());
        doctor.setSpecialization(
                request.getSpecialization());

        Doctor updated = doctorRepository.save(doctor);

        return toResponseDto(updated);
    }

    public void deleteDoctor(UUID id) {

        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException(id);
        }

        doctorRepository.deleteById(id);
    }

    private DoctorResponseDto toResponseDto(Doctor doctor) {

        return new DoctorResponseDto(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getMedicalLicenseNumber(),
                doctor.getYearsOfExperience(),
                doctor.getConsultationFee(),
                doctor.getSpecialization()
        );
    }
}