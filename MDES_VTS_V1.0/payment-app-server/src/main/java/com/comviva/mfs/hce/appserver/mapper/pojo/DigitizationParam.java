package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 2/2/2017.
 */
@Getter
public class DigitizationParam extends PayAppServerReq {
    String termAndConditionAcceptId;
    String termAndConditionAcceptTimeStamp;
    String paymentAppInstanceId;

    public DigitizationParam(String serviceId,
                             String termAndConditionAcceptId,
                             String termAndConditionAcceptTimeStamp,
                             String paymentAppInstanceId) {
        super(serviceId);
        this.termAndConditionAcceptId = termAndConditionAcceptId;
        this.termAndConditionAcceptTimeStamp = termAndConditionAcceptTimeStamp;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public DigitizationParam() {
    }

}