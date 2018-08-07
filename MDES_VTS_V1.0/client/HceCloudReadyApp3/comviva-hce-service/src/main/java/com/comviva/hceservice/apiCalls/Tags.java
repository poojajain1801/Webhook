package com.comviva.hceservice.apiCalls;

public enum Tags {

    OTP_VALUE("otpValue"),V_PROVISIONED_TOKEN_ID("vProvisionedTokenID"),URL_LOG("URL"),RESPONSE_LOG("RESPONSE"),EXCEPTION_LOG("EXCEPTION");

    private String tag;

    Tags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
