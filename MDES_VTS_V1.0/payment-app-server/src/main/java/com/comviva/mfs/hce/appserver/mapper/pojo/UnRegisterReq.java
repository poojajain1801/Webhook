package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;

@Getter
@Setter
public class UnRegisterReq {
    private String imei ;
    private String userID;
    private String paymentAppInstanceId;

    public UnRegisterReq(String imei, String userID, String paymentAppInstanceId) {
        this.imei = imei;
        this.userID = userID;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public UnRegisterReq(){

    }
}
