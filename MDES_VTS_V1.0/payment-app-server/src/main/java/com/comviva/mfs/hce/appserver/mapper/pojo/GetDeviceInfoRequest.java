package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 *  GetDeviceInfo Request
 * Created by Rishikesh Kumar on 3/30/2018.
 */
@Getter
@Setter
public class GetDeviceInfoRequest {

    private String tokenUniqueReference;
    private String paymentAppInstanceId;

    public GetDeviceInfoRequest(String tokenUniqueReference, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public GetDeviceInfoRequest() {
    }
}