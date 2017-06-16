package com.comviva.mfs.promotion.modules.mobilepaymentapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Request parameters for RequestSession API coming from MPA.
 * Created by tarkeshwar.v on 2/13/2017.
 */
@Getter
@Setter
public class RequestSession {
    private String paymentAppProviderId;
    private String paymentAppInstanceId;
    private String mobileKeysetId;

    public RequestSession(String paymentAppProviderId, String paymentAppInstanceId, String mobileKeysetId) {
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.mobileKeysetId = mobileKeysetId;
    }

    public RequestSession() {
    }
}
