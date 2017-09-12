package com.comviva.hceservice.common;

public enum SdkErrorStandardImpl implements SdkError {
    // SDK Errors
    SDK_INTERNAL_ERROR(100, "Internal error"),
    SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE(101, "Transaction credential not available"),
    SDK_JSON_EXCEPTION(102, "JSON Exception"),
    SDK_RNS_REG_EXCEPTION(103, "JSON Exception"),
    SDK_INVALID_USER(104, "Invalid User"),
    SDK_INVALID_CARD_NUMBER(105, "Invalid Card Number"),
    SDK_UNSUPPORTED_SCHEME(106, "Unsupported Scheme"),
    SDK_IO_ERROR(107, "IO Error"),
    SDK_CARD_NOT_ELIGIBLE(108, "Card is not eligible"),

    // Server Errors
    SERVER_INTERNAL_ERROR(300, "Internal error"),
    SERVER_JSON_EXCEPTION(301, "JSON Exception"),
    SERVER_INVALID_VALUE(302, "Invalid Value from server"),
    SERVER_NOT_RESPONDING(303, "Server is not responding"),

    // Common Errors
    COMMON_CRYPTO_ERROR(400, "Crypto Exception");

    private int errorCode;
    private String errorMessage;

    SdkErrorStandardImpl(int errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    public static SdkError getError(int errorCode) {
        switch (errorCode) {
            case 100:
                return SDK_INTERNAL_ERROR;

            case 101:
                return SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE;

            case 102:
                return SDK_JSON_EXCEPTION;

            case 103:
                return SDK_RNS_REG_EXCEPTION;

            case 104:
                return SDK_INVALID_USER;

            case 105:
                return SDK_INVALID_CARD_NUMBER;

            case 106:
                return SDK_UNSUPPORTED_SCHEME;

            case 107:
                return SDK_IO_ERROR;

            case 300:
                return SERVER_INTERNAL_ERROR;

            case 301:
                return SERVER_JSON_EXCEPTION;

            case 302:
                return SERVER_INVALID_VALUE;

            case 303:
                return SERVER_NOT_RESPONDING;

            default:
                return null;
        }
    }

}
