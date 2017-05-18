package com.comviva.mfs.hce.appserver.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.Errors;

/**
 * Created by sumit.das on 12/27/2016.
 */
@Getter
@ToString
public class ValidationException extends RuntimeException {
    private final Errors errors;

    public ValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }
}
