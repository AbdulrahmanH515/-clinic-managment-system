package org.student_api.clinc_system_management.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("A patient with email '" + email + "' is already registered");
    }
}
