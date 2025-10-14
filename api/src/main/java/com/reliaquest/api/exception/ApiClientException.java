package com.reliaquest.api.exception;

/**
 * Exception thrown when there's an error communicating with the external API.
 * Wraps various API-related failures like network issues, invalid responses, etc.
 */
public class ApiClientException extends RuntimeException {

    public ApiClientException(String message) {
        super(message);
    }

    public ApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
