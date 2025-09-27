package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for Mentor to submit feedback on a Sadhaka's report
 * This contains only the fields that mentor can modify
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorFeedbackDTO {
    
    @NotNull(message = "Report ID is required")
    private Long reportId;
    
    // Mentor feedback
    private String mentorFeedback;
    
    @Min(value = 1, message = "Rating must be between 1 and 10")
    @Max(value = 10, message = "Rating must be between 1 and 10")
    private Integer spiritualGrowthRating; // 1-10 scale
    
    private String strengthsObserved;
    private String areasForImprovement;
    private String specificGuidance;
    private String recommendedReading;
    
    // Optional: Quick suggestions
    private Boolean recommendIncreaseChanting;
    private Boolean recommendMoreReading;
    private Boolean recommendTempleVisit;
    private String priorityArea; // "Japa", "Reading", "Service", "Early Rising", etc.
}
