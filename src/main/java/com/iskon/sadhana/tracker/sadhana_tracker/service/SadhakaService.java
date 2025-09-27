package com.iskon.sadhana.tracker.sadhana_tracker.service;

import com.iskon.sadhana.tracker.sadhana_tracker.dto.SadhakaDTO;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.SadhakaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Sadhaka (devotee) operations
 * Contains business logic for sadhaka management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SadhakaService {
    
    private final SadhakaRepository sadhakaRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Register a new sadhaka
     */
    public SadhakaDTO registerSadhaka(SadhakaDTO sadhakaDTO, String password) {
        log.info("Registering new sadhaka: {}", sadhakaDTO.getUsername());
        
        // Check if username already exists
        if (sadhakaRepository.existsByUsername(sadhakaDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + sadhakaDTO.getUsername());
        }
        
        // Check if email already exists
        if (sadhakaRepository.existsByEmail(sadhakaDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + sadhakaDTO.getEmail());
        }
        
        // Create new sadhaka entity
        Sadhaka sadhaka = new Sadhaka();
        sadhaka.setUsername(sadhakaDTO.getUsername());
        sadhaka.setPassword(passwordEncoder.encode(password));
        sadhaka.setFirstName(sadhakaDTO.getFirstName());
        sadhaka.setLastName(sadhakaDTO.getLastName());
        sadhaka.setEmail(sadhakaDTO.getEmail());
        sadhaka.setPhoneNumber(sadhakaDTO.getPhoneNumber());
        sadhaka.setSpiritualName(sadhakaDTO.getSpiritualName());
        sadhaka.setTemple(sadhakaDTO.getTemple());
        sadhaka.setInitiationLevel(sadhakaDTO.getInitiationLevel());
        sadhaka.setLocation(sadhakaDTO.getLocation());
        sadhaka.setRole(Sadhaka.Role.SADHAKA);
        sadhaka.setActive(true);
        
        Sadhaka savedSadhaka = sadhakaRepository.save(sadhaka);
        log.info("Successfully registered sadhaka with ID: {}", savedSadhaka.getId());
        
        return convertToDTO(savedSadhaka);
    }
    
    /**
     * Register a new admin user
     */
    public SadhakaDTO registerAdmin(com.iskon.sadhana.tracker.sadhana_tracker.controller.AuthController.AdminRegistrationRequest request) {
        log.info("Registering new admin: {}", request.getUsername());
        
        // Check if username already exists
        if (sadhakaRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (sadhakaRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Create new admin entity (using Sadhaka entity with ADMIN role)
        Sadhaka admin = new Sadhaka();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setRole(Sadhaka.Role.ADMIN); // Set as ADMIN role
        admin.setActive(true);
        
        // Save admin
        Sadhaka savedAdmin = sadhakaRepository.save(admin);
        
        log.info("Successfully registered admin with ID: {} and username: {}", 
                savedAdmin.getId(), savedAdmin.getUsername());
        
        return convertToDTO(savedAdmin);
    }
    
    /**
     * Find sadhaka by username (for authentication)
     */
    @Transactional(readOnly = true)
    public Optional<Sadhaka> findByUsername(String username) {
        return sadhakaRepository.findByUsername(username);
    }
    
    /**
     * Get sadhaka profile by ID
     */
    @Transactional(readOnly = true)
    public Optional<SadhakaDTO> getSadhakaProfile(Long sadhakaId) {
        return sadhakaRepository.findById(sadhakaId)
                .map(this::convertToDTO);
    }
    
    /**
     * Update sadhaka profile
     */
    public SadhakaDTO updateProfile(Long sadhakaId, SadhakaDTO sadhakaDTO) {
        log.info("Updating profile for sadhaka ID: {}", sadhakaId);
        
        Sadhaka sadhaka = sadhakaRepository.findById(sadhakaId)
                .orElseThrow(() -> new RuntimeException("Sadhaka not found with ID: " + sadhakaId));
        
        // Update fields (except username and password)
        sadhaka.setFirstName(sadhakaDTO.getFirstName());
        sadhaka.setLastName(sadhakaDTO.getLastName());
        sadhaka.setEmail(sadhakaDTO.getEmail());
        sadhaka.setPhoneNumber(sadhakaDTO.getPhoneNumber());
        sadhaka.setSpiritualName(sadhakaDTO.getSpiritualName());
        sadhaka.setTemple(sadhakaDTO.getTemple());
        sadhaka.setInitiationLevel(sadhakaDTO.getInitiationLevel());
        sadhaka.setLocation(sadhakaDTO.getLocation());
        
        Sadhaka updatedSadhaka = sadhakaRepository.save(sadhaka);
        log.info("Successfully updated profile for sadhaka: {}", updatedSadhaka.getUsername());
        
        return convertToDTO(updatedSadhaka);
    }
    
    /**
     * Get all active sadhakas
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> getAllActiveSadhakas() {
        return sadhakaRepository.findByActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get sadhakas by temple
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> getSadhakasByTemple(String temple) {
        return sadhakaRepository.findByTempleIgnoreCase(temple)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get sadhakas under a specific mentor
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> getSadhakasByMentor(Long mentorId) {
        return sadhakaRepository.findSadhakasByMentorId(mentorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Search sadhakas by spiritual name
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> searchBySpiritualName(String spiritualName) {
        return sadhakaRepository.findBySpiritualNameContainingIgnoreCase(spiritualName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Deactivate sadhaka account
     */
    public void deactivateSadhaka(Long sadhakaId) {
        log.info("Deactivating sadhaka with ID: {}", sadhakaId);
        
        Sadhaka sadhaka = sadhakaRepository.findById(sadhakaId)
                .orElseThrow(() -> new RuntimeException("Sadhaka not found with ID: " + sadhakaId));
        
        sadhaka.setActive(false);
        sadhakaRepository.save(sadhaka);
        
        log.info("Successfully deactivated sadhaka: {}", sadhaka.getUsername());
    }
    
    /**
     * Get total count of active sadhakas
     */
    @Transactional(readOnly = true)
    public long getTotalActiveSadhakas() {
        return sadhakaRepository.countByActiveTrue();
    }
    
    /**
     * Change password
     */
    public void changePassword(Long sadhakaId, String currentPassword, String newPassword) {
        log.info("Changing password for sadhaka ID: {}", sadhakaId);
        
        Sadhaka sadhaka = sadhakaRepository.findById(sadhakaId)
                .orElseThrow(() -> new RuntimeException("Sadhaka not found with ID: " + sadhakaId));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, sadhaka.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update with new password
        sadhaka.setPassword(passwordEncoder.encode(newPassword));
        sadhakaRepository.save(sadhaka);
        
        log.info("Successfully changed password for sadhaka: {}", sadhaka.getUsername());
    }
    
    /**
     * Get all sadhakas (Admin only)
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> getAllSadhakas() {
        return sadhakaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unassigned sadhakas (Admin only)
     */
    @Transactional(readOnly = true)
    public List<SadhakaDTO> getUnassignedSadhakas() {
        return sadhakaRepository.findSadhakasByMentorsIsEmpty()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private SadhakaDTO convertToDTO(Sadhaka sadhaka) {
        SadhakaDTO dto = new SadhakaDTO();
        dto.setId(sadhaka.getId());
        dto.setUsername(sadhaka.getUsername());
        dto.setFirstName(sadhaka.getFirstName());
        dto.setLastName(sadhaka.getLastName());
        dto.setEmail(sadhaka.getEmail());
        dto.setPhoneNumber(sadhaka.getPhoneNumber());
        dto.setSpiritualName(sadhaka.getSpiritualName());
        dto.setTemple(sadhaka.getTemple());
        dto.setInitiationLevel(sadhaka.getInitiationLevel());
        dto.setLocation(sadhaka.getLocation());
        dto.setRole(sadhaka.getRole().toString());
        dto.setActive(sadhaka.getActive());
        return dto;
    }
}
