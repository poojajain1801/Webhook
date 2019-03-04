package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tanmay.Patel on 5/9/2017.
 */
@Getter
@Setter
public class NotifyTransactionDetailsReq {
    private String responseHost;
    private String requestId;
    private String tokenUniqueReference;
    private String registrationCode2;
    private String tdsUrl;
    private String paymentAppInstanceId;

    public NotifyTransactionDetailsReq(String requestId ,String tokenUniqueReference, String registrationCode2, String tdsUrl, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.registrationCode2 = registrationCode2;
        this.tdsUrl = tdsUrl;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.requestId = requestId;
    }
    public NotifyTransactionDetailsReq()
    {
        //Thsi is a Default Constructor.

    }
}
