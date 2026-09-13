package org.student_api.clinc_system_management.exception;

import org.student_api.clinc_system_management.role.AppointmentStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(AppointmentStatus currentStatus, AppointmentStatus targetStatus) {
        super("Cannot transition appointment from " + currentStatus + " to " + targetStatus);
    }
}