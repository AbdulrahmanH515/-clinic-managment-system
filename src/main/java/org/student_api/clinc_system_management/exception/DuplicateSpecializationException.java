package org.student_api.clinc_system_management.exception;

public class DuplicateSpecializationException extends RuntimeException {

    public DuplicateSpecializationException(String name) {
        super("Specialization name already exists: " + name);
    }
}
