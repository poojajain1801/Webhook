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
    USER_ID("userID"),
    ACTIVATION_CODE("activationCode"),
    TOKEN_INFO("tokenInfo"),
    MAC("mac"),
    API("api"),
    SC("sc"),
    TV1("tvl"),
    PROVISIONING_STATUS("provisioningStatus"),
    GUID("guid"),
    FAILURE_REASON("failureReason"),
    REPERSO("reperso"),
    ENCRYPTION_META_DATA("encryptionMetaData"),
    VPAN_ENROLLMENT_ID("vPanEnrollmentID"),
    V_PROVISIONED_TOKEN_ID("vprovisionedTokenID"),
    TERMS_CONDITION_ID("termsAndConditionsID"),
    RESPONSE_CODE("responseCode"),
    MESSAGE("message"),
    HVT_SUPPPORT("isHvtSupported"),
    HVT_LIMIT("hvtThreshold"),
    ENC_PAYMENT_INSTRUMENT("encPaymentInstrument");

    private String tag;

    private Tags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}