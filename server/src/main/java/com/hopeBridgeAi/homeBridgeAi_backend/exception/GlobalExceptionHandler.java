
package com.hopeBridgeAi.homeBridgeAi_backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Common error response format
    private Map<String, Object> createBody(
            int status,
            String error,
            String message
    ) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);

        return body;
    }

    // Handle exceptions with explicitly defined HTTP status
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex
    ) {
        HttpStatusCode status = ex.getStatusCode();
        HttpStatus resolved = HttpStatus.resolve(status.value());

        String error = resolved != null
                ? resolved.getReasonPhrase()
                : "Request failed";

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request failed";

        return ResponseEntity.status(status).body(
                createBody(
                        status.value(),
                        error,
                        message
                )
        );
    }

    // Handle DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": "
                                + error.getDefaultMessage()
                )
                .distinct()
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest().body(
                createBody(
                        400,
                        "Bad Request",
                        message
                )
        );
    }

    // Handle invalid JSON request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException ex
    ) {
        return ResponseEntity.badRequest().body(
                createBody(
                        400,
                        "Bad Request",
                        "Invalid or missing request body. Please send valid JSON."
                )
        );
    }

    // Handle invalid enum, numeric ID, and request parameter types
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        String message;

        if ("role".equals(ex.getName())) {
            message = "Invalid role. Allowed roles: USER, ADMIN, ORPHAN, DONOR, VOLUNTEER";
        } else {
            message = "Invalid value for parameter: " + ex.getName();
        }

        return ResponseEntity.badRequest().body(
                createBody(
                        400,
                        "Bad Request",
                        message
                )
        );
    }

    // Handle database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                createBody(
                        409,
                        "Conflict",
                        "The request conflicts with existing data."
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(
            MissingServletRequestParameterException ex
    ) {
        return ResponseEntity.badRequest().body(
                createBody(
                        400,
                        "Bad Request",
                        "Required parameter is missing: " + ex.getParameterName()
                )
        );
    }

    // Handle unexpected server errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(
            Exception ex
    ) {
        // Log the actual exception on the server,
        // but do not expose internal details to the client.
        System.err.println("Unexpected server exception: " + ex);

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(
                createBody(
                        500,
                        "Internal Server Error",
                        "An unexpected error occurred. Please try again later."
                )
        );
    }
}
