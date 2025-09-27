package com.iskon.sadhana.tracker.sadhana_tracker.controller;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AssignmentResponseDTO;
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

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Admin operations
 * Handles administrative tasks like user management and assignments
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final MentorService mentorService;
    private final SadhakaService sadhakaService;
    private final AuthenticationService authenticationService;
    
    /**
     * Assign a sadhaka to a mentor (Admin only)
     * POST /api/admin/assign-sadhaka
     */
    @PostMapping("/assign-sadhaka")
    public ResponseEntity<?> assignSadhakaToMentor(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody AssignmentRequest request) {
        try {
            // Verify admin role
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "Admin role required"));
            }
            
            AssignmentResponseDTO response = mentorService.assignSadhaka(request.getMentorId(), request.getSadhakaId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error assigning sadhaka to mentor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Assignment failed", e.getMessage()));
        }
    }
    
    /**
     * Remove sadhaka from mentor (Admin only)
     * DELETE /api/admin/remove-assignment
     */
    @DeleteMapping("/remove-assignment")
    public ResponseEntity<?> removeSadhakaFromMentor(@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody AssignmentRequest request) {
        try {
            // Verify admin role
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "Admin role required"));
            }
            
            mentorService.removeSadhaka(request.getMentorId(), request.getSadhakaId());
            
            return ResponseEntity.ok(new SuccessResponse("Sadhaka removed from mentor successfully", null));
            
        } catch (Exception e) {
            log.error("Error removing sadhaka from mentor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Removal failed", e.getMessage()));
        }
    }
    
    /**
     * Get all mentors (Admin only)
     * GET /api/admin/mentors
     */
    @GetMapping("/mentors")
    public ResponseEntity<?> getAllMentors(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "Admin role required"));
            }
            
            List<MentorDTO> mentors = mentorService.getAllMentors();
            
            return ResponseEntity.ok(mentors);
            
        } catch (Exception e) {
            log.error("Error getting all mentors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get all sadhakas (Admin only)
     * GET /api/admin/sadhakas
     */
    @GetMapping("/sadhakas")
    public ResponseEntity<?> getAllSadhakas(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "Admin role required"));
            }
            
            List<SadhakaDTO> sadhakas = sadhakaService.getAllSadhakas();
            
            return ResponseEntity.ok(sadhakas);
            
        } catch (Exception e) {
            log.error("Error getting all sadhakas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get unassigned sadhakas (Admin only)
     * GET /api/admin/sadhakas/unassigned
     */
    @GetMapping("/sadhakas/unassigned")
    public ResponseEntity<?> getUnassignedSadhakas(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "Admin role required"));
            }
            
            List<SadhakaDTO> unassignedSadhakas = sadhakaService.getUnassignedSadhakas();
            
            return ResponseEntity.ok(unassignedSadhakas);
            
        } catch (Exception e) {
            log.error("Error getting unassigned sadhakas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get mentor assignments overview (Admin only)
     * GET /api/admin/assignments
     */
    @GetMapping("/assignments")
    public ResponseEntity<?> getAssignmentsOverview(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "Admin role required"));
            }
            
            Map<String, Object> assignments = mentorService.getAssignmentOverviewDTO();
            
            return ResponseEntity.ok(assignments);
            
        } catch (Exception e) {
            log.error("Error getting assignments overview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Helper method to check if user is admin
     */
    private boolean isAdmin(String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) return false;
            
            AuthenticationService.UserDetails userDetails = authenticationService.validateToken(token);
            return "ADMIN".equals(userDetails.getRole());
            
        } catch (Exception e) {
            return false;
        }
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
     * Inner class for assignment requests
     */
    public static class AssignmentRequest {
        private Long mentorId;
        private Long sadhakaId;
        
        // Constructors
        public AssignmentRequest() {}
        
        public AssignmentRequest(Long mentorId, Long sadhakaId) {
            this.mentorId = mentorId;
            this.sadhakaId = sadhakaId;
        }
        
        // Getters and setters
        public Long getMentorId() { return mentorId; }
        public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
        
        public Long getSadhakaId() { return sadhakaId; }
        public void setSadhakaId(Long sadhakaId) { this.sadhakaId = sadhakaId; }
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
