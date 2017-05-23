package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 5/10/2017.
 */
@Getter
public class TDSRegistration {
    private String tokenUniqueReference;
    private String paymentAppInstanceId;

    public TDSRegistration(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public TDSRegistration(){

    }
}
