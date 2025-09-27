package com.iskon.sadhana.tracker.sadhana_tracker.repository;

import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sadhaka entity
 * Provides database operations for managing devotees
 */
@Repository
public interface SadhakaRepository extends JpaRepository<Sadhaka, Long> {
    
    /**
     * Find sadhaka by username (for authentication)
     */
    Optional<Sadhaka> findByUsername(String username);
    
    /**
     * Find sadhaka by email
     */
    Optional<Sadhaka> findByEmail(String email);
    
    /**
     * Find all active sadhakas
     */
    List<Sadhaka> findByActiveTrue();
    
    /**
     * Find sadhakas by temple
     */
    List<Sadhaka> findByTempleIgnoreCase(String temple);
    
    /**
     * Find sadhakas by initiation level
     */
    List<Sadhaka> findByInitiationLevel(String initiationLevel);
    
    /**
     * Find sadhakas by spiritual name (partial match)
     */
    List<Sadhaka> findBySpiritualNameContainingIgnoreCase(String spiritualName);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find sadhakas under a specific mentor
     */
    @Query("SELECT s FROM Sadhaka s JOIN s.mentors m WHERE m.id = :mentorId AND s.active = true")
    List<Sadhaka> findSadhakasByMentorId(@Param("mentorId") Long mentorId);
    
    /**
     * Count total active sadhakas
     */
    long countByActiveTrue();
    
    /**
     * Find sadhakas by location
     */
    List<Sadhaka> findByLocationIgnoreCase(String location);
    
    /**
     * Find sadhakas who are not assigned to any mentor
     */
    @Query("SELECT s FROM Sadhaka s WHERE s.mentors IS EMPTY AND s.active = true")
    List<Sadhaka> findSadhakasByMentorsIsEmpty();
}
