package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 2/2/2017.
 */
@Getter
public class DigitizationParam extends PayAppServerReq {
    private String termAndConditionAssetId;
    private String termAndConditionAcceptTimeStamp;
    private String paymentAppInstanceId;

    public DigitizationParam(String serviceId,
                             String termAndConditionAssetId,
                             String termAndConditionAcceptTimeStamp,
                             String paymentAppInstanceId) {
        super(serviceId);
        this.termAndConditionAssetId = termAndConditionAssetId;
        this.termAndConditionAcceptTimeStamp = termAndConditionAcceptTimeStamp;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public DigitizationParam() {
    }

}
