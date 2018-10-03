package com.comviva.hceservice.util;

/**
 * Created by tarkeshwar.v on 6/23/2017.
 */
public class Constants {
    public static final String DATABASE_NAME = "SDK DATABASE";
    public static final int LEN_CLIENT_DEVICE_ID = 24;
    public static final String PRESENTATION_TYPE = "NFC-HCE";
    public static final String PROTECTION_TYPE = "SOFTWARE";

    public static final String LOGGER_TAG_SDK_ERROR = "Comviva SDK Error";
    public static final String LOGGER_TAG_SERVER_ERROR = "Server Error";
    public static final byte DEFAULT_FILL_VALUE = (byte)0xFF;
    public static final String HTTP_RESPONSE_CODE_200 = "200" ;
    public static final String HTTP_RESPONSE_CODE_501 = "501" ;

    /** Shared Preference containing Configuration values */
    public static final String SHARED_PREF_CONF = "SharedPreferenceConfiguration";
    public static final String SHARED_PREF_USER_DETAILS = "SharedPreferenceUserDetails";
    public static final String SHARED_PREF_MDES_CARD_STATUS_DETAILS = "SharedPreferenceMdesCardStatusDetails";
    public static final String KEY_PAYMENT_APP_SERVER_IP = "PaymentAppServerIP";
    public static final String KEY_PAYMENT_APP_SERVER_PORT = "PaymentAppServerPort";
    public static final String KEY_CMS_D_SERVER_IP = "CmsDServerIP";
    public static final String KEY_CMS_D_SERVER_PORT = "CmsDServerPort";
    public static final String KEY_MDES_TDS_REG_STATUS = "MdesTdsRegistrationStatus";
    public static final String KEY_TDS_REG_TOKEN_UNIQUE_REF = "MdesTdsRegistrationStatus";
    public static final String KEY_HTTPS_ENABLED = "httpsEnabled";
    public static final String OS_NAME = "ANDROID";
    public static final String TOKEN_TYPE = "CLOUD";
    public static final String CARDLET_ID = "1.0";
    public static final String CONSUMER_LAN = "en";
    public static final String BROADCAST_ACTION = "comviva_broadcast";
    public static final String UNKNOWN_RESPONSE_CODE = "9000";
    public static final String SUSUPEND_USER = "SUSPENDUSER";
    public static final String DELETE_USER = "DELETEUSER";
    public static final String UNSUSPEND_USER = "UNSUSPENDUSER";
    public static final String CARD_HOLDER = "CARDHOLDER";
    public static final String TOKEN_STATUS_UPDATED = "TOKEN_STATUS_UPDATED";
    public static final String TOKEN_ADD_SUCCESS = "ADD_CARD_SUCCESS";
    public static final String UPDATE_TXN_HISTORY = "UPDATE_TXN_HISTORY";
    public static final String CMS_D_CERTIFICATE_NAME = "cms_d";
    public static final String PROVISION_CERTIFICATE_NAME = "entrust";
    public static final String[] FORCE_TLS_PROTOCOL = {"TLSv1.2"};

    public static final String CONTEXT_ROOT = "/payment-app2/";
    public static final String MESSAGE_TAG = "notificationData";
    public static final String KEY_NOTIFICATION_TYPE = "notificationType";
    public static final String OPERATION = "OPERATION";
    public static final String SUCCESS = "ADD";
    public static final String KEY_STATUS_UPDATED = "KEY_STATUS_UPDATED";
    public static final String UPDATE_CARD_META_DATA = "UPDATE_CARD_METADATA";
    public static final String TYPE_TDS_REGISTRATION_NOTIFICATION = "notificationTdsRegistration";
    public static final String TYPE_TDS_NOTIFICATION = "notificationTds";
    public static final String PAYMENT_APP_ID = "com.mahindracomvivahce.mdesvts";
    public static final String SHARED_PREF_USER = "SHARED_PREF_USER_DETAIL";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String REPLENISH_COMPLETED = "REPLENISH_COMPLETED";
    public static final String REPLENISH_FAILED = "REPLENISH_FAILED";
    public static final String DELETE_COMPLETED = "DELETE_COMPLETED";
    public static final String DELETE_FAILED = "DELETE_FAILED";
    public static final String ACTIVE = "ACTIVE";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String MARKED_FOR_DELETION = "MARKED_FOR_DELETION";
    public static final String INACTIVE = "INACTIVE";
    public static final String UNKNOWN = "UNKNOWN";

    //Certificate names

    public static final String CARD_ENCYPTION_CERTIFICATE_NAME = "nbk";
    public static final String PAYMENT_APP_CERTIFICATE = "paymentappserver";
    public static final String END_TO_END_ENCYPTION = "mycert";
    public static final String PAYMENT_CARD_NULL = "Payment Card is Null";

}
