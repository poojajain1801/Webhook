package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * RegisterUserRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class RegisterUserRequest {

    private String userId;
    private String clientDeviceID;
    private String imei;
    private String os_name;
    private String device_model;


    public RegisterUserRequest(String userId,String clientDeviceID,String imei,String os_name,String device_model) {

        this.userId=userId;
        this.clientDeviceID=clientDeviceID;
        this.imei=imei;
        this.os_name=os_name;
        this.device_model=device_model;
    }
    public RegisterUserRequest() {
    }
}