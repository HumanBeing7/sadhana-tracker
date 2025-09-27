package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for Mentor to view Sadhaka's report with feedback capability
 * This includes all sadhana data PLUS mentor feedback fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorReviewDTO {
    
    private Long reportId;
    private Long sadhakaId;
    private String sadhakaName;
    private String sadhakaSpritualName;
    private LocalDate reportDate;
    
    // Sadhana data (read-only for mentor)
    private LocalTime wakeUpTime;
    private Integer chantingRounds;
    private LocalTime lastRoundTime;
    private Double restHours;
    private LocalTime sleepTime;
    private Integer readingMinutes;
    private String readingBook;
    private Integer hearingMinutes;
    private String hearingTopic;
    private Double studyHours;
    private String studySubject;
    private Boolean slokaRecitation;
    private Integer slokasCount;
    
    // Additional activities
    private Boolean templeVisit;
    private Boolean deityDarshan;
    private Boolean prasadamHonor;
    private Double serviceHours;
    private String serviceDescription;
    
    // Sadhaka's reflections (read-only for mentor)
    private String spiritualMood;
    private String challenges;
    private String gratitude;
    private String goalsForTomorrow;
    
    // Mentor feedback section (editable by mentor)
    private String mentorFeedback;
    private Boolean mentorReviewed;
    private LocalDateTime mentorReviewDate;
    
    // Quick assessment fields for mentor
    private Integer spiritualGrowthRating; // 1-10 scale
    private String strengthsObserved;
    private String areasForImprovement;
    private String specificGuidance;
    private String recommendedReading;
}
