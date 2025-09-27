package com.iskon.sadhana.tracker.sadhana_tracker.controller;

import com.iskon.sadhana.tracker.sadhana_tracker.service.SadhakaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for public endpoints
 * Handles health checks and public information
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PublicController {
    
    private final SadhakaService sadhakaService;
    
    /**
     * Health check endpoint
     * GET /api/public/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Sadhana Tracker API");
        health.put("version", "1.0.0");
        health.put("message", "Hare Krishna! Service is running successfully 🙏");
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * API information endpoint
     * GET /api/public/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("applicationName", "Sadhana Tracker");
        info.put("description", "A spiritual practice tracking application for Krishna devotees");
        info.put("version", "1.0.0");
        info.put("developer", "Krishna Das Prabhu");
        info.put("purpose", "To serve Krishna and His devotees by tracking spiritual progress");
        info.put("endpoints", getAvailableEndpoints());
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Get total statistics (public)
     * GET /api/public/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getPublicStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get total active sadhakas
            long totalSadhakas = sadhakaService.getTotalActiveSadhakas();
            stats.put("totalActiveSadhakas", totalSadhakas);
            stats.put("message", "Join our community of dedicated devotees!");
            stats.put("inspiration", "\"Chanting Hare Krishna is the highest perfection of life\" - Srila Prabhupada");
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error getting public statistics: {}", e.getMessage());
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("message", "Statistics temporarily unavailable");
            return ResponseEntity.ok(errorStats);
        }
    }
    
    /**
     * Get system status
     * GET /api/public/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // Check database connectivity by trying to get total sadhakas
            sadhakaService.getTotalActiveSadhakas();
            
            status.put("database", "Connected");
            status.put("api", "Running");
            status.put("authentication", "Active");
            status.put("overall", "Healthy");
            status.put("lastChecked", LocalDateTime.now());
            status.put("message", "All systems operational - Jai Shri Krishna! 🙏");
            
        } catch (Exception e) {
            status.put("database", "Error");
            status.put("api", "Running");
            status.put("authentication", "Unknown");
            status.put("overall", "Degraded");
            status.put("lastChecked", LocalDateTime.now());
            status.put("message", "Some services may be unavailable");
            status.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Welcome message for the API
     * GET /api/public/welcome
     */
    @GetMapping("/welcome")
    public ResponseEntity<?> welcome() {
        Map<String, Object> welcome = new HashMap<>();
        welcome.put("message", "🙏 Hare Krishna! Welcome to the Sadhana Tracker API 🙏");
        welcome.put("greeting", "May your spiritual journey be filled with Krishna's blessings");
        welcome.put("purpose", "This API helps devotees track their daily spiritual practices");
        welcome.put("features", new String[]{
            "Daily sadhana report submission",
            "Mentor guidance and feedback",
            "Spiritual progress analytics",
            "Secure JWT authentication",
            "Community of devoted practitioners"
        });
        welcome.put("contact", "For support, contact Krishna Das Prabhu");
        welcome.put("inspiration", "\"The ultimate goal of life is to go back to Godhead\" - Srila Prabhupada");
        
        return ResponseEntity.ok(welcome);
    }
    
    /**
     * API documentation endpoints
     * GET /api/public/endpoints
     */
    @GetMapping("/endpoints")
    public ResponseEntity<?> getEndpoints() {
        return ResponseEntity.ok(getAvailableEndpoints());
    }
    
    /**
     * Helper method to get available endpoints information
     */
    private Map<String, Object> getAvailableEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        // Authentication endpoints
        Map<String, String> auth = new HashMap<>();
        auth.put("POST /api/auth/login", "Login for sadhakas and mentors");
        auth.put("POST /api/auth/register", "Register new sadhaka");
        auth.put("POST /api/auth/refresh", "Refresh JWT token");
        auth.put("GET /api/auth/me", "Get current user profile");
        endpoints.put("authentication", auth);
        
        // Sadhaka endpoints
        Map<String, String> sadhaka = new HashMap<>();
        sadhaka.put("GET /api/sadhaka/profile", "Get sadhaka profile");
        sadhaka.put("PUT /api/sadhaka/profile", "Update sadhaka profile");
        sadhaka.put("POST /api/sadhaka/report", "Submit daily sadhana report");
        sadhaka.put("GET /api/sadhaka/reports", "Get all reports");
        sadhaka.put("GET /api/sadhaka/statistics", "Get sadhana statistics");
        endpoints.put("sadhaka", sadhaka);
        
        // Mentor endpoints
        Map<String, String> mentor = new HashMap<>();
        mentor.put("GET /api/mentor/sadhakas", "Get assigned sadhakas");
        mentor.put("GET /api/mentor/reports", "Get reports for review");
        mentor.put("POST /api/mentor/feedback", "Submit feedback");
        mentor.put("GET /api/mentor/dashboard", "Get dashboard statistics");
        endpoints.put("mentor", mentor);
        
        // Public endpoints
        Map<String, String> publicEndpoints = new HashMap<>();
        publicEndpoints.put("GET /api/public/health", "Health check");
        publicEndpoints.put("GET /api/public/info", "API information");
        publicEndpoints.put("GET /api/public/statistics", "Public statistics");
        publicEndpoints.put("GET /api/public/welcome", "Welcome message");
        endpoints.put("public", publicEndpoints);
        
        return endpoints;
    }
}
