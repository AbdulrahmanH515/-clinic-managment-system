package org.student_api.clinc_system_management.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {
    private UUID id ;
    private UUID patientId ;
    private UUID doctorId ;
    private LocalDate date ;
    private LocalTime time ;
    private AppointmentStatus status ;
}
