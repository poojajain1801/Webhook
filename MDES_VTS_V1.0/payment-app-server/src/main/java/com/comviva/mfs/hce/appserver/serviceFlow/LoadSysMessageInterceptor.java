package com.comviva.mfs.hce.appserver.serviceFlow;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
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
@Order(-1)
@Component
public class LoadSysMessageInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSysMessageInterceptor.class);

    @Autowired
    private HCEControllerSupport hceControllerSupport;
    @Autowired
    private Environment env;

    public LoadSysMessageInterceptor() {
    }

    @Around("@annotation(com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep)")
    public Object invoke(ProceedingJoinPoint originalMethod) throws Throwable {
        String  responseCode = null;
        Map responseData;
        String responseMessage = null;
        Map responseMessageMap = null;
        try {
            responseData = (Map) originalMethod.proceed();
            responseCode = (String)responseData.get(HCEConstants.RESPONSE_CODE);
            responseMessage = (String)responseData.get(HCEConstants.MESSAGE);
            if(responseMessage == null && responseCode!= null){
                responseMessageMap = hceControllerSupport.formResponse(responseCode);
                responseMessageMap.putAll(responseData);
            }else if(responseCode == null ){
                responseCode = HCEMessageCodes.getSUCCESS();
                responseMessageMap = hceControllerSupport.formResponse(responseCode);
                responseMessageMap.putAll(responseData);
            }else{
                responseMessageMap = responseData;
            }
        } catch (HCEValidationException resHceValidationException){
            responseMessageMap = hceControllerSupport.formResponse(resHceValidationException.getMessageCode(),resHceValidationException.getMessage());
        }catch (HCEActionException resHceActionException){
            responseMessageMap = hceControllerSupport.formResponse(resHceActionException.getMessageCode());
        }catch (Exception resException) {
            LOGGER.error(" Exception Occured in LoadSysMessageInterceptor->invoke", resException);
            responseMessageMap = hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
        return responseMessageMap;
    }
}
