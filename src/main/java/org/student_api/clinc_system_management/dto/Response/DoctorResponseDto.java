package org.student_api.clinc_system_management.dto.Response;



import java.math.BigDecimal;
import java.util.UUID;

public class DoctorResponseDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String medicalLicenseNumber;
    private Integer yearsOfExperience;
    private BigDecimal consultationFee;
    private String specialization;


    public DoctorResponseDto(UUID id, String firstName, String lastName, String email, String phone, String medicalLicenseNumber, Integer yearsOfExperience, BigDecimal consultationFee, String specialization) {
    }
}