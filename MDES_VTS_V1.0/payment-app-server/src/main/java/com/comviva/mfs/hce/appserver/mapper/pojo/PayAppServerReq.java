package com.comviva.mfs.hce.appserver.mapper.pojo;



/**
 * Created by Tanmay.Patel on 2/2/2017.
 */

public class PayAppServerReq {
    private String serviceId;

    public PayAppServerReq(String serviceId) {
        this.serviceId = serviceId;
    }

    public PayAppServerReq() {
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
