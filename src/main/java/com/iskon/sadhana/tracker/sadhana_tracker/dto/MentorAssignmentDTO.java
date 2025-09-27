package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MentorAssignmentDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;
    private List<BasicSadhakaDTO> assignedSadhakas;
    
    // Constructors
    public MentorAssignmentDTO() {}
    
    public MentorAssignmentDTO(Long id, String username, String email, String name, 
                              String phoneNumber, String address, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<BasicSadhakaDTO> getAssignedSadhakas() {
        return assignedSadhakas;
    }
    
    public void setAssignedSadhakas(List<BasicSadhakaDTO> assignedSadhakas) {
        this.assignedSadhakas = assignedSadhakas;
    }
    
    // Inner class for basic sadhaka info
    public static class BasicSadhakaDTO {
        private Long id;
        private String username;
        private String name;
        private String email;
        
        public BasicSadhakaDTO() {}
        
        public BasicSadhakaDTO(Long id, String username, String name, String email) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.email = email;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }
}
