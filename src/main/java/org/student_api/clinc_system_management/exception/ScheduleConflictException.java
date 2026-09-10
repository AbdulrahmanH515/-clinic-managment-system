package org.student_api.clinc_system_management.exception;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String message) {

      super("Doctor already has a conflicting availability period");
    }
}
