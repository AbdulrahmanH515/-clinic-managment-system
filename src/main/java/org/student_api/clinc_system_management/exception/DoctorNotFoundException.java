package org.student_api.clinc_system_management.exception;

import java.util.UUID;

public class DoctorNotFoundException extends RuntimeException {

  public DoctorNotFoundException(UUID id) {
    super("Doctor not found with id: " + id);
  }
}