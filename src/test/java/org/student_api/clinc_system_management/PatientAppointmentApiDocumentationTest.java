package org.student_api.clinc_system_management;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientAppointmentApiDocumentationTest extends BaseApiDocumentationTest {

    private UUID setUpDoctorWithAvailability(String adminToken) throws Exception {
        UUID specializationId = createSpecialization(adminToken, "Cardiology-" + UUID.randomUUID());
        JsonNode doctor = registerDoctor("doctor-doc-" + UUID.randomUUID() + "@test.com", "doctor1234", specializationId);
        UUID doctorId = linkedProfileIdFromToken(token(doctor));

        Map<String, String> availBody = new HashMap<>();
        availBody.put("dayOfWeek", "MONDAY");
        availBody.put("startTime", "09:00:00");
        availBody.put("endTime", "17:00:00");
        mockMvc.perform(post("/api/doctors/" + doctorId + "/availability")
                        .header("Authorization", "Bearer " + token(doctor))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availBody)))
                .andExpect(status().isCreated());
        return doctorId;
    }

    @Test
    void documentGetPatientById() throws Exception {
        JsonNode patient = registerPatient("patient-get-doc-" + UUID.randomUUID() + "@test.com", "patient1234", "Sara", "Mostafa");
        String patientToken = token(patient);
        UUID patientId = linkedProfileIdFromToken(patientToken);

        docsMockMvc.perform(get("/api/patients/{id}", patientId)
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk())
                .andDo(document("get-patient-by-id",
                        pathParameters(
                                parameterWithName("id").description("The patient's ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — ADMIN, DOCTOR, or the PATIENT themselves")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Patient ID"),
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("phone").description("Phone number"),
                                fieldWithPath("dateOfBirth").description("Date of birth"),
                                fieldWithPath("gender").description("MALE or FEMALE"),
                                fieldWithPath("registrationDate").description("Date the patient was registered")
                        )
                ));
    }

    @Test
    void documentUpdatePatient() throws Exception {
        JsonNode patient = registerPatient("patient-update-doc-" + UUID.randomUUID() + "@test.com", "patient1234", "Sara", "Mostafa");
        String patientToken = token(patient);
        UUID patientId = linkedProfileIdFromToken(patientToken);

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Sara");
        body.put("lastName", "Mostafa-Updated");
        body.put("email", "patient-update-doc-" + UUID.randomUUID() + "@test.com");
        body.put("phone", "01099887766");
        body.put("dateOfBirth", "1998-05-10");
        body.put("gender", "FEMALE");

        docsMockMvc.perform(put("/api/patients/{id}", patientId)
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andDo(document("update-patient",
                        pathParameters(
                                parameterWithName("id").description("The patient's ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — ADMIN or the PATIENT themselves")
                        ),
                        requestFields(
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("phone").description("Phone number"),
                                fieldWithPath("dateOfBirth").description("Date of birth"),
                                fieldWithPath("gender").description("MALE or FEMALE")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Patient ID"),
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("phone").description("Phone number"),
                                fieldWithPath("dateOfBirth").description("Date of birth"),
                                fieldWithPath("gender").description("MALE or FEMALE"),
                                fieldWithPath("registrationDate").description("Date the patient was registered")
                        )
                ));
    }

    @Test
    void documentBookAppointment() throws Exception {
        String adminToken = token(registerAdmin("admin-book-doc-" + UUID.randomUUID() + "@test.com", "admin1234"));
        UUID doctorId = setUpDoctorWithAvailability(adminToken);

        JsonNode patient = registerPatient("patient-book-doc-" + UUID.randomUUID() + "@test.com", "patient1234", "Omar", "Khaled");
        String patientToken = token(patient);
        UUID patientId = linkedProfileIdFromToken(patientToken);

        Map<String, Object> body = new HashMap<>();
        body.put("patientId", patientId.toString());
        body.put("doctorId", doctorId.toString());
        body.put("date", "2026-11-02"); // Monday
        body.put("time", "10:00:00");

        docsMockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("book-appointment",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — PATIENT (for self) or ADMIN")
                        ),
                        requestFields(
                                fieldWithPath("patientId").description("The patient's ID (must match the authenticated patient, unless ADMIN)"),
                                fieldWithPath("doctorId").description("The doctor's ID"),
                                fieldWithPath("date").description("Appointment date (ISO format)"),
                                fieldWithPath("time").description("Appointment time (HH:mm:ss), must fall within the doctor's availability")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Generated appointment ID"),
                                fieldWithPath("patientId").description("Patient ID"),
                                fieldWithPath("doctorId").description("Doctor ID"),
                                fieldWithPath("date").description("Appointment date"),
                                fieldWithPath("time").description("Appointment time"),
                                fieldWithPath("status").description("Appointment status (SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW)")
                        )
                ));
    }

    @Test
    void documentCancelAppointment() throws Exception {
        String adminToken = token(registerAdmin("admin-cancel-doc-" + UUID.randomUUID() + "@test.com", "admin1234"));
        UUID doctorId = setUpDoctorWithAvailability(adminToken);

        JsonNode patient = registerPatient("patient-cancel-doc-" + UUID.randomUUID() + "@test.com", "patient1234", "Layla", "Ahmed");
        String patientToken = token(patient);
        UUID patientId = linkedProfileIdFromToken(patientToken);

        Map<String, Object> body = new HashMap<>();
        body.put("patientId", patientId.toString());
        body.put("doctorId", doctorId.toString());
        body.put("date", "2026-11-02");
        body.put("time", "11:00:00");

        var result = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID appointmentId = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());

        docsMockMvc.perform(patch("/api/appointments/{id}/cancel", appointmentId)
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk())
                .andDo(document("cancel-appointment",
                        pathParameters(
                                parameterWithName("id").description("The appointment's ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — the PATIENT or DOCTOR on the appointment, or ADMIN")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Appointment ID"),
                                fieldWithPath("patientId").description("Patient ID"),
                                fieldWithPath("doctorId").description("Doctor ID"),
                                fieldWithPath("date").description("Appointment date"),
                                fieldWithPath("time").description("Appointment time"),
                                fieldWithPath("status").description("Now CANCELLED")
                        )
                ));
    }

    @Test
    void documentCreateMedicalRecord() throws Exception {
        String adminToken = token(registerAdmin("admin-mr-doc-" + UUID.randomUUID() + "@test.com", "admin1234"));
        UUID doctorId = setUpDoctorWithAvailability(adminToken);

        JsonNode doctorLookup = null; // not needed directly; we re-derive doctor token below
        // نحتاج توكن الدكتور نفسه لعمل confirm/complete/create — نسجله بنفس الـ specialization إللي عملناها جوه setUpDoctorWithAvailability
        // فبدل كده هنعيد التسجيل بنفس الطريقة كاملة هنا عشان نضمن معرفة الـ doctorToken

        UUID specializationId = createSpecialization(adminToken, "Orthopedics-" + UUID.randomUUID());
        JsonNode doctor = registerDoctor("doctor-mr-doc-" + UUID.randomUUID() + "@test.com", "doctor1234", specializationId);
        String doctorToken = token(doctor);
        UUID realDoctorId = linkedProfileIdFromToken(doctorToken);

        Map<String, String> availBody = new HashMap<>();
        availBody.put("dayOfWeek", "MONDAY");
        availBody.put("startTime", "09:00:00");
        availBody.put("endTime", "17:00:00");
        mockMvc.perform(post("/api/doctors/" + realDoctorId + "/availability")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availBody)))
                .andExpect(status().isCreated());

        JsonNode patient = registerPatient("patient-mr-doc-" + UUID.randomUUID() + "@test.com", "patient1234", "Hana", "Adel");
        String patientToken = token(patient);
        UUID patientId = linkedProfileIdFromToken(patientToken);

        Map<String, Object> bookBody = new HashMap<>();
        bookBody.put("patientId", patientId.toString());
        bookBody.put("doctorId", realDoctorId.toString());
        bookBody.put("date", "2026-11-02");
        bookBody.put("time", "12:00:00");

        var bookResult = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBody)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID appointmentId = UUID.fromString(
                objectMapper.readTree(bookResult.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(patch("/api/appointments/" + appointmentId + "/confirm")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/appointments/" + appointmentId + "/complete")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());

        Map<String, String> recordBody = new HashMap<>();
        recordBody.put("diagnosis", "Mild sprain");
        recordBody.put("treatment", "Rest and physiotherapy");
        recordBody.put("notes", "Follow up in 2 weeks");

        docsMockMvc.perform(post("/api/appointments/{appointmentId}/medical-record", appointmentId)
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordBody)))
                .andExpect(status().isCreated())
                .andDo(document("create-medical-record",
                        pathParameters(
                                parameterWithName("appointmentId").description("The appointment's ID — must be COMPLETED")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — the DOCTOR on the appointment, or ADMIN")
                        ),
                        requestFields(
                                fieldWithPath("diagnosis").description("Diagnosis (required)"),
                                fieldWithPath("treatment").description("Treatment plan (optional)"),
                                fieldWithPath("notes").description("Additional notes (optional)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Generated medical record ID"),
                                fieldWithPath("appointmentId").description("The related appointment ID"),
                                fieldWithPath("patientId").description("Patient ID"),
                                fieldWithPath("doctorId").description("Doctor ID"),
                                fieldWithPath("diagnosis").description("Diagnosis"),
                                fieldWithPath("treatment").description("Treatment plan"),
                                fieldWithPath("notes").description("Additional notes"),
                                fieldWithPath("recordDate").description("Date and time the record was created")
                        )
                ));
    }
}