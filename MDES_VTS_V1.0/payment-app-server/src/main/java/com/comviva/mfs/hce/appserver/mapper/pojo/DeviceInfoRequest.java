package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  DeviceInfo Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
@AllArgsConstructor
public class DeviceInfoRequest {

    private String deviceName;
    private String formFactor;
    private String imei;
    private String id;
    private String msisdn;
    private String nfcCapable;
    private String osName;
    private String osVersion;
    private String serialNumber;
    private String storageTechnology;
    private String osType;
    private String deviceType;
    private String clientDeviceID;


    public DeviceInfoRequest() {
    }
}