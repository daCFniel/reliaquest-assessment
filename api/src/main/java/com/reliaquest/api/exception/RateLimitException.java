package com.reliaquest.api.exception;

/**
 * Exception thrown when the API rate limit has been exceeded.
 * Typically results in HTTP 429 Too Many Requests response.
 */
public class RateLimitException extends RuntimeException {

    private final Integer retryAfterSeconds;

    public RateLimitException(String message) {
        this(message, null);
    }

    public RateLimitException(String message, Integer retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public Integer getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
