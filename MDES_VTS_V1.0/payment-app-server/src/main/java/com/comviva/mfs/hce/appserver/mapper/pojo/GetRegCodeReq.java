package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 5/9/2017.
 */
@Getter
public class GetRegCodeReq {
    private String tokenUniqueReference;
    private String paymentAppInstanceId;


    public GetRegCodeReq(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public GetRegCodeReq()
    {
        
    }
}
