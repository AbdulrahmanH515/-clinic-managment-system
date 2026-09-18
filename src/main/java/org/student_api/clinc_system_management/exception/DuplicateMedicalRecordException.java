package org.student_api.clinc_system_management.exception;

import java.util.UUID;

public class DuplicateMedicalRecordException extends RuntimeException {
    public DuplicateMedicalRecordException(UUID appointmentId) {
        super("A medical record already exists for appointment: " + appointmentId);
    }
}