package org.student_api.clinc_system_management;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MedicalRecordTest extends BaseIntegrationTest {

    private String adminToken;
    private String doctorToken;
    private String patientToken;
    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUpClinic() throws Exception {
        adminToken = token(registerAdmin("admin-" + UUID.randomUUID() + "@test.com", "admin1234"));

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

        Map<String, String> availBody = new HashMap<>();
        availBody.put("dayOfWeek", "MONDAY");
        availBody.put("startTime", "09:00:00");
        availBody.put("endTime", "17:00:00");
        mockMvc.perform(post("/api/doctors/" + doctorId + "/availability")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availBody)))
                .andExpect(status().isCreated());

        JsonNode patient = registerPatient("patient-" + UUID.randomUUID() + "@test.com", "patient1234", "Sara", "Mostafa");
        patientToken = token(patient);
        patientId = linkedProfileIdFromToken(patientToken);
    }

    private UUID bookAppointment() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("patientId", patientId.toString());
        body.put("doctorId", doctorId.toString());
        body.put("date", "2026-11-02"); // Monday
        body.put("time", "10:00:00");

        var result = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    @Test
    void createMedicalRecord_forNonCompletedAppointment_isRejected() throws Exception {
        UUID appointmentId = bookAppointment(); // لسه SCHEDULED، مش COMPLETED

        Map<String, String> recordBody = new HashMap<>();
        recordBody.put("diagnosis", "Flu");
        recordBody.put("treatment", "Rest and fluids");

        mockMvc.perform(post("/api/appointments/" + appointmentId + "/medical-record")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordBody)))
                .andExpect(status().isConflict());
    }

    @Test
    void createMedicalRecord_forCompletedAppointment_succeeds() throws Exception {
        UUID appointmentId = bookAppointment();

        // نكمل الـ workflow الصحيح: confirm ثم complete قبل ما نعمل السجل
        mockMvc.perform(patch("/api/appointments/" + appointmentId + "/confirm")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/appointments/" + appointmentId + "/complete")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());

        Map<String, String> recordBody = new HashMap<>();
        recordBody.put("diagnosis", "Flu");
        recordBody.put("treatment", "Rest and fluids");

        mockMvc.perform(post("/api/appointments/" + appointmentId + "/medical-record")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordBody)))
                .andExpect(status().isCreated());
    }

    @Test
    void createMedicalRecord_duplicateForSameAppointment_isRejected() throws Exception {
        UUID appointmentId = bookAppointment();
        mockMvc.perform(patch("/api/appointments/" + appointmentId + "/confirm")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/appointments/" + appointmentId + "/complete")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());

        Map<String, String> recordBody = new HashMap<>();
        recordBody.put("diagnosis", "Flu");
        recordBody.put("treatment", "Rest and fluids");

        mockMvc.perform(post("/api/appointments/" + appointmentId + "/medical-record")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordBody)))
                .andExpect(status().isCreated());

        // محاولة إنشاء سجل تاني لنفس الموعد
        mockMvc.perform(post("/api/appointments/" + appointmentId + "/medical-record")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordBody)))
                .andExpect(status().isConflict());
    }
}