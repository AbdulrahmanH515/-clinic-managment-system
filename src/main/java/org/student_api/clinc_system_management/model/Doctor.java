package org.student_api.clinc_system_management.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, unique = true)
    private String medicalLicenseNumber;

    @Column(nullable = false)
    private Integer yearsOfExperience;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(nullable = false, length = 100)
    private String specialization;



    public Doctor(String firstName, String lastName, String email, String phone,
                  String medicalLicenseNumber, Integer yearsOfExperience,
                  BigDecimal consultationFee, String specialization) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.medicalLicenseNumber = medicalLicenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.specialization = specialization;
    }


}
