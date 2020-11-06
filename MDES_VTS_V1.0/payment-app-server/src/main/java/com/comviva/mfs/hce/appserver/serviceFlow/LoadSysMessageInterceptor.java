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
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.SysMessageRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Aspect
@Order(-1)
@Component
public class LoadSysMessageInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSysMessageInterceptor.class);
    private  UserDetailRepository userDetailRepository ;
    private  DeviceDetailRepository deviceDetailRepository;
    private  CardDetailRepository cardDetailRepository;
    private SysMessageRepository sysMessageRepository;

    @Autowired
    private HCEControllerSupport hceControllerSupport;
    @Autowired
    private Environment env;
    @Autowired
    public LoadSysMessageInterceptor(UserDetailRepository userDetailRepository, DeviceDetailRepository deviceDetailRepository, CardDetailRepository cardDetailRepository, HCEControllerSupport hceControllerSupport ,SysMessageRepository sysMessageRepository) {
        this.userDetailRepository = userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.deviceDetailRepository=deviceDetailRepository;
        this.sysMessageRepository = sysMessageRepository;
        this.cardDetailRepository = cardDetailRepository;
    }

    public LoadSysMessageInterceptor() {
    }

    @Around("@annotation(com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep)")
    public Object invoke(ProceedingJoinPoint originalMethod) throws Throwable {
        String  responseCode = null;
        String responseMessage = null;
        String reasonCode = null;
        Map errorResponse = null;
        List<SysMessage> sysMessage = null;
        Map responseData = null;
        Map responseMessageMap = new HashMap();
        String userId = null;
        try {
            LOGGER.debug("inside SysMessage interceptor before hitting third party  *****************");
            String requestData = (String)originalMethod.getArgs()[0];
            userId = hceControllerSupport.findUserId(requestData);
            hceControllerSupport.setUserId(userId);

            responseData = (Map) originalMethod.proceed();

            LOGGER.debug("Response data **********************" +responseData);
            //LOGGER.debug("inside SysMessage interceptor after hitting third party  *****************");

            responseCode = (String) responseData.get(HCEConstants.RESPONSE_CODE);
            errorResponse = ((Map) responseData.get(HCEConstants.ERROR_RESPONSE));
            if (errorResponse != null){
                reasonCode = (String) errorResponse.get(HCEConstants.REASON);
            }else if(responseData.get(HCEConstants.ERROR_CODE) !=null){
                reasonCode = (String)responseData.get(HCEConstants.ERROR_CODE);
            }else if (responseData.get(HCEConstants.STATUS_CODE) !=null){
                responseCode = (String) responseData.get(HCEConstants.STATUS_CODE);
            }
            else {
                reasonCode = (String)responseData.get(HCEConstants.REASON_CODE);
            }
            if (reasonCode != null){
                sysMessage = sysMessageRepository.findByReasonCode(reasonCode);
                if (sysMessage !=null && !sysMessage.isEmpty()) {
                    responseCode = sysMessage.get(0).getId().getMessageCode();
                    responseMessageMap.putAll(responseData);
                    responseMessageMap.putAll(hceControllerSupport.formResponse(responseCode));
                }else {
                    responseMessageMap.putAll(responseData);
                }
            } else if (responseCode !=null && !responseCode.equals("400")){
                responseMessageMap.putAll(responseData);
                responseMessageMap.putAll(hceControllerSupport.formResponse(responseCode));

            } else {
                responseMessageMap.putAll(responseData);
            }
            LOGGER.debug("Response map*******************"+responseMessageMap);

            /*responseMessage = (String)responseData.get(HCEConstants.MESSAGE);
            if(responseMessage == null && responseCode!= null){
                responseMessageMap = hceControllerSupport.formResponse(responseCode);
                responseMessageMap.putAll(responseData);
            }else if(responseCode == null ){
                responseCode = HCEMessageCodes.getSUCCESS();
                responseMessageMap = hceControllerSupport.formResponse(responseCode);
                responseMessageMap.putAll(responseData);
            }else{
                responseMessageMap = responseData;
            }*/
        } catch (HCEValidationException resHceValidationException){
            LOGGER.debug(resHceValidationException + "");
            responseMessageMap = hceControllerSupport.formResponse(resHceValidationException.getMessageCode(),resHceValidationException.getMessage());
        }catch (HCEActionException resHceActionException){
            LOGGER.debug(resHceActionException+"");
            responseMessageMap = hceControllerSupport.formResponse(resHceActionException.getMessageCode());
        }catch (Exception resException) {
            LOGGER.error(" Exception Occured in LoadSysMessageInterceptor->invoke", resException);
            responseMessageMap = hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
        return responseMessageMap;
    }
}

