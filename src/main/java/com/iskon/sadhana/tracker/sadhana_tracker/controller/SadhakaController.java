package com.iskon.sadhana.tracker.sadhana_tracker.controller;

import com.iskon.sadhana.tracker.sadhana_tracker.security.filter.JwtAuthenticationFilter;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.DailySadhanaReportDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.SadhakaDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.service.DailySadhanaReportService;
import com.iskon.sadhana.tracker.sadhana_tracker.service.SadhakaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Sadhaka operations
 * Handles sadhaka profile management and sadhana report operations
 */
@RestController
@RequestMapping("/api/sadhaka")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SadhakaController {
    
    private final SadhakaService sadhakaService;
    private final DailySadhanaReportService reportService;
    
    /**
     * Get current sadhaka profile
     * GET /api/sadhaka/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            Optional<SadhakaDTO> sadhaka = sadhakaService.getSadhakaProfile(sadhakaId);
            
            if (sadhaka.isPresent()) {
                return ResponseEntity.ok(sadhaka.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Profile not found", "Sadhaka profile not found"));
            }
            
        } catch (Exception e) {
            log.error("Error getting sadhaka profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Update sadhaka profile
     * PUT /api/sadhaka/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                         @Valid @RequestBody SadhakaDTO sadhakaDTO) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            SadhakaDTO updatedSadhaka = sadhakaService.updateProfile(sadhakaId, sadhakaDTO);
            
            return ResponseEntity.ok(new SuccessResponse("Profile updated successfully", updatedSadhaka));
            
        } catch (Exception e) {
            log.error("Error updating sadhaka profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Update failed", e.getMessage()));
        }
    }
    
    /**
     * Submit daily sadhana report
     * POST /api/sadhaka/report
     */
    @PostMapping("/sadhana")
    public ResponseEntity<?> submitReport(Authentication authentication,
                                        @Valid @RequestBody DailySadhanaReportDTO reportDTO) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            DailySadhanaReportDTO savedReport = reportService.submitReport(sadhakaId, reportDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Report submitted successfully", savedReport));
            
        } catch (Exception e) {
            log.error("Error submitting sadhana report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Report submission failed", e.getMessage()));
        }
    }
    
    /**
     * Update existing sadhana report
     * PUT /api/sadhaka/report/{reportId}
     */
    @PutMapping("/report/{reportId}")
    public ResponseEntity<?> updateReport(Authentication authentication,
                                        @PathVariable Long reportId,
                                        @Valid @RequestBody DailySadhanaReportDTO reportDTO) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            DailySadhanaReportDTO updatedReport = reportService.updateReport(reportId, sadhakaId, reportDTO);
            
            return ResponseEntity.ok(new SuccessResponse("Report updated successfully", updatedReport));
            
        } catch (Exception e) {
            log.error("Error updating sadhana report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Report update failed", e.getMessage()));
        }
    }
    
    /**
     * Get report for specific date
     * GET /api/sadhaka/report?date=2025-07-13
     */
    @GetMapping("/report")
    public ResponseEntity<?> getReportByDate(Authentication authentication,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            Optional<DailySadhanaReportDTO> report = reportService.getReportByDate(sadhakaId, date);
            
            if (report.isPresent()) {
                return ResponseEntity.ok(report.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Report not found", "No report found for date: " + date));
            }
            
        } catch (Exception e) {
            log.error("Error getting sadhana report by date: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get all reports for current sadhaka
     * GET /api/sadhaka/reports
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(Authentication authentication) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            List<DailySadhanaReportDTO> reports = reportService.getAllReportsForSadhaka(sadhakaId);
            
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            log.error("Error getting all sadhana reports: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get reports in date range
     * GET /api/sadhaka/reports/range?startDate=2025-07-01&endDate=2025-07-13
     */
    @GetMapping("/reports/range")
    public ResponseEntity<?> getReportsInRange(Authentication authentication,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            List<DailySadhanaReportDTO> reports = reportService.getReportsInDateRange(sadhakaId, startDate, endDate);
            
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            log.error("Error getting sadhana reports in range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get recent reports (last N days)
     * GET /api/sadhaka/reports/recent?days=7
     */
    @GetMapping("/reports/recent")
    public ResponseEntity<?> getRecentReports(Authentication authentication,
                                            @RequestParam(defaultValue = "7") int days) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            List<DailySadhanaReportDTO> reports = reportService.getRecentReports(sadhakaId, days);
            
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            log.error("Error getting recent sadhana reports: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Get sadhana statistics
     * GET /api/sadhaka/statistics?days=30
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getSadhanaStatistics(Authentication authentication,
                                                 @RequestParam(defaultValue = "30") int days) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            DailySadhanaReportService.SadhanaStatistics stats = reportService.getSadhanaStatistics(sadhakaId, days);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error getting sadhana statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Server error", e.getMessage()));
        }
    }
    
    /**
     * Delete a report
     * DELETE /api/sadhaka/report/{reportId}
     */
    @DeleteMapping("/report/{reportId}")
    public ResponseEntity<?> deleteReport(Authentication authentication,
                                        @PathVariable Long reportId) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            reportService.deleteReport(reportId, sadhakaId);
            
            return ResponseEntity.ok(new SuccessResponse("Report deleted successfully", null));
            
        } catch (Exception e) {
            log.error("Error deleting sadhana report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Delete failed", e.getMessage()));
        }
    }
    
    /**
     * Change password
     * POST /api/sadhaka/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                          @RequestBody ChangePasswordRequest request) {
        try {
            Long sadhakaId = getCurrentUserId(authentication);
            
            sadhakaService.changePassword(sadhakaId, request.getCurrentPassword(), request.getNewPassword());
            
            return ResponseEntity.ok(new SuccessResponse("Password changed successfully", null));
            
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Password change failed", e.getMessage()));
        }
    }
    
    /**
     * Helper method to get current user ID from Authentication
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        JwtAuthenticationFilter.UserPrincipal userPrincipal = 
            (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUserId();
    }
    
    /**
     * Inner class for change password request
     */
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        
        // Constructors
        public ChangePasswordRequest() {}
        
        public ChangePasswordRequest(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }
        
        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
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
