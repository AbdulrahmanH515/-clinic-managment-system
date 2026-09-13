package org.student_api.clinc_system_management.exception;

import java.util.UUID;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(UUID id) {
        super("Appointment not found with id: " + id);
    }
}