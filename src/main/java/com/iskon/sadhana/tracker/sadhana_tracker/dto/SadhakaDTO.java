package com.iskon.sadhana.tracker.sadhana_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SadhakaDTO {
    
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String spiritualName;
    private String temple;
    private String initiationLevel;
    private String location;
    private String role;
    private Boolean active;
}
