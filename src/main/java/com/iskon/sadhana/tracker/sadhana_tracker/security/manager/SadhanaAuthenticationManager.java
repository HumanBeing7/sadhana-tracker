package com.iskon.sadhana.tracker.sadhana_tracker.security.manager;

import com.iskon.sadhana.tracker.sadhana_tracker.entity.Sadhaka;
import com.iskon.sadhana.tracker.sadhana_tracker.entity.Mentor;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.SadhakaRepository;
import com.iskon.sadhana.tracker.sadhana_tracker.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

/**
 * Custom Authentication Manager for Sadhana Tracker
 * 
 * Handles authentication for both Sadhakas and Mentors
 * Provides custom authentication logic with proper error handling
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SadhanaAuthenticationManager implements AuthenticationManager {

    private final SadhakaRepository sadhakaRepository;
    private final MentorRepository mentorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        log.debug("Attempting authentication for username: {}", username);

        // Try to authenticate as Sadhaka first
        Optional<Sadhaka> sadhakaOpt = sadhakaRepository.findByUsername(username);
        if (sadhakaOpt.isPresent()) {
            return authenticateSadhaka(sadhakaOpt.get(), password);
        }

        // Try to authenticate as Mentor
        Optional<Mentor> mentorOpt = mentorRepository.findByUsername(username);
        if (mentorOpt.isPresent()) {
            return authenticateMentor(mentorOpt.get(), password);
        }

        log.warn("Authentication failed: User not found - {}", username);
        throw new BadCredentialsException("Invalid username or password");
    }

    /**
     * Authenticate Sadhaka user
     */
    private Authentication authenticateSadhaka(Sadhaka sadhaka, String password) {
        // Check if account is active
        if (!sadhaka.getActive()) {
            log.warn("Authentication failed: Account is deactivated - {}", sadhaka.getUsername());
            throw new DisabledException("Account is deactivated. Please contact administrator.");
        }

        // Verify password
        if (!passwordEncoder.matches(password, sadhaka.getPassword())) {
            log.warn("Authentication failed: Invalid password for sadhaka - {}", sadhaka.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("Successfully authenticated sadhaka: {}", sadhaka.getUsername());

        // Create authorities
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + sadhaka.getRole().name());

        // Create authentication token with user details
        return new UsernamePasswordAuthenticationToken(
                new SadhakaUserPrincipal(sadhaka),
                null,
                Collections.singletonList(authority)
        );
    }

    /**
     * Authenticate Mentor user
     */
    private Authentication authenticateMentor(Mentor mentor, String password) {
        // Check if account is active
        if (!mentor.getActive()) {
            log.warn("Authentication failed: Account is deactivated - {}", mentor.getUsername());
            throw new DisabledException("Account is deactivated. Please contact administrator.");
        }

        // Verify password
        if (!passwordEncoder.matches(password, mentor.getPassword())) {
            log.warn("Authentication failed: Invalid password for mentor - {}", mentor.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("Successfully authenticated mentor: {}", mentor.getUsername());

        // Create authorities
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + mentor.getRole().name());

        // Create authentication token with user details
        return new UsernamePasswordAuthenticationToken(
                new MentorUserPrincipal(mentor),
                null,
                Collections.singletonList(authority)
        );
    }

    /**
     * User Principal for Sadhaka
     */
    public static class SadhakaUserPrincipal {
        private final Sadhaka sadhaka;

        public SadhakaUserPrincipal(Sadhaka sadhaka) {
            this.sadhaka = sadhaka;
        }

        public Long getId() { return sadhaka.getId(); }
        public String getUsername() { return sadhaka.getUsername(); }
        public String getRole() { return sadhaka.getRole().name(); }
        public String getEmail() { return sadhaka.getEmail(); }
        public String getFullName() { return sadhaka.getFirstName() + " " + sadhaka.getLastName(); }
        public String getSpiritualName() { return sadhaka.getSpiritualName(); }
        public String getTemple() { return sadhaka.getTemple(); }
        public Sadhaka getSadhaka() { return sadhaka; }

        @Override
        public String toString() { return sadhaka.getUsername(); }
    }

    /**
     * User Principal for Mentor
     */
    public static class MentorUserPrincipal {
        private final Mentor mentor;

        public MentorUserPrincipal(Mentor mentor) {
            this.mentor = mentor;
        }

        public Long getId() { return mentor.getId(); }
        public String getUsername() { return mentor.getUsername(); }
        public String getRole() { return mentor.getRole().name(); }
        public String getEmail() { return mentor.getEmail(); }
        public String getFullName() { return mentor.getFirstName() + " " + mentor.getLastName(); }
        public String getSpecialization() { return mentor.getSpecialization(); }
        public String getTemple() { return mentor.getTemple(); }
        public Mentor getMentor() { return mentor; }

        @Override
        public String toString() { return mentor.getUsername(); }
    }
}
