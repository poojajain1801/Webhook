package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

/**
 * Remote Notification Service.
 * Created by tarkeshwar.v on 2/14/2017.
 */
public interface RemoteNotification {
    /** Server's registration key with RNS server */
    String SERVER_KEY = "AAAAiK6Zc-I:APA91bHO5ou4YoEhJ4fYZBXA2OKVfq2vzhZ9wOYHLrsiQs2uy4IjVg2bjCls-OnmuMjivtb2cQBRzahhHvKS4gz7bNM3iH506Sfxos8502vFMwNmiT-AxE0MtJCjJn6DB1yyq38NRMpX";

    String KEY_NOTIFICATION_TYPE = "notificationType";
    String TYPE_TDS_REGISTRATION_NOTIFICATION = "notificationTdsRegistration";
    String TYPE_TDS_NOTIFICATION = "notificationTds";

    String VERSION = "1.0";
    /** Expiry duration for session code */
    int RNS_SESSION_EXPIRY_DURATION_IN_MONTH = 1;
    /** The number of seconds after which the remote management session code will expire after first use */
    int RNS_SESSION_VALIDITY_DURATION_IN_SECONDS = 3600;

    enum PENDING_ACTION {
        REGISTER,
        REQUEST_SESSION,
        PROVISION,
        NOTIFY_PROVISION_RESULT,
        CHANGE_PIN,
        DELETE,
        REPLENISH,
        GET_TASK_STATUS,
        RESET_MOBILE_PIN
    }

    /**
     * Send notification message to the target device.
     * @param rnsPostData           Remote Notification data to be posted
     * @return  Response
     */
    RnsResponse sendRns(byte[] rnsPostData);
}
