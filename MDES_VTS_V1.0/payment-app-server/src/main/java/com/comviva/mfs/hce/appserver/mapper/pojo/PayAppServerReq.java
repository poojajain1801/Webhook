package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

/**
 * Created by Tanmay.Patel on 2/2/2017.
 */
@Getter
public class PayAppServerReq {
    private String serviceId;

    public PayAppServerReq(String serviceId) {
        this.serviceId = serviceId;
    }

    public PayAppServerReq() {
    }
}