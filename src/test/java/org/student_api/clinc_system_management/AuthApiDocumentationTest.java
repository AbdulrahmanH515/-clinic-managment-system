package org.student_api.clinc_system_management;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiDocumentationTest extends BaseApiDocumentationTest {

    @Test
    void documentRegisterAdmin() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "admin-" + UUID.randomUUID() + "@test.com");
        body.put("password", "admin1234");

        docsMockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("register-admin",
                        requestFields(
                                fieldWithPath("email").description("Admin's email address (must be unique)"),
                                fieldWithPath("password").description("Password, minimum 8 characters")
                        ),
                        responseFields(
                                fieldWithPath("token").description("JWT access token, valid for 24 hours"),
                                fieldWithPath("userId").description("Generated user ID"),
                                fieldWithPath("email").description("Registered email"),
                                fieldWithPath("role").description("Always ADMIN for this endpoint")
                        )
                ));
    }

    @Test
    void documentLogin() throws Exception {
        String email = "login-" + UUID.randomUUID() + "@test.com";
        registerAdmin(email, "admin1234");

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", "admin1234");

        docsMockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("email").description("Registered email address"),
                                fieldWithPath("password").description("Account password")
                        ),
                        responseFields(
                                fieldWithPath("token").description("JWT access token, valid for 24 hours"),
                                fieldWithPath("userId").description("The user's ID"),
                                fieldWithPath("email").description("The user's email"),
                                fieldWithPath("role").description("The user's role: ADMIN, DOCTOR, or PATIENT")
                        )
                ));
    }

    @Test
    void documentRegisterPatient() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Sara");
        body.put("lastName", "Mostafa");
        body.put("email", "patient-doc-" + UUID.randomUUID() + "@test.com");
        body.put("phone", "01099887766");
        body.put("dateOfBirth", "1998-05-10");
        body.put("gender", "FEMALE");
        body.put("password", "patient1234");

        docsMockMvc.perform(post("/api/auth/register/patient")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("register-patient",
                        requestFields(
                                fieldWithPath("firstName").description("Patient's first name"),
                                fieldWithPath("lastName").description("Patient's last name"),
                                fieldWithPath("email").description("Email address (must be unique)"),
                                fieldWithPath("phone").description("Phone number"),
                                fieldWithPath("dateOfBirth").description("Date of birth (ISO format, must be in the past)"),
                                fieldWithPath("gender").description("MALE or FEMALE"),
                                fieldWithPath("password").description("Password, minimum 8 characters")
                        ),
                        responseFields(
                                fieldWithPath("token").description("JWT access token"),
                                fieldWithPath("userId").description("Generated user ID"),
                                fieldWithPath("email").description("Registered email"),
                                fieldWithPath("role").description("Always PATIENT for this endpoint")
                        )
                ));
    }

    @Test
    void documentRegisterDoctor() throws Exception {
        String adminToken = token(registerAdmin("admin-for-doctor-doc-" + UUID.randomUUID() + "@test.com", "admin1234"));
        UUID specializationId = createSpecialization(adminToken, "Cardiology-" + UUID.randomUUID());

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Ahmed");
        body.put("lastName", "Ibrahim");
        body.put("email", "doctor-doc-" + UUID.randomUUID() + "@test.com");
        body.put("phone", "01012345678");
        body.put("medicalLicenseNumber", "LIC-" + UUID.randomUUID());
        body.put("yearsOfExperience", 5);
        body.put("consultationFee", 300.00);
        body.put("specializationId", specializationId.toString());
        body.put("password", "doctor1234");

        docsMockMvc.perform(post("/api/auth/register/doctor")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andDo(document("register-doctor",
                        requestFields(
                                fieldWithPath("firstName").description("Doctor's first name"),
                                fieldWithPath("lastName").description("Doctor's last name"),
                                fieldWithPath("email").description("Email address (must be unique)"),
                                fieldWithPath("phone").description("Phone number"),
                                fieldWithPath("medicalLicenseNumber").description("Medical license number"),
                                fieldWithPath("yearsOfExperience").description("Years of professional experience"),
                                fieldWithPath("consultationFee").description("Consultation fee"),
                                fieldWithPath("specializationId").description("ID of an existing specialization"),
                                fieldWithPath("password").description("Password, minimum 8 characters")
                        ),
                        responseFields(
                                fieldWithPath("token").description("JWT access token"),
                                fieldWithPath("userId").description("Generated user ID"),
                                fieldWithPath("email").description("Registered email"),
                                fieldWithPath("role").description("Always DOCTOR for this endpoint")
                        )
                ));
    }
}