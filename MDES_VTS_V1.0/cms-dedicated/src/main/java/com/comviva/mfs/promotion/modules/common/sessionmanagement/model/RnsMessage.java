package com.comviva.mfs.promotion.modules.common.sessionmanagement.model;

import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.PaymentAppRegistrationData;

/**
 * Remote Notification Service data.
 * Created by tarkeshwar.v on 2/16/2017.
 */
public class RnsMessage {
    private String responseHost;
    private PaymentAppRegistrationData registrationData;
    private String mobileKeysetId;
    private String encryptedData;

    /** Non Parameterize constructor */
    public RnsMessage() {
    }

    /**
     * Parameterize constructor
     *
     * @param responseHost Remote Host.
     * @param registrationData Registration data.
     * @param mobileKeysetId Mobile key set id.
     * @param encryptedData encrypted data.
     */
    public RnsMessage(String responseHost, PaymentAppRegistrationData registrationData,
                      String mobileKeysetId, String encryptedData) {
        this.responseHost = responseHost;
        this.registrationData = registrationData;
        this.mobileKeysetId = mobileKeysetId;
        this.encryptedData = encryptedData;
    }
 }
