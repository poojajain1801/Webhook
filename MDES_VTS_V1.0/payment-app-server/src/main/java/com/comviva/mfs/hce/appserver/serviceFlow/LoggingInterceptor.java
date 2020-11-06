package com.comviva.mfs.hce.appserver.serviceFlow;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    @Autowired
    private HCEControllerSupport hceControllerSupport;
    @Autowired
    private Environment env;

    public LoggingInterceptor() {
    }

    @Around("@annotation(com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep)")
    public Object invoke(ProceedingJoinPoint originalMethod) throws Throwable {
        String requestData = (String)originalMethod.getArgs()[0];
        String methodName = originalMethod.getSignature().getName();
        String  responseCode = null;
        long startTime = 0;
        Map responseData =null;
        String requestId = null;
        String clientDeviceId  = null;
        String startTimeValue = null;
        try {
            startTimeValue = MDC.get(HCEConstants.START_TIME);
            if(startTimeValue!= null){
                startTime = Long.valueOf(startTimeValue);
            }else{
                startTime = System.currentTimeMillis();
            }
            requestId = hceControllerSupport.findUserId(requestData);
            clientDeviceId = hceControllerSupport.findClientDeviceID(requestData);
            responseData = (Map) originalMethod.proceed();
        } catch (HCEActionException hceActionExp){
            LOGGER.error("Exception Occured in LoggingInterceptor->invoke", hceActionExp);
            responseData = hceControllerSupport.formResponse(hceActionExp.getMessageCode());
        }catch (Exception e) {
            LOGGER.error("Exception Occured in LoggingInterceptor->invoke", e);
            responseData = hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }finally {
            MDC.remove(HCEConstants.START_TIME);

            final long endTime = System.currentTimeMillis();
            final long totalTime = endTime - startTime;

            if (null != responseData) {
                responseCode = (String) responseData.get(HCEConstants.RESPONSE_CODE);
            }
            if(HCEConstants.ACTIVE.equals(env.getProperty("audit.trail.required"))){
                hceControllerSupport.maintainAudiTrail(requestId,clientDeviceId,methodName.toUpperCase(),responseCode,requestData, HCEUtil.getJsonStringFromMap(responseData),String.valueOf(totalTime));
            }
            HCEUtil.writeHCELog(totalTime,responseCode,requestId,requestData, HCEUtil.getJsonStringFromMap(responseData),methodName.toUpperCase());
        }
        return responseData;
    }

}

