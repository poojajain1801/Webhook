package com.comviva.mfs.promotion.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Exception e) {
        super(e);
    }
}
