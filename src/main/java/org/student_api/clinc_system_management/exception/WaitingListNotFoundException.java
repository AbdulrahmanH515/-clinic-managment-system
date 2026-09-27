package org.student_api.clinc_system_management.exception;

public class WaitingListNotFoundException extends RuntimeException {
    public WaitingListNotFoundException(String message)
    {
        super(message);
    }
}
