package com.comviva.hceservice.mdes.digitizatioApi;

/**
 * Created by tarkeshwar.v on 6/6/2017.
 */

public enum CardLcmReasonCode {
    /** Token device lost */
    DEVICE_LOST,
    /** Token device stolen */
    DEVICE_STOLEN,
    /** Account closed */
    ACCOUNT_CLOSED, //
    /** Suspected fraudulent token transactions. */
    SUSPECTED_FRAUD, //
    /** Other – default, used if value not provided */
    OTHER
}
