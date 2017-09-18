package com.comviva.hceservice.common;

public enum SdkErrorStandardImpl implements SdkError {
    // SDK Errors
    SDK_INTERNAL_ERROR(SW_COMMON_CRYPTO_ERROR, "Internal error"),
    SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE(SW_SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE,
            "Transaction credential not available"),
    SDK_JSON_EXCEPTION(SW_SDK_JSON_EXCEPTION, "JSON Exception"),
    SDK_RNS_REG_EXCEPTION(SW_SDK_RNS_REG_EXCEPTION, "JSON Exception"),
    SDK_INVALID_USER(SW_SDK_INVALID_USER, "Invalid User"),
    SDK_INVALID_CARD_NUMBER(SW_SDK_INVALID_CARD_NUMBER, "Invalid Card Number"),
    SDK_UNSUPPORTED_SCHEME(SW_SDK_UNSUPPORTED_SCHEME, "Unsupported Scheme"),
    SDK_IO_ERROR(SW_SDK_IO_ERROR, "IO Error"),
    SDK_CARD_ELIGIBILITY_NOT_PERFORMED(SW_SDK_CARD_ELIGIBILITY_NOT_PERFORMED, "Please invoke card eligibility first"),
    SDK_MORE_TYPE_OF_CARD_IN_LCM(SW_SDK_MORE_TYPE_OF_CARD_IN_LCM, "Only one type of card is allowed in LCM operation at a time"),
    SDK_ONLY_ONE_VISA_CARD_IN_LCM(SW_SDK_ONLY_ONE_VISA_CARD_IN_LCM, "Only one Visa card is allowed in LCM operation at a time"),
    SDK_TASK_ALREADY_IN_PROGRESS(SW_SDK_TASK_ALREADY_IN_PROGRESS, "Task is already in progress"),
    SDK_INVALID_NO_OF_TXN_RECORDS(SW_SDK_INVALID_NO_OF_TXN_RECORDS, "Invalid number of records provided to fetch for transaction history"),

    // Server Errors
    SERVER_INTERNAL_ERROR(SW_SERVER_INTERNAL_ERROR, "Internal error"),
    SERVER_JSON_EXCEPTION(SW_SERVER_JSON_EXCEPTION, "JSON Exception"),
    SERVER_INVALID_VALUE(SW_SERVER_INVALID_VALUE, "Invalid Value from server"),
    SERVER_NOT_RESPONDING(SW_SERVER_NOT_RESPONDING, "Server is not responding"),

    // Common Errors
    COMMON_CRYPTO_ERROR(SW_COMMON_CRYPTO_ERROR, "Crypto Exception"),
    COMMON_DEVICE_ROOTED(SW_COMMON_DEVICE_ROOTED, "Device is rooted"),
    COMMON_APK_TAMPERED(SW_COMMON_DEVICE_ROOTED, "SDK is tampered"),
    COMMON_CARD_NOT_ELIGIBLE(SW_COMMON_CARD_NOT_ELIGIBLE, "Card is not eligible"),
    COMMON_DEBUG_MODE(SW_COMMON_DEBUG_MODE, "Debug is not allowed");

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
