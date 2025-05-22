package com.benorim.ridepally.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization Errors (1000-1999)
    AUTHENTICATION_FAILED("ERR_1000", "Authentication failed", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACCESS("ERR_1001", "Unauthorized access", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_ACTION("ERR_1002", "Unauthorized access", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("ERR_1003", "Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("ERR_1004", "Token has expired", HttpStatus.UNAUTHORIZED),
    
    // User Profile Errors (2000-2999)
    USER_NOT_FOUND("ERR_2000", "User not found", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND("ERR_2001", "User profile not found", HttpStatus.NOT_FOUND),
    DISPLAY_NAME_TAKEN("ERR_2002", "Display name is already taken", HttpStatus.CONFLICT),
    INVALID_PROFILE_DATA("ERR_2003", "Invalid profile data", HttpStatus.BAD_REQUEST),
    
    // Location & Geocoding Errors (3000-3999)
    INVALID_COORDINATES("ERR_3000", "Invalid coordinates provided", HttpStatus.BAD_REQUEST),
    LOCATION_NOT_FOUND("ERR_3001", "Location not found", HttpStatus.NOT_FOUND),
    GEOCODING_FAILED("ERR_3002", "Failed to geocode location", HttpStatus.BAD_REQUEST),
    
    // Motorcycle Errors (4000-4999)
    MOTORCYCLE_NOT_FOUND("ERR_4000", "Motorcycle not found", HttpStatus.NOT_FOUND),
    DUPLICATE_NICKNAME("ERR_4001", "Motorcycle nickname already exists", HttpStatus.CONFLICT),
    INVALID_MOTORCYCLE_DATA("ERR_4002", "Invalid motorcycle data", HttpStatus.BAD_REQUEST),
    
    // System Errors (9000-9999)
    INTERNAL_SERVER_ERROR("ERR_9000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("ERR_9001", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("ERR_9002", "External service error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}