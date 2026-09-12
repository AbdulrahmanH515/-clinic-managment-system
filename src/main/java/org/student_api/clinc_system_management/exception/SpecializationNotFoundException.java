package org.student_api.clinc_system_management.exception;

import java.util.UUID;

public class SpecializationNotFoundException extends RuntimeException {

    public SpecializationNotFoundException(UUID id) {
        super("Specialization not found with id: " + id);
    }
}
