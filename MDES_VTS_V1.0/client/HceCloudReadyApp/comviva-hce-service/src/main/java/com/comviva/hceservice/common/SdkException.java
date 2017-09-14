package com.comviva.hceservice.common;

/**
 * Generic Exception Object thrown if any error found while invoking SDK APIs.
 */
public class SdkException extends Exception {
    private int errorCode;

    /**
     * Public Constructor.
     * @param errorCode Error Code Constant
     */
    public SdkException(SdkError errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getErrorCode();
    }

    public SdkException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    /**
     * Returns Error Code
     * @return Error Code
     */
    public int getErrorCode() {
        return errorCode;
    }
}
