package org.student_api.clinc_system_management.exception;

public class DuplicateWaitingListException extends RuntimeException {
    public DuplicateWaitingListException(String message)
    {
        super(message);
    }
}
