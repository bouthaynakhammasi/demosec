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
        String details = null;
        if (ex.getMostSpecificCause() != null) {
            details = ex.getMostSpecificCause().getMessage();
        } else {
            details = ex.getMessage();
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid request body",
                "details", details
        ));
    }
}
