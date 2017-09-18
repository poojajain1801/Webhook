package com.comviva.hceservice.register;

import com.comviva.hceservice.common.DeviceType;

/**
 * Device information.
 */
public class DeviceInfo {
    private String deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String imei;
    private String msisdn;
    private String nfcCapable;
    private String osName;
    private String serialNumber;
    private String osVersion;

    /**
     * Set unique device id which will never change entire lifespan of the device.
     * @param deviceId  Unique Device Id i.e. Android ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Set name of device
     * @param deviceName    Name of device e.g. Nexus6, Samsung galaxy
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Set type of device i.e. form factor.
     * @param deviceType    Device type e.g. Tablet, phone etc
     */
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Set IMEI number.
     * @param imei  IMEI number of device.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Set MSISDN number
     * @param msisdn    MSISDN number of the SIM card inserted in the device.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Set that device is NFC capable or not.
     * @param nfcCapable    <code>true </code>Device is NFC capable <br>
     *     <code>false </code>Device is not NFC capable
     */
    public void setNfcCapable(String nfcCapable) {
        this.nfcCapable = nfcCapable;
    }

    /**
     * Set OS name
     * @param osName    OS Name of device
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * Set serial number
     * @param serialNumber  Serial number of device
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Set OS Version
     * @param osVersion OS Version of the device e.g. 4.4 (Android KitKat version)
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * @return Unique Device Id i.e. Android ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @return  Name of device e.g. Nexus-6
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @return  Type of device e.g. PHONE, TABLET etc.
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * @return  Device's IMEI number
     */
    public String getImei() {
        return imei;
    }

    /**
     * @return  Device's MSISDN number
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Returns NFC capability.
     * @return <code>true </code>Device is NFC capable <br>
     *     <code>false </code>Device is not NFC capable
     */
    public String getNfcCapable() {
        return nfcCapable;
    }

    /**
     * @return  Device OS Name
     */
    public String getOsName() {
        return osName;
    }

    /**
     * @return  Device Serial Number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @return  Device OS Version
     */
    public String getOsVersion() {
        return osVersion;
    }
}
