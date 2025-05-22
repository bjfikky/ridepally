package com.benorim.ridepally.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Authentication & Authorization Errors (1000-1999)
    AUTHENTICATION_FAILED("ERR_1000", "Authentication failed"),
    UNAUTHORIZED_ACCESS("ERR_1001", "Unauthorized access"),
    INVALID_TOKEN("ERR_1002", "Invalid token"),
    TOKEN_EXPIRED("ERR_1003", "Token has expired"),
    
    // User Profile Errors (2000-2999)
    USER_NOT_FOUND("ERR_2000", "User not found"),
    PROFILE_NOT_FOUND("ERR_2001", "User profile not found"),
    DISPLAY_NAME_TAKEN("ERR_2002", "Display name is already taken"),
    INVALID_PROFILE_DATA("ERR_2003", "Invalid profile data"),
    
    // Location & Geocoding Errors (3000-3999)
    INVALID_COORDINATES("ERR_3000", "Invalid coordinates provided"),
    LOCATION_NOT_FOUND("ERR_3001", "Location not found"),
    GEOCODING_FAILED("ERR_3002", "Failed to geocode location"),
    
    // Motorcycle Errors (4000-4999)
    MOTORCYCLE_NOT_FOUND("ERR_4000", "Motorcycle not found"),
    DUPLICATE_NICKNAME("ERR_4001", "Motorcycle nickname already exists"),
    INVALID_MOTORCYCLE_DATA("ERR_4002", "Invalid motorcycle data"),
    
    // System Errors (9000-9999)
    INTERNAL_SERVER_ERROR("ERR_9000", "Internal server error"),
    DATABASE_ERROR("ERR_9001", "Database operation failed"),
    EXTERNAL_SERVICE_ERROR("ERR_9002", "External service error");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

}