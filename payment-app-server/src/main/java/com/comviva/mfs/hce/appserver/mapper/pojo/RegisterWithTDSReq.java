package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by tanmay.patel on 9/28/2018.
 */
@Getter
public class RegisterWithTDSReq {
    private String tokenUniqueReference;
    private String paymentAppInstanceId;

    public RegisterWithTDSReq(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public RegisterWithTDSReq() {
    }
}
