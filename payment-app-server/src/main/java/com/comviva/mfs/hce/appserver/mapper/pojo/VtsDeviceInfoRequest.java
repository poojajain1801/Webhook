package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 *  VtsDeviceInfo Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
public class VtsDeviceInfoRequest {

    public VtsDeviceInfoRequest(String osType, String osVersion, String osBuildID, String deviceType,
                                String deviceIDType, String deviceManufacturer, String deviceBrand, String deviceModel,
                                String deviceName, String hostDeviceID, String phoneNumber) {
        this.osType = osType;
        this.osVersion = osVersion;
        this.osBuildID = osBuildID;
        this.deviceType = deviceType;
        this.deviceIDType = deviceIDType;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.deviceName = deviceName;
        this.hostDeviceID = hostDeviceID;
        this.phoneNumber = phoneNumber;
    }
    private String osType;
    private String osVersion;
    private String osBuildID;
    private String deviceType;
    private String deviceIDType;
    private String deviceManufacturer;
    private String deviceBrand;
    private String deviceModel;
    private String deviceName;
    private String hostDeviceID;
    private String phoneNumber;

    public VtsDeviceInfoRequest() {
    }
}