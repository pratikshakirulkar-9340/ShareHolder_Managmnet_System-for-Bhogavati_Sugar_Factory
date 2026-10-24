package com.shms.exception;

/**
 * Base unchecked exception for all application-specific errors.
 * Subclasses map to specific HTTP statuses in {@link GlobalExceptionHandler}.
 */
public class ShmsException extends RuntimeException {

    public ShmsException(String message) {
        super(message);
    }

    public ShmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
