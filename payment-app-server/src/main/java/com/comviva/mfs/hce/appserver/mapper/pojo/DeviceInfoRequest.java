package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 *  DeviceInfo Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
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

    public DeviceInfoRequest(String deviceName, String formFactor, String imei, String id, String msisdn, String nfcCapable, String osName, String osVersion, String serialNumber, String storageTechnology) {
        this.deviceName = deviceName;
        this.formFactor = formFactor;
        this.imei = imei;
        this.id = id;
        this.msisdn = msisdn;
        this.nfcCapable = nfcCapable;
        this.osName = osName;
        this.osVersion = osVersion;
        this.serialNumber = serialNumber;
        this.storageTechnology = storageTechnology;
    }

    public DeviceInfoRequest() {
    }
}