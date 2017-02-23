package com.comviva.mfs.promotion.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(Exception e) {
        super(e);
    }
}
