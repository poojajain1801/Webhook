package com.comviva.mfs.promotion.constants;

/**
 * All constants
 * Created by tarkeshwar.v on 2/2/2017.
 */
public class Constants {
    public static final byte[] AES_KEY = {
            (byte)0x42, (byte)0x45, (byte)0xBB, (byte)0x09, (byte)0x53, (byte)0x4A, (byte)0xAE, (byte)0x4D,
            (byte)0xA6, (byte)0xC5, (byte)0xD5, (byte)0x2E, (byte)0x5A, (byte)0x07, (byte)0x41, (byte)0x00
    };

    public static final byte[] KEK_KEY = {
            (byte)0x42, (byte)0x45, (byte)0xBB, (byte)0x09, (byte)0x53, (byte)0x4A, (byte)0xAE, (byte)0x4D,
            (byte)0xA6, (byte)0xC5, (byte)0xD5, (byte)0x2E, (byte)0x5A, (byte)0x07, (byte)0x41, (byte)0x00
    };

    public static final byte[] CCM_NONCE = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B
    };

    public static final int LEN_TOKEN_UNIQUE_REFERENCE = 64;
    public static final int LEN_SESSION_CODE = 29;
    public static final int LEN_MAC = 8;

    // http://172.19.4.107:9099/mdes
    public static final String RESPONSE_HOST = "http://172.19.4.107:9099/mdes";
    public static final String SET_OR_CHANGE_MPIN_TASK_ID = "0f7ab62b-1b85-4670-acc5-a0ca52d0076c";//"setorchangemobilepin1234";
    public static final String  PAYMENT_APP_PROVIDER_ID = "comviva123";

    public static final int MAX_PIN_TRY_COUNTER = 3;
    public static final int MIN_PIN_LENGTH = 4;
    public static final int MAX_PIN_LENGTH = 8;

}
