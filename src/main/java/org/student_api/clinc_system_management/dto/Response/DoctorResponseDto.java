package org.student_api.clinc_system_management.dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@SuppressWarnings("unused")
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String medicalLicenseNumber;
    private Integer yearsOfExperience;
    private BigDecimal consultationFee;
    private UUID specializationId;
    private String specializationName;



}