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


    public RegisterUserRequest(String userId,String clientDeviceID) {

        this.userId=userId;
        this.clientDeviceID=clientDeviceID;
    }

    public RegisterUserRequest() {
    }
}