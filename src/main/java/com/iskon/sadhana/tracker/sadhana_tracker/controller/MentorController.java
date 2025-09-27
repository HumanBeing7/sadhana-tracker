package com.iskon.sadhana.tracker.sadhana_tracker.controller;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AssignmentResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.DailySadhanaReportDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorFeedbackDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorFeedbackResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.SadhakaDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.DailySadhanaReport;
import com.iskon.sadhana.tracker.sadhana_tracker.service.AuthenticationService;
import com.iskon.sadhana.tracker.sadhana_tracker.service.DailySadhanaReportService;
import com.iskon.sadhana.tracker.sadhana_tracker.service.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Mentor operations
 * Handles mentor guidance, feedback, and sadhaka management
 */
@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MentorController {
    
    private final MentorService mentorService;
    private final DailySadhanaReportService reportService;
    private final AuthenticationService authenticationService;
    
    /**
     * Get all sadhakas under current mentor
     * GET /api/mentor/sadhakas
     */
    @GetMapping("/sadhakas")
    public ResponseEntity<?> getMySadhakas(@RequestHeader("Authorization") String authHeader) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            List<SadhakaDTO> sadhakas = mentorService.getMySadhakas(mentorId);
            
            return ResponseEntity.ok(sadhakas);
            
        } catch (Exception e) {
            log.error("Error getting mentor's sadhakas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Assign a sadhaka to current mentor
     * POST /api/mentor/sadhakas/{sadhakaId}/assign
     */
    @PostMapping("/sadhakas/{sadhakaId}/assign")
    public ResponseEntity<?> assignSadhaka(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long sadhakaId) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            AssignmentResponseDTO response = mentorService.assignSadhaka(mentorId, sadhakaId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error assigning sadhaka: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Assignment failed", e.getMessage()));
        }
    }
    
    /**
     * Remove a sadhaka from current mentor
     * DELETE /api/mentor/sadhakas/{sadhakaId}
     */
    @DeleteMapping("/sadhakas/{sadhakaId}")
    public ResponseEntity<?> removeSadhaka(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long sadhakaId) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            mentorService.removeSadhaka(mentorId, sadhakaId);
            
            return ResponseEntity.ok(new SuccessResponse("Sadhaka removed successfully", null));
            
        } catch (Exception e) {
            log.error("Error removing sadhaka: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Removal failed", e.getMessage()));
        }
    }
    
    /**
     * Get reports for mentor review (from all assigned sadhakas)
     * GET /api/mentor/reports?startDate=2025-07-01&endDate=2025-07-13
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getReportsForReview(@RequestHeader("Authorization") String authHeader,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            // Default to last 7 days if dates not provided
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(7);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            List<DailySadhanaReportDTO> reports = reportService.getReportsForMentorReview(mentorId, startDate, endDate);
            
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            log.error("Error getting reports for mentor review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get reports that need feedback
     * GET /api/mentor/reports/pending?days=7
     */
    @GetMapping("/reports/pending")
    public ResponseEntity<?> getPendingReports(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "7") int days) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            List<DailySadhanaReport> pendingReports = mentorService.getReportsNeedingFeedback(mentorId, days);
            
            return ResponseEntity.ok(pendingReports);
            
        } catch (Exception e) {
            log.error("Error getting pending reports: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Submit feedback for a sadhaka's report
     * POST /api/mentor/feedback
     */
    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestHeader("Authorization") String authHeader,
                                          @Valid @RequestBody MentorFeedbackDTO feedbackDTO) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            mentorService.submitFeedback(mentorId, feedbackDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Feedback submitted successfully", null));
            
        } catch (Exception e) {
            log.error("Error submitting feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Feedback submission failed", e.getMessage()));
        }
    }
    
    /**
     * Update existing feedback
     * PUT /api/mentor/feedback/{feedbackId}
     */
    @PutMapping("/feedback/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable Long feedbackId,
                                          @Valid @RequestBody MentorFeedbackDTO feedbackDTO) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            mentorService.updateFeedback(mentorId, feedbackId, feedbackDTO);
            
            return ResponseEntity.ok(new SuccessResponse("Feedback updated successfully", null));
            
        } catch (Exception e) {
            log.error("Error updating feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Feedback update failed", e.getMessage()));
        }
    }
    
    /**
     * Get all feedback given by current mentor
     * GET /api/mentor/feedback
     */
    @GetMapping("/feedback")
    public ResponseEntity<?> getMyFeedback(@RequestHeader("Authorization") String authHeader) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            List<MentorFeedbackResponseDTO> feedbackList = mentorService.getMyFeedback(mentorId);
            
            return ResponseEntity.ok(feedbackList);
            
        } catch (Exception e) {
            log.error("Error getting mentor's feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get feedback for a specific sadhaka
     * GET /api/mentor/sadhakas/{sadhakaId}/feedback
     */
    @GetMapping("/sadhakas/{sadhakaId}/feedback")
    public ResponseEntity<?> getFeedbackForSadhaka(@RequestHeader("Authorization") String authHeader,
                                                  @PathVariable Long sadhakaId) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            List<MentorFeedbackResponseDTO> feedbackList = mentorService.getFeedbackForSadhaka(mentorId, sadhakaId);
            
            return ResponseEntity.ok(feedbackList);
            
        } catch (Exception e) {
            log.error("Error getting feedback for sadhaka: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get mentor dashboard statistics
     * GET /api/mentor/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(@RequestHeader("Authorization") String authHeader) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            MentorService.MentorDashboardStats stats = mentorService.getDashboardStats(mentorId);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error getting mentor dashboard stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get sadhana statistics for a specific sadhaka
     * GET /api/mentor/sadhakas/{sadhakaId}/statistics?days=30
     */
    @GetMapping("/sadhakas/{sadhakaId}/statistics")
    public ResponseEntity<?> getSadhakaStatistics(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable Long sadhakaId,
                                                 @RequestParam(defaultValue = "30") int days) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            // Verify mentor has access to this sadhaka
            List<SadhakaDTO> mySadhakas = mentorService.getMySadhakas(mentorId);
            boolean hasAccess = mySadhakas.stream().anyMatch(s -> s.getId().equals(sadhakaId));
            
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "You don't have access to this sadhaka's data"));
            }
            
            DailySadhanaReportService.SadhanaStatistics stats = reportService.getSadhanaStatistics(sadhakaId, days);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error getting sadhaka statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get reports for a specific sadhaka
     * GET /api/mentor/sadhakas/{sadhakaId}/reports?days=7
     */
    @GetMapping("/sadhakas/{sadhakaId}/reports")
    public ResponseEntity<?> getSadhakaReports(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable Long sadhakaId,
                                             @RequestParam(defaultValue = "7") int days) {
        try {
            Long mentorId = getCurrentUserId(authHeader);
            
            // Verify mentor has access to this sadhaka
            List<SadhakaDTO> mySadhakas = mentorService.getMySadhakas(mentorId);
            boolean hasAccess = mySadhakas.stream().anyMatch(s -> s.getId().equals(sadhakaId));
            
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "You don't have access to this sadhaka's reports"));
            }
            
            List<DailySadhanaReportDTO> reports = reportService.getRecentReports(sadhakaId, days);
            
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            log.error("Error getting sadhaka reports: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Helper method to get current user ID from token
     */
    private Long getCurrentUserId(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            throw new RuntimeException("Invalid authorization header");
        }
        
        AuthenticationService.UserDetails userDetails = authenticationService.validateToken(token);
        return userDetails.getUserId();
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
