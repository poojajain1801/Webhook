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
       private String schemeType;//ALL,MDES,VTS
       private String gcmRegistrationId;
       private String clientDeviceID;
       private MdesDeviceRequest mdes;
       private VtsDeviceRequest vts;

    public EnrollDeviceRequest(String userId, String schemeType, String gcmRegistrationId, String clientDeviceID, MdesDeviceRequest mdes, VtsDeviceRequest vts) {
        this.userId = userId;
        this.schemeType = schemeType;
        this.gcmRegistrationId = gcmRegistrationId;
        this.clientDeviceID = clientDeviceID;
        this.mdes = mdes;
        this.vts = vts;
    }

    public EnrollDeviceRequest() {
    }

}