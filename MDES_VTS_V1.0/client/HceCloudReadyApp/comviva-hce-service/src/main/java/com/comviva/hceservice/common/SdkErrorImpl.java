package com.comviva.hceservice.common;


public class SdkErrorImpl implements SdkError {
    private int errorCode;
    private String errorMessage;

    private SdkErrorImpl(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static SdkError getInstance(int errorCode, String errorMessage) {
        return new SdkErrorImpl(errorCode, errorMessage);
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }
}
