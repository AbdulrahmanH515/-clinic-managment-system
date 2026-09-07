
package org.student_api.clinc_system_management.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")

public class AssignSpecializationRequestDto {

    @NotNull(message = "Specialization id is required")
    private UUID specializationId;

}
