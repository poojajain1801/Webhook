package com.comviva.mfs.promotion.exception;

/**
 * Created by sumit.das on 12/22/2016.
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Exception e) {
        super(e);
    }
}
