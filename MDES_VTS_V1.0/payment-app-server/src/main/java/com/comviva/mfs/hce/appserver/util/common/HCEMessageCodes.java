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
    public static String USER_ALREADY_REGISTERED = "501";
    @Value("${USER_ALREADY_REGISTERED}")
    public  void setUserAlreadyRegistered(String userAlreadyRegistered){
        USER_ALREADY_REGISTERED = userAlreadyRegistered;

    }
    /** The Constant USER_NOT_ACTIVE. */
    public static String INVALID_USER = "205";
    @Value("${INVALID_USER}")

    public static String getInvalidUser() {
        return INVALID_USER;
    }

    /** The Constant INVALID_OPERATION. */
    public static String INVALID_OPERATION = "262";
    @Value("${INVALID_OPERATION}")

    public static String getInvalidOperation() {
        return INVALID_OPERATION;
    }
}
