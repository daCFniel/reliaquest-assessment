package com.reliaquest.api.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Typically results in HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s with id '%s' not found", resourceType, resourceId));
    }
}
