# Clinic Appointment Management System

A REST API for managing a clinic's patients, doctors, appointments, medical records, and prescriptions — built with Spring Boot as a series of incremental coursework assignments.

## Tech Stack

| Component        | Technology                                  |
|-------------------|---------------------------------------------|
| Language          | Java 17                                     |
| Framework         | Spring Boot 4.1.1 (Spring MVC, Spring Data JPA) |
| Database          | H2 (in-memory, `jdbc:h2:mem:clinicdb`) — MySQL driver also on classpath |
| Validation        | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Build tool        | Maven                                       |
| Boilerplate       | Lombok (`@Getter`/`@Setter`/`@AllArgsConstructor`/`@NoArgsConstructor`) |
| IDs               | `UUID` (auto-generated) for every entity    |
| API testing       | Postman collection (`Clinic.postman_collection.json`) |

## Project Structure

```
src/main/java/org/student_api/clinc_system_management/
├── controller/       REST endpoints
├── service/          Business logic
├── repository/       Spring Data JPA repositories
├── model/            JPA entities
├── dto/
│   ├── Request/       Incoming request bodies
│   └── Response/      Outgoing response bodies
├── exception/         Custom exceptions + ErrorResponse
├── handler/           GlobalExceptionHandler (@RestControllerAdvice)
└── role/              Enums (AppointmentStatus, Gender)
```

## Running the Project

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`. The H2 database is in-memory and **resets on every restart** — all data must be re-created each run (e.g. via the Postman collection).

H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:clinicdb`
- User: `sa`
- Password: *(empty)*

## Domain Model

```
Specialization 1───* Doctor 1───* DoctorAvailability
                        │
                        │ 1
                        │
                        *
Patient 1───* Appointment ───1 Doctor
                │ 1
                │
                1
        MedicalRecord 1───* Prescription
```

- A **Doctor** belongs to one **Specialization** and has multiple **DoctorAvailability** windows (day of week + start/end time).
- An **Appointment** links one **Patient** and one **Doctor** at a specific date/time, with a status lifecycle.
- A completed **Appointment** can have exactly one **MedicalRecord** (`@OneToOne`, unique constraint on `appointment_id`).
- A **MedicalRecord** can have multiple **Prescriptions** (`@ManyToOne`).
- `Prescription` and `MedicalRecord` responses derive `patientId`/`doctorId` from the linked `Appointment` rather than storing them redundantly.

### Appointment Status Lifecycle

```
SCHEDULED ──► CONFIRMED ──► COMPLETED
    │              │
    └──► CANCELLED │
    └──► NO_SHOW ◄──┘
```
Invalid transitions (e.g. `COMPLETED → CANCELLED`) are rejected with `409 Invalid Status Transition`.

## API Reference

### Patients
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/patients` | Register a patient |
| GET | `/api/patients` | List patients (paginated) |
| GET | `/api/patients/{id}` | Get patient by id |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Delete patient |
| GET | `/api/patients/{id}/appointments` | Patient's appointments (filter by `status`, `date`) |
| GET | `/api/patients/{patientId}/medical-records` | All medical records for a patient |
| GET | `/api/patients/{patientId}/prescriptions` | All prescriptions for a patient |

### Doctors
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/doctors` | Register a doctor |
| GET | `/api/doctors` | List doctors — supports `?name=`, `?specialization=`, pagination & sorting |
| GET | `/api/doctors/{id}` | Get doctor by id |
| PUT | `/api/doctors/{id}` | Update doctor |
| DELETE | `/api/doctors/{id}` | Delete doctor |
| PUT | `/api/doctors/{id}/specialization` | Assign/change specialization |
| GET | `/api/doctors/{id}/appointments` | Doctor's appointments (filter by `status`, `date`) |
| GET | `/api/doctors/{id}/available-slots?date=` | Free appointment slots for a date |
| POST | `/api/doctors/{doctorId}/availability` | Add a working-hours window |
| GET | `/api/doctors/{doctorId}/availability` | List a doctor's availability windows |

### Specializations
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/specializations` | Create a specialization |
| GET | `/api/specializations` | List all specializations |
| GET | `/api/specializations/{id}` | Get specialization by id |
| GET | `/api/specializations/{id}/doctors` | Doctors under a specialization |

### Appointments
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/appointments` | Book an appointment |
| GET | `/api/appointments` | List appointments — filter by `status`/`date`, paginated |
| GET | `/api/appointments/{id}` | Get appointment by id |
| PATCH | `/api/appointments/{id}/confirm` | SCHEDULED → CONFIRMED |
| PATCH | `/api/appointments/{id}/cancel` | → CANCELLED |
| PATCH | `/api/appointments/{id}/complete` | CONFIRMED → COMPLETED |
| PATCH | `/api/appointments/{id}/no-show` | → NO_SHOW |

Booking validates: patient/doctor exist, date/time is in the future, falls within the doctor's availability window, and doesn't conflict with an existing (non-cancelled) appointment for either the doctor or the patient.

### Medical Records
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/appointments/{appointmentId}/medical-record` | Create a medical record for a completed appointment |
| GET | `/api/medical-records/{id}` | Get a medical record by id |
| GET | `/api/patients/{patientId}/medical-records` | All medical records for a patient |

Business rules: the appointment must exist and be `COMPLETED`; only one medical record is allowed per appointment (`409 Duplicate Medical Record` otherwise).

### Prescriptions
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/medical-records/{recordId}/prescriptions` | Add a prescription to a medical record |
| GET | `/api/medical-records/{recordId}/prescriptions` | All prescriptions for a medical record |
| GET | `/api/patients/{patientId}/prescriptions` | All prescriptions for a patient |

Business rules: the medical record must exist; medication fields (`medicationName`, `dosage`, `frequency`) are required; a medical record may have multiple prescriptions.

## Error Handling

All errors return a consistent JSON shape via `GlobalExceptionHandler`:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Patient Not Found",
  "message": "Patient not found with id: ...",
  "path": "/api/patients/...",
  "details": null
}
```

| Status | Cases |
|---|---|
| 400 | Validation failures, malformed parameters, illegal arguments |
| 404 | Patient/Doctor/Specialization/Appointment/Medical Record not found |
| 409 | Duplicate email/license/specialization, schedule conflict, doctor unavailable, invalid status transition, duplicate medical record, appointment not completed |
| 500 | Unexpected server errors (generic fallback) |

## Assignment Coverage

| # | Feature |
|---|---|
| 1 | Analysis (actors, requirements, business rules, entities) |
| 2–3 | Patient CRUD |
| 4 | Doctor CRUD |
| 5 | Specializations |
| 6–8 | Doctor availability, appointment booking, conflict prevention |
| 9–10 | Appointment status transitions |
| 11 | Available appointment slots |
| 12 | Search, pagination & sorting |
| 13 | Global validation & error handling |
| 14 | Medical visit records |
| 15 | Prescriptions |

## Postman Collection

A ready-to-import collection is included: `Clinic.postman_collection.json`.
