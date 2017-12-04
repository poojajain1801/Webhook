package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Madan amgoth on 5/9/2017.
 */
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
    @RequestMapping(value = "/getPaymentDataGivenTokenID",method = RequestMethod.POST)
    public Map<String,Object> getPaymentDataGivenTokenID(@RequestBody GetPaymentDataGivenTokenIDRequest getPaymentDataGivenTokenIDRequest){
        return tokenLifeCycleManagementService.getPaymentDataGivenTokenID(getPaymentDataGivenTokenIDRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/getTokenStatus",method = RequestMethod.POST)
    public Map<String,Object>getTokenStatus(@RequestBody GetTokenStatusRequest getTokenStatusRequest){
        LOGGER.debug("Enter TokenLifeCycleManagementController->getTokenStatus");
        Map<String,Object> getTokenStatus = tokenLifeCycleManagementService.getTokenStatus(getTokenStatusRequest);
        LOGGER.debug("Exit TokenLifeCycleManagementController->getTokenStatus");
        return getTokenStatus;
    }

    @ResponseBody
    @RequestMapping(value = "/lifeCycleManagementVisa",method = RequestMethod.POST)
    public Map<String,Object>lifeCycleManagementVisa(@RequestBody LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest){
        LOGGER.debug("Enter TokenLifeCycleManagementController->lifeCycleManagementVisa");
        Map <String,Object> deleteTokenResp =  tokenLifeCycleManagementService.lifeCycleManagementVisa(lifeCycleManagementVisaRequest);
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
        }catch (HCEValidationException registerDeviceValidationException){
            LOGGER.error("Exception Occured in  TokenLifeCycleManagementController->getTokenList",registerDeviceValidationException);
            throw registerDeviceValidationException;
        }catch (HCEActionException regDeviceHCEActionException){
            LOGGER.error("Exception Occured in TokenLifeCycleManagementController->getTokenList",regDeviceHCEActionException);
            throw regDeviceHCEActionException;
        }catch (Exception regDeviceException) {
            LOGGER.error(" Exception Occured in TokenLifeCycleManagementController->getTokenList", regDeviceException);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }
        return tokenListResponse;


    }
}