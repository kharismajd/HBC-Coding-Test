package com.harebusiness.form.exceptions;

import com.harebusiness.form.constants.ExceptionMessageConstant;
import com.harebusiness.form.dtos.response.BasicExceptionResponseDto;
import com.harebusiness.form.dtos.response.InvalidFieldExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto("Invalid JSON body"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<InvalidFieldExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = toSnakeCase(error.getField());
            String errorMessage = error.getDefaultMessage();

            String flattenedFieldName = fieldName.replaceAll("\\[\\d+\\]", "");

            errors.computeIfAbsent(flattenedFieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        errors.forEach((key, value) -> {
            List<String> uniqueMessages = value.stream().distinct().toList();
            errors.put(key, new ArrayList<>(uniqueMessages));
        });

        InvalidFieldExceptionResponseDto response = new InvalidFieldExceptionResponseDto(ExceptionMessageConstant.INVALID_FIELD_MESSAGE, errors);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ValidationFieldException.class)
    public ResponseEntity<Object> handleManualValidation(ValidationFieldException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ExceptionMessageConstant.INVALID_FIELD_MESSAGE);
        body.put("errors", ex.getErrors());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(value = IncorrectEmailOrPasswordException.class)
    public ResponseEntity<?> handleIncorrectEmailOrPasswordException(IncorrectEmailOrPasswordException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UnauthenticatedException.class)
    public ResponseEntity<?> handleUnauthenticatedException(UnauthenticatedException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ForbiddenAccessException.class)
    public ResponseEntity<?> handleForbiddenAccessException(ForbiddenAccessException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = OneResponseLimitException.class)
    public ResponseEntity<?> handleOneResponseLimitException(OneResponseLimitException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = InvalidChoiceTypeException.class)
    public ResponseEntity<?> handleInvalidChoiceTypeException(InvalidChoiceTypeException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicExceptionResponseDto> handleGeneralException(Exception e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
