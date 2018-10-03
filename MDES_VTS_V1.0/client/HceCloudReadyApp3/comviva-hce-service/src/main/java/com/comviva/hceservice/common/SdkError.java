package com.comviva.hceservice.common;

/**
 * <p>Contains all error code that can be thrown from SDK APIs.</p>
 * <p><i>Note-40xx : SDK Error <br>
 *            50xx : Server Error <br>
 *            60xx : Common Error</i>
 * </p>
 */
public interface SdkError {

    /**
     * <p>Some exception occurred at SDK. </p>
     */
    int SW_SDK_INTERNAL_ERROR = 4000;
    /**
     * <p>Transaction Credential is not available. </p>
     */
    int SW_SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE = SW_SDK_INTERNAL_ERROR + 1;
    /**
     * <p>JSON Exception while preparing request to server. </p>
     */
    int SW_SDK_JSON_EXCEPTION = SW_SDK_TRANSACTION_CREDENTIAL_NOT_AVAILABLE + 1;
    /**
     * <p>Error while registering application with FCM server. </p>
     */
    int SW_SDK_RNS_REG_EXCEPTION = SW_SDK_JSON_EXCEPTION + 1;
    /**
     * <p>Invalid User Error. </p>
     */
    int SW_SDK_INVALID_USER = SW_SDK_RNS_REG_EXCEPTION + 1;
    /**
     * <p>Invalid Card Number. </p>
     */
    int SW_SDK_INVALID_CARD_NUMBER = SW_SDK_INVALID_USER + 1;
    /**
     * <p>Unsupported scheme type is provided. </p>
     */
    int SW_SDK_UNSUPPORTED_SCHEME = SW_SDK_INVALID_CARD_NUMBER + 1;
    /**
     * <p>IO error while reading any IO operation e.g. reading file. </p>
     */
    int SW_SDK_IO_ERROR = SW_SDK_UNSUPPORTED_SCHEME + 1;
    /**
     * <p>Card Eligibility is not invoked prior to digitization. </p>
     */
    int SW_SDK_CARD_ELIGIBILITY_NOT_PERFORMED = SW_SDK_IO_ERROR + 1;
    /**
     * <p>More than one type of card is provided in LCM operation at time. </p>
     */
    int SW_SDK_MORE_TYPE_OF_CARD_IN_LCM = SW_SDK_CARD_ELIGIBILITY_NOT_PERFORMED + 1;
    /**
     * <p>More than one Visa card is provided in LCM operation at time. </p>
     */
    int SW_SDK_ONLY_ONE_VISA_CARD_IN_LCM = SW_SDK_MORE_TYPE_OF_CARD_IN_LCM + 1;
    /**
     * <p>Task is already in progress. </p>
     */
    int SW_SDK_TASK_ALREADY_IN_PROGRESS = SW_SDK_ONLY_ONE_VISA_CARD_IN_LCM + 1;
    /**
     * <p>Invalid number of records provided to fetch for transaction history </p>
     */
    int SW_SDK_INVALID_NO_OF_TXN_RECORDS = SW_SDK_TASK_ALREADY_IN_PROGRESS + 1;


    /**
     * <p>Invalid number of records provided to fetch for transaction history </p>
     */
    int SW_SDK_ROLLOVER_IN_PROGRESS= SW_SDK_TASK_ALREADY_IN_PROGRESS + 1;


    /**
     * <p>Internal error from server. </p>
     */
    int SW_SERVER_INTERNAL_ERROR =  5000;
    /**
     * <p>JSON exception while converting response from server to JSON Object i.e. wrong data
     * or response structure from server. </p>
     */
    int SW_SERVER_JSON_EXCEPTION = SW_SERVER_INTERNAL_ERROR + 1;
    /**
     * <p>Invalid/incorrect value from server. </p>
     */
    int SW_SERVER_INVALID_VALUE = SW_SERVER_JSON_EXCEPTION + 1;
    /**
     * <p>Server is not responding. </p>
     */
    int SW_SERVER_NOT_RESPONDING = SW_SERVER_INVALID_VALUE + 1;

    /**
     * <p>Error while encrypting/decrypting data. <p/>
     */
    int SW_COMMON_CRYPTO_ERROR =  6000;
    /**
     * <p>Device is rooted. <p/>
     */
    int SW_COMMON_DEVICE_ROOTED = SW_COMMON_CRYPTO_ERROR + 1;
    /**
     * <p>APK is tampered. <p/>
     */
    int SW_COMMON_APK_TAMPERED = SW_COMMON_DEVICE_ROOTED + 1;
    /**
     * <p>Debugger is enabled / running on emulator / Application is in debug mode. <p/>
     */
    int SW_COMMON_DEBUG_MODE = SW_COMMON_APK_TAMPERED + 1;
    /**
     * <p>Card is not eligible for digitization. <p/>
     */
    int SW_COMMON_CARD_NOT_ELIGIBLE = SW_COMMON_DEBUG_MODE + 1;

    /**
     * Returns error message
     * @return Error message
     */
    String getMessage();

    /**
     * Returns Error Code.
     * @return Error Code
     */
    int getErrorCode();
}
