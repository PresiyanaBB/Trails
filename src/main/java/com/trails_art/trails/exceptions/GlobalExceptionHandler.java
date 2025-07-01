package com.trails_art.trails.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // catch internal server errors
    @ExceptionHandler({IllegalArgumentException.class})
    public final ResponseEntity handleInternalServerErrors(IllegalArgumentException e) {
        log.error(e.getMessage());

        return new ResponseEntity(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({InvalidArgumentIdException.class})
    public final ResponseEntity handleValidationException(InvalidArgumentIdException e) {
        log.error(Map.of().toString());

        return new ResponseEntity(new HashMap<>(Map.of()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({InvalidDTOFormat.class})
    public final ResponseEntity handleValidationException(InvalidDTOFormat e) {
        log.error(Map.of().toString());

        return new ResponseEntity(new HashMap<>(Map.of()), HttpStatus.BAD_REQUEST);
    }

    // catch DB exceptions
    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity handleNotFoundDBException(EntityNotFoundException e) {
        log.error(e.getMessage());

        return new ResponseEntity(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity handleInsertingDuplicatesToDB(DataIntegrityViolationException e) {
        log.error(e.getMessage());

        return new ResponseEntity(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // catch Hibernate validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity handleArgumentValidationException(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (o1, _) -> o1, HashMap::new));

        log.error(errors.toString());

        return ResponseEntity.badRequest().body(errors);
    }
}
