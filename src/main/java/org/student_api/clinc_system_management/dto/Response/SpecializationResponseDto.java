package org.student_api.clinc_system_management.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@SuppressWarnings("unused")
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationResponseDto {

    private UUID id;
    private String name;
    private String description;
    private int doctorCount;

}
