package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 *  EnrollDevice Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
public class EnrollDeviceRequest {

       private String userId;
       private String gcmRegistrationId;
       private MdesDeviceRequest mdes;
       private VtsDeviceRequest vts;

    public EnrollDeviceRequest(String userId,String gcmRegistrationId,MdesDeviceRequest mdes,VtsDeviceRequest vts) {
        this.userId=userId;
        this.gcmRegistrationId=gcmRegistrationId;
        this.mdes=mdes;
        this.vts=vts;
    }
    public EnrollDeviceRequest() {
    }
}