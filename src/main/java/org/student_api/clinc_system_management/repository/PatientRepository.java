package org.student_api.clinc_system_management.repository;

import org.student_api.clinc_system_management.model.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByEmail(String email);
}

