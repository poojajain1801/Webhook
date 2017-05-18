package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.ProvisionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Amgoth.madan on 5/2/2017.
 */
@RestController
@RequestMapping("/api/provision")
public class ProvisionManagementController {

    @Autowired
    private ProvisionManagementService provisionManagementService;

    public ProvisionManagementController(ProvisionManagementService provisionManagementService ) {
        this.provisionManagementService = provisionManagementService;
    }

    @ResponseBody
    @RequestMapping(value = "/provisionTokenWithPanEnrollmentId", method = RequestMethod.POST)
    public Map<String, Object> provisionTokenWithPanEnrollmentId(@RequestBody ProvisionTokenGivenPanEnrollmentIdRequest provisionTokenGivenPanEnrollmentIdRequest){
        return provisionManagementService.ProvisionTokenGivenPanEnrollmentId(provisionTokenGivenPanEnrollmentIdRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/provisionTokenWithPanData", method = RequestMethod.POST)
    public Map<String, Object> provisionTokenWithPanData(@RequestBody ProvisionTokenWithPanDataRequest provisionTokenWithPanDataRequest){
        return provisionManagementService.ProvisionTokenWithPanData(provisionTokenWithPanDataRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/confirmProvisioning",method = RequestMethod.POST)
    public Map<String, Object>confirmProvisioning(@RequestBody ConfirmProvisioningRequest confirmProvisioningRequest){
        return provisionManagementService.ConfirmProvisioning(confirmProvisioningRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/activeAccountManagementReplenish",method = RequestMethod.POST)
    public Map<String ,Object>activeAccountManagementReplenish(ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest){
        return provisionManagementService.ActiveAccountManagementReplenish(activeAccountManagementReplenishRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/activeAccountManagementConfirmReplenishment",method = RequestMethod.POST)
    public Map<String ,Object>activeAccountManagementConfirmReplenishment(@RequestBody ActiveAccountManagementConfirmReplenishmentRequest activeAccountManagementConfirmReplenishmentRequest){
        return provisionManagementService.ActiveAccountManagementConfirmReplenishment(activeAccountManagementConfirmReplenishmentRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/replenishODAData",method = RequestMethod.POST)
    public Map<String ,Object>replenishODAData(@RequestBody  ReplenishODADataRequest replenishODADataRequest){
        return provisionManagementService.ReplenishODAData(replenishODADataRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/submitIDandVStepupMethodRequest",method =RequestMethod.POST )
    public Map<String ,Object>submitIDandVStepupMethodRequest(@RequestParam SubmitIDandVStepupMethodRequest submitIDandVStepupMethodRequest){
        return  provisionManagementService.submitIDandVStepupMethod(submitIDandVStepupMethodRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/validateOTP",method = RequestMethod.POST)
    public Map<String,Object>validateOTP(@RequestParam ValidateOTPRequest validateOTPRequest){
        return provisionManagementService.validateOTP(validateOTPRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/validateAuthenticationCode",method = RequestMethod.POST)
    public Map<String,Object>validateAuthenticationCode(@RequestParam ValidateAuthenticationCodeRequest validateAuthenticationCodeRequest){
        return provisionManagementService.validateAuthenticationCode(validateAuthenticationCodeRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/getStepUpOptions",method = RequestMethod.POST)
    public Map<String ,Object>getStepUpOptions(@RequestParam GetStepUpOptionsRequest getStepUpOptionsRequest){
        return provisionManagementService.getStepUpOptions(getStepUpOptionsRequest);
    }
}