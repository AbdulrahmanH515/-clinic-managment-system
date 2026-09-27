package org.student_api.clinc_system_management.dto.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WaitingListRequestDto {

    @NotNull
    private UUID patientId ;

    @NotNull
    private UUID doctorId ;

    @NotNull
    @Future
    private LocalDate date ;

    @NotNull
    private LocalTime time ;
}
