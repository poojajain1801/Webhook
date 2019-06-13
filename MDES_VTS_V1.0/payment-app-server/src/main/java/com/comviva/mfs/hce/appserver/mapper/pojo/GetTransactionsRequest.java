package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tanmay.patel on 9/28/2018.
 */
@Getter
@Setter
public class GetTransactionsRequest {

    private String tokenUniqueReference;
    private String paymentAppInstanceId;

    public GetTransactionsRequest(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public GetTransactionsRequest() {

    }
}
