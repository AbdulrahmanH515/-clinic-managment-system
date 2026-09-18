package org.student_api.clinc_system_management.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordRequestDto {

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatment;

    private String notes;
}