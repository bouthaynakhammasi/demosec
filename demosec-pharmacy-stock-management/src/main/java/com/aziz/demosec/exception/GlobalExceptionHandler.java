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
}
