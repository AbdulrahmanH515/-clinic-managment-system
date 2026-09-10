package org.student_api.clinc_system_management.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import org.student_api.clinc_system_management.dto.Request.AppointmentRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.model.Appointment;
import org.student_api.clinc_system_management.model.Doctor;
import org.student_api.clinc_system_management.model.DoctorAvailability;
import org.student_api.clinc_system_management.model.Patient;
import org.student_api.clinc_system_management.repository.AppointmentRepository;
import org.student_api.clinc_system_management.repository.DoctorAvailabilityRepository;
import org.student_api.clinc_system_management.repository.DoctorRepository;
import org.student_api.clinc_system_management.repository.PatientRepository;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, DoctorAvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
    }
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (request.getDate().isBefore(today) || (request.getDate().isEqual(today ) && !request.getTime().isAfter(now))){
            throw new IllegalArgumentException("Appointment must be in the future");
        }


        List<DoctorAvailability> availabilityList = availabilityRepository.findByDoctorId(request.getDoctorId());
        boolean available = false ;
        for (DoctorAvailability availability : availabilityList){
            if (availability.getDayOfWeek() == request.getDate().getDayOfWeek() && !request.getTime().isBefore(availability.getStartTime()) && request.getTime().isBefore(availability.getEndTime())) {
                available = true ;
                break;
            }
        }
        if (!available){
            throw new IllegalArgumentException("Doctor is not available at the request date and time ");
        }


        List<Appointment> doctorAppointments =
                appointmentRepository.findByDoctorIdAndDateAndTime(request.getDoctorId(), request.getDate(), request.getTime());
        for (Appointment existing : doctorAppointments) {
            if (existing.getStatus() != AppointmentStatus.CANCELLED) {
                throw new IllegalArgumentException("Doctor already has an appointment at this time");
            }
        }


        List<Appointment> patientAppointments =
                appointmentRepository.findByPatientIdAndDateAndTime(request.getPatientId(), request.getDate(), request.getTime());
        for (Appointment existing : patientAppointments) {
            if (existing.getStatus() != AppointmentStatus.CANCELLED) {
                throw new IllegalArgumentException("Patient already has an appointment at this time");
            }
        }


        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);
        return new AppointmentResponseDto(saved.getId(), saved.getPatient().getId(), saved.getDoctor().getId(), saved.getDate(), saved.getTime() , saved.getStatus());
    }

    public void cancelAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public AppointmentResponseDto getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        return new AppointmentResponseDto(appointment.getId(), appointment.getPatient().getId(), appointment.getDoctor().getId(), appointment.getDate(), appointment.getTime() , appointment.getStatus());
    }

}
