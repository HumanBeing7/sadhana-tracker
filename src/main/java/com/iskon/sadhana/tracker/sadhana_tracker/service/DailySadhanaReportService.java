package com.iskon.sadhana.tracker.sadhana_tracker.service;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.DailySadhanaReportDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.DailySadhanaReport;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.DailySadhanaReportRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.SadhakaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Daily Sadhana Reports
 * Contains business logic for sadhana tracking and analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DailySadhanaReportService {
    
    private final DailySadhanaReportRepository reportRepository;
    private final SadhakaRepository sadhakaRepository;
    
    /**
     * Submit a daily sadhana report
     */
    public DailySadhanaReportDTO submitReport(Long sadhakaId, DailySadhanaReportDTO reportDTO) {
        log.info("Submitting sadhana report for sadhaka ID: {} on date: {}", sadhakaId, reportDTO.getReportDate());
        
        // Verify sadhaka exists
        Sadhaka sadhaka = sadhakaRepository.findById(sadhakaId)
                .orElseThrow(() -> new RuntimeException("Sadhaka not found with ID: " + sadhakaId));
        
        // Check if report already exists for this date
        if (reportRepository.existsBySadhakaIdAndReportDate(sadhakaId, reportDTO.getReportDate())) {
            throw new RuntimeException("Report already exists for date: " + reportDTO.getReportDate());
        }
        
        // Create new report
        DailySadhanaReport report = convertToEntity(reportDTO, sadhaka);
        DailySadhanaReport savedReport = reportRepository.save(report);
        
        log.info("Successfully submitted report ID: {} for sadhaka: {}", savedReport.getId(), sadhaka.getUsername());
        return convertToDTO(savedReport);
    }
    
    /**
     * Update an existing daily sadhana report
     */
    public DailySadhanaReportDTO updateReport(Long reportId, Long sadhakaId, DailySadhanaReportDTO reportDTO) {
        log.info("Updating sadhana report ID: {} for sadhaka ID: {}", reportId, sadhakaId);
        
        DailySadhanaReport existingReport = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
        
        // Verify the report belongs to the sadhaka
        if (!existingReport.getSadhaka().getId().equals(sadhakaId)) {
            throw new RuntimeException("Report does not belong to this sadhaka");
        }
        
        // Update report fields
        updateReportFields(existingReport, reportDTO);
        DailySadhanaReport updatedReport = reportRepository.save(existingReport);
        
        log.info("Successfully updated report ID: {}", updatedReport.getId());
        return convertToDTO(updatedReport);
    }
    
    /**
     * Get report by sadhaka and date
     */
    @Transactional(readOnly = true)
    public Optional<DailySadhanaReportDTO> getReportByDate(Long sadhakaId, LocalDate date) {
        return reportRepository.findBySadhakaIdAndReportDate(sadhakaId, date)
                .map(this::convertToDTO);
    }
    
    /**
     * Get all reports for a sadhaka
     */
    @Transactional(readOnly = true)
    public List<DailySadhanaReportDTO> getAllReportsForSadhaka(Long sadhakaId) {
        return reportRepository.findBySadhakaIdOrderByReportDateDesc(sadhakaId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reports for a sadhaka in date range
     */
    @Transactional(readOnly = true)
    public List<DailySadhanaReportDTO> getReportsInDateRange(Long sadhakaId, LocalDate startDate, LocalDate endDate) {
        return reportRepository.findBySadhakaIdAndReportDateBetweenOrderByReportDateDesc(sadhakaId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent reports (last N days)
     */
    @Transactional(readOnly = true)
    public List<DailySadhanaReportDTO> getRecentReports(Long sadhakaId, int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return reportRepository.findRecentReports(sadhakaId, fromDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reports for mentor review
     */
    @Transactional(readOnly = true)
    public List<DailySadhanaReportDTO> getReportsForMentorReview(Long mentorId, LocalDate startDate, LocalDate endDate) {
        return reportRepository.findReportsForMentorReview(mentorId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if report exists for date
     */
    @Transactional(readOnly = true)
    public boolean hasReportForDate(Long sadhakaId, LocalDate date) {
        return reportRepository.existsBySadhakaIdAndReportDate(sadhakaId, date);
    }
    
    /**
     * Get sadhana statistics for a sadhaka
     */
    @Transactional(readOnly = true)
    public SadhanaStatistics getSadhanaStatistics(Long sadhakaId, int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        List<Object[]> statsResult = reportRepository.getSadhanaStatistics(sadhakaId, fromDate);
        
        SadhanaStatistics statistics = new SadhanaStatistics();
        if (statsResult != null && !statsResult.isEmpty() && statsResult.get(0) != null) {
            Object[] stats = statsResult.get(0);
            // The query returns: [count, avgRounds, avgReading, avgHearing]
            statistics.setTotalReports(stats[0] != null ? ((Number) stats[0]).longValue() : 0);
            statistics.setAverageRounds(stats[1] != null ? ((Number) stats[1]).doubleValue() : 0.0);
            statistics.setAverageReadingMinutes(stats[2] != null ? ((Number) stats[2]).doubleValue() : 0.0);
            statistics.setAverageHearingMinutes(stats[3] != null ? ((Number) stats[3]).doubleValue() : 0.0);
        } else {
            // If no data found, set default values
            statistics.setTotalReports(0L);
            statistics.setAverageRounds(0.0);
            statistics.setAverageReadingMinutes(0.0);
            statistics.setAverageHearingMinutes(0.0);
        }
        
        return statistics;
    }
    
    /**
     * Get average chanting rounds for a sadhaka
     */
    @Transactional(readOnly = true)
    public Double getAverageChantingRounds(Long sadhakaId, LocalDate startDate, LocalDate endDate) {
        return reportRepository.findAverageChantingRounds(sadhakaId, startDate, endDate);
    }
    
    /**
     * Delete a report
     */
    public void deleteReport(Long reportId, Long sadhakaId) {
        log.info("Deleting report ID: {} for sadhaka ID: {}", reportId, sadhakaId);
        
        DailySadhanaReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
        
        // Verify the report belongs to the sadhaka
        if (!report.getSadhaka().getId().equals(sadhakaId)) {
            throw new RuntimeException("Report does not belong to this sadhaka");
        }
        
        reportRepository.delete(report);
        log.info("Successfully deleted report ID: {}", reportId);
    }
    
    /**
     * Convert DTO to Entity
     */
    private DailySadhanaReport convertToEntity(DailySadhanaReportDTO dto, Sadhaka sadhaka) {
        DailySadhanaReport report = new DailySadhanaReport();
        report.setSadhaka(sadhaka);
        report.setReportDate(dto.getReportDate());
        updateReportFields(report, dto);
        return report;
    }
    
    /**
     * Update report fields from DTO
     */
    private void updateReportFields(DailySadhanaReport report, DailySadhanaReportDTO dto) {
        report.setWakeUpTime(dto.getWakeUpTime());
        report.setChantingRounds(dto.getChantingRounds());
        report.setLastRoundTime(dto.getLastRoundTime());
        report.setRestHours(dto.getRestHours());
        report.setSleepTime(dto.getSleepTime());
        report.setReadingMinutes(dto.getReadingMinutes());
        report.setReadingBook(dto.getReadingBook());
        report.setHearingMinutes(dto.getHearingMinutes());
        report.setHearingTopic(dto.getHearingTopic());
        report.setStudyHours(dto.getStudyHours());
        report.setStudySubject(dto.getStudySubject());
        report.setSlokaRecitation(dto.getSlokaRecitation());
        report.setSlokasCount(dto.getSlokasCount());
        report.setTempleVisit(dto.getTempleVisit());
        report.setDeityDarshan(dto.getDeityDarshan());
        report.setPrasadamHonor(dto.getPrasadamHonor());
        report.setServiceHours(dto.getServiceHours());
        report.setServiceDescription(dto.getServiceDescription());
        report.setSpiritualMood(dto.getSpiritualMood());
        report.setChallenges(dto.getChallenges());
        report.setGratitude(dto.getGratitude());
        report.setGoalsForTomorrow(dto.getGoalsForTomorrow());
    }
    
    /**
     * Convert Entity to DTO
     */
    private DailySadhanaReportDTO convertToDTO(DailySadhanaReport report) {
        DailySadhanaReportDTO dto = new DailySadhanaReportDTO();
        dto.setId(report.getId());
        dto.setSadhakaId(report.getSadhaka().getId());
        dto.setReportDate(report.getReportDate());
        dto.setWakeUpTime(report.getWakeUpTime());
        dto.setChantingRounds(report.getChantingRounds());
        dto.setLastRoundTime(report.getLastRoundTime());
        dto.setRestHours(report.getRestHours());
        dto.setSleepTime(report.getSleepTime());
        dto.setReadingMinutes(report.getReadingMinutes());
        dto.setReadingBook(report.getReadingBook());
        dto.setHearingMinutes(report.getHearingMinutes());
        dto.setHearingTopic(report.getHearingTopic());
        dto.setStudyHours(report.getStudyHours());
        dto.setStudySubject(report.getStudySubject());
        dto.setSlokaRecitation(report.getSlokaRecitation());
        dto.setSlokasCount(report.getSlokasCount());
        dto.setTempleVisit(report.getTempleVisit());
        dto.setDeityDarshan(report.getDeityDarshan());
        dto.setPrasadamHonor(report.getPrasadamHonor());
        dto.setServiceHours(report.getServiceHours());
        dto.setServiceDescription(report.getServiceDescription());
        dto.setSpiritualMood(report.getSpiritualMood());
        dto.setChallenges(report.getChallenges());
        dto.setGratitude(report.getGratitude());
        dto.setGoalsForTomorrow(report.getGoalsForTomorrow());
        return dto;
    }
    
    /**
     * Inner class for sadhana statistics
     */
    public static class SadhanaStatistics {
        private Long totalReports;
        private Double averageRounds;
        private Double averageReadingMinutes;
        private Double averageHearingMinutes;
        
        // Getters and setters
        public Long getTotalReports() { return totalReports; }
        public void setTotalReports(Long totalReports) { this.totalReports = totalReports; }
        
        public Double getAverageRounds() { return averageRounds; }
        public void setAverageRounds(Double averageRounds) { this.averageRounds = averageRounds; }
        
        public Double getAverageReadingMinutes() { return averageReadingMinutes; }
        public void setAverageReadingMinutes(Double averageReadingMinutes) { this.averageReadingMinutes = averageReadingMinutes; }
        
        public Double getAverageHearingMinutes() { return averageHearingMinutes; }
        public void setAverageHearingMinutes(Double averageHearingMinutes) { this.averageHearingMinutes = averageHearingMinutes; }
    }
}
