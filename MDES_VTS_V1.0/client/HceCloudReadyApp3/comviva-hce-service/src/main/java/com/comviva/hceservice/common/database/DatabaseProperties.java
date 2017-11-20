package com.comviva.hceservice.common.database;

public class DatabaseProperties {
    public static final String DATABASE_NAME = "CommonDb";
    public static final int DATABASE_VERSION = 1;

    /** This table contains all application level common attributes */
    public static final String TBL_APP_PROPERTIES = "app_properties";
    /** true if SDK is initialized otherwise false */
    public static final String COL_INITIALIZE_STATE = "INIT_STATE";
    /** Remote Notification registration ID. It can be GCM or FCM */
    public static final String COL_RNS_ID = "RNS_REG_ID";
    /** Type of RNS i.e. GCM or FCM */
    public static final String COL_RNS_TYPE = "RNS_TYPE";
    /** Initialization state of VTS SDK */
    public static final String COL_VTS_INIT_STATE = "VTS_INIT_STATE";
    /** Initialization state of MDES SDK */
    public static final String COL_MDES_INIT_STATE = "MDES_INIT_STATE";
    /** Replenishment Thresold limit */
    public static final String COL_REPLENISH_THRESOLD_LIMIT = "REPLENISH_THRESOLD_LIMIT";
    /** High Value Transaction is supported or not */
    public static final String COL_HVT_SUPPORTED = "HVT_SUPPORT";
    /** High Value Transaction limit */
    public static final String COL_HVT_LIMIT = "HVT_LIMIT";
    /** Client Wallet Account ID */
    public static final String COL_CLIENT_WALLET_ACC_ID = "CLIENT_WALLET_ACC_ID";


    /** Token Unique reference if application for the task */
    public static final String COL_TOKEN_UNIQUE_REFERENCE = "TOKEN_UNIQUE_REFERENCE";

    /** This table contains default card */
    public static final String TBL_DEFAULT_CARD = "default_card";
    public static final String COL_CARD_UNIQUE_ID = "CARD_UNIQUE_ID";
    public static final String COL_CARD_TYPE = "CARD_TYPE";

    public static final String TBL_VISA_LUK_INFO = "visa_luk_info";
    public static final String COL_MAX_PAYMENTS = "MAX_PAYMENTS";
    public static final String COL_LUK_EXP_TS = "LUK_EXP_TS";
}
