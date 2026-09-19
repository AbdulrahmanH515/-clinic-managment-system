package org.student_api.clinc_system_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.student_api.clinc_system_management.model.Prescription;

import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByMedicalRecordId(UUID medicalRecordId);
    List<Prescription> findByMedicalRecord_Appointment_Patient_Id(UUID patientId);
}