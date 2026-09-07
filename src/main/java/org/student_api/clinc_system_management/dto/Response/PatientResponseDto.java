package org.student_api.clinc_system_management.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.student_api.clinc_system_management.role.Gender;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter

public class PatientResponseDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private LocalDate registrationDate;

    public PatientResponseDto(UUID id, String firstName, String lastName, String email, String phone, LocalDate dateOfBirth, Gender gender, LocalDate registrationDate) {
    }
}