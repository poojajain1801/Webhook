package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 *  MdesDevice Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
public class MdesDeviceRequest {

    private DeviceInfoRequest deviceInfo;
    private String mobilePin;
    private String paymentAppId;
    private String paymentAppInstanceId;
    private String publicKeyFingerprint;
    private String deviceFingerprint;
    private String rgk;

    public MdesDeviceRequest(DeviceInfoRequest deviceInfo, String mobilePin, String paymentAppId,
                             String paymentAppInstanceId, String publicKeyFingerprint, String deviceFingerprint, String rgk) {

        this.deviceInfo=deviceInfo;
        this.mobilePin=mobilePin;
        this.paymentAppId=paymentAppId;
        this.paymentAppInstanceId=paymentAppInstanceId;
        this.publicKeyFingerprint=publicKeyFingerprint;
        this.deviceFingerprint = deviceFingerprint;
        this.rgk=rgk;
    }
    public MdesDeviceRequest() {
    }
}