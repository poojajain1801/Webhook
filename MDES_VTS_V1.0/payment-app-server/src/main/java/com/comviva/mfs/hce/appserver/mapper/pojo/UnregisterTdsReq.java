package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 05-10-2018.
 */

@Getter
@Setter
public class UnregisterTdsReq {

    private String tokenUniqueReference ;
    private String paymentAppInstanceId ;

    public UnregisterTdsReq(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public UnregisterTdsReq() {

    }
}
