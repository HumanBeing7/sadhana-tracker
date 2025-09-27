package com.iskon.sadhana.tracker.sadhana_tracker.security.config;

import com.iskon.sadhana.tracker.sadhana_tracker.security.filter.JwtAuthenticationFilter;
import com.iskon.sadhana.tracker.sadhana_tracker.security.manager.SadhanaAuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration for Sadhana Tracker Application
 * 
 * This configuration class sets up:
 * 1. JWT-based authentication with custom filters
 * 2. Role-based authorization (RBAC)
 * 3. Password encryption with BCrypt
 * 4. API endpoint security rules
 * 5. Custom authentication manager
 * 
 * Security Architecture:
 * - JwtAuthenticationFilter: Validates JWT tokens and sets authentication context
 * - Spring Security Authorization: Enforces role-based access control using built-in mechanisms
 * - SadhanaAuthenticationManager: Custom authentication for Sadhakas and Mentors
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SadhanaAuthenticationManager sadhanaAuthenticationManager;

    /**
     * Configure security filter chain for Sadhana Tracker
     * 
     * Public endpoints (no authentication needed):
     * - /api/auth/** (login, register, password reset)
     * - /api/public/** (public information, announcements)
     * - /swagger-ui/** (API documentation)
     * - /h2-console/** (development database console)
     * 
     * Protected endpoints (authentication required):
     * - /api/sadhaka/** (sadhaka operations) - SADHAKA, MENTOR, ADMIN roles
     * - /api/mentor/** (mentor operations) - MENTOR, ADMIN roles
     * - /api/admin/** (admin operations) - ADMIN role only
     * 
     * Filter Chain Order:
     * 1. JwtAuthenticationFilter - Validates JWT and sets authentication
     * 2. Spring Security Authorization - Checks role-based permissions using built-in filters
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT is stateless
            .authenticationManager(sadhanaAuthenticationManager) // Use our custom authentication manager
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication needed
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // For development
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger docs
                .requestMatchers("/error", "/favicon.ico").permitAll()
                
                // Sadhaka endpoints - accessible by SADHAKA, MENTOR, and ADMIN
                .requestMatchers("/api/sadhaka/**").hasAnyRole("SADHAKA", "MENTOR", "ADMIN")
                
                // Mentor endpoints - accessible by MENTOR and ADMIN
                .requestMatchers("/api/mentor/**").hasAnyRole("MENTOR", "ADMIN")
                
                // Admin endpoints - accessible by ADMIN only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
