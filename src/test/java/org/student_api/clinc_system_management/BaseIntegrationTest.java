package org.student_api.clinc_system_management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public abstract class BaseIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpBaseMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    protected JsonNode registerAdmin(String email, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        return performAndParse(post("/api/auth/register"), body, 201);
    }

    protected JsonNode registerPatient(String email, String password, String firstName, String lastName) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("email", email);
        body.put("phone", "01000000000");
        body.put("dateOfBirth", LocalDate.of(1998, 5, 10).toString());
        body.put("gender", "FEMALE");
        body.put("password", password);
        return performAndParse(post("/api/auth/register/patient"), body, 201);
    }

    protected JsonNode registerDoctor(String email, String password, UUID specializationId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Test");
        body.put("lastName", "Doctor");
        body.put("email", email);
        body.put("phone", "01011111111");
        body.put("medicalLicenseNumber", "LIC-" + UUID.randomUUID());
        body.put("yearsOfExperience", 5);
        body.put("consultationFee", 300.00);
        body.put("specializationId", specializationId.toString());
        body.put("password", password);
        return performAndParse(post("/api/auth/register/doctor"), body, 201);
    }

    protected JsonNode performAndParse(MockHttpServletRequestBuilder builder,
                                       Object body, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected String token(JsonNode authResponse) {
        return authResponse.get("token").asText();
    }

    protected UUID linkedProfileIdFromToken(String jwt) {
        String[] parts = jwt.split("\\.");
        String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        try {
            JsonNode payload = objectMapper.readTree(payloadJson);
            return UUID.fromString(payload.get("linkedProfileId").asText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}