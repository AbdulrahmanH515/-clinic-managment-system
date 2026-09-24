package org.student_api.clinc_system_management;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClinicManagementApiDocumentationTest extends BaseApiDocumentationTest {

    @Test
    void documentCreateSpecialization() throws Exception {
        String adminToken = token(registerAdmin("admin-spec-doc-" + UUID.randomUUID() + "@test.com", "admin1234"));

        Map<String, String> body = new HashMap<>();
        body.put("name", "Dermatology-" + UUID.randomUUID());
        body.put("description", "Skin diseases and conditions");

        docsMockMvc.perform(post("/api/specializations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("create-specialization",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — ADMIN role required")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Specialization name (must be unique)"),
                                fieldWithPath("description").description("Short description of the specialization")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Generated specialization ID"),
                                fieldWithPath("name").description("Specialization name"),
                                fieldWithPath("description").description("Specialization description"),
                                fieldWithPath("doctorCount").description("Number of doctors currently assigned to this specialization")
                        )
                ));
    }

    @Test
    void documentCreateDoctorByAdmin() throws Exception {
        String adminToken = token(registerAdmin("admin-doc-create-" + UUID.randomUUID() + "@test.com", "admin1234"));
        UUID specializationId = createSpecialization(adminToken, "Neurology-" + UUID.randomUUID());

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Mona");
        body.put("lastName", "Youssef");
        body.put("email", "doctor-create-" + UUID.randomUUID() + "@test.com");
        body.put("phone", "01055566677");
        body.put("medicalLicenseNumber", "LIC-" + UUID.randomUUID());
        body.put("yearsOfExperience", 8);
        body.put("consultationFee", 250.00);
        body.put("specializationId", specializationId.toString());

        docsMockMvc.perform(post("/api/doctors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("create-doctor-by-admin",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — ADMIN role required")
                        ),
                        requestFields(
                                fieldWithPath("firstName").description("Doctor's first name"),
                                fieldWithPath("lastName").description("Doctor's last name"),
                                fieldWithPath("email").description("Email address (must be unique)"),
                                fieldWithPath("phone").description("Phone number"),
                                fieldWithPath("medicalLicenseNumber").description("Medical license number"),
                                fieldWithPath("yearsOfExperience").description("Years of professional experience"),
                                fieldWithPath("consultationFee").description("Consultation fee"),
                                fieldWithPath("specializationId").description("ID of an existing specialization")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Generated doctor ID"),
                                fieldWithPath("firstName").description("Doctor's first name"),
                                fieldWithPath("lastName").description("Doctor's last name"),
                                fieldWithPath("email").description("Doctor's email"),
                                fieldWithPath("phone").description("Doctor's phone number"),
                                fieldWithPath("medicalLicenseNumber").description("Medical license number"),
                                fieldWithPath("yearsOfExperience").description("Years of experience"),
                                fieldWithPath("consultationFee").description("Consultation fee"),
                                fieldWithPath("specializationId").description("Assigned specialization ID"),
                                fieldWithPath("specializationName").description("Assigned specialization name")
                        )
                ));
    }

    @Test
    void documentAddDoctorAvailability() throws Exception {
        String adminToken = token(registerAdmin("admin-avail-doc-" + UUID.randomUUID() + "@test.com", "admin1234"));
        UUID specializationId = createSpecialization(adminToken, "Pediatrics-" + UUID.randomUUID());
        JsonNode doctor = registerDoctor("doctor-avail-doc-" + UUID.randomUUID() + "@test.com", "doctor1234", specializationId);
        String doctorToken = token(doctor);
        UUID doctorId = linkedProfileIdFromToken(doctorToken);

        Map<String, String> body = new HashMap<>();
        body.put("dayOfWeek", "MONDAY");
        body.put("startTime", "09:00:00");
        body.put("endTime", "17:00:00");

        docsMockMvc.perform(post("/api/doctors/{id}/availability", doctorId)
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("add-doctor-availability",
                        pathParameters(
                                parameterWithName("id").description("The doctor's ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token — any authenticated user")
                        ),
                        requestFields(
                                fieldWithPath("dayOfWeek").description("Day of the week (e.g. MONDAY, TUESDAY, ...)"),
                                fieldWithPath("startTime").description("Start time (HH:mm:ss)"),
                                fieldWithPath("endTime").description("End time (HH:mm:ss)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Generated availability record ID"),
                                fieldWithPath("doctorId").description("The doctor's ID"),
                                fieldWithPath("dayOfWeek").description("Day of the week"),
                                fieldWithPath("startTime").description("Start time"),
                                fieldWithPath("endTime").description("End time")
                        )
                ));
    }
}