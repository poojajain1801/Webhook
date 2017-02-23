package com.comviva.mfs.promotion.exception;

/**
 * Created by sumit.das on 12/21/2016.
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(Exception e) {
        super(e);
    }
}
