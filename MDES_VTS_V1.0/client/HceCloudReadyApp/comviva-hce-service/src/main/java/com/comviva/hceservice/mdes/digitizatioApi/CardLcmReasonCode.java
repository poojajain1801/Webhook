package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * The reason for the action (SUSPEND, UNSUSPEND, DELETE) to be taken
 */

public enum CardLcmReasonCode {
    /**
     * Token device lost
     */
    DEVICE_LOST,
    /**
     * Token device stolen
     */
    DEVICE_STOLEN,
    /**
     * Account closed
     */
    ACCOUNT_CLOSED,
    /**
     * Suspected fraudulent token transactions.
     */
    SUSPECTED_FRAUD, //
    /** Other – default, used if value not provided */
    OTHER,

    /**
     * Token device found or not stolen.
     * Used in case of RESUME operation only.
     */
    DEVICE_FOUND,
    /**
     * Confirmed no fraudulent token transactions.
     * Used in case of RESUME operation only.
     */
    NOT_FRAUD,
}
