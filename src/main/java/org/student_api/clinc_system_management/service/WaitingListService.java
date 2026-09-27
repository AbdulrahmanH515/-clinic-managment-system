package org.student_api.clinc_system_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.student_api.clinc_system_management.dto.Request.WaitingListRequestDto;
import org.student_api.clinc_system_management.dto.Response.WaitingListResponseDto;
import org.student_api.clinc_system_management.exception.DoctorNotFoundException;
import org.student_api.clinc_system_management.exception.DuplicateWaitingListException;
import org.student_api.clinc_system_management.exception.PatientNotFoundException;
import org.student_api.clinc_system_management.exception.WaitingListSlotAvailableException;
import org.student_api.clinc_system_management.model.Appointment;
import org.student_api.clinc_system_management.model.Doctor;
import org.student_api.clinc_system_management.model.Patient;
import org.student_api.clinc_system_management.model.WaitingList;
import org.student_api.clinc_system_management.repository.AppointmentRepository;
import org.student_api.clinc_system_management.repository.DoctorRepository;
import org.student_api.clinc_system_management.repository.PatientRepository;
import org.student_api.clinc_system_management.repository.WaitingListRepository;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingListService {

    private final WaitingListRepository waitingListRepository ;
    private final DoctorRepository doctorRepository ;
    private final PatientRepository patientRepository ;
    private final AppointmentRepository appointmentRepository ;

    public WaitingListResponseDto joinWaitingList(WaitingListRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException( request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(request.getDoctorId()));

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndDateAndTime(request.getDoctorId(), request.getDate(), request.getTime());

        if (appointments.isEmpty()) {
            throw new WaitingListSlotAvailableException("The requested appointment slot is available");
        }

        boolean slotUnavailable = false;

        for (Appointment appointment : appointments) {
            if (appointment.getStatus() != AppointmentStatus.CANCELLED) {
                slotUnavailable = true;
                break;
            }
        }
        if (!slotUnavailable) {throw new WaitingListSlotAvailableException("The requested appointment slot is available");
        }

        boolean alreadyWaiting = waitingListRepository
                .existsByPatientIdAndDoctorIdAndDateAndTime(request.getPatientId(), request.getDoctorId(), request.getDate(), request.getTime());
        if (alreadyWaiting) {
            throw new DuplicateWaitingListException("Patient is already in the waiting list for this appointment slot");
        }

        WaitingList waitingList = new WaitingList();

        waitingList.setPatient(patient);
        waitingList.setDoctor(doctor);
        waitingList.setDate(request.getDate());
        waitingList.setTime(request.getTime());
        waitingList.setJoinedAt(LocalDateTime.now());

        WaitingList savedWaitingList = waitingListRepository.save(waitingList);

        return new WaitingListResponseDto(savedWaitingList.getId(),
                savedWaitingList.getPatient().getId(),
                savedWaitingList.getDoctor().getId(),
                savedWaitingList.getDate(), savedWaitingList.getTime(),
                savedWaitingList.getJoinedAt());
    }
}
