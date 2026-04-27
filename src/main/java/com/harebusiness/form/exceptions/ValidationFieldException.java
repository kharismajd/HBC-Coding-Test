package com.harebusiness.form.exceptions;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ValidationFieldException extends RuntimeException {
    private final Map<String, List<String>> errors;

    public ValidationFieldException(Map<String, List<String>> errors) {
        super("Invalid field");
        this.errors = errors;
    }
}
