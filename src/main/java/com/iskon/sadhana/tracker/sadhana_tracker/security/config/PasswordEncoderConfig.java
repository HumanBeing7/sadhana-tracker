package com.iskon.sadhana.tracker.sadhana_tracker.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration for Sadhana Tracker
 * 
 * This configuration is separated from SecurityConfig to avoid circular dependencies.
 * The PasswordEncoder is used by various security components including:
 * - SadhanaAuthenticationManager (for password validation)
 * - AuthenticationService (for user registration/login)
 * - Any password-related operations
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Password encoder bean for encrypting passwords
     * BCrypt is a strong hashing function designed for password hashing
     * with configurable work factor for future-proofing against hardware improvements
     * 
     * Features:
     * - Adaptive hashing algorithm
     * - Built-in salt generation
     * - Configurable cost parameter (default: 10)
     * - Resistant to rainbow table attacks
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
