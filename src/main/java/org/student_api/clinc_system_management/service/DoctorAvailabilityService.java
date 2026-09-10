package org.student_api.clinc_system_management.service;

import org.springframework.stereotype.Service;
import org.student_api.clinc_system_management.dto.Request.DoctorAvailabilityRequestDto;
import org.student_api.clinc_system_management.dto.Response.DoctorAvailabilityResponseDto;
import org.student_api.clinc_system_management.exception.DoctorNotFoundException;
import org.student_api.clinc_system_management.exception.ScheduleConflictException;
import org.student_api.clinc_system_management.model.Doctor;
import org.student_api.clinc_system_management.model.DoctorAvailability;
import org.student_api.clinc_system_management.repository.DoctorAvailabilityRepository;
import org.student_api.clinc_system_management.repository.DoctorRepository;

import javax.print.Doc;
import java.util.List;
import java.util.UUID;

@Service
public class DoctorAvailabilityService {
    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilityRepository, DoctorRepository doctorRepository) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
    }
/// //////////// ////// ////// ////// //////
    public DoctorAvailabilityResponseDto addAvailability(UUID doctorId, DoctorAvailabilityRequestDto request) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new DoctorNotFoundException(doctorId));
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before and time");
        }
        List<DoctorAvailability> existingAvailability = availabilityRepository.findByDoctorId(doctorId);
        for (DoctorAvailability existing : existingAvailability) {
            if (existing.getDayOfWeek() == request.getDayOfWeek()) {
                boolean conflict =
                        request.getStartTime().isBefore(existing.getEndTime()) && request.getEndTime().isAfter(existing.getStartTime());
                if (conflict) {
                    throw new ScheduleConflictException("Doctor already has a conflicting availability period");
                }
            }
        }
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        DoctorAvailability saved = availabilityRepository.save(availability);
        return toResponseDto(saved);
    }
    /// / //// // // / ///// // /
    public List<DoctorAvailabilityResponseDto> getDoctorAvailability(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        return availabilityRepository.findByDoctorId(doctorId).stream().map(this::toResponseDto).toList();
    }

    public DoctorAvailabilityResponseDto toResponseDto(DoctorAvailability availability) {
        return new DoctorAvailabilityResponseDto(
                availability.getId(),
                availability.getDoctor().getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }
}
