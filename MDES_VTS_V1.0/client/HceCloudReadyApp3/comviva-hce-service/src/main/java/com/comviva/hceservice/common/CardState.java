package com.comviva.hceservice.common;

/**
 * Card's current state.
 */
public enum CardState {
    /** Card is provisioned and activated*/
    INITIALIZED,
    /** Card is provisioned but not activated*/
    UNINITIALIZED,
    /** Card is in Suspended State*/
    SUSPENDED,
    /** Card is in UNKNOWN State*/
    ACTIVE,
    /** Card is in UNKNOWN State*/
    DELETED
    /** Card is in UNKNOWN State*/

}
