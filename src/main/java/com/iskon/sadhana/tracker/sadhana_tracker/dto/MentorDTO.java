package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import com.iskon.sadhana.tracker.sadhana_tracker.entity.Mentor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Mentor
 * Used for API requests and responses involving mentor data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorDTO {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    private String phoneNumber;
    
    @Size(max = 100, message = "Spiritual name cannot exceed 100 characters")
    private String spiritualName;
    
    @Size(max = 100, message = "Temple name cannot exceed 100 characters")
    private String temple;
    
    private String initiationLevel;
    
    private String designation;
    
    private Integer yearsOfExperience;
    
    private String specialization;
    
    private Mentor.Role role;
    
    private Boolean active;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors for common use cases
    public MentorDTO(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = Mentor.Role.MENTOR;
        this.active = true;
    }
}
