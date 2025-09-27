package com.iskon.sadhana.tracker.sadhana_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "daily_sadhana_reports", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"sadhaka_id", "report_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySadhanaReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sadhaka_id", nullable = false)
    private Sadhaka sadhaka;
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    // Wake up time
    @Column(name = "wake_up_time")
    private LocalTime wakeUpTime;
    
    // Chanting details
    @Column(name = "chanting_rounds")
    private Integer chantingRounds;
    
    @Column(name = "last_round_time")
    private LocalTime lastRoundTime;
    
    // Rest/Sleep details
    @Column(name = "rest_hours")
    private Double restHours; // In hours (e.g., 2.5 for 2 hours 30 minutes)
    
    @Column(name = "sleep_time")
    private LocalTime sleepTime;
    
    // Reading details
    @Column(name = "reading_minutes")
    private Integer readingMinutes; // In minutes
    
    @Column(name = "reading_book")
    private String readingBook; // Which book they read
    
    // Hearing details
    @Column(name = "hearing_minutes")
    private Integer hearingMinutes; // In minutes
    
    @Column(name = "hearing_topic")
    private String hearingTopic; // What they heard (lecture topic, etc.)
    
    // Study details
    @Column(name = "study_hours")
    private Double studyHours; // Academic/spiritual study
    
    @Column(name = "study_subject")
    private String studySubject;
    
    // Sloka recitation
    @Column(name = "sloka_recitation")
    private Boolean slokaRecitation = false;
    
    @Column(name = "slokas_count")
    private Integer slokasCount; // Number of slokas recited
    
    // Additional spiritual activities
    @Column(name = "temple_visit")
    private Boolean templeVisit = false;
    
    @Column(name = "deity_darshan")
    private Boolean deityDarshan = false;
    
    @Column(name = "prasadam_honor")
    private Boolean prasadamHonor = false;
    
    @Column(name = "service_hours")
    private Double serviceHours; // Seva hours
    
    @Column(name = "service_description")
    private String serviceDescription;
    
    // Mood and notes
    @Column(name = "spiritual_mood", length = 1000)
    private String spiritualMood; // How they felt spiritually
    
    @Column(name = "challenges", length = 1000)
    private String challenges; // Any challenges faced
    
    @Column(name = "gratitude", length = 1000)
    private String gratitude; // What they're grateful for
    
    @Column(name = "goals_for_tomorrow", length = 1000)
    private String goalsForTomorrow;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper method to calculate total spiritual activities time
    public Integer getTotalSpiritualMinutes() {
        int total = 0;
        if (readingMinutes != null) total += readingMinutes;
        if (hearingMinutes != null) total += hearingMinutes;
        if (studyHours != null) total += (int)(studyHours * 60);
        if (serviceHours != null) total += (int)(serviceHours * 60);
        return total;
    }
    
    // Helper method to check if daily goals are met
    public boolean isDailyGoalsMet() {
        // Basic criteria: minimum 16 rounds, wake up before 4 AM, etc.
        return chantingRounds != null && chantingRounds >= 16 &&
               wakeUpTime != null && wakeUpTime.isBefore(LocalTime.of(4, 0));
    }
}
