package org.student_api.clinc_system_management.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityResponseDto {
    private UUID id ;
    private UUID doctorId ;
    private DayOfWeek dayOfWeek ;
    private LocalTime startTime ;
    private  LocalTime endTime ;
}
