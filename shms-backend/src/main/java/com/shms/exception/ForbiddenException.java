package com.shms.exception;

/** Thrown when an authenticated user lacks permission for the requested action. Maps to HTTP 403. */
public class ForbiddenException extends ShmsException {

    public ForbiddenException(String message) {
        super(message);
    }
}
