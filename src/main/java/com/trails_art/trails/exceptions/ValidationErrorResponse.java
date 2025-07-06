package com.trails_art.trails.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, String path, Map<String, String> fieldErrors) {
        super(status, message, timestamp, path);
        this.fieldErrors = fieldErrors;
    }
}
