package com.comviva.hceservice.common;

/**
 * Created by amit.randhawa on 16-Aug-17.
 */
public enum Tags {
    CLIENT_APP_ID("clientAppId"),
    CLIENT_WALLET_ACCOUNT_ID("clientWalletAccountId"),
    EMAIL_ADDRESS("emailAddress"),
    PROTECTION_TYPE("protectionType"),
    PRESENTATION_TYPE("presentationType"),
    CLIENT_DEVICE_ID("clientDeviceID"),
    PAN_ENROLLMENT_ID("panEnrollmentID"),
    TERMS_AND_CONDITION_ID("termsAndConditionsID"),
    VPAN_ENROLLMENT_ID("vPanEnrollmentID"),
    TERMS_CONDITION_ID("termsAndConditionsID");

    private String tag;

    private Tags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}