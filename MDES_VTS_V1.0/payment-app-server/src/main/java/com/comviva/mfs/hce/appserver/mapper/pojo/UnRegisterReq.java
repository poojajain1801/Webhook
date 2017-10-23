package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;

@Getter
@Setter
public class UnRegisterReq {
    private String paymentAppInstanceId;
    private String clientDeviceID;
    private String imei;
    private String userId;

    public UnRegisterReq(String paymentAppInstanceId, String clientDeviceID, String imei, String userId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.clientDeviceID = clientDeviceID;
        this.imei = imei;
        this.userId = userId;
    }

    public UnRegisterReq(){

    }
}
