package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 19-07-2017.
 */
@Getter
@Setter
public class SearchTokensReq {
    private String paymentAppInstanceId;
    private CardInfo cardInfo ;
    private String tokenRequestorId ;

    public SearchTokensReq(String paymentAppInstanceId, CardInfo cardInfo , String tokenRequestorId) {

        this.paymentAppInstanceId = paymentAppInstanceId;
        this.cardInfo = cardInfo ;
        this.tokenRequestorId = tokenRequestorId;
    }
    public SearchTokensReq() {

    }
}
