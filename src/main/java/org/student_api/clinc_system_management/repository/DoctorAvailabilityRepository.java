package org.student_api.clinc_system_management.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.student_api.clinc_system_management.model.DoctorAvailability;

import java.util.List;
import java.util.UUID;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, UUID> {
    List<DoctorAvailability> findByDoctorId(UUID doctorId);

}
