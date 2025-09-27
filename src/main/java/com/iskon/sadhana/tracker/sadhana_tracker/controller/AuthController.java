package com.iskon.sadhana.tracker.sadhana_tracker.controller;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthRequestDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.AuthResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.SadhakaDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.service.AuthenticationService;
import com.iskon.sadhana.tracker.sadhana_tracker.service.MentorService;
import com.iskon.sadhana.tracker.sadhana_tracker.service.SadhakaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for Authentication operations
 * Handles login, registration, and token management
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final SadhakaService sadhakaService;
    private final MentorService mentorService;
    
    /**
     * Login endpoint for both Sadhakas and Mentors
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            log.info("Login attempt for username: {}", authRequest.getUsername());
            
            AuthResponseDTO authResponse = authenticationService.authenticate(authRequest);
            
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            log.error("Login failed for username: {}, error: {}", authRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", e.getMessage()));
        }
    }
    
    /**
     * Register new Sadhaka
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerSadhaka(@Valid @RequestBody SadhakaRegistrationRequest request) {
        try {
            log.info("Registration attempt for username: {}", request.getSadhakaDTO().getUsername());
            
            SadhakaDTO registeredSadhaka = sadhakaService.registerSadhaka(
                request.getSadhakaDTO(), 
                request.getPassword()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Registration successful", registeredSadhaka));
            
        } catch (Exception e) {
            log.error("Registration failed for username: {}, error: {}", 
                     request.getSadhakaDTO().getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed", e.getMessage()));
        }
    }
    
    /**
     * Register new Mentor
     * POST /api/auth/register/mentor
     */
    @PostMapping("/register/mentor")
    public ResponseEntity<?> registerMentor(@Valid @RequestBody MentorRegistrationRequest request) {
        try {
            log.info("Mentor registration attempt for username: {}", request.getMentorDTO().getUsername());
            
            MentorDTO registeredMentor = mentorService.registerMentor(
                request.getMentorDTO(), 
                request.getPassword()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Mentor registration successful", registeredMentor));
            
        } catch (Exception e) {
            log.error("Mentor registration failed for username: {}, error: {}", 
                     request.getMentorDTO().getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Mentor registration failed", e.getMessage()));
        }
    }
    
    /**
     * Register new Admin (Super Admin only or initial setup)
     * POST /api/auth/register/admin
     */
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        try {
            log.info("Admin registration attempt for username: {}", request.getUsername());
            
            // For security, you might want to add additional checks here
            // For now, allowing admin registration for initial setup
            
            SadhakaDTO adminUser = sadhakaService.registerAdmin(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Admin registration successful", adminUser));
            
        } catch (Exception e) {
            log.error("Admin registration failed for username: {}, error: {}", 
                     request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Admin registration failed", e.getMessage()));
        }
    }
    
    /**
     * Refresh JWT token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid token", "Authorization header is missing or invalid"));
            }
            
            AuthResponseDTO authResponse = authenticationService.refreshToken(token);
            
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token refresh failed", e.getMessage()));
        }
    }
    
    /**
     * Get current user profile
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid token", "Authorization header is missing or invalid"));
            }
            
            AuthenticationService.CurrentUser currentUser = authenticationService.getCurrentUser(token);
            
            return ResponseEntity.ok(currentUser);
            
        } catch (Exception e) {
            log.error("Get current user failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required", e.getMessage()));
        }
    }
    
    /**
     * Validate token endpoint
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid token", "Authorization header is missing or invalid"));
            }
            
            AuthenticationService.UserDetails userDetails = authenticationService.validateToken(token);
            
            return ResponseEntity.ok(new SuccessResponse("Token is valid", userDetails));
            
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token", e.getMessage()));
        }
    }
    
    /**
     * Logout endpoint (client-side token removal)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In JWT, logout is typically handled client-side by removing the token
        // Server-side logout would require token blacklisting (future enhancement)
        return ResponseEntity.ok(new SuccessResponse("Logout successful", 
                "Please remove the token from client storage"));
    }
    
    /**
     * Helper method to extract token from Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    /**
     * Inner class for registration request
     */
    public static class SadhakaRegistrationRequest {
        private SadhakaDTO sadhakaDTO;
        private String password;
        
        // Constructors
        public SadhakaRegistrationRequest() {}
        
        public SadhakaRegistrationRequest(SadhakaDTO sadhakaDTO, String password) {
            this.sadhakaDTO = sadhakaDTO;
            this.password = password;
        }
        
        // Getters and setters
        public SadhakaDTO getSadhakaDTO() { return sadhakaDTO; }
        public void setSadhakaDTO(SadhakaDTO sadhakaDTO) { this.sadhakaDTO = sadhakaDTO; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * Inner class for mentor registration request
     */
    public static class MentorRegistrationRequest {
        private MentorDTO mentorDTO;
        private String password;
        
        // Constructors
        public MentorRegistrationRequest() {}
        
        public MentorRegistrationRequest(MentorDTO mentorDTO, String password) {
            this.mentorDTO = mentorDTO;
            this.password = password;
        }
        
        // Getters and setters
        public MentorDTO getMentorDTO() { return mentorDTO; }
        public void setMentorDTO(MentorDTO mentorDTO) { this.mentorDTO = mentorDTO; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * Inner class for admin registration request
     */
    public static class AdminRegistrationRequest {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        
        // Constructors
        public AdminRegistrationRequest() {}
        
        public AdminRegistrationRequest(String username, String password, String firstName, 
                                      String lastName, String email) {
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
    
    /**
     * Inner class for success responses
     */
    public static class SuccessResponse {
        private String message;
        private Object data;
        
        public SuccessResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }
        
        public String getMessage() { return message; }
        public Object getData() { return data; }
    }
    
    /**
     * Inner class for error responses
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
