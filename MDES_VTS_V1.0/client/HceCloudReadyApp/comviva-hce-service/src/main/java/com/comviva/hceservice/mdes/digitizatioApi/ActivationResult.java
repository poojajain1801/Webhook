package com.comviva.hceservice.mdes.digitizatioApi;

public enum ActivationResult {
    /**
     * Activation was successful
     */
    SUCCESS,
    /**
     * Authentication Code was incorrect and rejected. Retries permitted.
     */
    INCORRECT_CODE,
    /**
     * Authentication Code was incorrect and the maximum number of retries now exceeded.
     */
    INCORRECT_CODE_RETRIES_EXCEEDED,
    /**
     * Authentication Code has expired or was invalidated.
     */
    EXPIRED_CODE,
    /**
     * Tokenization Authentication Value was incorrect and rejected.
     */
    INCORRECT_TAV,
    /**
     * The Token cannot be activated because the digitization session has expired. The caller must delete any artifacts relating to the Token and restart the process from the beginning
     */
    EXPIRED_SESSION,

}
