package com.harebusiness.form.exceptions;

public class OneResponseLimitException extends RuntimeException {
    public OneResponseLimitException(String message) {
        super(message);
    }
}
