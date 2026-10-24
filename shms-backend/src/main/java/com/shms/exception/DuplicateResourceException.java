package com.shms.exception;

/** Thrown when attempting to create a record that violates a uniqueness rule. Maps to HTTP 409. */
public class DuplicateResourceException extends ShmsException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
