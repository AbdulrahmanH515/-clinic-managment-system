package org.student_api.clinc_system_management.service;

import org.springframework.stereotype.Service;
import org.student_api.clinc_system_management.dto.Request.MedicalRecordRequestDto;
import org.student_api.clinc_system_management.dto.Response.MedicalRecordResponseDto;
import org.student_api.clinc_system_management.exception.*;
import org.student_api.clinc_system_management.model.Appointment;
import org.student_api.clinc_system_management.model.MedicalRecord;
import org.student_api.clinc_system_management.repository.AppointmentRepository;
import org.student_api.clinc_system_management.repository.MedicalRecordRepository;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public MedicalRecordResponseDto createMedicalRecord(UUID appointmentId, MedicalRecordRequestDto request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new AppointmentNotCompletedException(appointmentId);
        }

        if (medicalRecordRepository.existsByAppointmentId(appointmentId)) {
            throw new DuplicateMedicalRecordException(appointmentId);
        }

        MedicalRecord record = new MedicalRecord();
        record.setAppointment(appointment);
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setNotes(request.getNotes());
        record.setRecordDate(LocalDateTime.now());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return toResponseDto(saved);
    }

    public MedicalRecordResponseDto getMedicalRecordById(UUID id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException(id));
        return toResponseDto(record);
    }

    public List<MedicalRecordResponseDto> getMedicalRecordsByPatient(UUID patientId) {
        return medicalRecordRepository.findByAppointment_Patient_Id(patientId)
                .stream().map(this::toResponseDto).toList();
    }

    private MedicalRecordResponseDto toResponseDto(MedicalRecord record) {
        return new MedicalRecordResponseDto(
                record.getId(),
                record.getAppointment().getId(),
                record.getAppointment().getPatient().getId(),
                record.getAppointment().getDoctor().getId(),
                record.getDiagnosis(),
                record.getTreatment(),
                record.getNotes(),
                record.getRecordDate()
        );
    }
}