package com.comviva.hceservice.requestobjects;

/**
 * The reason for the action (SUSPEND, UNSUSPEND, DELETE) to be taken
 */

public enum CardLcmReasonCode {
    /**
     * Token device lost. <br>
     * Note - Used in case of MasterCard for SUSPEND & DELETE
     */
    DEVICE_LOST,

    /**
     * Token device stolen.<br>
     * Note - Used in case of MasterCard for SUSPEND & DELETE
     */
    DEVICE_STOLEN,

    /**
     * Account closed. <br>
     * Note - Used in case of MasterCard for DELETE
     */
    ACCOUNT_CLOSED,

    /**
     * Suspected fraudulent token transactions. <br>
     * Note - Used in case of MasterCard for SUSPEND & DELETE
     */
    SUSPECTED_FRAUD,

    /**
     * Other – default, used if value not provided.
     */
    OTHER,

    /**
     * Token device found or not stolen. <br>
     * Used in case of MasterCard for RESUME operation only.
     */
    DEVICE_FOUND,

    /**
     * Confirmed no fraudulent token transactions.<br>
     * Used in case of MasterCard for RESUME operation only.
     */
    NOT_FRAUD,

    /**
     * If any fraud happened.<br>
     * Note - Used for VISA case only
     */
    FRAUD,

    /**
     * Consumer Initiated.<br>
     * Note - Used for VISA case only
     */
    CUSTOMER_CONFIRMED

}
