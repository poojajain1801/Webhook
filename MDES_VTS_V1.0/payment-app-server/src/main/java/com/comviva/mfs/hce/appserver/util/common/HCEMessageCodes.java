package com.comviva.mfs.hce.appserver.util.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Constants contains message code
 */
@Configuration
@PropertySource("classpath:HCEcodes.properties")
public class HCEMessageCodes {

    /** The Constant SERVICE_FAILED. */
    public static String SERVICE_FAILED;
    @Value("${SERVICE_FAILED}")
    public void setServiceFailed(String serviceFailed){
        SERVICE_FAILED = serviceFailed;
    }
    /** The Constant SUCCESS. */
    public static String SUCCESS;
    @Value("${SUCCESS}")
    public void setSuccess(String success){
        SUCCESS = success;
    }

    /** The Constant SUCCESS. */
    public static String INSUFFICIENT_DATA;
    @Value("${INSUFFICIENT_DATA}")
    public void setInsufficientData(String insufficientData){
        INSUFFICIENT_DATA = insufficientData;
    }
    /** The Constant SUCCESS. */
    public static String INVALID_USER_AND_DEVICE;
    @Value("${INVALID_USER_AND_DEVICE}")
    public void setInvalidUserAndDevice(String invalidUserAndDevice){
        INVALID_USER_AND_DEVICE = invalidUserAndDevice;
    }

    /** The Constant INVALID_ACTIVATION_CODE. */
    public static String INVALID_ACTIVATION_CODE;
    @Value("${INVALID_ACTIVATION_CODE}")
    public void setInvalidActivationCode(String invalidActivationCode){
        INVALID_ACTIVATION_CODE = invalidActivationCode;
    }


    /** The Constant USER_IS_ACTIVATED. */
    public static  String USER_IS_ACTIVATED ;
    @Value("${USER_IS_ACTIVATED}")
    public void setUserIsActivated(String userIsActivated){
        USER_IS_ACTIVATED = userIsActivated;
    }
    /** The Constant USER_ACTIVATION_REQUIRED. */
    public static String USER_ACTIVATION_REQUIRED;
    @Value("${USER_ACTIVATION_REQUIRED}")
    public void setUserActivationRequired(String userActivationRequired){
        USER_ACTIVATION_REQUIRED = userActivationRequired;
    }


    /** The Constant USER_ALREADY_REGISTERED. */
    public static String USER_ALREADY_REGISTERED;
    @Value("${USER_ALREADY_REGISTERED}")
    public  void setUserAlreadyRegistered(String userAlreadyRegistered){
        USER_ALREADY_REGISTERED = userAlreadyRegistered;
    }


    /** The Constant INVALID_PROPERTY. */
    public static String INVALID_PROPERTY;
    @Value("${INVALID_PROPERTY}")
    public  void setInvalidProperty(String invalidProperty){
        INVALID_PROPERTY = invalidProperty;
    }


    /** The Constant USER_NOT_ACTIVE. */
    public static String INVALID_USER ;
    @Value("${INVALID_USER}")
    public void setInvalidUser(String invalidUser) {
         INVALID_USER = invalidUser;
    }

    /** The Constant INVALID_OPERATION. */
    public static String INVALID_OPERATION;
    @Value("${INVALID_OPERATION}")
    public void setInvalidOperation(String invalidOperation){
        INVALID_OPERATION = invalidOperation;
    }


    /** The Constant CLIENT_DEVICEID_EXIST. */
    public static String CLIENT_DEVICEID_EXIST;
    @Value("${CLIENT_DEVICEID_EXIST}")
    public void setClientDeviceidExist(String clientDeviceidExist) {
         CLIENT_DEVICEID_EXIST = clientDeviceidExist;
    }

    /** The Constant INVALID_CLIENT_DEVICE_ID. */
    public static String INVALID_CLIENT_DEVICE_ID;
    @Value("${INVALID_CLIENT_DEVICE_ID}")
    public void setInvalidClientDeviceId(String invalidClientDeviceId) {
         INVALID_CLIENT_DEVICE_ID = invalidClientDeviceId;
    }

    /** The Constant VISA_FINAL_CODE. */
    public static String DEVICE_REGISTRATION_FAILED;
    @Value("${DEVICE_REGISTRATION_FAILED}")
    public void setDeviceRegistrationFailed(String deviceRegistrationFailed) {
        DEVICE_REGISTRATION_FAILED= deviceRegistrationFailed;
    }

    /** The Constant VISA_FINAL_CODE. */
    public static String DEVICE_NOT_REGISTERED;
    @Value("${DEVICE_NOT_REGISTERED}")
    public void setDeviceNotRegistered(String deviceNotRegistered) {
        DEVICE_NOT_REGISTERED= deviceNotRegistered;
    }




    /** The Constant VISA_FINAL_CODE. */
    public static String CARD_ALREADY_REGISTERED;
    @Value("${CARD_ALREADY_REGISTERED}")
    public void setCardAlreadyRegistered(String cardAlreadyRegistered) {
        CARD_ALREADY_REGISTERED= cardAlreadyRegistered;
    }


    /** The Constant VISA_FINAL_CODE. */
    public static String UNABLE_TO_PARSE_REQUEST;
    @Value("${UNABLE_TO_PARSE_REQUEST}")
    public void setUnableToParseRequest(String unableToParseRequest) {
        UNABLE_TO_PARSE_REQUEST= unableToParseRequest;
    }


    /** The Constant CARD_DETAILS_NOT_EXIST. */
    public static String CARD_DETAILS_NOT_EXIST;
    @Value("${CARD_DETAILS_NOT_EXIST}")
    public void setCardDetailsNotExist(String cardDetailsNotExist) {
        CARD_DETAILS_NOT_EXIST= cardDetailsNotExist;
    }


    /** The Constant CARD_DETAILS_NOT_EXIST. */
    public static String FAILED_AT_THIRED_PARTY;
    @Value("${FAILED_AT_THIRED_PARTY}")
    public void setFailedAtThiredParty(String failedAtThiredParty) {
        FAILED_AT_THIRED_PARTY= failedAtThiredParty;
    }








}
