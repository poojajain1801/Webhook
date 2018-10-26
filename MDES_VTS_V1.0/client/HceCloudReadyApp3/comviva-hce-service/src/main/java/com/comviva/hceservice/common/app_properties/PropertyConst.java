package com.comviva.hceservice.common.app_properties;

/**
 * Properties that will be necessary for SDK.
 * All these properties values will be provided by mobile application.
 */
public class PropertyConst {

    private PropertyConst()
    {

    }
    /**
     * Name of the property file containing application properties like IP and ports of server
     */
    public static final String COMVIVA_HCE_PROPERTY_FILE = "ComvivaSdk.properties";

    /**
     * Name of the property file containing certificate names and passwords(keystore)
     */
    public static final String COMVIVA_HCE_CREDENTIALS_FILE = "credentials.properties";
    /**
     * Payment App Server's IP.
     */
    public static final String KEY_IP_PAY_APP_SERVER = "IP_PAY_APP_SERVER";
    /**
     * Port number for Payment Appp Server.
     */
    public static final String KEY_PORT_PAY_APP_SERVER = "PORT_PAY_APP_SERVER";

    /**
     * Certificate name .
     */
    public static final String KEY_CERT_NAME= "CERT_NAME";


    /**
     * SDK Keystore Password
     * (All Public certificates are stored in this keystore).
     */
    public static final String KEY_SDK_KEYSTORE_PASS= "SDK_KEYSTORE_PASS";


    /**
     * END_TO_END Encryption Certificate name
     * (All Public certificates are stored in this keystore).
     */
    public static final String KEY_END_TO_END_ENCYPTION= "END_TO_END_ENCYPTION";



    /**
     * Master Card Connection with CMS -D certificate name
     */
    public static final String KEY_CMS_D_CERTIFICATE_NAME= "CMS_D_CERTIFICATE_NAME";



    /**
     * Master Card Provision Certificate name
     */
    public static final String KEY_PROVISION_CERTIFICATE_NAME= "PROVISION_CERTIFICATE_NAME";


    /**
     * SDK Keystore Name
     * (All Public certificates are stored in this keystore).
     */
    public static final String KEY_SDK_KEYSTORE_NAME= "SDK_KEYSTORE_NAME";
    /**
     * CMS-d Server's IP.
     */
   // public static final String KEY_IP_CMS_D = "IP_CMS_D_SERVER";
    /**
     * Port number for CMS-d Server.
     */
   // public static final String KEY_PORT_CMS_D = "PORT_CMS_D_SERVER";
    /**
     * The Payment App Provider Id (Wallet Identifier)
     */
   // public static final String KEY_PAYMENT_APP_PROVIDER_ID = "PAYMENT_APP_PROVIDER_ID";
    /**
     * The Payment App Instance Id (Wallet Identifier)
     */
   // public static final String KEY_PAYMENT_APP_INSTANCE_ID = "PAYMENT_APP_INSTANCE_ID";
    /**
     * Unique identifier for the client application
     */
  //  public static final String KEY_CLIENT_APP_ID = "CLIENT_APP_ID";
    /**
     * Public key fingerprint for Add Card
     */
   // public static final String PUBLIC_KEY_FINGERPRINT = "PUBLIC_KEY_FINGERPRINT";

    /**
     * Public key fingerprint for Add Card
     */
   // public static final String CARDLET_ID = "CARDLET_ID";
    /**
     * Public key fingerprint for Add Card
     */
    //public static final String CERT_NAME = "CERT_NAME";

}
