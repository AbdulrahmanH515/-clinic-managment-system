package org.student_api.clinc_system_management.DTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@SuppressWarnings("unused")
public class PatientResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDate registrationDate;

    public PatientResponseDto() {
    }

    public PatientResponseDto(Long id, String firstName, String lastName, String email, String phone,
                              LocalDate dateOfBirth, String gender, LocalDate registrationDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }
}