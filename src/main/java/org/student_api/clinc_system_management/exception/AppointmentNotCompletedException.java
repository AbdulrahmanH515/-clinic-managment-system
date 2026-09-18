package org.student_api.clinc_system_management.exception;

import java.util.UUID;

public class AppointmentNotCompletedException extends RuntimeException {
    public AppointmentNotCompletedException(UUID appointmentId) {
        super("Appointment " + appointmentId + " must be completed before creating a medical record");
    }
}