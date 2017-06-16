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
    public static final int INVALID_FIELD_FORMAT = 221;
    public static final int INVALID_FIELD_LENGTH = 222;
    public static final int INVALID_FIELD_VALUE = 223;
    public static final int INVALID_RESPONSE_HOST = 224;
    public static final int INVALID_OPERATION = 225;
    public static final int MISSING_REQUIRED_FIELD = 226;
    public static final int CRYPTOGRAPHY_ERROR = 227;
    public static final int INTERNAL_SERVICE_FAILURE = 228;
    public static final int DUPLICATE_REQUEST = 229;
    public static final int INVALID_TASK_ID = 230;
    public static final int INVALID_TOKEN_UNIQUE_REFERENCE = 231;
    public static final int INVALID_TOKEN_STATUS = 232;
    public static final int MAX_NUM_TRANSACTION_CREDENTIALS_REACHED = 233;
    public static final int RNS_UNAVAILABLE = 234;
    public static final int DEVICE_UNREACHABLE = 235;
    public static final int DEVICE_SUSPICIOUS = 236;
    public static final int INVALID_PAYMENT_APP_PROVIDER_ID = 237;
    public static final int INVALID_PAYMENT_APP_INSTANCE_ID = 238;
    public static final int PAYMENT_APP_INSTANCE_NOT_REGISTERED = 239;
    public static final int INVALID_RNS_INFO =240;

    public static final int INVALID_TOKEN_TYPE = 241;
    public static final int SESSION_EXPIRED = 242;
    public static final int INVALID_SESSION = 243;
    public static final int INVALID_MOBILE_PIN = 244;
    public static final int MAX_PIN_TRY_LIMIT_REACHED = 245;

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
        errorCodes.put(INVALID_TOKEN_UNIQUE_REFERENCE, "Invalid Token Unique Reference");
        errorCodes.put(INVALID_TOKEN_STATUS, "Invalid Token status");
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
