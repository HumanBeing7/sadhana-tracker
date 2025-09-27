package com.iskon.sadhana.tracker.sadhana_tracker.controller;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthRequestDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private com.iskon.sadhana.tracker.sadhana_tracker.service.SadhakaService sadhakaService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequestDTO validAuthRequest;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        validAuthRequest = new AuthRequestDTO();
        validAuthRequest.setUsername("testuser");
        validAuthRequest.setPassword("password123");

        authResponse = new AuthResponseDTO();
        authResponse.setToken("jwt.token.here");
        authResponse.setTokenType("Bearer");
        authResponse.setUsername("testuser");
        authResponse.setRole("SADHAKA");
        authResponse.setSpiritualName("Test Das");
    }

    @Test
    void login_Success() throws Exception {
        // Given
        when(authenticationService.authenticate(any(AuthRequestDTO.class)))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("SADHAKA"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        // Given
        when(authenticationService.authenticate(any(AuthRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_EmptyUsername() throws Exception {
        // Given
        validAuthRequest.setUsername("");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isBadRequest());
    }
}
