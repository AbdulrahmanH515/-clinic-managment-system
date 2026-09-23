package org.student_api.clinc_system_management.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.student_api.clinc_system_management.role.Role;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final UUID userId;
    private final String email;
    private final Role role;
    private final UUID linkedProfileId;
}