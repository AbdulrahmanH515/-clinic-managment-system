package org.student_api.clinc_system_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.student_api.clinc_system_management.model.MedicalRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
    boolean existsByAppointmentId(UUID appointmentId);
    Optional<MedicalRecord> findByAppointmentId(UUID appointmentId);
    List<MedicalRecord> findByAppointment_Patient_Id(UUID patientId);
}