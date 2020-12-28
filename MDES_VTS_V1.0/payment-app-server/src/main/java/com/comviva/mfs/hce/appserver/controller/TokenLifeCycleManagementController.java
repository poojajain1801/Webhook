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
package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.decryptFlow.DecryptFlowStep;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenListRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenStatusRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Madan amgoth on 5/9/2017.
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/token")
public class TokenLifeCycleManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenLifeCycleManagementController.class);

    @Autowired
    private TokenLifeCycleManagementService tokenLifeCycleManagementService;
    @Autowired
    private HCEControllerSupport hceControllerSupport;

    public TokenLifeCycleManagementController(TokenLifeCycleManagementService tokenLifeCycleManagementService ) {
        this.tokenLifeCycleManagementService=tokenLifeCycleManagementService;
    }


    @ResponseBody
    @RequestMapping(value = "/getTokenStatus",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>getTokenStatus(@RequestBody String getTokenStatusRequest){
        LOGGER.debug("Enter TokenLifeCycleManagementController->getTokenStatus");
        GetTokenStatusRequest getTokenStatusRequestpojo = null;
        Map<String, Object> getTokenStatus = null;
        try {
            getTokenStatusRequestpojo = (GetTokenStatusRequest) hceControllerSupport.requestFormation(getTokenStatusRequest, GetTokenStatusRequest.class);
            getTokenStatus = tokenLifeCycleManagementService.getTokenStatus(getTokenStatusRequestpojo);
        }
        catch (HCEActionException enrollPanHceActionException){
            LOGGER.error("Exception Occured in TokenLifeCycleManagementController->getTokenStatus",enrollPanHceActionException);
            throw enrollPanHceActionException;
        }catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in TokenLifeCycleManagementController->getTokenStatus", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit TokenLifeCycleManagementController->getTokenStatus");
        return getTokenStatus;
    }

    @ResponseBody
    @RequestMapping(value = "/lifeCycleManagementVisa",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    @DecryptFlowStep("decryptData")
    public Map<String,Object>lifeCycleManagementVisa(@RequestBody String lifeCycleManagementVisaRequest){
        LOGGER.debug("Enter TokenLifeCycleManagementController->lifeCycleManagementVisa");
        LifeCycleManagementVisaRequest lifeCycleManagementVisaRequestpojo = null;
        Map <String,Object> deleteTokenResp = null;
        try {
            lifeCycleManagementVisaRequestpojo = (LifeCycleManagementVisaRequest) hceControllerSupport.requestFormation(lifeCycleManagementVisaRequest, LifeCycleManagementVisaRequest.class);
            deleteTokenResp = tokenLifeCycleManagementService.lifeCycleManagementVisa(lifeCycleManagementVisaRequestpojo);
        }
        catch (HCEActionException lifeCycleManagementHceActionException){
            LOGGER.error("Exception Occured in TokenLifeCycleManagementController->lifeCycleManagementVisa",lifeCycleManagementHceActionException);
            throw lifeCycleManagementHceActionException;
        }catch (Exception lifeCycleManagementPanExcetption) { 
            LOGGER.error(" Exception Occured in TokenLifeCycleManagementController->lifeCycleManagementVisa", lifeCycleManagementPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Enter TokenLifeCycleManagementController->lifeCycleManagementVisa");
        return deleteTokenResp;
    }


    @ResponseBody
    @RequestMapping(value = "/getTokenList",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>getTokenList(@RequestBody String  tokenListRequest){
        Map<String,Object> tokenListResponse = null;
        GetTokenListRequest tokenListRequestPojo = null;
        try{
            LOGGER.debug("Enter TokenLifeCycleManagementController->getTokenList");
            tokenListRequestPojo =(GetTokenListRequest) hceControllerSupport.requestFormation(tokenListRequest,GetTokenListRequest.class);
            tokenListResponse = tokenLifeCycleManagementService.getTokenList(tokenListRequestPojo);
            LOGGER.debug("Exit TokenLifeCycleManagementController->getTokenList");
        }catch (HCEActionException regDeviceHCEActionException){
            LOGGER.error("Exception Occured in TokenLifeCycleManagementController->getTokenList",regDeviceHCEActionException);
            throw regDeviceHCEActionException;
        }catch (Exception regDeviceException) {
            LOGGER.error(" Exception Occured in TokenLifeCycleManagementController->getTokenList", regDeviceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return tokenListResponse;


    }
}
