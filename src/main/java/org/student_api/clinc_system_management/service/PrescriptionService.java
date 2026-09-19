package org.student_api.clinc_system_management.service;

import org.springframework.stereotype.Service;
import org.student_api.clinc_system_management.dto.Request.PrescriptionRequestDto;
import org.student_api.clinc_system_management.dto.Response.PrescriptionResponseDto;
import org.student_api.clinc_system_management.exception.MedicalRecordNotFoundException;
import org.student_api.clinc_system_management.model.MedicalRecord;
import org.student_api.clinc_system_management.model.Prescription;
import org.student_api.clinc_system_management.repository.MedicalRecordRepository;
import org.student_api.clinc_system_management.repository.PrescriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, MedicalRecordRepository medicalRecordRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public PrescriptionResponseDto createPrescription(UUID recordId, PrescriptionRequestDto request) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(recordId));

        Prescription prescription = new Prescription();
        prescription.setMedicalRecord(record);
        prescription.setMedicationName(request.getMedicationName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDuration(request.getDuration());
        prescription.setInstructions(request.getInstructions());
        prescription.setPrescribedDate(LocalDateTime.now());

        Prescription saved = prescriptionRepository.save(prescription);
        return toResponseDto(saved);
    }

    public List<PrescriptionResponseDto> getPrescriptionsByRecord(UUID recordId) {
        if (!medicalRecordRepository.existsById(recordId)) {
            throw new MedicalRecordNotFoundException(recordId);
        }
        return prescriptionRepository.findByMedicalRecordId(recordId)
                .stream().map(this::toResponseDto).toList();
    }

    public List<PrescriptionResponseDto> getPrescriptionsByPatient(UUID patientId) {
        return prescriptionRepository.findByMedicalRecord_Appointment_Patient_Id(patientId)
                .stream().map(this::toResponseDto).toList();
    }

    private PrescriptionResponseDto toResponseDto(Prescription prescription) {
        MedicalRecord record = prescription.getMedicalRecord();
        return new PrescriptionResponseDto(
                prescription.getId(),
                record.getId(),
                record.getAppointment().getPatient().getId(),
                record.getAppointment().getDoctor().getId(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getDuration(),
                prescription.getInstructions(),
                prescription.getPrescribedDate()
        );
    }
}