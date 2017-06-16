package com.comviva.mfs.promotion.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.Errors;

@Getter
@ToString
public class ValidationException extends RuntimeException {
    private final Errors errors;

    public ValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }
}
