package com.iskon.sadhana.tracker.sadhana_tracker.repository;

import com.iskon.sadhana.tracker.sadhana_tracker.entity.DailySadhanaReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DailySadhanaReport entity
 * Provides database operations for managing daily spiritual practice reports
 */
@Repository
public interface DailySadhanaReportRepository extends JpaRepository<DailySadhanaReport, Long> {
    
    /**
     * Find report by sadhaka and specific date
     */
    Optional<DailySadhanaReport> findBySadhakaIdAndReportDate(Long sadhakaId, LocalDate reportDate);
    
    /**
     * Find all reports for a sadhaka
     */
    List<DailySadhanaReport> findBySadhakaIdOrderByReportDateDesc(Long sadhakaId);
    
    /**
     * Find reports for a sadhaka in date range
     */
    List<DailySadhanaReport> findBySadhakaIdAndReportDateBetweenOrderByReportDateDesc(
        Long sadhakaId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find recent reports for a sadhaka (last N days)
     */
    @Query("SELECT r FROM DailySadhanaReport r WHERE r.sadhaka.id = :sadhakaId " +
           "AND r.reportDate >= :fromDate ORDER BY r.reportDate DESC")
    List<DailySadhanaReport> findRecentReports(@Param("sadhakaId") Long sadhakaId, 
                                              @Param("fromDate") LocalDate fromDate);
    
    /**
     * Find all reports for a specific date (for mentor review)
     */
    List<DailySadhanaReport> findByReportDate(LocalDate reportDate);
    
    /**
     * Find reports that need mentor review (sadhakas under specific mentor)
     */
    @Query("SELECT r FROM DailySadhanaReport r JOIN r.sadhaka s JOIN s.mentors m " +
           "WHERE m.id = :mentorId AND r.reportDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.reportDate DESC")
    List<DailySadhanaReport> findReportsForMentorReview(@Param("mentorId") Long mentorId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    
    /**
     * Check if report exists for sadhaka on specific date
     */
    boolean existsBySadhakaIdAndReportDate(Long sadhakaId, LocalDate reportDate);
    
    /**
     * Count reports by sadhaka
     */
    long countBySadhakaId(Long sadhakaId);
    
    /**
     * Find sadhakas with consistent chanting (16+ rounds for last N days)
     */
    @Query("SELECT DISTINCT r.sadhaka FROM DailySadhanaReport r " +
           "WHERE r.reportDate >= :fromDate AND r.chantingRounds >= 16")
    List<Long> findConsistentChanters(@Param("fromDate") LocalDate fromDate);
    
    /**
     * Find average chanting rounds for a sadhaka in date range
     */
    @Query("SELECT AVG(r.chantingRounds) FROM DailySadhanaReport r " +
           "WHERE r.sadhaka.id = :sadhakaId AND r.reportDate BETWEEN :startDate AND :endDate")
    Double findAverageChantingRounds(@Param("sadhakaId") Long sadhakaId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Find reports with early wake up times (before 4 AM)
     */
    @Query("SELECT r FROM DailySadhanaReport r WHERE HOUR(r.wakeUpTime) < 4 " +
           "AND r.reportDate >= :fromDate ORDER BY r.reportDate DESC")
    List<DailySadhanaReport> findEarlyRisers(@Param("fromDate") LocalDate fromDate);
    
    /**
     * Get sadhana statistics for a sadhaka
     */
    @Query("SELECT " +
           "COUNT(r) as totalReports, " +
           "COALESCE(AVG(r.chantingRounds), 0) as avgRounds, " +
           "COALESCE(AVG(r.readingMinutes), 0) as avgReading, " +
           "COALESCE(AVG(r.hearingMinutes), 0) as avgHearing " +
           "FROM DailySadhanaReport r " +
           "WHERE r.sadhaka.id = :sadhakaId AND r.reportDate >= :fromDate")
    List<Object[]> getSadhanaStatistics(@Param("sadhakaId") Long sadhakaId, 
                                       @Param("fromDate") LocalDate fromDate);
    
    /**
     * Get sadhaka ID from report ID to avoid lazy loading issues
     */
    @Query("SELECT r.sadhaka.id FROM DailySadhanaReport r WHERE r.id = :reportId")
    Long getSadhakaIdByReportId(@Param("reportId") Long reportId);
}
