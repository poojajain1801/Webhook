/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.decryptFlow;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.serviceFlow.LoggingInterceptor;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
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

/**
 * Created by tanmay.patel on 04-12-2017.
 */


@Aspect
@Order(-3)
@Component
public class DecryptData {


    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
    @Autowired
    private HCEControllerSupport hceControllerSupport;
    @Autowired
    private Environment env;

    public DecryptData() {}

    /**
     * invoke
     * @param originalMethod originalMethod
     * @return Object
     * */
    @Around("@annotation(com.comviva.mfs.hce.appserver.decryptFlow.DecryptFlowStep)")
    public Object invoke(ProceedingJoinPoint originalMethod) throws Throwable {
        String requestData = (String)originalMethod.getArgs()[0];
        Map responseData =null;
        Object[] args  = null;
        long startTime = 0;
        try {
            LOGGER.debug("{} Received Request: {}", originalMethod.getSignature(), requestData);
            startTime = System.currentTimeMillis();
            MDC.put(HCEConstants.START_TIME,Long.toString(startTime));
            requestData = decryptData(requestData);
            args = new Object[1];
            args[0] = requestData;
            responseData = (Map) originalMethod.proceed(args);
        } catch (HCEActionException hceActionExp){
            LOGGER.error("Exception Occured in DecryptData->invoke", hceActionExp);
            responseData = hceControllerSupport.formResponse(hceActionExp.getMessageCode());
        }catch (Exception e) {
            LOGGER.error("Exception Occured in DecryptData->invoke", e);
            responseData = hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
        return responseData;
    }

    /**
     * decryptData
     * @param requestData requestData
     * @return string
     * */
    private String decryptData(String requestData) throws HCEActionException{
        if(HCEConstants.ACTIVE.equals(env.getProperty("enable.end.to.end.encryption"))){
            LOGGER.debug("Encrypted request :-",requestData);
            requestData = hceControllerSupport.decryptRequest(requestData);
        }
        return requestData;
    }
}
