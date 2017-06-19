package com.comviva.hceservice.fcm;

public class RnsInfo {
    private String registrationId;
    private RNS_TYPE rnsType;

    public enum RNS_TYPE {
        GCM, FCM
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public RNS_TYPE getRnsType() {
        return rnsType;
    }

    public void setRnsType(RNS_TYPE rnsType) {
        this.rnsType = rnsType;
    }


}
