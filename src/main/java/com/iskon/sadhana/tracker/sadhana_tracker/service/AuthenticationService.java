package com.iskon.sadhana.tracker.sadhana_tracker.service;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthRequestDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Mentor;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.MentorRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.SadhakaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling authentication and JWT operations
 * Manages login for both Sadhakas and Mentors
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {
    
    private final SadhakaRepository sadhakaRepository;
    private final MentorRepository mentorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    /**
     * Authenticate user (Sadhaka or Mentor) and return JWT token
     */
    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        log.info("Authentication attempt for username: {}", authRequest.getUsername());
        
        // First try to find as Sadhaka
        Optional<Sadhaka> sadhakaOpt = sadhakaRepository.findByUsername(authRequest.getUsername());
        if (sadhakaOpt.isPresent()) {
            Sadhaka sadhaka = sadhakaOpt.get();
            
            // Check if account is active
            if (!sadhaka.getActive()) {
                throw new RuntimeException("Account is deactivated. Please contact administrator.");
            }
            
            // Verify password
            if (!passwordEncoder.matches(authRequest.getPassword(), sadhaka.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            
            // Generate JWT token
            String token = jwtService.generateToken(
                sadhaka.getUsername(), 
                sadhaka.getId(), 
                sadhaka.getRole().toString()
            );
            
            log.info("Successfully authenticated sadhaka: {}", sadhaka.getUsername());
            return new AuthResponseDTO(
                token, 
                sadhaka.getUsername(), 
                sadhaka.getRole().toString(),
                sadhaka.getSpiritualName()
            );
        }
        
        // Then try to find as Mentor
        Optional<Mentor> mentorOpt = mentorRepository.findByUsername(authRequest.getUsername());
        if (mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();
            
            // Check if account is active
            if (!mentor.getActive()) {
                throw new RuntimeException("Account is deactivated. Please contact administrator.");
            }
            
            // Verify password
            if (!passwordEncoder.matches(authRequest.getPassword(), mentor.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            
            // Generate JWT token
            String token = jwtService.generateToken(
                mentor.getUsername(), 
                mentor.getId(), 
                mentor.getRole().toString()
            );
            
            log.info("Successfully authenticated mentor: {}", mentor.getUsername());
            return new AuthResponseDTO(
                token, 
                mentor.getUsername(), 
                mentor.getRole().toString(),
                mentor.getSpiritualName()
            );
        }
        
        // User not found
        log.warn("Authentication failed for username: {}", authRequest.getUsername());
        throw new RuntimeException("Invalid credentials");
    }
    
    /**
     * Validate JWT token and return user details
     */
    @Transactional(readOnly = true)
    public UserDetails validateToken(String token) {
        try {
            // Extract claims from token
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);
            Long userId = jwtService.extractUserId(token);
            
            // Verify token is not expired
            if (jwtService.isTokenExpired(token)) {
                throw new RuntimeException("Token has expired");
            }
            
            return new UserDetails(userId, username, role);
            
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }
    
    /**
     * Refresh JWT token
     */
    public AuthResponseDTO refreshToken(String oldToken) {
        try {
            // Extract user details from old token
            String username = jwtService.extractUsername(oldToken);
            String role = jwtService.extractRole(oldToken);
            Long userId = jwtService.extractUserId(oldToken);
            
            // Generate new token
            String newToken = jwtService.generateToken(username, userId, role);
            
            // Get spiritual name based on role
            String spiritualName = null;
            if ("SADHAKA".equals(role)) {
                spiritualName = sadhakaRepository.findByUsername(username)
                    .map(Sadhaka::getSpiritualName)
                    .orElse(null);
            } else if ("MENTOR".equals(role)) {
                spiritualName = mentorRepository.findByUsername(username)
                    .map(Mentor::getSpiritualName)
                    .orElse(null);
            }
            
            log.info("Successfully refreshed token for user: {}", username);
            return new AuthResponseDTO(newToken, username, role, spiritualName);
            
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Unable to refresh token");
        }
    }
    
    /**
     * Get current user details from token
     */
    @Transactional(readOnly = true)
    public CurrentUser getCurrentUser(String token) {
        UserDetails userDetails = validateToken(token);
        
        if ("SADHAKA".equals(userDetails.getRole())) {
            Sadhaka sadhaka = sadhakaRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Sadhaka not found"));
            
            return new CurrentUser(
                sadhaka.getId(),
                sadhaka.getUsername(),
                sadhaka.getFirstName() + " " + sadhaka.getLastName(),
                sadhaka.getSpiritualName(),
                sadhaka.getEmail(),
                sadhaka.getRole().toString(),
                sadhaka.getTemple()
            );
        } else if ("MENTOR".equals(userDetails.getRole())) {
            Mentor mentor = mentorRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
            
            return new CurrentUser(
                mentor.getId(),
                mentor.getUsername(),
                mentor.getFirstName() + " " + mentor.getLastName(),
                mentor.getSpiritualName(),
                mentor.getEmail(),
                mentor.getRole().toString(),
                mentor.getTemple()
            );
        }
        
        throw new RuntimeException("Invalid user role");
    }
    
    /**
     * Inner class for user details from token
     */
    public static class UserDetails {
        private final Long userId;
        private final String username;
        private final String role;
        
        public UserDetails(Long userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
        
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }
    
    /**
     * Inner class for current user information
     */
    public static class CurrentUser {
        private final Long id;
        private final String username;
        private final String fullName;
        private final String spiritualName;
        private final String email;
        private final String role;
        private final String temple;
        
        public CurrentUser(Long id, String username, String fullName, String spiritualName, 
                          String email, String role, String temple) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.spiritualName = spiritualName;
            this.email = email;
            this.role = role;
            this.temple = temple;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getSpiritualName() { return spiritualName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getTemple() { return temple; }
    }
}
