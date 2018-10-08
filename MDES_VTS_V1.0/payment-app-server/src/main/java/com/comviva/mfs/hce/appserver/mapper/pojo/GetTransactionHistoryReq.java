package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 5/11/2017.
 */
@Getter
public class GetTransactionHistoryReq {
    private String tokenUniqueReference;
    private String paymentAppInstanceId;


    public GetTransactionHistoryReq(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public GetTransactionHistoryReq()
    {
        //This is a default constructor.
    }
}

