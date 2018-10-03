package com.comviva.hceservice.digitizationApi;

/**
 * Source of PAN
 */
public enum PanSource {
    /**
     * The wallet provider already has the PAN data for the customer in their records.
     */
    ONFILE,
    /**
     * The customer is expected to type the card number or capture a picture of the card.
     */
    MANUALLYENTERED,
    /**
     * The PAN data was provided by the issuer of the PAN; see provider field in Encrypted Payment Instrument.
     */
    ISSUER_PUSH_PROVISION,

    CARD_ADDED_MANUALLY,
    CARD_ON_FILE,
    CARD_ADDED_VIA_APPLICATION
}
