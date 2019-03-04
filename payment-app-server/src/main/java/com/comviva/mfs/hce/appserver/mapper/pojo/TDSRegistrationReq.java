package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 5/10/2017.
 */
@Getter
@Setter
public class TDSRegistrationReq {
    private String tokenUniqueReference;
    private String paymentAppInstanceId;

    public TDSRegistrationReq(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public TDSRegistrationReq(){

    }
}
