package com.comviva.hceservice.digitizationApi.authentication;

/**
 * Created by tarkeshwar.v on 6/20/2017.
 */
public class OpenMobileAppParameters {
    private String paymentAppProviderId;
    private String paymentAppId;
    private String paymentAppInstanceId;
    private String tokenUniqueReference;

    public String getPaymentAppProviderId() {
        return paymentAppProviderId;
    }

    public void setPaymentAppProviderId(String paymentAppProviderId) {
        this.paymentAppProviderId = paymentAppProviderId;
    }

    public String getPaymentAppId() {
        return paymentAppId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }
}
