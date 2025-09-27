package com.iskon.sadhana.tracker.sadhana_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sadhakas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sadhaka {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String phoneNumber;
    
    @Column
    private String spiritualName; // Like "Krishna Das", "Radha Priya", etc.
    
    @Column
    private String temple; // Which temple they belong to
    
    @Column
    private String initiationLevel; // "Aspiring", "First Initiation", "Second Initiation"
    
    @Column
    private String location; // City/Country
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.SADHAKA;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many-to-Many relationship with Mentor (a sadhaka can have multiple mentors)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sadhaka_mentor",
        joinColumns = @JoinColumn(name = "sadhaka_id"),
        inverseJoinColumns = @JoinColumn(name = "mentor_id")
    )
    private Set<Mentor> mentors = new HashSet<>();
    
    // One-to-Many relationship with DailySadhanaReport
    @OneToMany(mappedBy = "sadhaka", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailySadhanaReport> sadhanaReports;
    
    public enum Role {
        SADHAKA, MENTOR, ADMIN
    }
}
