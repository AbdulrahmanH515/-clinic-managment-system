package org.student_api.clinc_system_management.exception;

public class DuplicateLicenseException extends RuntimeException {

    public DuplicateLicenseException(String licenseNumber) {
        super("Medical license number already exists: " + licenseNumber);
    }
}