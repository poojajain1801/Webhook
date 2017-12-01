package com.comviva.hceservice.digitizationApi;

/**
 * Type of Activation Code
 */
public enum ActivationCodeType {
    /**
     *  The Authentication Code as entered by the Cardholder to activate the Token.
     */
    AUTHENTICATION_CODE,

    /**
     * The Tokenization Authentication Value (TAV) as cryptographically signed by the Issuer to activate this Token.
     */
    TOKENIZATION_AUTHENTICATION_VALUE
}
