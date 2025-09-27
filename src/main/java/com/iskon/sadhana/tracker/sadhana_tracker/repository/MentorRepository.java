package com.iskon.sadhana.tracker.sadhana_tracker.repository;

import com.iskon.sadhana.tracker.sadhana_tracker.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Mentor entity
 * Provides database operations for managing spiritual guides
 */
@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
    
    /**
     * Find mentor by username (for authentication)
     */
    Optional<Mentor> findByUsername(String username);
    
    /**
     * Find mentor by email
     */
    Optional<Mentor> findByEmail(String email);
    
    /**
     * Find all active mentors
     */
    List<Mentor> findByActiveTrue();
    
    /**
     * Find mentors by temple
     */
    List<Mentor> findByTempleIgnoreCase(String temple);
    
    /**
     * Find mentors by designation
     */
    List<Mentor> findByDesignation(String designation);
    
    /**
     * Find mentors by specialization
     */
    List<Mentor> findBySpecializationContainingIgnoreCase(String specialization);
    
    /**
     * Find experienced mentors (minimum years)
     */
    List<Mentor> findByYearsOfExperienceGreaterThanEqualAndActiveTrue(Integer minYears);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find mentors who are guiding a specific sadhaka
     */
    @Query("SELECT m FROM Mentor m JOIN m.sadhakas s WHERE s.id = :sadhakaId AND m.active = true")
    List<Mentor> findMentorsBySadhakaId(@Param("sadhakaId") Long sadhakaId);
    
    /**
     * Count total active mentors
     */
    long countByActiveTrue();
    
    /**
     * Find available mentors (with capacity for more sadhakas)
     * This is a business logic that can be customized
     */
    @Query("SELECT m FROM Mentor m WHERE m.active = true AND SIZE(m.sadhakas) < 10")
    List<Mentor> findAvailableMentors();
    
    /**
     * Assign sadhaka to mentor directly in join table to avoid circular reference
     */
    @Modifying
    @Query(value = "INSERT INTO sadhaka_mentor (sadhaka_id, mentor_id) VALUES (:sadhakaId, :mentorId)", nativeQuery = true)
    void assignSadhakaToMentor(@Param("sadhakaId") Long sadhakaId, @Param("mentorId") Long mentorId);
    
    /**
     * Check if sadhaka is already assigned to mentor
     */
    @Query(value = "SELECT COUNT(*) FROM sadhaka_mentor WHERE sadhaka_id = :sadhakaId AND mentor_id = :mentorId", nativeQuery = true)
    int checkAssignmentExists(@Param("sadhakaId") Long sadhakaId, @Param("mentorId") Long mentorId);
    
    /**
     * Remove sadhaka from mentor directly in join table
     */
    @Modifying
    @Query(value = "DELETE FROM sadhaka_mentor WHERE sadhaka_id = :sadhakaId AND mentor_id = :mentorId", nativeQuery = true)
    void removeSadhakaFromMentor(@Param("sadhakaId") Long sadhakaId, @Param("mentorId") Long mentorId);
}
