package com.iskon.sadhana.tracker.sadhana_tracker.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT Configuration Properties for Sadhana Tracker
 * 
 * This class holds JWT-related configuration values
 * that are read from application.properties file.
 * 
 * Properties:
 * - app.jwt.secret: Secret key for signing JWT tokens
 * - app.jwt.expiration: Token expiration time in milliseconds
 * - app.jwt.token-prefix: Token prefix (typically "Bearer ")
 * - app.jwt.header-name: HTTP header name for JWT token
 */
@Configuration
public class JwtConfig {
    
    /**
     * JWT Secret key for signing tokens
     * Read from application.properties: app.jwt.secret
     * Default: Strong secret key for development
     */
    @Value("${app.jwt.secret:SadhanaTrackerSecretKeyForJWTTokenGenerationHareKrishna2025}")
    private String secret;
    
    /**
     * JWT Token expiration time in milliseconds
     * Read from application.properties: app.jwt.expiration
     * Default: 24 hours (86400000 ms)
     */
    @Value("${app.jwt.expiration:86400000}")
    private long expiration;
    
    /**
     * JWT Token prefix (typically "Bearer ")
     * Used when parsing Authorization header
     */
    @Value("${app.jwt.token-prefix:Bearer }")
    private String tokenPrefix;
    
    /**
     * JWT Header name (typically "Authorization")
     * HTTP header where JWT token is expected
     */
    @Value("${app.jwt.header-name:Authorization}")
    private String headerName;
    
    // Getters for accessing configuration values
    
    /**
     * Get the JWT secret key for token signing/validation
     */
    public String getSecret() {
        return secret;
    }
    
    /**
     * Get the JWT token expiration time in milliseconds
     */
    public long getExpiration() {
        return expiration;
    }
    
    /**
     * Get the JWT token prefix (e.g., "Bearer ")
     */
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    /**
     * Get the HTTP header name for JWT tokens
     */
    public String getHeaderName() {
        return headerName;
    }
}
