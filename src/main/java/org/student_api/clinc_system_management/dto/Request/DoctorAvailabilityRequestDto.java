package org.student_api.clinc_system_management.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.locks.LockSupport;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityRequestDto {
    @NotNull(message = "Day of week is requierd")
    private DayOfWeek dayOfWeek ;

    @NotNull(message = "Start time is requierd")
    private LocalTime startTime ;

    @NotNull(message = "End time is requierd")
    private LocalTime endTime ;
}
