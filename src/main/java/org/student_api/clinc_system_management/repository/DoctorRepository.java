package org.student_api.clinc_system_management.repository;

import org.student_api.clinc_system_management.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);

    List<Doctor> findBySpecializationId(UUID specializationId);

    Page<Doctor> findAll(Pageable pageable);

    Page<Doctor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);
    Page<Doctor> findBySpecializationNameIgnoreCase(String name, Pageable pageable);


}