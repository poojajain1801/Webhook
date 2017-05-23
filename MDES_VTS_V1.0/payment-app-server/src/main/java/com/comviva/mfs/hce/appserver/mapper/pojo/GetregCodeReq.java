package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 5/9/2017.
 */
@Getter
public class GetregCodeReq {
    private String tokenUniqueReference;
    private String paymentAppInstanceId;


    public GetregCodeReq(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public GetregCodeReq()
    {
        
    }
}
