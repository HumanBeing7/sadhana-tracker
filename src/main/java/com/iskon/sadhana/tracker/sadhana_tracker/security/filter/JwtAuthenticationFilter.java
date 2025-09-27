package com.iskon.sadhana.tracker.sadhana_tracker.security.filter;

import com.iskon.sadhana.tracker.sadhana_tracker.service.JwtService;
import com.iskon.sadhana.tracker.sadhana_tracker.security.exception.SecurityExceptionHandler.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter for Sadhana Tracker
 * 
 * This filter intercepts HTTP requests and validates JWT tokens.
 * It extracts user information from valid tokens and sets up Spring Security context.
 * 
 * Security Features:
 * - Token validation and parsing
 * - User authentication context setup
 * - Role-based authority assignment
 * - Comprehensive error handling
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if Authorization header is present and starts with Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in request to: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from header
        jwt = authHeader.substring(7);
        
        try {
            // Extract username from JWT token
            username = jwtService.extractUsername(jwt);
            
            // If username is present and no authentication is set in context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Validate the JWT token
                if (jwtService.isTokenValid(jwt)) {
                    
                    // Extract user details from token
                    String role = jwtService.extractRole(jwt);
                    Long userId = jwtService.extractUserId(jwt);
                    
                    // Create authorities based on role
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            new UserPrincipal(userId, username, role),
                            null,
                            Collections.singletonList(authority)
                    );
                    
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Successfully authenticated user: {} with role: {} for request: {}", 
                            username, role, request.getRequestURI());
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                    throw new JwtAuthenticationException("Invalid JWT token");
                }
            }
        } catch (JwtAuthenticationException e) {
            log.error("JWT Authentication failed: {}", e.getMessage());
            handleAuthenticationError(response, e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication: {}", e.getMessage());
            handleAuthenticationError(response, "Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the endpoint is public and doesn't require authentication
     */
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/public/") ||
               requestURI.startsWith("/api/auth/") ||
               requestURI.startsWith("/swagger-ui/") ||
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.equals("/error") ||
               requestURI.equals("/favicon.ico");
    }

    /**
     * Handle authentication errors by setting appropriate response
     */
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}", 
            message, 
            java.time.LocalDateTime.now()
        ));
    }

    /**
     * Custom UserPrincipal class to hold user information
     */
    public static class UserPrincipal {
        private final Long userId;
        private final String username;
        private final String role;

        public UserPrincipal(Long userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }

        @Override
        public String toString() {
            return username;
        }
    }
}
