package com.iskon.sadhana.tracker.sadhana_tracker.security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Security Exception Handler for Sadhana Tracker
 * 
 * Handles all security-related exceptions and provides appropriate HTTP responses
 * with detailed error messages for different authentication and authorization failures
 */
@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    /**
     * Handle Bad Credentials Exception
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {
        
        log.warn("Bad credentials attempt from: {}", request.getRemoteUser());
        
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid username or password",
                ex.getMessage(),
                request
        );
    }

    /**
     * Handle Disabled Account Exception
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledException(
            DisabledException ex, WebRequest request) {
        
        log.warn("Disabled account access attempt from: {}", request.getRemoteUser());
        
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCOUNT_DISABLED",
                "Account is disabled",
                ex.getMessage(),
                request
        );
    }

    /**
     * Handle Access Denied Exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        
        log.warn("Access denied for user: {} to resource: {}", 
                request.getRemoteUser(), request.getDescription(false));
        
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "Access denied. Insufficient permissions",
                "You don't have permission to access this resource",
                request
        );
    }

    /**
     * Handle Insufficient Authentication Exception
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientAuthentication(
            InsufficientAuthenticationException ex, WebRequest request) {
        
        log.warn("Insufficient authentication for resource: {}", request.getDescription(false));
        
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INSUFFICIENT_AUTHENTICATION",
                "Authentication required",
                "Please provide valid authentication credentials",
                request
        );
    }

    /**
     * Handle Generic Authentication Exception
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        log.error("Authentication error: {}", ex.getMessage());
        
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_FAILED",
                "Authentication failed",
                ex.getMessage(),
                request
        );
    }

    /**
     * Handle JWT Token Exceptions
     */
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleJwtAuthenticationException(
            JwtAuthenticationException ex, WebRequest request) {
        
        log.warn("JWT authentication error: {}", ex.getMessage());
        
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_TOKEN",
                "Invalid or expired token",
                ex.getMessage(),
                request
        );
    }

    /**
     * Build standardized error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String errorCode,
            String message,
            String details,
            WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("errorCode", errorCode);
        errorDetails.put("message", message);
        errorDetails.put("details", details);
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorDetails, status);
    }

    /**
     * Custom JWT Authentication Exception
     */
    public static class JwtAuthenticationException extends AuthenticationException {
        public JwtAuthenticationException(String message) {
            super(message);
        }
        
        public JwtAuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
