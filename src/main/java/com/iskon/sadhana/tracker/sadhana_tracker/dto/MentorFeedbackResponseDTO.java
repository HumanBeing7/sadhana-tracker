package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning mentor feedback data safely without circular references
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorFeedbackResponseDTO {
    
    private Long id;
    private Long reportId;
    private Long mentorId;
    private String mentorName;
    private Long sadhakaId;
    private String sadhakaName;
    private LocalDateTime reportDate;
    
    // Feedback content
    private String feedbackText;
    private Integer spiritualGrowthRating;
    private String strengthsObserved;
    private String areasForImprovement;
    private String specificGuidance;
    private String recommendedReading;
    
    // Recommendations
    private Boolean recommendIncreaseChanting;
    private Boolean recommendMoreReading;
    private Boolean recommendTempleVisit;
    private String priorityArea;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
