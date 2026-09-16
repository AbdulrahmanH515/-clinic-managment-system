package org.student_api.clinc_system_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.student_api.clinc_system_management.dto.Request.AppointmentRequestDto;
import org.student_api.clinc_system_management.dto.Response.AppointmentResponseDto;
import org.student_api.clinc_system_management.exception.*;
import org.student_api.clinc_system_management.model.Appointment;
import org.student_api.clinc_system_management.model.Doctor;
import org.student_api.clinc_system_management.model.DoctorAvailability;
import org.student_api.clinc_system_management.model.Patient;
import org.student_api.clinc_system_management.repository.*;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;


    private static final Map<AppointmentStatus, Set<AppointmentStatus>> VALID_TRANSITIONS = new EnumMap<>(AppointmentStatus.class);static {
        VALID_TRANSITIONS.put(AppointmentStatus.SCHEDULED, EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW));
        VALID_TRANSITIONS.put(AppointmentStatus.CONFIRMED, EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW));
        VALID_TRANSITIONS.put(AppointmentStatus.CANCELLED, EnumSet.noneOf(AppointmentStatus.class));
        VALID_TRANSITIONS.put(AppointmentStatus.COMPLETED, EnumSet.noneOf(AppointmentStatus.class));
        VALID_TRANSITIONS.put(AppointmentStatus.NO_SHOW, EnumSet.noneOf(AppointmentStatus.class));
    }
    private static final int APPOINTMENT_DURATION_MINUTES = 30;
    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, DoctorAvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(() -> new PatientNotFoundException(request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(() -> new DoctorNotFoundException(request.getDoctorId()));
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (request.getDate().isBefore(today) || (request.getDate().isEqual(today) && !request.getTime().isAfter(now))) {
            throw new IllegalArgumentException("Appointment must be in the future");
        }

        List<DoctorAvailability> availabilityList = availabilityRepository.findByDoctorId(request.getDoctorId());
        boolean available = false;
        for (DoctorAvailability availability : availabilityList) {
            if (availability.getDayOfWeek() == request.getDate().getDayOfWeek() && !request.getTime().isBefore(availability.getStartTime()) && request.getTime().isBefore(availability.getEndTime())) {
                available = true;
                break;
            }
        }
        if (!available) {
            throw new DoctorUnavailableException("Doctor is not available at the request date and time ");
        }

        List<Appointment> doctorAppointments =
                appointmentRepository.findByDoctorIdAndDateAndTime(request.getDoctorId(), request.getDate(), request.getTime());
        for (Appointment existing : doctorAppointments) {
            if (existing.getStatus() != AppointmentStatus.CANCELLED) {
                throw new ScheduleConflictException("Doctor already has an appointment at this time");
            }
        }

        List<Appointment> patientAppointments =
                appointmentRepository.findByPatientIdAndDateAndTime(request.getPatientId(), request.getDate(), request.getTime());
        for (Appointment existing : patientAppointments) {
            if (existing.getStatus() != AppointmentStatus.CANCELLED) {
                throw new ScheduleConflictException("Patient already has an appointment at this time");
            }
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);
        return toResponseDto(saved);
    }

    public List<LocalTime> getAvailableSlots(UUID doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new DoctorNotFoundException(doctorId));
        List<DoctorAvailability> availabilityList = availabilityRepository.findByDoctorId(doctorId);
        LocalTime startTime = null;
        LocalTime endTime = null;
        for (DoctorAvailability availability : availabilityList) {
            if (availability.getDayOfWeek() == date.getDayOfWeek()) {
                startTime = availability.getStartTime();
                endTime = availability.getEndTime();
                break;
            }
        }
        if (startTime == null || endTime == null) {
            return List.of();
        }
        List<Appointment> appointments =appointmentRepository.findByDoctorIdAndDate(doctorId, date);
        List<LocalTime> availableSlots = new ArrayList<>();

        LocalTime currentTime = startTime;

        while (currentTime.plusMinutes(APPOINTMENT_DURATION_MINUTES).compareTo(endTime) <= 0) {
            availableSlots.add(currentTime);
            currentTime = currentTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);
        }
        availableSlots.removeIf(slot ->
                appointments.stream().anyMatch(appointment ->
                        appointment.getTime().equals(slot) && appointment.getStatus() != AppointmentStatus.CANCELLED));

        return availableSlots;
    }

    public AppointmentResponseDto getAppointmentById(UUID id) {
        return toResponseDto(findAppointmentOrThrow(id));
    }

    public Page<AppointmentResponseDto> getAllAppointments(AppointmentStatus status, LocalDate date, Pageable pageable) {
        return appointmentRepository.findAllWithFilters(status, date, pageable).map(this::toResponseDto);
    }

    public List<AppointmentResponseDto> getAppointmentsByPatient(UUID patientId, AppointmentStatus status, LocalDate date) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException(patientId);
        }
        return appointmentRepository.findByPatientIdWithFilters(patientId, status, date).stream().map(this::toResponseDto).toList();
    }


    public List<AppointmentResponseDto> getAppointmentsByDoctor(UUID doctorId, AppointmentStatus status, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        return appointmentRepository.findByDoctorIdWithFilters(doctorId, status, date).stream().map(this::toResponseDto).toList();
    }


    public AppointmentResponseDto confirmAppointment(UUID id) {
        return changeStatus(id, AppointmentStatus.CONFIRMED);
    }

    public AppointmentResponseDto cancelAppointment(UUID id) {
        return changeStatus(id, AppointmentStatus.CANCELLED);
    }

    public AppointmentResponseDto completeAppointment(UUID id) {
        return changeStatus(id, AppointmentStatus.COMPLETED);
    }

    public AppointmentResponseDto markNoShow(UUID id) {
        return changeStatus(id, AppointmentStatus.NO_SHOW);
    }

    private AppointmentResponseDto changeStatus(UUID id, AppointmentStatus newStatus) {
        Appointment appointment = findAppointmentOrThrow(id);
        AppointmentStatus currentStatus = appointment.getStatus();
        Set<AppointmentStatus> allowedNextStatuses = VALID_TRANSITIONS.getOrDefault(currentStatus, EnumSet.noneOf(AppointmentStatus.class));
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }
        appointment.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        return toResponseDto(saved);
    }

    private Appointment findAppointmentOrThrow(UUID id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private AppointmentResponseDto toResponseDto(Appointment appointment) {
        return new AppointmentResponseDto(appointment.getId(), appointment.getPatient().getId(), appointment.getDoctor().getId(), appointment.getDate(), appointment.getTime(), appointment.getStatus());
    }
}
