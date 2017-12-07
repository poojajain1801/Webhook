package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.ProvisionManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionManagementController.class);

    public ProvisionManagementController(ProvisionManagementService provisionManagementService ) {
        this.provisionManagementService = provisionManagementService;
    }

    @ResponseBody
    @RequestMapping(value = "/provisionTokenWithPanEnrollmentId", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> provisionTokenWithPanEnrollmentId(@RequestBody String provisionTokenGivenPanEnrollmentIdRequest){
        Map <String,Object> provisonResp= null;
        ProvisionTokenGivenPanEnrollmentIdRequest provisionTokenGivenPanEnrollmentIdRequestPojo = null;
        try{
            provisionTokenGivenPanEnrollmentIdRequestPojo =(ProvisionTokenGivenPanEnrollmentIdRequest) hCEControllerSupport.requestFormation(provisionTokenGivenPanEnrollmentIdRequest,ProvisionTokenGivenPanEnrollmentIdRequest.class);
            provisonResp = provisionManagementService.ProvisionTokenGivenPanEnrollmentId(provisionTokenGivenPanEnrollmentIdRequestPojo);
        }catch (HCEValidationException enrollPanRequestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->provisionTokenWithPanEnrollmentId",enrollPanRequestValidation);
            throw enrollPanRequestValidation;
        }catch (HCEActionException enrollPanHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->provisionTokenWithPanEnrollmentId",enrollPanHceActionException);
            throw enrollPanHceActionException;
        }catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->provisionTokenWithPanEnrollmentId", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }
        LOGGER.debug("Exit ProvisionManagementController->provisionTokenWithPanEnrollmentId");

        return provisonResp;
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
    public Map<String ,Object>activeAccountManagementReplenish( @RequestBody ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest){
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