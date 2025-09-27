package com.iskon.sadhana.tracker.sadhana_tracker.dto;

public class AssignmentResponseDTO {
    private String message;
    private Long mentorId;
    private String mentorName;
    private Long sadhakaId;
    private String sadhakaName;
    
    public AssignmentResponseDTO() {}
    
    public AssignmentResponseDTO(String message, Long mentorId, String mentorName, Long sadhakaId, String sadhakaName) {
        this.message = message;
        this.mentorId = mentorId;
        this.mentorName = mentorName;
        this.sadhakaId = sadhakaId;
        this.sadhakaName = sadhakaName;
    }
    
    // Getters and setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Long getMentorId() {
        return mentorId;
    }
    
    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }
    
    public String getMentorName() {
        return mentorName;
    }
    
    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }
    
    public Long getSadhakaId() {
        return sadhakaId;
    }
    
    public void setSadhakaId(Long sadhakaId) {
        this.sadhakaId = sadhakaId;
    }
    
    public String getSadhakaName() {
        return sadhakaName;
    }
    
    public void setSadhakaName(String sadhakaName) {
        this.sadhakaName = sadhakaName;
    }
}
