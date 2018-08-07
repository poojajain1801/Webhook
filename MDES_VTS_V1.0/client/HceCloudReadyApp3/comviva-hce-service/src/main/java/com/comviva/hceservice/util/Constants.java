package com.comviva.hceservice.util;

/**
 * Created by tarkeshwar.v on 6/23/2017.
 */
public class Constants {
    public static final int LEN_CLIENT_DEVICE_ID = 24;
    public static final String PRESENTATION_TYPE = "NFC-HCE";
    public static final String PROTECTION_TYPE = "SOFTWARE";

    public static final String LOGGER_TAG_SDK_ERROR = "Comviva SDK Error";
    public static final String LOGGER_TAG_SERVER_ERROR = "Server Error";
    public static final byte DEFAULT_FILL_VALUE = (byte)0xFF;
    public static final String HTTP_SUCCESS_RESPONSE_CODE = "200" ;

    /** Shared Preference containing Configuration values */
    public static final String SHARED_PREF_CONF = "SharedPreferenceConfiguration";
    public static final String KEY_PAYMENT_APP_SERVER_IP = "PaymentAppServerIP";
    public static final String KEY_PAYMENT_APP_SERVER_PORT = "PaymentAppServerPort";
    public static final String KEY_CMS_D_SERVER_IP = "CmsDServerIP";
    public static final String KEY_CMS_D_SERVER_PORT = "CmsDServerPort";
    public static final String KEY_MDES_TDS_REG_STATUS = "MdesTdsRegistrationStatus";
    public static final String KEY_TDS_REG_TOKEN_UNIQUE_REF = "MdesTdsRegistrationStatus";
}
