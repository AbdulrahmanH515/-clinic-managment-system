package org.student_api.clinc_system_management.exception;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(UUID id) {
        super("Patient not found with id: " + id);
    }
}
