package com.comviva.mfs.promotion.modules.device_management.model;

import com.comviva.mfs.promotion.modules.device_management.domain.DeviceInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Object to contain all device registration inputs.
 * Created by tarkeshwar.v on 1/10/2017.
 */
@Getter
@Setter

public class RegDeviceParam {
    /* Information entered by the user */
    private String userId;
    private String activationCode;
    private String mobilePin;

    // Information extracted by MPA sdk
    private String paymentAppId;
    private String paymentAppInstanceId;
    private String publicKeyFingerprint;
    private String rgk;
    private String gcmRegistrationId;
    private DeviceInfo deviceInfo;

    public RegDeviceParam(String userId, String activationCode, String mobilePin, String paymentAppId, String paymentAppInstanceId, String publicKeyFingerprint, String rgk, String gcmRegistrationId, DeviceInfo deviceInfo) {
        this.userId = userId;
        this.activationCode = activationCode;
        this.mobilePin = mobilePin;
        this.paymentAppId = paymentAppId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.rgk = rgk;
        this.gcmRegistrationId = gcmRegistrationId;
        this.deviceInfo = deviceInfo;
    }

    public RegDeviceParam() {
    }
}
