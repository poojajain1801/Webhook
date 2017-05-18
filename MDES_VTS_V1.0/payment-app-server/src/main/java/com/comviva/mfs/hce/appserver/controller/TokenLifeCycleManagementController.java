package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Madan amgoth on 5/9/2017.
 */
@RestController
@RequestMapping("/api/token")
public class TokenLifeCycleManagementController {

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
        return tokenLifeCycleManagementService.getTokenStatus(getTokenStatusRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/suspendToken",method = RequestMethod.POST)
    public Map<String,Object>suspendToken(@RequestBody SuspendTokenRequest suspendTokenRequest){
        return tokenLifeCycleManagementService.suspendToken(suspendTokenRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/resumeToken",method = RequestMethod.POST)
    public Map<String,Object>resumeToken(@RequestBody ResumeTokenRequest resumeTokenRequest){
        return tokenLifeCycleManagementService.resumeToken(resumeTokenRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/deleteToken",method = RequestMethod.POST)
    public Map<String,Object>deleteToken(@RequestBody DeleteTokenRequest deleteTokenRequest){
        return tokenLifeCycleManagementService.deleteToken(deleteTokenRequest);
    }
}