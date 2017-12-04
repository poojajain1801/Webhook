package com.comviva.mfs.hce.appserver.serviceFlow;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Order(-2)
@Component
public class LoggingInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
    public static final String ERROR_OCCURRED_CODE = "error.occurred";
    public static final String ERROR_OCCURRED_MESSAGE = "Error occurred. Please try again later";


    @Autowired
    HCEControllerSupport hceControllerSupport;
    @Autowired
    Environment env;

    public LoggingInterceptor() {
    }

    @Around("@annotation(com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep)")
    public Object invoke(ProceedingJoinPoint originalMethod) throws Throwable {
        String requestData = (String)originalMethod.getArgs()[0];
        String methodName = originalMethod.getSignature().getName();
        String  responseCode = null;
        long startTime = 0;
        Map responseData;
        try {

            LOGGER.debug("{} Received Request: {}", originalMethod.getSignature(), requestData);
            startTime = System.currentTimeMillis();
            responseData = (Map) originalMethod.proceed();
            responseCode = (String)responseData.get(HCEConstants.RESPONSE_CODE);
            if(HCEConstants.ACTIVE.equals(env.getProperty("audit.trail.required"))){
                hceControllerSupport.maintainAudiTrail(null,methodName.toUpperCase(),responseCode,requestData, HCEUtil.getJsonStringFromMap(responseData));
            }
            final long endTime = System.currentTimeMillis();
            final long totalTime = endTime - startTime;
            HCEUtil.writeHCELog(totalTime,responseCode,null,requestData, HCEUtil.getJsonStringFromMap(responseData));
        } catch (Exception e) {
            LOGGER.error("Exception Occured in LoggingInterceptor->invoke", e);
            responseData = hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
        return responseData;
    }


}
