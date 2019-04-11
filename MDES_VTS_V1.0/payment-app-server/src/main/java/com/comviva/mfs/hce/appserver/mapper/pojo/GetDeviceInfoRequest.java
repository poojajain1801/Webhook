package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 27-03-2019.
 */
@Getter
@Setter
public class GetDeviceInfoRequest {

    private String tokenUniqueReference;
    private String paymentAppInstanceId;
    private String responseHost;
    private String requestId;

    public GetDeviceInfoRequest(String tokenUniqueReference, String paymentAppInstanceId, String responseHost, String requestId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.responseHost = responseHost;
        this.requestId = requestId;
    }

    public GetDeviceInfoRequest() {
    }
}
