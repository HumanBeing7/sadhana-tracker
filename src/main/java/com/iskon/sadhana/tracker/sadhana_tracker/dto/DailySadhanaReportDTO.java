package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySadhanaReportDTO {
    
    private Long id;
    private Long sadhakaId;
    private LocalDate reportDate;
    
    // Basic sadhana fields matching your sample
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
    
    // Reflections
    private String spiritualMood;
    private String challenges;
    private String gratitude;
    private String goalsForTomorrow;
}
