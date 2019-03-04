package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 19-07-2017.
 */
@Getter
public class SearchTokensReq {
    private String paymentAppInstanceId;

    public SearchTokensReq(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }
    public SearchTokensReq() {

    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }
}
