package org.student_api.clinc_system_management.repository;

import org.student_api.clinc_system_management.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);
}