package com.comviva.hceservice.register;

/***
 * Registration parameter object.
 */
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

    /**
     * Returns device registration token of FCM.
     * @return  device registration token
     */
    public String getFcmRegistrationId() {
        return fcmRegistrationId;
    }

    /**
     * Set device registration token of FCM.
     * @param fcmRegistrationId device registration token
     */
    public void setFcmRegistrationId(String fcmRegistrationId) {
        this.fcmRegistrationId = fcmRegistrationId;
    }

    /**
     * Get Mobile PIN which is used at time of payment.
     * @return  Mobile PIN
     */
    public String getMobilePin() {
        return mobilePin;
    }

    /**
     * Set Mobile PIN.
     * @param mobilePin Mobile PIN
     */
    public void setMobilePin(String mobilePin) {
        this.mobilePin = mobilePin;
    }

    /**
     * Payment App Instance Id which is unique identifier of the application instance.
     * @return Payment App Instance Id
     */
    public String getPaymentAppId() {
        return paymentAppId;
    }

    /**
     * Set Payment App Instance Id which is Identifier for the specific Mobile Payment App instance
     * @param paymentAppId  Payment App Instance Id
     */
    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    /**
     * Returns Payment App Instance Id
     * @return  Payment App Instance Id
     */
    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    /**
     * Set Payment App Instance Id
     * @param paymentAppInstanceId  Payment App Instance Id
     */
    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    /**
     * Returns Public Key Fingerprint which uniquely identifies public key.
     * @return  Public Key Fingerprint
     */
    public String getPublicKeyFingerprint() {
        return publicKeyFingerprint;
    }

    /**
     * Sets Public Key Fingerprint
     * @param publicKeyFingerprint  Public Key Fingerprint
     */
    public void setPublicKeyFingerprint(String publicKeyFingerprint) {
        this.publicKeyFingerprint = publicKeyFingerprint;
    }

    /**
     * Returns Randomly Generated Key used to encrypt Mobile PIN.
     * @return Randomly Generated Key
     */
    public String getRgk() {
        return rgk;
    }

    /**
     * Sets Randomly Generated Key
     * @param rgk   Randomly Generated Key
     */
    public void setRgk(String rgk) {
        this.rgk = rgk;
    }

    /**
     * Returns User Id.
     * @return User Id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set User Id
     * @param userId    User Id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns Activation code to activate user.
     * @return  Activation code
     */
    public String getActivationCode() {
        return activationCode;
    }

    /**
     * Set Activation code
     * @param activationCode    Activation code
     */
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    /**
     * Returns Device information.
     * @return Device information
     */
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * Set Device information
     * @param deviceInfo Device information
     */
    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    /**
     * Returns Device name
     * @return Device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Set Device name
     * @param deviceName    Device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
