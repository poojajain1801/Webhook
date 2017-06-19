package com.comviva.hceservice.register;


public class DeviceInfo {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String imei;
    private String msisdn;
    private String nfcCapable;
    private String osName;
    private String serialNumber;
    private String osVersion;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setNfcCapable(String nfcCapable) {
        this.nfcCapable = nfcCapable;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getImei() {
        return imei;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getNfcCapable() {
        return nfcCapable;
    }

    public String getOsName() {
        return osName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getOsVersion() {
        return osVersion;
    }
}
