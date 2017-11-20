package com.comviva.hceservice.security;


public enum DeviceStatus {
    /**
     * Device is safe i.e. no debugger, emulator rooted or tampered.
     */
    SAFE("SAFE"),

    /**
     * Device is not safe to operate SDK.
     */
    NOT_SAFE("NOT_SAFE");

    private String deviceStatus;

    /**
     * Constructor
     * @param deviceStatus Device Status
     */
    DeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    /**
     * Returns status of device
     * @return Device State
     */
    public String getDeviceStatus() {
        return deviceStatus;
    }

}
