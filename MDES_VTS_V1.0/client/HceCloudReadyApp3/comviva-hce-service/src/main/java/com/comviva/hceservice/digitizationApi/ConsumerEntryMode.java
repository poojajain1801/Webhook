package com.comviva.hceservice.digitizationApi;

/**
 * The method of consumer entry
 */
public enum ConsumerEntryMode {
    /** Input is given by user explicitly using keyboard/Keypad*/
    KEYENTERED,
    /** Input is captured by scanning using camera  */
    CAMERACAPTURED,
    /** Other methods of input */
    UNKNOWN
}
