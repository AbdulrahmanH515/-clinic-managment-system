package org.student_api.clinc_system_management.repository;

import org.student_api.clinc_system_management.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {

    boolean existsByName(String name);
}
