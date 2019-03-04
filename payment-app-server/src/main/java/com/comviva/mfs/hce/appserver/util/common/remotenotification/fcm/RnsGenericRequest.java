package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Setter
@Getter
public class RnsGenericRequest {
    /** RNS Registration ID for the application */
    private String registrationId;

    /** Remote Notification Message type. i.e. MDES/VTS */
    private UniqueIdType idType;

    /** Remote Notification Data */
    private HashMap<String, String> rnsData;
}
