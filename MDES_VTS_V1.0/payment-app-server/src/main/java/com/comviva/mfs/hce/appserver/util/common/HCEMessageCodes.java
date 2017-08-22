package com.comviva.mfs.hce.appserver.util.common;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shadab.ali on 22-08-2017.
 */
public class HCEMessageCodes {


    public static final String SERVICE_FAILED = "500";
    public static final String SUCCESS = "200";
    public static final String USER_ACTIVATION_REQUIRED = "201";
    public static final String USER_ALREADY_REGISTERED = "202";
    public static final String INSUFFICIENT_DATA = "300";

    public static final Map<String,Object> ERROR_MESSAGE_MAP= ImmutableMap.of(SERVICE_FAILED,"Unable to process request",INSUFFICIENT_DATA,"");
    public static final Map<String,Object> SUCCESS_MESSAGE_MAP= ImmutableMap.of(SUCCESS,"Transaction success",USER_ALREADY_REGISTERED,"User already  registered in the system",USER_ACTIVATION_REQUIRED,"User already  registered in the system ,Activate account with below Activaction code");
}
