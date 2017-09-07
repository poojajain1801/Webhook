package com.comviva.hceservice.common;

/**
 * Exception Object thrown while invoking payment related APIs.
 */
public class SdkException extends Exception {
    private final SdkErrorCodes errorCode;

    public SdkException(SdkErrorCodes errorCode, String reason) {
        super(reason);
        this.errorCode = errorCode;
    }

    public SdkException(SdkErrorCodes errorCode) {
        super("Internal Error");
        this.errorCode = errorCode;
    }

}
