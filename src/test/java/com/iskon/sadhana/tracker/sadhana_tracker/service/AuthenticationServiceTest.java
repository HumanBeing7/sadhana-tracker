package com.iskon.sadhana.tracker.sadhana_tracker.service;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthRequestDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.SadhakaRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.MentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private SadhakaRepository sadhakaRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Sadhaka testSadhaka;
    private AuthRequestDTO authRequest;

    @BeforeEach
    void setUp() {
        testSadhaka = new Sadhaka();
        testSadhaka.setId(1L);
        testSadhaka.setUsername("testuser");
        testSadhaka.setPassword("encodedPassword");
        testSadhaka.setEmail("test@iskcon.org");
        testSadhaka.setFirstName("Test");
        testSadhaka.setLastName("User");
        testSadhaka.setActive(true);

        authRequest = new AuthRequestDTO();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
    }

    @Test
    void authenticate_Success() {
        // Given
        String token = "jwt.token.here";

        when(sadhakaRepository.findByUsername(authRequest.getUsername()))
                .thenReturn(Optional.of(testSadhaka));
        when(passwordEncoder.matches(authRequest.getPassword(), testSadhaka.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(anyString(), anyLong(), anyString()))
                .thenReturn(token);

        // When
        AuthResponseDTO result = authenticationService.authenticate(authRequest);

        // Then
        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertEquals(authRequest.getUsername(), result.getUsername());
        assertEquals("Bearer", result.getTokenType());

        verify(sadhakaRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), testSadhaka.getPassword());
        verify(jwtService).generateToken(anyString(), anyLong(), anyString());
    }

    @Test
    void authenticate_InvalidCredentials() {
        // Given
        authRequest.setPassword("wrongpassword");

        when(sadhakaRepository.findByUsername(authRequest.getUsername()))
                .thenReturn(Optional.of(testSadhaka));
        when(passwordEncoder.matches(authRequest.getPassword(), testSadhaka.getPassword()))
                .thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                authenticationService.authenticate(authRequest));

        verify(sadhakaRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), testSadhaka.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyLong(), anyString());
    }

    @Test
    void authenticate_UserNotFound() {
        // Given
        authRequest.setUsername("nonexistent");

        when(sadhakaRepository.findByUsername(authRequest.getUsername()))
                .thenReturn(Optional.empty());
        when(mentorRepository.findByUsername(authRequest.getUsername()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                authenticationService.authenticate(authRequest));

        verify(sadhakaRepository).findByUsername(authRequest.getUsername());
        verify(mentorRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyLong(), anyString());
    }

    @Test
    void authenticate_InactiveUser() {
        // Given
        testSadhaka.setActive(false);

        when(sadhakaRepository.findByUsername(authRequest.getUsername()))
                .thenReturn(Optional.of(testSadhaka));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                authenticationService.authenticate(authRequest));

        assertEquals("Account is deactivated. Please contact administrator.", exception.getMessage());

        verify(sadhakaRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyLong(), anyString());
    }
}
