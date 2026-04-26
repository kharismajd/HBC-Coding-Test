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
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<InvalidFieldExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = toSnakeCase(error.getField());
            String errorMessage = error.getDefaultMessage();

            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        InvalidFieldExceptionResponseDto response = new InvalidFieldExceptionResponseDto(ExceptionMessageConstant.INVALID_FIELD, errors);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(value = IncorrectEmailOrPasswordException.class)
    public ResponseEntity<?> handleIncorrectEmailOrPasswordException(IncorrectEmailOrPasswordException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UnauthenticatedException.class)
    public ResponseEntity<?> handleUnauthenticatedException(UnauthenticatedException e) {
        return new ResponseEntity<>(new BasicExceptionResponseDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
