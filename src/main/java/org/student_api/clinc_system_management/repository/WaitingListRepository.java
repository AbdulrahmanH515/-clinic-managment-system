package org.student_api.clinc_system_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.student_api.clinc_system_management.model.WaitingList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface WaitingListRepository extends JpaRepository<WaitingList, UUID> {

    boolean existsByPatientIdAndDoctorIdAndDateAndTime(UUID patientId, UUID doctorId, LocalDate date, LocalTime time);

    List<WaitingList> findByDoctorIdAndDateAndTimeOrderByJoinedAtAsc(UUID doctorId, LocalDate date, LocalTime time);
}