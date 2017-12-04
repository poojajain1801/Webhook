package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Activate request
 * Created by tarkeshwar.v on 2/10/2017.
 */
@Getter
@Setter
public class ActivationCodeReq {
    private String responseHost;
    private String requestId;
    private String paymentAppInstanceId;
    private String tokenUniqueReference;
    private AuthenticationMethod authenticationMethod;

    public ActivationCodeReq() {
    }

    public ActivationCodeReq(String responseHost,
                             String requestId,
                             String paymentAppInstanceId,
                             String tokenUniqueReference,
                             AuthenticationMethod authenticationMethod) {
        this.responseHost = responseHost;
        this.requestId = requestId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.authenticationMethod = authenticationMethod;
    }


}
