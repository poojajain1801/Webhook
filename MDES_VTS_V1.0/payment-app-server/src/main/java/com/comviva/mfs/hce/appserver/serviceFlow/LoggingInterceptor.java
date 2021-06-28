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

