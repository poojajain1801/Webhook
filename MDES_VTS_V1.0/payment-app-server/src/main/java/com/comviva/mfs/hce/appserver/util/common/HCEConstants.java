package com.comviva.mfs.hce.appserver.util.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by tanmaya.patel on 22-08-2017.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class HCEConstants {

    /** The Constant SERVICE_FAILED. */
    public static final String SERVICE_FAILED = "SERVICE_FAILED";
    /** The Constant RESPONSE_CODE. */
    public static final String RESPONSE_CODE = "responseCode";
    public static final String ERROR_RESPONSE = "errorResponse";

    public static final String SUCCESS = "SUCCESS";
    /** The Constant MESSAGE. */
    public static final String MESSAGE = "message";
    /** The Constant USER_DETAILS. */
    public static final String USER_DETAILS = "userDetails";
    /** The Constant ACTIVATION_CODE. */
    public static final String ACTIVATION_CODE = "activationCode";
    /** The Constant ACTIVE. */
    public static final String ACTIVE = "Y";
    /** The Constant INACTIVE. */
    public static final String INACTIVE = "N";

    /** The Constant SUSUPEND. */
    public static final String SUSUPEND = "S";

    public static final String APPROVED = "APPROVED";

    public static final String DECLINED = "DECLINED";

    /** The Constant SUSUPEND. */
    public static final String SUSUPEND_USER = "SUSPENDUSER";
    /** The Constant SUSUPEND. */
    public static final String DELETE_USER = "DELETEUSER";
    /** The Constant SUSUPEND. */
    public static final String UNSUSPEND_USER = "UNSUSPENDUSER";

    /** The Constant INITIATE. */
    public static final String INITIATE = "I";
       /** The Constant CHANGE_DEVICE. */
    public static final String CHANGE_DEVICE ="CD";
    /** The Constant USER_PREFIX. */
    public static final String USER_PREFIX = "PT";

    /** The Constant CHANGE_DEVICE. */
    public static final String INTERNAL_SERVER_ERROR ="Inernal server error";
    public static final String DEFAULT_LANGAUAGE_CODE = "1";
    /** The Constant START_TIME. */
    public static final String START_TIME = "startTime";
    /** The Constant REQUEST_OBJECT. */
    public static final String REQUEST_OBJECT = "requestObject";
    /** The Constant RESPONSE_OBJECT. */
    public static final String RESPONSE_OBJECT = "responseObject";

    public static final String TOKEN_CREATED = "TOKEN_CREATED";
    public static final String TOKEN_STATUS_UPDATED = "TOKEN_STATUS_UPDATED";
    public static final String KEY_STATUS_UPDATED = "KEY_STATUS_UPDATED";
    public static final String OPERATION = "OPERATION";
    public static final String UPDATE_CARD_METADATA = "UPDATE_CARD_METADATA";
    public static final String UPDATE_TXN_HISTORY = "UPDATE_TXN_HISTORY";
    /** The Constant MDES_RESPONSE_CODE. */
    public static final String MDES_RESPONSE_CODE = "mdesResponseCode";
    /** The Constant MDES_RESPONSE_CODE. */
    public static final String MDES_MESSAGE = "mdesMessage";
    /** The Constant MDES_FINAL_CODE. */
    public static final String MDES_FINAL_CODE = "mdesFinalCode";
    /** The Constant MDES_FINAL_MESSAGE. */
    public static final String MDES_FINAL_MESSAGE = "mdesFinalMessage";
    /** The Constant MDES_RESPONSE_MAP. */
    public static final String MDES_RESPONSE_MAP = "mdes";

    /** The Constant VTS_MESSAGE. */
    public static final String VTS_MESSAGE = "vtsMessage";
    /** The Constant VTS_RESPONSE_CODE. */
    public static final String VTS_RESPONSE_CODE = "vtsResponseCode";
    /** The Constant VISA_FINAL_CODE. */
    public static final String VISA_FINAL_CODE = "visaFinalCode";
    /** The Constant VISA_FINAL_MESSAGE. */
    public static final String VISA_FINAL_MESSAGE = "visaFinalMessage";
    /** The Constant VTS_RESPONSE_MAP. */
    public static final String VTS_RESPONSE_MAP = "vts";
    /** The Constant STATUS_CODE. */
    public static final String STATUS_CODE = "statusCode";
    /** The Constant STATUS_MESSAGE. */
    public static final String STATUS_MESSAGE = "statusMessage";

    /** The Constant CLIENT_WALLET_ACCOUNT_ID. */
    public static final String CLIENT_WALLET_ACCOUNT_ID = "clientWalletAccountId";


    /** The Constant DEVICE_NAME. */
    public static final String DEVICE_NAME = "deviceName";

    /** The Constant CLIENT_WALLET_ACCOUNT_ID. */
    public static final String CARD_PREFIX = "PI";


    /** The Constant CARD_ID. */
    public static final String CARD_ID = "cardId";
    /** The Constant CARD_IDENTIFIER. */
    public static final String CARD_IDENTIFIER = "cardIdentifier";
    /** The Constant CARD_SUFFIX. */
    public static final String CARD_SUFFIX = "cardSuffix";
    /** The Constant CARD_TYPE. */
    public static final String CARD_TYPE = "cardType";
    /** The Constant PAN_UNIQUE_REFERENCE. */
    public static final String PAN_UNIQUE_REFERENCE = "panUniqueReference";

    /** The Constant MASTER_TOKEN_UNIQUE_REFERENCE. */
    public static final String MASTER_TOKEN_UNIQUE_REFERENCE = "masterTokenUniqueReference";

    /** The Constant VISA_PROVISION_TOKENID. */
    public static final String VISA_PROVISION_TOKENID = "visaProvisionTokenId";

    /** The Constant STATUS. */
    public static final String STATUS = "status";
    /** The Constant REPLENISH_ON. */
    public static final String REPLENISH_ON = "replenishOn";
    /** The Constant TOKEN_SUFFIX. */
    public static final String TOKEN_SUFFIX = "tokenSuffix";
    /** The Constant CLIENT_DEVICE_ID. */
    public static final String CLIENT_DEVICE_ID = "clientDeviceId";
    /** The Constant CREATED_ON. */
    public static final String CREATED_ON = "createdOn";
    /** The Constant DEVICE_MODEL. */
    public static final String DEVICE_MODEL = "deviceModel";
    /** The Constant HOST_DEVICE_ID. */
    public static final String HOST_DEVICE_ID = "hostDeviceId";
    /** The Constant IMEI. */
    public static final String IMEI = "imei";
    /** The Constant IS_MASTER_CARD_ENABLED. */
    public static final String IS_MASTER_CARD_ENABLED = "isMasterCardEnabled";

    /** The Constant IS_VISA_ENABLED. */
    public static final String IS_VISA_ENABLED = "isVisaEnabled";
    /** The Constant MODIFIED_ON. */
    public static final String MODIFIED_ON = "modifiedOn";
    /** The Constant NFC_CAPABLE. */
    public static final String NFC_CAPABLE = "nfcCapable";
    /** The Constant OS_NAME. */
    public static final String OS_NAME = "osName";

    /** The Constant OS_VERSION. */
    public static final String OS_VERSION = "osVersion";

    /** The Constant USER_ID. */
    public static final String USER_ID = "userId";
    /** The Constant CARD_DETAILS_LIST. */
    public static final String CARD_DETAILS_LIST = "cardDetailsList";

    /** The Constant NOT_AVAILABLE. */
    public static final String NOT_AVAILABLE = "NA";

    /** The Constant TOTAL_COUNT. */
    public static final String TOTAL_COUNT = "totalCount";

    /** The Constant VISA. */
    public static final String VISA = "VISA";

    /** The Constant MASTERCARD. */
    public static final String MASTERCARD = "MASTERCARD";

    /** The Constant REQUEST_ID_PREFIX. */
    public static final String REQUEST_ID_PREFIX = "RI";

    /** The Constant REQUEST_ID_. */
    public static final String REQUEST_ID = "requestId";

    /** The Constant REQUEST_ID_. */
    public static final String GENERIC_ERROR = "We Are Unable To Process Your Transaction. For Further Assistance Please Contact 1801801";

    /** The Constant MASKING_PROPERTIES. */
    private static String MASKING_PROPERTIES ;
    @Value("${masking.properties}")
    public void setMaskingProperties(String maskingProperties){
        MASKING_PROPERTIES = maskingProperties;
    }

    public static String getMaskingProperties() {
        return MASKING_PROPERTIES;
    }

    /** The Constant MASKING_PARAM_REGEX. */
    private static String MASKING_PARAM_REGEX;
    @Value("${masking.param.regex}")
    public void setMaskingParamRegex(String maskingParamRegex){
        MASKING_PARAM_REGEX = maskingParamRegex;
    }

    public static String getMaskingParamRegex() {
        return MASKING_PARAM_REGEX;
    }

    /** The Constant MASK_TYPE_PRE. */
    public static final String MASK_TYPE_PRE = "PRE";

    /** The Constant UNMASK_TYPE_PRE. */
    public static final String UNMASK_TYPE_PRE = "UPRE";

    /** The Constant UNMASK_TYPE_POST. */
    public static final String UNMASK_TYPE_POST = "UPOST";
    /** The Constant MASK_TYPE_POST. */
    public static final String MASK_TYPE_POST = "POST";

    public static final String FAILURE = "FAILURE";

    public static final String NULL = "null";


    //By Rishikesh
    public static final String REASON_CODE = "reasonCode";
    public static final String REASON = "reason";
    public static final String ERROR_CODE= "errorCode";
    public static final String REASON_DESCRIPTION = "reasonDescription";
    public static final int REASON_CODE1 = 220;
    public static final String REASON_DESCRIPTION1 = "Invalid Payment App Instance Id";
    public static final int REASON_CODE2 = 211;
    public static final String REASON_DESCRIPTION2 = "Token type is not supported";
    public static final int REASON_CODE3 = 211;
    public static final int REASON_CODE4 = 212;
    public static final int REASON_CODE5 = 230;
    public static final int REASON_CODE6 = 231;
    public static final int REASON_CODE7 = 200;
    public static final int REASON_CODE_234 = 234;
    public static final int REASON_CODE8 = 201;
    public static final int REASON_CODE9 = 400;


    //By Shivaranjan
    public static final String PAYMENT_APP_INSTANCE_ID= "SBICARDS";
}
