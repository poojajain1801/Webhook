package com.comviva.hceservice.common;

public enum SdkErrorStandardImpl implements SdkError {
    // SDK Errors
    SDK_INTERNAL_ERROR(SW_SDK_INTERNAL_ERROR, "Internal error"),
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
    COMMON_APK_TAMPERED(SW_COMMON_APK_TAMPERED, "SDK is tampered"),
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

    /**
     * Returns error enum instance corresponding to given error code.
     * @param errorCode Error Code
     * @return SdkError instance
     */
    public static SdkError getError(int errorCode) {
        switch (errorCode) {
            case SW_SDK_INTERNAL_ERROR:
                return SDK_INTERNAL_ERROR;

            case SW_SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE:
                return SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE;

            case SW_SDK_JSON_EXCEPTION:
                return SDK_JSON_EXCEPTION;

            case SW_SDK_RNS_REG_EXCEPTION:
                return SDK_RNS_REG_EXCEPTION;

            case SW_SDK_INVALID_USER:
                return SDK_INVALID_USER;

            case SW_SDK_INVALID_CARD_NUMBER:
                return SDK_INVALID_CARD_NUMBER;

            case SW_SDK_UNSUPPORTED_SCHEME:
                return SDK_UNSUPPORTED_SCHEME;

            case SW_SDK_IO_ERROR:
                return SDK_IO_ERROR;

            case SW_SDK_CARD_ELIGIBILITY_NOT_PERFORMED:
                return SDK_CARD_ELIGIBILITY_NOT_PERFORMED;

            case SW_SDK_MORE_TYPE_OF_CARD_IN_LCM:
                return SDK_MORE_TYPE_OF_CARD_IN_LCM;

            case SW_SDK_ONLY_ONE_VISA_CARD_IN_LCM:
                return SDK_ONLY_ONE_VISA_CARD_IN_LCM;

            case SW_SDK_TASK_ALREADY_IN_PROGRESS:
                return SDK_TASK_ALREADY_IN_PROGRESS;

            case SW_SDK_INVALID_NO_OF_TXN_RECORDS:
                return SDK_INVALID_NO_OF_TXN_RECORDS;

                // Server Errors
            case SW_SERVER_INTERNAL_ERROR:
                return SERVER_INTERNAL_ERROR;

            case SW_SERVER_JSON_EXCEPTION:
                return SERVER_JSON_EXCEPTION;

            case SW_SERVER_INVALID_VALUE:
                return SERVER_INVALID_VALUE;

            case SW_SERVER_NOT_RESPONDING:
                return SERVER_NOT_RESPONDING;

                // Common Errors
            case SW_COMMON_CRYPTO_ERROR:
                return COMMON_CRYPTO_ERROR;

            case SW_COMMON_DEVICE_ROOTED:
                return COMMON_DEVICE_ROOTED;

            case SW_COMMON_APK_TAMPERED:
                return COMMON_APK_TAMPERED;

            case SW_COMMON_CARD_NOT_ELIGIBLE:
                return COMMON_CARD_NOT_ELIGIBLE;

            case SW_COMMON_DEBUG_MODE:
                return COMMON_DEBUG_MODE;

            default:
                return null;
        }
    }

}
