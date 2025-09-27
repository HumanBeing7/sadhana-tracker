package com.iskon.sadhana.tracker.sadhana_tracker.service;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.AssignmentResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorAssignmentDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorFeedbackDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.MentorFeedbackResponseDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.dto.SadhakaDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.DailySadhanaReport;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Mentor;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.MentorFeedback;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.DailySadhanaReportRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.MentorFeedbackRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.MentorRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.SadhakaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for Mentor operations
 * Contains business logic for mentor guidance and feedback
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MentorService {
    
    private final MentorRepository mentorRepository;
    private final SadhakaRepository sadhakaRepository;
    private final DailySadhanaReportRepository reportRepository;
    private final MentorFeedbackRepository feedbackRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Register a new mentor
     */
    public MentorDTO registerMentor(MentorDTO mentorDTO, String password) {
        log.info("Registering new mentor with username: {}", mentorDTO.getUsername());
        
        // Check if username already exists
        if (mentorRepository.findByUsername(mentorDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + mentorDTO.getUsername());
        }
        
        // Check if email already exists
        if (mentorRepository.findByEmail(mentorDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + mentorDTO.getEmail());
        }
        
        // Convert DTO to entity
        Mentor mentor = convertDTOToEntity(mentorDTO);
        mentor.setPassword(passwordEncoder.encode(password));
        mentor.setRole(Mentor.Role.MENTOR);
        mentor.setActive(true);
        
        // Save mentor
        Mentor savedMentor = mentorRepository.save(mentor);
        
        log.info("Successfully registered mentor with ID: {} and username: {}", 
                savedMentor.getId(), savedMentor.getUsername());
        
        return convertEntityToDTO(savedMentor);
    }
    
    /**
     * Get mentor profile
     */
    @Transactional(readOnly = true)
    public Optional<MentorDTO> getMentorProfile(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .map(this::convertEntityToDTO);
    }
    
    /**
     * Get all sadhakas under a mentor
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> getMySadhakas(Long mentorId) {
        return sadhakaRepository.findSadhakasByMentorId(mentorId)
                .stream()
                .map(this::convertSadhakaToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Assign a sadhaka to mentor and return assignment details
     */
    @Transactional
    public AssignmentResponseDTO assignSadhaka(Long mentorId, Long sadhakaId) {
        log.info("Assigning sadhaka ID: {} to mentor ID: {}", sadhakaId, mentorId);
        
        // Fetch mentor and sadhaka entities to validate and get details
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found with ID: " + mentorId));
        
        Sadhaka sadhaka = sadhakaRepository.findById(sadhakaId)
                .orElseThrow(() -> new RuntimeException("Sadhaka not found with ID: " + sadhakaId));
        
        // Check if already assigned using direct query
        int existingAssignment = mentorRepository.checkAssignmentExists(sadhakaId, mentorId);
        if (existingAssignment > 0) {
            throw new RuntimeException("Sadhaka is already assigned to this mentor");
        }
        
        // Insert assignment directly into join table to avoid circular reference issues
        mentorRepository.assignSadhakaToMentor(sadhakaId, mentorId);
        
        log.info("Successfully assigned sadhaka: {} to mentor: {}", sadhaka.getUsername(), mentor.getUsername());
        
        // Return assignment details without circular references
        return new AssignmentResponseDTO(
            "Sadhaka successfully assigned to mentor",
            mentor.getId(),
            mentor.getFirstName() + " " + mentor.getLastName(),
            sadhaka.getId(),
            sadhaka.getFirstName() + " " + sadhaka.getLastName()
        );
    }
    
    /**
     * Remove sadhaka from mentor
     */
    @Transactional
    public void removeSadhaka(Long mentorId, Long sadhakaId) {
        log.info("Removing sadhaka ID: {} from mentor ID: {}", sadhakaId, mentorId);
        
        // Validate that both mentor and sadhaka exist
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found with ID: " + mentorId));
        
        Sadhaka sadhaka = sadhakaRepository.findById(sadhakaId)
                .orElseThrow(() -> new RuntimeException("Sadhaka not found with ID: " + sadhakaId));
        
        // Check if assignment exists
        int existingAssignment = mentorRepository.checkAssignmentExists(sadhakaId, mentorId);
        if (existingAssignment == 0) {
            throw new RuntimeException("Sadhaka is not assigned to this mentor");
        }
        
        // Remove assignment directly from join table to avoid circular reference issues
        mentorRepository.removeSadhakaFromMentor(sadhakaId, mentorId);
        
        log.info("Successfully removed sadhaka: {} from mentor: {}", sadhaka.getUsername(), mentor.getUsername());
    }
    
    /**
     * Submit feedback for a sadhaka's report
     */
    @Transactional
    public void submitFeedback(Long mentorId, MentorFeedbackDTO feedbackDTO) {
        log.info("Submitting feedback for report ID: {} by mentor ID: {}", feedbackDTO.getReportId(), mentorId);
        
        // Verify mentor exists
        if (!mentorRepository.existsById(mentorId)) {
            throw new RuntimeException("Mentor not found with ID: " + mentorId);
        }
        
        // Verify report exists
        if (!reportRepository.existsById(feedbackDTO.getReportId())) {
            throw new RuntimeException("Report not found with ID: " + feedbackDTO.getReportId());
        }
        
        // Get sadhaka ID from report to avoid lazy loading issues
        Long sadhakaId = reportRepository.getSadhakaIdByReportId(feedbackDTO.getReportId());
        if (sadhakaId == null) {
            throw new RuntimeException("Unable to find sadhaka for report ID: " + feedbackDTO.getReportId());
        }
        
        // Verify mentor has access to this sadhaka using direct query to avoid circular reference
        int assignmentExists = mentorRepository.checkAssignmentExists(sadhakaId, mentorId);
        if (assignmentExists == 0) {
            throw new RuntimeException("Mentor does not have access to this sadhaka's reports");
        }
        
        // Check if feedback already exists
        if (feedbackRepository.existsByReportId(feedbackDTO.getReportId())) {
            throw new RuntimeException("Feedback already exists for this report");
        }
        
        // Create feedback using IDs to avoid loading full entities
        MentorFeedback feedback = new MentorFeedback();
        
        // Create minimal entities with just IDs to avoid lazy loading issues
        DailySadhanaReport reportRef = new DailySadhanaReport();
        reportRef.setId(feedbackDTO.getReportId());
        
        Mentor mentorRef = new Mentor();
        mentorRef.setId(mentorId);
        
        feedback.setReport(reportRef);
        feedback.setMentor(mentorRef);
        feedback.setFeedbackText(feedbackDTO.getMentorFeedback());
        feedback.setSpiritualGrowthRating(feedbackDTO.getSpiritualGrowthRating());
        feedback.setStrengthsObserved(feedbackDTO.getStrengthsObserved());
        feedback.setAreasForImprovement(feedbackDTO.getAreasForImprovement());
        feedback.setSpecificGuidance(feedbackDTO.getSpecificGuidance());
        feedback.setRecommendedReading(feedbackDTO.getRecommendedReading());
        feedback.setRecommendIncreaseChanting(feedbackDTO.getRecommendIncreaseChanting());
        feedback.setRecommendMoreReading(feedbackDTO.getRecommendMoreReading());
        feedback.setRecommendTempleVisit(feedbackDTO.getRecommendTempleVisit());
        feedback.setPriorityArea(feedbackDTO.getPriorityArea());
        
        feedbackRepository.save(feedback);
        log.info("Successfully submitted feedback for report ID: {}", feedbackDTO.getReportId());
    }
    
    /**
     * Update existing feedback
     */
    @Transactional
    public void updateFeedback(Long mentorId, Long feedbackId, MentorFeedbackDTO feedbackDTO) {
        log.info("Updating feedback ID: {} by mentor ID: {}", feedbackId, mentorId);
        
        MentorFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId));
        
        // Verify the feedback belongs to this mentor
        if (!feedback.getMentor().getId().equals(mentorId)) {
            throw new RuntimeException("Feedback does not belong to this mentor");
        }
        
        // Update feedback
        feedback.setFeedbackText(feedbackDTO.getMentorFeedback());
        feedback.setSpiritualGrowthRating(feedbackDTO.getSpiritualGrowthRating());
        feedback.setStrengthsObserved(feedbackDTO.getStrengthsObserved());
        feedback.setAreasForImprovement(feedbackDTO.getAreasForImprovement());
        feedback.setSpecificGuidance(feedbackDTO.getSpecificGuidance());
        feedback.setRecommendedReading(feedbackDTO.getRecommendedReading());
        feedback.setRecommendIncreaseChanting(feedbackDTO.getRecommendIncreaseChanting());
        feedback.setRecommendMoreReading(feedbackDTO.getRecommendMoreReading());
        feedback.setRecommendTempleVisit(feedbackDTO.getRecommendTempleVisit());
        feedback.setPriorityArea(feedbackDTO.getPriorityArea());
        
        feedbackRepository.save(feedback);
        log.info("Successfully updated feedback ID: {}", feedbackId);
    }
    
    /**
     * Get all feedback given by a mentor
     */
    @Transactional(readOnly = true)
    public List<MentorFeedbackResponseDTO> getMyFeedback(Long mentorId) {
        List<MentorFeedback> feedbacks = feedbackRepository.findByMentorIdOrderByCreatedAtDesc(mentorId);
        
        return feedbacks.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get feedback for a specific sadhaka
     */
    @Transactional(readOnly = true)
    public List<MentorFeedbackResponseDTO> getFeedbackForSadhaka(Long mentorId, Long sadhakaId) {
        // Verify mentor has access to this sadhaka using direct query to avoid lazy loading
        int assignmentExists = mentorRepository.checkAssignmentExists(sadhakaId, mentorId);
        if (assignmentExists == 0) {
            throw new RuntimeException("Mentor does not have access to this sadhaka");
        }
        
        List<MentorFeedback> feedbacks = feedbackRepository.findFeedbackByMentorForSadhaka(mentorId, sadhakaId);
        
        return feedbacks.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reports that need feedback from mentor
     */
    @Transactional(readOnly = true)
    public List<DailySadhanaReport> getReportsNeedingFeedback(Long mentorId, int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return reportRepository.findReportsForMentorReview(mentorId, fromDate, LocalDate.now());
    }
    
    /**
     * Get mentor dashboard statistics
     */
    @Transactional(readOnly = true)
    public MentorDashboardStats getDashboardStats(Long mentorId) {
        long totalSadhakas = sadhakaRepository.findSadhakasByMentorId(mentorId).size();
        long totalFeedbackGiven = feedbackRepository.countByMentorId(mentorId);
        
        // Get reports from last 7 days that need feedback
        LocalDate lastWeek = LocalDate.now().minusDays(7);
        List<DailySadhanaReport> recentReports = reportRepository.findReportsForMentorReview(mentorId, lastWeek, LocalDate.now());
        long pendingFeedback = recentReports.size();
        
        return new MentorDashboardStats(totalSadhakas, totalFeedbackGiven, pendingFeedback);
    }
    
    /**
     * Get all mentors (Admin only)
     */
    @Transactional(readOnly = true)
    public List<MentorDTO> getAllMentors() {
        return mentorRepository.findAll()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get assignments overview for admin
     */
    @Transactional(readOnly = true)
    public List<MentorAssignmentOverview> getAssignmentsOverview() {
        List<Mentor> mentors = mentorRepository.findAll();
        return mentors.stream()
                .map(mentor -> {
                    List<String> sadhakaNames = mentor.getSadhakas().stream()
                            .map(sadhaka -> sadhaka.getFirstName() + " " + sadhaka.getLastName())
                            .collect(Collectors.toList());
                    
                    return new MentorAssignmentOverview(
                            mentor.getId(),
                            mentor.getFirstName() + " " + mentor.getLastName(),
                            mentor.getEmail(),
                            mentor.getSadhakas().size(),
                            sadhakaNames
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Inner class for mentor assignment overview
     */
    public static class MentorAssignmentOverview {
        private Long mentorId;
        private String mentorName;
        private String mentorEmail;
        private int assignedSadhakas;
        private List<String> sadhakaNames;
        
        // Constructors
        public MentorAssignmentOverview() {}
        
        public MentorAssignmentOverview(Long mentorId, String mentorName, String mentorEmail, 
                                      int assignedSadhakas, List<String> sadhakaNames) {
            this.mentorId = mentorId;
            this.mentorName = mentorName;
            this.mentorEmail = mentorEmail;
            this.assignedSadhakas = assignedSadhakas;
            this.sadhakaNames = sadhakaNames;
        }
        
        // Getters and setters
        public Long getMentorId() { return mentorId; }
        public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
        
        public String getMentorName() { return mentorName; }
        public void setMentorName(String mentorName) { this.mentorName = mentorName; }
        
        public String getMentorEmail() { return mentorEmail; }
        public void setMentorEmail(String mentorEmail) { this.mentorEmail = mentorEmail; }
        
        public int getAssignedSadhakas() { return assignedSadhakas; }
        public void setAssignedSadhakas(int assignedSadhakas) { this.assignedSadhakas = assignedSadhakas; }
        
        public List<String> getSadhakaNames() { return sadhakaNames; }
        public void setSadhakaNames(List<String> sadhakaNames) { this.sadhakaNames = sadhakaNames; }
    }

    /**
     * Convert Sadhaka entity to DTO
     */
    private SadhakaDTO convertSadhakaToDTO(Sadhaka sadhaka) {
        SadhakaDTO dto = new SadhakaDTO();
        dto.setId(sadhaka.getId());
        dto.setUsername(sadhaka.getUsername());
        dto.setFirstName(sadhaka.getFirstName());
        dto.setLastName(sadhaka.getLastName());
        dto.setEmail(sadhaka.getEmail());
        dto.setPhoneNumber(sadhaka.getPhoneNumber());
        dto.setSpiritualName(sadhaka.getSpiritualName());
        dto.setTemple(sadhaka.getTemple());
        dto.setInitiationLevel(sadhaka.getInitiationLevel());
        dto.setLocation(sadhaka.getLocation());
        dto.setRole(sadhaka.getRole().toString());
        dto.setActive(sadhaka.getActive());
        return dto;
    }
    
    /**
     * Convert MentorDTO to Mentor entity
     */
    private Mentor convertDTOToEntity(MentorDTO mentorDTO) {
        Mentor mentor = new Mentor();
        mentor.setId(mentorDTO.getId());
        mentor.setUsername(mentorDTO.getUsername());
        mentor.setFirstName(mentorDTO.getFirstName());
        mentor.setLastName(mentorDTO.getLastName());
        mentor.setEmail(mentorDTO.getEmail());
        mentor.setPhoneNumber(mentorDTO.getPhoneNumber());
        mentor.setSpiritualName(mentorDTO.getSpiritualName());
        mentor.setTemple(mentorDTO.getTemple());
        mentor.setInitiationLevel(mentorDTO.getInitiationLevel());
        mentor.setDesignation(mentorDTO.getDesignation());
        mentor.setYearsOfExperience(mentorDTO.getYearsOfExperience());
        mentor.setSpecialization(mentorDTO.getSpecialization());
        mentor.setActive(mentorDTO.getActive());
        return mentor;
    }
    
    /**
     * Convert Mentor entity to DTO
     */
    private MentorDTO convertEntityToDTO(Mentor mentor) {
        MentorDTO dto = new MentorDTO();
        dto.setId(mentor.getId());
        dto.setUsername(mentor.getUsername());
        dto.setFirstName(mentor.getFirstName());
        dto.setLastName(mentor.getLastName());
        dto.setEmail(mentor.getEmail());
        dto.setPhoneNumber(mentor.getPhoneNumber());
        dto.setSpiritualName(mentor.getSpiritualName());
        dto.setTemple(mentor.getTemple());
        dto.setInitiationLevel(mentor.getInitiationLevel());
        dto.setDesignation(mentor.getDesignation());
        dto.setYearsOfExperience(mentor.getYearsOfExperience());
        dto.setSpecialization(mentor.getSpecialization());
        dto.setRole(mentor.getRole());
        dto.setActive(mentor.getActive());
        dto.setCreatedAt(mentor.getCreatedAt());
        dto.setUpdatedAt(mentor.getUpdatedAt());
        return dto;
    }
    
    /**
     * Get assignment overview using DTOs to avoid circular references
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAssignmentOverviewDTO() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // Get all mentors using DTOs
            List<MentorAssignmentDTO> mentorsWithAssignments = mentorRepository.findAll().stream()
                    .map(mentor -> {
                        MentorAssignmentDTO dto = new MentorAssignmentDTO(
                            mentor.getId(),
                            mentor.getUsername(),
                            mentor.getEmail(),
                            mentor.getFirstName() + " " + mentor.getLastName(),
                            mentor.getPhoneNumber(),
                            "N/A", // Address not available in Mentor entity
                            mentor.getCreatedAt()
                        );
                        
                        // Get assigned sadhakas using direct query to avoid lazy loading
                        List<MentorAssignmentDTO.BasicSadhakaDTO> assignedSadhakas = sadhakaRepository.findSadhakasByMentorId(mentor.getId()).stream()
                                .map(sadhaka -> new MentorAssignmentDTO.BasicSadhakaDTO(
                                    sadhaka.getId(),
                                    sadhaka.getUsername(),
                                    sadhaka.getFirstName() + " " + sadhaka.getLastName(),
                                    sadhaka.getEmail()
                                ))
                                .collect(Collectors.toList());
                        
                        dto.setAssignedSadhakas(assignedSadhakas);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            overview.put("mentors", mentorsWithAssignments);
            overview.put("totalMentors", mentorsWithAssignments.size());
            
            long totalAssignments = mentorsWithAssignments.stream()
                    .mapToLong(m -> m.getAssignedSadhakas().size())
                    .sum();
            overview.put("totalAssignments", totalAssignments);
            
            // Add additional statistics
            long totalSadhakas = sadhakaRepository.countByActiveTrue();
            long unassignedSadhakas = sadhakaRepository.findSadhakasByMentorsIsEmpty().size();
            
            overview.put("totalSadhakas", totalSadhakas);
            overview.put("unassignedSadhakas", unassignedSadhakas);
            
        } catch (Exception e) {
            log.error("Error creating assignment overview: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate assignment overview: " + e.getMessage());
        }
        
        return overview;
    }

    /**
     * Inner class for mentor dashboard statistics
     */
    public static class MentorDashboardStats {
        private final long totalSadhakas;
        private final long totalFeedbackGiven;
        private final long pendingFeedback;
        
        public MentorDashboardStats(long totalSadhakas, long totalFeedbackGiven, long pendingFeedback) {
            this.totalSadhakas = totalSadhakas;
            this.totalFeedbackGiven = totalFeedbackGiven;
            this.pendingFeedback = pendingFeedback;
        }
        
        public long getTotalSadhakas() { return totalSadhakas; }
        public long getTotalFeedbackGiven() { return totalFeedbackGiven; }
        public long getPendingFeedback() { return pendingFeedback; }
    }

    /**
     * Helper method to convert MentorFeedback entity to DTO
     */
    private MentorFeedbackResponseDTO convertToResponseDTO(MentorFeedback feedback) {
        MentorFeedbackResponseDTO dto = new MentorFeedbackResponseDTO();
        
        dto.setId(feedback.getId());
        dto.setReportId(feedback.getReport().getId());
        dto.setMentorId(feedback.getMentor().getId());
        dto.setMentorName(feedback.getMentor().getFirstName() + " " + feedback.getMentor().getLastName());
        
        // Get sadhaka info via report to avoid circular references
        if (feedback.getReport().getSadhaka() != null) {
            dto.setSadhakaId(feedback.getReport().getSadhaka().getId());
            dto.setSadhakaName(feedback.getReport().getSadhaka().getFirstName() + " " + feedback.getReport().getSadhaka().getLastName());
        }
        
        if (feedback.getReport().getReportDate() != null) {
            dto.setReportDate(feedback.getReport().getReportDate().atStartOfDay());
        }
        
        dto.setFeedbackText(feedback.getFeedbackText());
        dto.setSpiritualGrowthRating(feedback.getSpiritualGrowthRating());
        dto.setStrengthsObserved(feedback.getStrengthsObserved());
        dto.setAreasForImprovement(feedback.getAreasForImprovement());
        dto.setSpecificGuidance(feedback.getSpecificGuidance());
        dto.setRecommendedReading(feedback.getRecommendedReading());
        dto.setRecommendIncreaseChanting(feedback.getRecommendIncreaseChanting());
        dto.setRecommendMoreReading(feedback.getRecommendMoreReading());
        dto.setRecommendTempleVisit(feedback.getRecommendTempleVisit());
        dto.setPriorityArea(feedback.getPriorityArea());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setUpdatedAt(feedback.getUpdatedAt());
        
        return dto;
    }
}
