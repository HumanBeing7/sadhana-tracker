package com.iskon.sadhana.tracker.sadhana_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Separate entity for Mentor Feedback
 * This is completely independent from DailySadhanaReport
 * One-to-One relationship with DailySadhanaReport
 */
@Entity
@Table(name = "mentor_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Reference to the sadhana report
    @OneToOne
    @JoinColumn(name = "report_id", nullable = false)
    private DailySadhanaReport report;
    
    // Reference to the mentor who gave feedback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;
    
    // Feedback content
    @Column(name = "feedback_text", length = 2000)
    private String feedbackText;
    
    @Column(name = "spiritual_growth_rating")
    private Integer spiritualGrowthRating; // 1-10 scale
    
    @Column(name = "strengths_observed", length = 1000)
    private String strengthsObserved;
    
    @Column(name = "areas_for_improvement", length = 1000)
    private String areasForImprovement;
    
    @Column(name = "specific_guidance", length = 1000)
    private String specificGuidance;
    
    @Column(name = "recommended_reading", length = 500)
    private String recommendedReading;
    
    // Quick recommendations
    @Column(name = "recommend_increase_chanting")
    private Boolean recommendIncreaseChanting = false;
    
    @Column(name = "recommend_more_reading")
    private Boolean recommendMoreReading = false;
    
    @Column(name = "recommend_temple_visit")
    private Boolean recommendTempleVisit = false;
    
    @Column(name = "priority_area")
    private String priorityArea; // "Japa", "Reading", "Service", "Early Rising"
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
