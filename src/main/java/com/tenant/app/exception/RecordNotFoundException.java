package com.tenant.app.exception;

/**
 * Thrown by the service layer when no payment record exists for a given id.
 * The GlobalExceptionHandler turns this into a 404 response.
 * This class knows nothing about HTTP on purpose.
 */
public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(Long id) {
        super("Payment record with id " + id + " not found");
    }
}