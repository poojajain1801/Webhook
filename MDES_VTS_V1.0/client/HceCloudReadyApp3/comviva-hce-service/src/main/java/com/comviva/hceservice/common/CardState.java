package com.comviva.hceservice.common;

/**
 * Card's current state.
 */
public enum CardState {
    /** Card is provisioned and activated*/
    INITIALIZED,
    /** Card is provisioned but not activated*/
    UNINITIALIZED,
    /** Card is in UNINITIALIZED State*/
    SUSPENDED,
    /** Card is in SUSPENDED State*/
    ACTIVE,
    /** Card is in ACTIVE State*/
    DELETED,
    /** Card is in DELETED State*/
    INACTIVE
    /** Card is in INACTIVE State*/

}
