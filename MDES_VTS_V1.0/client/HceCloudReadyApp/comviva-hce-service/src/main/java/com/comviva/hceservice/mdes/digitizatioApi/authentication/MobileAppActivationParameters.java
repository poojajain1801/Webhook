package com.comviva.hceservice.mdes.digitizatioApi.authentication;

/**
 * Created by tarkeshwar.v on 6/20/2017.
 */
public class MobileAppActivationParameters {
    private String paymentAppProviderId;
    private String paymentAppInstanceId;
    private String tokenUniqueReference;
    /** The last few digits (typically four) of the Account PAN being digitized. */
    private String accountPanSuffix;
    /** The expiry of the Account PAN being digitized, given in MMYY format. */
    private String accountExpiry;

    public String getPaymentAppProviderId() {
        return paymentAppProviderId;
    }

    public void setPaymentAppProviderId(String paymentAppProviderId) {
        this.paymentAppProviderId = paymentAppProviderId;
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

    public String getAccountPanSuffix() {
        return accountPanSuffix;
    }

    public void setAccountPanSuffix(String accountPanSuffix) {
        this.accountPanSuffix = accountPanSuffix;
    }

    public String getAccountExpiry() {
        return accountExpiry;
    }

    public void setAccountExpiry(String accountExpiry) {
        this.accountExpiry = accountExpiry;
    }
}
