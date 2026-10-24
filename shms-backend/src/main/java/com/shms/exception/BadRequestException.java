package com.shms.exception;

/** Thrown for invalid input or a violated business rule. Maps to HTTP 400. */
public class BadRequestException extends ShmsException {

    public BadRequestException(String message) {
        super(message);
    }
}
