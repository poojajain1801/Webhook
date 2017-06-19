package com.comviva.hceservice.register;

public class RegisterParam {
    private String userId;
    private String activationCode;
    private String fcmRegistrationId;
    private String mobilePin;
    private String paymentAppId;
    private String paymentAppInstanceId;
    private String publicKeyFingerprint;
    private String rgk;
    private DeviceInfo deviceInfo;
    private String deviceName;

    public String getFcmRegistrationId() {
        return fcmRegistrationId;
    }

    public void setFcmRegistrationId(String fcmRegistrationId) {
        this.fcmRegistrationId = fcmRegistrationId;
    }

    public String getMobilePin() {
        return mobilePin;
    }

    public void setMobilePin(String mobilePin) {
        this.mobilePin = mobilePin;
    }

    public String getPaymentAppId() {
        return paymentAppId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getPublicKeyFingerprint() {
        return publicKeyFingerprint;
    }

    public void setPublicKeyFingerprint(String publicKeyFingerprint) {
        this.publicKeyFingerprint = publicKeyFingerprint;
    }

    public String getRgk() {
        return rgk;
    }

    public void setRgk(String rgk) {
        this.rgk = rgk;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
