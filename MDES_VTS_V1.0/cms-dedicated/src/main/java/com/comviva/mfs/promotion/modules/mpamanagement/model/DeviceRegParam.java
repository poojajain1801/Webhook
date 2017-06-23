package com.comviva.mfs.promotion.modules.mpamanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Object to contain all device registration inputs.
 * Created by tarkeshwar.v on 1/10/2017.
 */
@Getter
@Setter
public class DeviceRegParam {
    private String paymentAppId;
    private String paymentAppInstanceId;
    private RnsInfo rnsInfo;
    private String publicKeyFingerprint;
    private String rgk;
    private String deviceFingerprint;
    private String newMobilePin;

    public DeviceRegParam(String paymentAppId,
                          String paymentAppInstanceId,
                          RnsInfo rnsInfo,
                          String publicKeyFingerprint,
                          String rgk,
                          String deviceFingerprint,
                          String newMobilePin) {
        this.paymentAppId = paymentAppId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.rnsInfo = rnsInfo;
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.rgk = rgk;
        this.deviceFingerprint = deviceFingerprint;
        this.newMobilePin = newMobilePin;

    }

    public DeviceRegParam()
    {
        this(null, null, null, null, null, null, null);
    }

}
