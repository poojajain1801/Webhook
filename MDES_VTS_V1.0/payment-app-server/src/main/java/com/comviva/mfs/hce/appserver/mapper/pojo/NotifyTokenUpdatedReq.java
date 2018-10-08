package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 06-08-2018.
 */
@Getter
@Setter
public class NotifyTokenUpdatedReq {
    private EncryptedPayload encryptedPayload;
    private  String responseHost;
    private String requestId;

    public NotifyTokenUpdatedReq(EncryptedPayload encryptedPayload, String responseHost, String requestId) {
        this.encryptedPayload = encryptedPayload;
        this.responseHost = responseHost;
        this.requestId = requestId;
    }

    public NotifyTokenUpdatedReq(){

    }
}

