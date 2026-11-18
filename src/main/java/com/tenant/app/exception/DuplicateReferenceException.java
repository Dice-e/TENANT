package com.tenant.app.exception;

/**
 * Thrown by the service layer when a reference number already belongs to
 * another payment record. The GlobalExceptionHandler turns this into a
 * 409 response. The service decides WHEN to throw it; this class only
 * carries the message.
 */
public class DuplicateReferenceException extends RuntimeException {

    public DuplicateReferenceException(String reference) {
        super("Reference number '" + reference + "' is already used by another record");
    }
}