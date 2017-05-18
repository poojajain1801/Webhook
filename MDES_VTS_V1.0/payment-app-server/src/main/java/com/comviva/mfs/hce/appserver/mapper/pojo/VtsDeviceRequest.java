package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 *  EnrollDevice Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
public class VtsDeviceRequest {

    private VtsDeviceInfoRequest deviceInfo;
    public VtsDeviceRequest(VtsDeviceInfoRequest deviceInfo) {
        this.deviceInfo=deviceInfo;
    }




    public VtsDeviceRequest() {
    }
}