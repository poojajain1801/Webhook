package com.comviva.hceservice.mdes.digitizatioApi.authentication;

/**
 * Created by tarkeshwar.v on 6/20/2017.
 */
public enum AuthenticationType {
    /** Text message to Cardholder's mobile phone number.
     Value will be the Cardholder’s masked mobile phone number */
    TEXT_TO_CARDHOLDER_NUMBER,

    /** Email to Cardholder's email address.
     Value will be the Cardholder’s masked email address. */
    EMAIL_TO_CARDHOLDER_ADDRESS,

    /** Cardholder-initiated call to automated call center phone number.
     Value will be the phone number for the Cardholder to call. */
    CARDHOLDER_TO_CALL_AUTOMATED_NUMBER,

    /** Cardholder-initiated call to manned call center phone number.
     Value will be the phone number for the Cardholder to call. */
    CARDHOLDER_TO_CALL_MANNED_NUMBER,

    /** Cardholder to visit a website.
     Value will be the website URL. */
    CARDHOLDER_TO_VISIT_WEBSITE,

    /** Cardholder to use a specific mobile for authentication.
     Value will be an IssuerMobileApp object with the applicable activation app method for the device. */
    CARDHOLDER_TO_USE_ISSUER_MOBILE_APP,

    /** Issuer-initiated voice call to Cardholder’s phone.
     Value will be the Cardholder’s masked voice call phone number. */
    ISSUER_TO_CALL_CARDHOLDER_NUMBER
}
