package com.comviva.mfs.promotion.exception;

/**
 * Created by sumit.das on 12/26/2016.
 */
public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String message, Exception exception) {
        super(message, exception);
    }
}
