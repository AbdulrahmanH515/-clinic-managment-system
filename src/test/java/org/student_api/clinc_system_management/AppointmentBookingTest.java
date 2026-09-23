package org.student_api.clinc_system_management;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AppointmentBookingTest extends BaseIntegrationTest {

    private String adminToken;
    private String doctorToken;
    private String patientToken;
    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUpClinic() throws Exception {
        JsonNode admin = registerAdmin("admin-" + UUID.randomUUID() + "@test.com", "admin1234");
        adminToken = token(admin);

        Map<String, String> specBody = new HashMap<>();
        specBody.put("name", "Cardiology-" + UUID.randomUUID());
        specBody.put("description", "Heart diseases");

        var specResult = mockMvc.perform(post("/api/specializations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specBody)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID specializationId = UUID.fromString(
                objectMapper.readTree(specResult.getResponse().getContentAsString()).get("id").asText());

        JsonNode doctor = registerDoctor("doctor-" + UUID.randomUUID() + "@test.com", "doctor1234", specializationId);
        doctorToken = token(doctor);
        doctorId = linkedProfileIdFromToken(doctorToken);

        for (String day : new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}) {
            Map<String, String> availBody = new HashMap<>();
            availBody.put("dayOfWeek", day);
            availBody.put("startTime", "09:00:00");
            availBody.put("endTime", "17:00:00");

            mockMvc.perform(post("/api/doctors/" + doctorId + "/availability")
                            .header("Authorization", "Bearer " + doctorToken)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(availBody)))
                    .andExpect(status().isCreated());
        }


        JsonNode patient = registerPatient("patient-" + UUID.randomUUID() + "@test.com", "patient1234", "Sara", "Mostafa");
        patientToken = token(patient);
        patientId = linkedProfileIdFromToken(patientToken);
    }

    @Test
    void bookAppointment_withValidData_succeeds() throws Exception {
        Map<String, Object> body = appointmentBody(patientId, doctorId, "2026-11-02", "10:00:00"); // Monday

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    private Map<String, Object> appointmentBody(UUID patientId, UUID doctorId, String date, String time) {
        Map<String, Object> body = new HashMap<>();
        body.put("patientId", patientId.toString());
        body.put("doctorId", doctorId.toString());
        body.put("date", date);
        body.put("time", time);
        return body;
    }
    @Test
    void bookAppointment_doctorDoubleBooking_isRejected() throws Exception {
        Map<String, Object> firstBody = appointmentBody(patientId, doctorId, "2026-11-02", "10:00:00");
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstBody)))
                .andExpect(status().isCreated());


        JsonNode secondPatient = registerPatient("patient2-" + UUID.randomUUID() + "@test.com", "patient1234", "Omar", "Khaled");
        UUID secondPatientId = linkedProfileIdFromToken(token(secondPatient));

        Map<String, Object> conflictBody = appointmentBody(secondPatientId, doctorId, "2026-11-02", "10:00:00");
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + token(secondPatient))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictBody)))
                .andExpect(status().isConflict());
    }

    @Test
    void bookAppointment_patientDoubleBooking_isRejected() throws Exception {
        Map<String, Object> firstBody = appointmentBody(patientId, doctorId, "2026-11-02", "10:00:00");
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstBody)))
                .andExpect(status().isCreated());


        Map<String, String> specBody = new HashMap<>();
        specBody.put("name", "Dermatology-" + UUID.randomUUID());
        specBody.put("description", "Skin diseases");
        var specResult = mockMvc.perform(post("/api/specializations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specBody)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID spec2Id = UUID.fromString(
                objectMapper.readTree(specResult.getResponse().getContentAsString()).get("id").asText());

        JsonNode secondDoctor = registerDoctor("doctor2-" + UUID.randomUUID() + "@test.com", "doctor1234", spec2Id);
        UUID secondDoctorId = linkedProfileIdFromToken(token(secondDoctor));

        Map<String, String> availBody = new HashMap<>();
        availBody.put("dayOfWeek", "MONDAY");
        availBody.put("startTime", "09:00:00");
        availBody.put("endTime", "17:00:00");
        mockMvc.perform(post("/api/doctors/" + secondDoctorId + "/availability")
                        .header("Authorization", "Bearer " + token(secondDoctor))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availBody)))
                .andExpect(status().isCreated());

        Map<String, Object> conflictBody = appointmentBody(patientId, secondDoctorId, "2026-11-02", "10:00:00");
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictBody)))
                .andExpect(status().isConflict());
    }

    @Test
    void bookAppointment_outsideWorkingHours_isRejected() throws Exception {
        Map<String, Object> body = appointmentBody(patientId, doctorId, "2026-11-02", "20:00:00");

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }
    @Test
    void cancelAppointment_succeeds() throws Exception {
        Map<String, Object> body = appointmentBody(patientId, doctorId, "2026-11-02", "10:00:00");
        var result = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID appointmentId = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/appointments/" + appointmentId + "/cancel")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void afterCancellation_slotBecomesAvailableAgain() throws Exception {
        Map<String, Object> firstBody = appointmentBody(patientId, doctorId, "2026-11-02", "10:00:00");
        var result = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstBody)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID appointmentId = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/appointments/" + appointmentId + "/cancel")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk());

        JsonNode secondPatient = registerPatient("patient3-" + UUID.randomUUID() + "@test.com", "patient1234", "Layla", "Ahmed");
        UUID secondPatientId = linkedProfileIdFromToken(token(secondPatient));

        Map<String, Object> secondBody = appointmentBody(secondPatientId, doctorId, "2026-11-02", "10:00:00");
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + token(secondPatient))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondBody)))
                .andExpect(status().isCreated());
    }

    @Test
    void invalidStatusTransition_isRejected() throws Exception {
        Map<String, Object> body = appointmentBody(patientId, doctorId, "2026-11-02", "10:00:00");
        var result = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID appointmentId = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/appointments/" + appointmentId + "/cancel")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/appointments/" + appointmentId + "/confirm")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isConflict());
    }
}