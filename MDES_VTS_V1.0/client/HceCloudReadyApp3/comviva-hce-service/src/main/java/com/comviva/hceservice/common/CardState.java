package com.comviva.hceservice.common;

/**
 * Card's current state.
 */
public enum CardState {
    SUSPENDED,
    /** Card is in SUSPENDED State*/
    ACTIVE,
    /** Card is in ACTIVE State*/

    INACTIVE,
    /** Card is in INACTIVE State*/

    /** Card is in Marked for Deletion*/
    MARKED_FOR_DELETION,
    /** Card state is unknown*/
    UNKNOWN

}
