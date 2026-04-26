package com.harebusiness.form.exceptions;

public class IncorrectEmailOrPasswordException extends RuntimeException {
    public IncorrectEmailOrPasswordException(String message) {
        super(message);
    }
}
