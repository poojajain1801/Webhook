package com.comviva.hceservice.register;

import com.comviva.hceservice.common.SchemeType;

/***
 * Registration parameter object.
 */
public class RegisterParam {
    private String userId;
    private String mobilePin;
    private String paymentAppId;
    private String publicKeyFingerprint;
    private String deviceName;
    private SchemeType schemeType;

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

    /**
     * Returns type of schemes/schemes for which registration is needed.
     * @return Scheme Type
     */
    public SchemeType getSchemeType() {
        return schemeType;
    }

    /**
     * Set type of schemes/schemes for which registration is needed.
     * @param schemeType Scheme Type
     */
    public void setSchemeType(SchemeType schemeType) {
        this.schemeType = schemeType;
    }
}
