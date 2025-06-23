package com.spring.eac.ai.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the API
 * Handles specific exceptions and returns appropriate HTTP responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Error response data structure
     */
    public record ErrorResponse(String message, String errorType) {}

    /**
     * Handles IllegalArgumentException and returns HTTP 400 Bad Request
     * Used when client provides invalid inputs, such as non-capital related queries
     *
     * @param ex The IllegalArgumentException thrown
     * @return ResponseEntity with error details and HTTP 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument provided: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "Invalid Request"
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
