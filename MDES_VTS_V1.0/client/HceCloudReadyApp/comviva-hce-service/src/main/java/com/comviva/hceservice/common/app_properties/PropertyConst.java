package com.comviva.hceservice.common.app_properties;

/**
 * Properties that will be necessary for SDK.
 * All these properties values will be provided by mobile application.
 */
public class PropertyConst {
    /**
     * Name of the property file containing application properties like IP and ports of server
     */
    public static final String COMVIVA_HCE_PROPERTY_FILE = "ComvivaSdk.properties";
    /**
     * Payment App Server's IP.
     */
    public static final String KEY_IP_PAY_APP_SERVER = "IP_PAY_APP_SERVER";
    /**
     * Port number for Payment Appp Server.
     */
    public static final String KEY_PORT_PAY_APP_SERVER = "PORT_PAY_APP_SERVER";
    /**
     * CMS-d Server's IP.
     */
    public static final String KEY_IP_CMS_D = "IP_CMS_D_SERVER";
    /**
     * Port number for CMS-d Server.
     */
    public static final String KEY_PORT_CMS_D = "PORT_CMS_D_SERVER";
    /**
     * The Payment App Provider Id (Wallet Identifier)
     */
    public static final String KEY_PAYMENT_APP_PROVIDER_ID = "PAYMENT_APP_PROVIDER_ID";

    public static final String KEY_CLIENT_APP_ID = "CLIENT_APP_ID";

    public static final String KEY_CLIENT_WALLET_ACCOUNT_ID = "CLIENT_WALLET_ACCOUNT_ID";

}
