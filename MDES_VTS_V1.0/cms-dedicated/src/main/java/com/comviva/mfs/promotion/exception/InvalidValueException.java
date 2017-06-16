package com.comviva.mfs.promotion.exception;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String message, Exception exception) {
        super(message, exception);
    }
}
