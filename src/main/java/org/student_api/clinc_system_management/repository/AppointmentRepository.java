package org.student_api.clinc_system_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.student_api.clinc_system_management.model.Appointment;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByDoctorIdAndDateAndTime(UUID doctorId, LocalDate date, LocalTime time);
    List<Appointment> findByDoctorIdAndDate(UUID doctorId, LocalDate date);
    List<Appointment> findByPatientIdAndDateAndTime(UUID patientId, LocalDate date, LocalTime time);

    @Query("SELECT a FROM Appointment a WHERE (:status IS NULL OR a.status = :status) AND (:date IS NULL OR a.date = :date)")
    List<Appointment> findAllWithFilters(@Param("status") AppointmentStatus status, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND (:status IS NULL OR a.status = :status) AND (:date IS NULL OR a.date = :date)")
    List<Appointment> findByPatientIdWithFilters(@Param("patientId") UUID patientId, @Param("status") AppointmentStatus status, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND (:status IS NULL OR a.status = :status) AND (:date IS NULL OR a.date = :date)")
    List<Appointment> findByDoctorIdWithFilters(@Param("doctorId") UUID doctorId, @Param("status") AppointmentStatus status, @Param("date") LocalDate date);
}