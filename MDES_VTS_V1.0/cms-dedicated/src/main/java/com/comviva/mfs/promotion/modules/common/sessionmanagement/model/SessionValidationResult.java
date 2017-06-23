package com.comviva.mfs.promotion.modules.common.sessionmanagement.model;

/**
 * Created by tarkeshwar.v on 2/20/2017.
 */
public enum SessionValidationResult {
    SESSION_OK,
    SESSION_NOT_FOUND,
    SESSION_EXPIRED,
    INCORRECT_DATE_FORMAT,
    // Request data is in incorrect JSON format
    INCORRECT_REQUEST_DATA,
    CRYPTO_ERROR,
    INVALID_MOBILE_KEYSET_ID,
    MAC_ERROR
}
