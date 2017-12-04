package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Activate request
 * Created by tarkeshwar.v on 2/10/2017.
 */
@Getter
@Setter
public class ActivateReq {
    private String responseHost;
    private String requestId;
    private String paymentAppInstanceId;
    private String tokenUniqueReference;
    private String authenticationCode;
    private String tokenizationAuthenticationValue;

    public ActivateReq(String responseHost, String requestId, String paymentAppInstanceId, String tokenUniqueReference, String authenticationCode, String tokenizationAuthenticationValue) {
        this.responseHost = responseHost;
        this.requestId = requestId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.authenticationCode = authenticationCode;
        this.tokenizationAuthenticationValue = tokenizationAuthenticationValue;
    }

    public ActivateReq() {
    }
}
