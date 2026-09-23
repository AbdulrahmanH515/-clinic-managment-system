package org.student_api.clinc_system_management.dto.Request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRegisterRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Medical license number is required")
    @Size(max = 50)
    private String medicalLicenseNumber;

    @NotNull(message = "Years of experience is required")
    @Min(0)
    private Integer yearsOfExperience;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin("0.01")
    private BigDecimal consultationFee;

    @NotNull(message = "Specialization id is required")
    private UUID specializationId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}