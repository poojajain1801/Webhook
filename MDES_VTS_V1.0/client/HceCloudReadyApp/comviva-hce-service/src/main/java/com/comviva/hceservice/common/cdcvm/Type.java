package com.comviva.hceservice.common.cdcvm;

/**
 * Type of CDCVM
 */
public enum Type {
    /** No CDCVM */
    NONE,
    /** Swipe unlock */
    SWIPE,
    /** Patter Lock */
    PATTERN,
    /** PIN Lock */
    PIN,
    /** Passcode Lock */
    PASSCODE,
    /** Fingerprint authentication */
    BIO_FINGERPRINT
}
