package com.shms.exception;

/** Thrown when a requested entity does not exist. Maps to HTTP 404. */
public class ResourceNotFoundException extends ShmsException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entityName, Object identifier) {
        return new ResourceNotFoundException(entityName + " not found with identifier: " + identifier);
    }
}
