package org.student_api.clinc_system_management;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientRegistrationTest extends BaseIntegrationTest {

    @Test
    void registerPatient_withValidData_succeeds() throws Exception {
        JsonNode response = registerPatient("patient1@test.com", "password123", "Sara", "Mostafa");

        assertThat(response.get("token").asText()).isNotBlank();
        assertThat(response.get("role").asText()).isEqualTo("PATIENT");
    }

    @Test
    void registerPatient_withDuplicateEmail_isRejected() throws Exception {
        registerPatient("duplicate@test.com", "password123", "Omar", "Khaled");

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Omar");
        body.put("lastName", "Khaled");
        body.put("email", "duplicate@test.com");
        body.put("phone", "01000000000");
        body.put("dateOfBirth", "1998-05-10");
        body.put("gender", "MALE");
        body.put("password", "password456");

        mockMvc.perform(post("/api/auth/register/patient")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }
}