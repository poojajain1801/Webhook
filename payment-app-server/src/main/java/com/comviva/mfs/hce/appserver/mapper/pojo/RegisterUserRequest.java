package com.comviva.mfs.hce.appserver.mapper.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
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
@ApiModel("RegisterUserRequest")
public class RegisterUserRequest {

    @NotEmpty
    @Length(max = 60, min = 2)
    @ApiModelProperty(notes = "the id of the item", required = true)
    private String userId;
    @ApiModelProperty(notes = "clientDeviceID is to Identify Device")
    private String clientDeviceID;
    @ApiModelProperty(notes = "imei of the Device")
    private String imei;
    @ApiModelProperty(notes = "OS of the Device")
    private String os_name;
    @ApiModelProperty(notes = "Model of the  Device")
    private String device_model;
    @ApiModelProperty(notes = "languageCode to Identify the user Language")
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