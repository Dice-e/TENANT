package com.tenant.app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * One class that turns exceptions into HTTP responses for ALL controllers.
 * Error body formats follow Chapter 4, Code 4.14:
 *   400 -> map of field name to message
 *   404 and 409 -> plain message string
 * Security errors (401, 403) happen before the controller and never reach here.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Thrown by Spring when @Valid finds a bad request DTO, before the controller method runs.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors); // 400
    }

    // Thrown by the service when a payment record id does not exist.
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<String> handleNotFound(RecordNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404
    }

    // Thrown by the service when a reference number belongs to another record.
    @ExceptionHandler(DuplicateReferenceException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateReferenceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage()); // 409
    }
}