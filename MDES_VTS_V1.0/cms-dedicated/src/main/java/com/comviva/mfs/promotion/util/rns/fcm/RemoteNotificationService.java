package com.comviva.mfs.promotion.util.rns.fcm;

/**
 * Remote Notification Service.
 * Created by tarkeshwar.v on 2/14/2017.
 */
public interface RemoteNotificationService {
    /** Server's registration key with RNS server */
    String SERVER_KEY = "AAAAjxiVr-o:APA91bHPswyHutJAK1qBAYfjXctQu9n-Y8woGt_HTZZbbd8M0-yLVAVEyMMnqMu3_9WNCXrdV_HNG5ra0sEe6EYUMXj52RruhOb8PDwa_id5goRBKuGJSEktQ-CmNV3d4LdPfmcAiYpo";

    String VERSION = "1.0";
    /** Expiry duration for session code */
    int RNS_SESSION_EXPIRY_DURATION_IN_MONTH = 1;
    /** The number of seconds after which the remote management session code will expire after first use */
    int RNS_SESSION_VALIDITY_DURATION_IN_SECONDS = 3600;

    enum PENDING_ACTION {
        PROVISION, RESET_MOBILE_PIN
    }

    /**
     * Send notification message to the target device.
     * @param deviceRegistrationId  Device registration id
     * @param rnsPostData           Remote Notification data to be posted
     * @return  Response
     */
    RnsResponse sendRns(String deviceRegistrationId, byte[] rnsPostData);
}
