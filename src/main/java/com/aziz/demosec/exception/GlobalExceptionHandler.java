package com.aziz.demosec.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadRequest(
            HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid request body. Check the 'role' field. Accepted values: ADMIN, DOCTOR, CLINIC, PHARMACIST, LABORATORY, NUTRITIONIST, VISITOR, PATIENT, HOME_CARE_PROVIDER"
        ));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation failed",
                "details", errors
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
                "error", "Internal Server Error",
                "message", ex.getMessage() != null ? ex.getMessage() : "Unknown error",
                "type", ex.getClass().getName()
        ));
    }
}
