package com.comviva.hceservice.fcm;

/**
 * Remote Notification Information. Remote notification service used may be GCM or FCM.
 */
public class RnsInfo {
    private String registrationId;
    private RNS_TYPE rnsType;

    /**
     * Type of Remote Notification Service.
     * <list>
     *     <li>GCM - Google Cloud Messaging Service</li>
     *     <li>FCM - Firebase Cloud Messaging Service</li>
     * </list>
     */
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

    public static RNS_TYPE getRnsType(String rnsType) {
        if(rnsType != null && rnsType.equalsIgnoreCase(RNS_TYPE.GCM.name())) {
            return RNS_TYPE.GCM;
        }
        if(rnsType != null && rnsType.equalsIgnoreCase(RNS_TYPE.FCM.name())) {
            return RNS_TYPE.FCM;
        }
        return null;
    }
}
