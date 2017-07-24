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

    /** This table contains all pending task which need to be completed within Remote Session with CMS-D */
    public static final String TBL_RM_PENDING_TASK = "rm_pending_task";
    /** Pending Task */
    public static final String COL_TASK_ID = "TASK_ID";
    /** Token Unique reference if application for the task */
    public static final String COL_TOKEN_UNIQUE_REFERENCE = "TOKEN_UNIQUE_REFERENCE";

    /** This table contains registration code-1 for TDS. Used in MDES case */
    public static final String TBL_TDS_REG = "tds_registration";
    /** Registration Code-01 */
    public static final String COL_TDS_REG_CODE1 = "TDS_REG_CODE1";
    /** Authentication Code */
    public static final String COL_TDS_AUTH_CODE = "TDS_AUTH_CODE";
    /** TDS Url to access for transaction history */
    public static final String COL_TDS_URL = "TDS_URL";
}
