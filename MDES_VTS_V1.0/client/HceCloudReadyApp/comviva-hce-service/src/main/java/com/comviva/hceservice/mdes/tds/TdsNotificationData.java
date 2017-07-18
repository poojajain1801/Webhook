package com.comviva.hceservice.mdes.tds;

public class TdsNotificationData {
    private String tokenUniqueReference;
    private String registrationCode2;
    private String tdsUrl;
    private String paymentAppInstanceId;

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    public String getRegistrationCode2() {
        return registrationCode2;
    }

    public void setRegistrationCode2(String registrationCode2) {
        this.registrationCode2 = registrationCode2;
    }

    public String getTdsUrl() {
        return tdsUrl;
    }

    public void setTdsUrl(String tdsUrl) {
        this.tdsUrl = tdsUrl;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

}
