package com.harebusiness.form.exceptions;

import com.harebusiness.form.dtos.response.BasicExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IncorrectEmailOrPasswordException.class)
    public ResponseEntity<?> handleIncorrectEmailOrPasswordException(IncorrectEmailOrPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BasicExceptionResponseDto(e.getMessage()));
    }
}
