package org.student_api.clinc_system_management.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WaitingListResponseDto {

    private UUID id ;
    private UUID patientID ;
    private UUID doctorID ;
    private LocalDate date ;
    private LocalTime time ;
    private LocalDateTime joinedAt ;
}
