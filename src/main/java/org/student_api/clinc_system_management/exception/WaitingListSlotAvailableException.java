package org.student_api.clinc_system_management.exception;

public class WaitingListSlotAvailableException extends RuntimeException {
    public WaitingListSlotAvailableException(String message)
    {
        super(message);
    }
}
