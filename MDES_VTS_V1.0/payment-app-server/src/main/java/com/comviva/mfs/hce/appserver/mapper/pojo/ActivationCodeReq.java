package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Activate request
 * Created by tarkeshwar.v on 2/10/2017.
 */
@Getter
@Setter
@ToString
public class ActivationCodeReq {

    private String paymentAppInstanceId;
    private String tokenUniqueReference;
    private String authenticationCodeId;

    public ActivationCodeReq() {
    }

    public ActivationCodeReq(String paymentAppInstanceId, String tokenUniqueReference, String authenticationCodeId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.authenticationCodeId = authenticationCodeId;
    }
}
