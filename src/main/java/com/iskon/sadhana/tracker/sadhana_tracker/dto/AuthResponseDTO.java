package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private String spiritualName;
    
    public AuthResponseDTO(String token, String username, String role, String spiritualName) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.spiritualName = spiritualName;
    }
}
