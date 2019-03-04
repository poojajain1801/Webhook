package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Getter
@ToString
@EqualsAndHashCode
public class DeviceRegistrationResponse {
    private final Map response;

    public DeviceRegistrationResponse(Map response) {
        this.response = response;
    }
}
