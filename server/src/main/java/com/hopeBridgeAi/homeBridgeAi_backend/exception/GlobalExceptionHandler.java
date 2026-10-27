
package com.hopeBridgeAi.homeBridgeAi_backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.time.Instant;

import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createBody(
            int status, String error, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {

        HttpStatusCode status = ex.getStatusCode();
        HttpStatus resolved = HttpStatus.resolve(status.value());

        String error = resolved != null
                ? resolved.getReasonPhrase()
                : "Request failed";

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request failed";

        return ResponseEntity.status(status)
                .body(createBody(status.value(), error, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", message);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(
                createBody(
                        400,
                        "Bad Request",
                        "Invalid or missing request body. Please send valid JSON."
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                createBody(
                        409,
                        "Conflict",
                        "The request conflicts with existing data."
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(
            Exception ex) {

        // Log the actual exception on the server, not in the API response.
        System.err.println("Unexpected server exception: " + ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createBody(
                        500,
                        "Internal Server Error",
                        "An unexpected error occurred. Please try again later."
                )
        );
    }
}
