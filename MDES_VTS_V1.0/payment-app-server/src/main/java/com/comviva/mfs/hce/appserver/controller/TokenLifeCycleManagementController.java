package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
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
}