package org.student_api.clinc_system_management.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")

public class SpecializationRequestDto {

    @NotBlank(message = "Specialization name is required")
    @Size(max = 100, message = "Specialization name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

}
