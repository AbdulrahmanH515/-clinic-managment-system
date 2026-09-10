package org.student_api.clinc_system_management.repository;

import org.student_api.clinc_system_management.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByDoctorIdAndDateAndTime(UUID doctorId, LocalDate date, LocalTime time);
    List<Appointment> findByPatientIdAndDateAndTime(UUID patientId, LocalDate date, LocalTime time);
}