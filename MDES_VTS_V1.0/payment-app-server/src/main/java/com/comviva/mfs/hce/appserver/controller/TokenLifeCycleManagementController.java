package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.decryptFlow.DecryptFlowStep;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenListRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenStatusRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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