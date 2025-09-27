package com.iskon.sadhana.tracker.sadhana_tracker.repository;

import com.iskon.sadhana.tracker.sadhana_tracker.entity.MentorFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MentorFeedback entity
 * Provides database operations for managing mentor feedback on sadhana reports
 */
@Repository
public interface MentorFeedbackRepository extends JpaRepository<MentorFeedback, Long> {
    
    /**
     * Find feedback for a specific report
     */
    Optional<MentorFeedback> findByReportId(Long reportId);
    
    /**
     * Find all feedback given by a specific mentor
     */
    List<MentorFeedback> findByMentorIdOrderByCreatedAtDesc(Long mentorId);
    
    /**
     * Find feedback for reports of a specific sadhaka
     */
    @Query("SELECT f FROM MentorFeedback f WHERE f.report.sadhaka.id = :sadhakaId " +
           "ORDER BY f.createdAt DESC")
    List<MentorFeedback> findFeedbackForSadhaka(@Param("sadhakaId") Long sadhakaId);
    
    /**
     * Find feedback by mentor for a specific sadhaka
     */
    @Query("SELECT f FROM MentorFeedback f WHERE f.mentor.id = :mentorId " +
           "AND f.report.sadhaka.id = :sadhakaId ORDER BY f.createdAt DESC")
    List<MentorFeedback> findFeedbackByMentorForSadhaka(@Param("mentorId") Long mentorId,
                                                       @Param("sadhakaId") Long sadhakaId);
    
    /**
     * Find recent feedback (last N days)
     */
    @Query("SELECT f FROM MentorFeedback f WHERE f.report.reportDate >= :fromDate " +
           "ORDER BY f.createdAt DESC")
    List<MentorFeedback> findRecentFeedback(@Param("fromDate") LocalDate fromDate);
    
    /**
     * Check if feedback exists for a report
     */
    boolean existsByReportId(Long reportId);
    
    /**
     * Count feedback given by a mentor
     */
    long countByMentorId(Long mentorId);
    
    /**
     * Find feedback with high ratings (spiritual growth rating >= threshold)
     */
    List<MentorFeedback> findBySpiritualGrowthRatingGreaterThanEqualOrderByCreatedAtDesc(
        Integer minRating);
    
    /**
     * Find feedback with specific priority areas
     */
    List<MentorFeedback> findByPriorityAreaIgnoreCase(String priorityArea);
    
    /**
     * Get average spiritual growth rating for a sadhaka
     */
    @Query("SELECT AVG(f.spiritualGrowthRating) FROM MentorFeedback f " +
           "WHERE f.report.sadhaka.id = :sadhakaId AND f.spiritualGrowthRating IS NOT NULL")
    Double getAverageSpiritualRating(@Param("sadhakaId") Long sadhakaId);
    
    /**
     * Find reports that still need feedback from a specific mentor
     */
    @Query("SELECT r FROM DailySadhanaReport r JOIN r.sadhaka s JOIN s.mentors m " +
           "WHERE m.id = :mentorId AND r.reportDate >= :fromDate " +
           "AND NOT EXISTS (SELECT f FROM MentorFeedback f WHERE f.report = r AND f.mentor = m)")
    List<Object> findReportsNeedingFeedback(@Param("mentorId") Long mentorId,
                                          @Param("fromDate") LocalDate fromDate);
}
