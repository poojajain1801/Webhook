package com.comviva.mfs.promotion.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all error codes and their corresponding error message.
 * Created by tarkeshwar.v on 2/2/2017.
 */
public class ConstantErrorCodes {
    public static final Map<Integer, String> errorCodes = new HashMap<>();

    public static final int SC_OK = 200;

    public static final int INVALID_JSON = 220;
    public static final int INVALID_FIELD_FORMAT = INVALID_JSON + 1;
    public static final int INVALID_FIELD_LENGTH = INVALID_FIELD_FORMAT + 1;
    public static final int INVALID_FIELD_VALUE = INVALID_FIELD_LENGTH + 1;
    public static final int INVALID_RESPONSE_HOST = INVALID_FIELD_VALUE + 1;
    public static final int INVALID_OPERATION = INVALID_RESPONSE_HOST + 1;
    public static final int MISSING_REQUIRED_FIELD = INVALID_OPERATION + 1;
    public static final int CRYPTOGRAPHY_ERROR = MISSING_REQUIRED_FIELD + 1;
    public static final int INTERNAL_SERVICE_FAILURE = CRYPTOGRAPHY_ERROR + 1;
    public static final int DUPLICATE_REQUEST = INTERNAL_SERVICE_FAILURE + 1;
    public static final int INVALID_TASK_ID = DUPLICATE_REQUEST + 1;
    public static final int INVALID_TOKEN_UNIQUE_REFERENCE = INVALID_TASK_ID + 1;
    public static final int INVALID_TOKEN_STATUS = INVALID_TOKEN_UNIQUE_REFERENCE + 1;
    public static final int MAX_NUM_TRANSACTION_CREDENTIALS_REACHED = INVALID_TOKEN_STATUS + 1;
    public static final int RNS_UNAVAILABLE = MAX_NUM_TRANSACTION_CREDENTIALS_REACHED + 1;
    public static final int DEVICE_UNREACHABLE = RNS_UNAVAILABLE + 1;
    public static final int DEVICE_SUSPICIOUS = DEVICE_UNREACHABLE + 1;
    public static final int INVALID_PAYMENT_APP_PROVIDER_ID = DEVICE_SUSPICIOUS + 1;
    public static final int INVALID_PAYMENT_APP_INSTANCE_ID = INVALID_PAYMENT_APP_PROVIDER_ID + 1;
    public static final int PAYMENT_APP_INSTANCE_NOT_REGISTERED = INVALID_PAYMENT_APP_INSTANCE_ID + 1;
    public static final int INVALID_RNS_INFO = PAYMENT_APP_INSTANCE_NOT_REGISTERED + 1;

    public static final int INVALID_TOKEN_TYPE = INVALID_RNS_INFO + 1;
    public static final int SESSION_EXPIRED = INVALID_TOKEN_TYPE + 1;
    public static final int INVALID_SESSION = SESSION_EXPIRED + 1;

    static
    {
        // Standard
        errorCodes.put(INVALID_JSON, "Invalid JSON");
        errorCodes.put(INVALID_FIELD_FORMAT, "Invalid Field Format");
        errorCodes.put(INVALID_FIELD_LENGTH, "Invalid Field Length");
        errorCodes.put(INVALID_FIELD_VALUE, "Invalid Field Value");
        errorCodes.put(INVALID_RESPONSE_HOST, "Invalid Response Host");
        errorCodes.put(INVALID_OPERATION, "Invalid Operation");
        errorCodes.put(MISSING_REQUIRED_FIELD, "issing Required Field - {fieldName}");
        errorCodes.put(CRYPTOGRAPHY_ERROR, "Cryptography Error");
        errorCodes.put(INTERNAL_SERVICE_FAILURE, "Internal Service Failure");
        errorCodes.put(DUPLICATE_REQUEST, "Duplicate Request");
        errorCodes.put(INVALID_TASK_ID, "Invalid Task Id");
        errorCodes.put(INVALID_TOKEN_UNIQUE_REFERENCE, "Invalid SessionInfo Unique Reference");
        errorCodes.put(INVALID_TOKEN_STATUS, "Invalid SessionInfo status");
        errorCodes.put(MAX_NUM_TRANSACTION_CREDENTIALS_REACHED, "Max number of Transaction Credentials reached");
        errorCodes.put(RNS_UNAVAILABLE, "RNS Unavailable");
        errorCodes.put(DEVICE_UNREACHABLE, "Device Unreachable");
        errorCodes.put(DEVICE_SUSPICIOUS, "Device Suspicious");
        errorCodes.put(INVALID_PAYMENT_APP_PROVIDER_ID, "Invalid Payment App Provider Id");
        errorCodes.put(INVALID_PAYMENT_APP_INSTANCE_ID, "Invalid Payment App Instance Id");
        errorCodes.put(PAYMENT_APP_INSTANCE_NOT_REGISTERED, "Payment App Instance Not Registered");
        errorCodes.put(INVALID_RNS_INFO, "Invalid RNS Info");
        errorCodes.put(SESSION_EXPIRED, "Session Expired");
        errorCodes.put(INVALID_SESSION, "Session is invalid");

        // Extended
        errorCodes.put(INVALID_TOKEN_TYPE, "Invalid tokenType");
    }

}
