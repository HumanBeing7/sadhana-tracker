package com.iskon.sadhana.tracker.sadhana_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mentors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mentor {
    
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
    private String spiritualName;
    
    @Column
    private String temple;
    
    @Column
    private String initiationLevel;
    
    @Column
    private String designation; // "Brahmachari", "Sannyasi", "Grihasta Mentor", etc.
    
    @Column
    private Integer yearsOfExperience;
    
    @Column
    private String specialization; // "Japa", "Book Distribution", "Deity Worship", etc.
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MENTOR;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many-to-Many relationship with Sadhaka (a mentor can guide multiple sadhakas)
    @ManyToMany(mappedBy = "mentors", fetch = FetchType.LAZY)
    private Set<Sadhaka> sadhakas = new HashSet<>();
    
    public enum Role {
        SADHAKA, MENTOR, ADMIN
    }
}
