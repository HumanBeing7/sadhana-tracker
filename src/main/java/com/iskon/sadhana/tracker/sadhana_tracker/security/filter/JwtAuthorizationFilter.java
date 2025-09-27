package com.iskon.sadhana.tracker.sadhana_tracker.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JWT Authorization Filter for Sadhana Tracker
 * 
 * NOTE: This filter is no longer used as Spring Security's built-in 
 * authorization mechanism handles role-based access control.
 * 
 * @deprecated Use Spring Security's hasRole() and hasAnyRole() methods in SecurityConfig
 */
@Slf4j
@Deprecated
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // Define role-based access rules
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        // Admin permissions
        ROLE_PERMISSIONS.put("ADMIN", Set.of(
            "/api/admin/**",
            "/api/sadhaka/**",
            "/api/mentor/**",
            "/api/reports/**",
            "/api/statistics/**"
        ));
        
        // Mentor permissions
        ROLE_PERMISSIONS.put("MENTOR", Set.of(
            "/api/mentor/**",
            "/api/sadhaka/*/reports/**", // Can view sadhaka reports they mentor
            "/api/reports/mentee/**"
        ));
        
        // Sadhaka permissions
        ROLE_PERMISSIONS.put("SADHAKA", Set.of(
            "/api/sadhaka/profile",
            "/api/sadhaka/reports",
            "/api/sadhaka/goals",
            "/api/sadhaka/badges"
        ));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip authorization for public endpoints
        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // If no authentication, let other filters handle it
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Check if user has permission to access this endpoint
            if (!hasPermission(authentication, requestURI, method)) {
                log.warn("Access denied for user: {} to {} {}", 
                        authentication.getName(), method, requestURI);
                handleAccessDenied(response);
                return;
            }
            
            log.debug("Authorization granted for user: {} to {} {}", 
                    authentication.getName(), method, requestURI);
            
        } catch (Exception e) {
            log.error("Authorization error: {}", e.getMessage());
            handleAuthorizationError(response, "Authorization failed");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Check if user has permission to access the requested resource
     */
    private boolean hasPermission(Authentication authentication, String requestURI, String method) {
        // Get user role
        String userRole = getUserRole(authentication);
        if (userRole == null) {
            return false;
        }
        
        // Admin has access to everything
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // Check role-based permissions
        Set<String> allowedPatterns = ROLE_PERMISSIONS.get(userRole);
        if (allowedPatterns == null) {
            return false;
        }
        
        // Check if any pattern matches the request URI
        for (String pattern : allowedPatterns) {
            if (matchesPattern(requestURI, pattern)) {
                // Additional checks for user-specific resources
                return checkUserSpecificAccess(authentication, requestURI, userRole);
            }
        }
        
        return false;
    }
    
    /**
     * Check user-specific access for resources that belong to the authenticated user
     */
    private boolean checkUserSpecificAccess(Authentication authentication, String requestURI, String userRole) {
        // For Sadhakas, ensure they can only access their own resources
        if ("SADHAKA".equals(userRole)) {
            // Get user ID for potential future use in user-specific resource checks
            getUserId(authentication);
            
            // Check if accessing own profile/reports
            if (requestURI.contains("/sadhaka/") && !requestURI.contains("/sadhaka/profile") 
                && !requestURI.contains("/sadhaka/reports") && !requestURI.contains("/sadhaka/goals") 
                && !requestURI.contains("/sadhaka/badges")) {
                // This might be accessing another sadhaka's resource, deny access
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Extract user role from authentication
     */
    private String getUserRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_"))
                .map(authority -> authority.getAuthority().substring(5)) // Remove "ROLE_" prefix
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Extract user ID from authentication principal
     */
    private Long getUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.UserPrincipal) {
            return ((JwtAuthenticationFilter.UserPrincipal) principal).getUserId();
        }
        return null;
    }
    
    /**
     * Check if request URI matches the permission pattern
     */
    private boolean matchesPattern(String requestURI, String pattern) {
        if (pattern.endsWith("/**")) {
            String basePattern = pattern.substring(0, pattern.length() - 3);
            return requestURI.startsWith(basePattern);
        }
        return requestURI.equals(pattern) || requestURI.matches(pattern.replace("*", ".*"));
    }
    
    /**
     * Check if the endpoint is public and doesn't require authorization
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
     * Handle access denied by setting appropriate response
     */
    private void handleAccessDenied(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Forbidden\",\"message\":\"Access denied. Insufficient permissions.\",\"timestamp\":\"%s\"}", 
            java.time.LocalDateTime.now()
        ));
    }
    
    /**
     * Handle authorization errors
     */
    private void handleAuthorizationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Authorization Error\",\"message\":\"%s\",\"timestamp\":\"%s\"}", 
            message, 
            java.time.LocalDateTime.now()
        ));
    }
}
