package com.comviva.mfs.hce.appserver.mapper.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * RegisterUserRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class RegisterUserRequest {

    @NotEmpty
    @Length(max = 60, min = 2)
    private String userId;
    private String clientDeviceID;
    private String imei;
    private String os_name;
    private String device_model;
    private String languageCode;


    public RegisterUserRequest(String userId, String clientDeviceID, String imei, String os_name, String device_model, String languageCode) {
        this.userId = userId;
        this.clientDeviceID = clientDeviceID;
        this.imei = imei;
        this.os_name = os_name;
        this.device_model = device_model;
        this.languageCode = languageCode;
    }

    public RegisterUserRequest() {
    }
}