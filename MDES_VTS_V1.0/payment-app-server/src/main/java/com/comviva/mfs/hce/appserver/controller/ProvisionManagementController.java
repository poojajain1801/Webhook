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
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
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
    @ServiceFlowStep("paymentApp")
    public Map<String, Object>confirmProvisioning(@RequestBody String confirmProvisioningRequest){
        LOGGER.debug("Enter ProvisionManagementController-> confirmProvisioning ");
        ConfirmProvisioningRequest confirmProvisioningRequestpojo = null;
        Map <String,Object>confirmProvisioningResp= null;
        try{
            confirmProvisioningRequestpojo = (ConfirmProvisioningRequest)hCEControllerSupport.requestFormation(confirmProvisioningRequest,ConfirmProvisioningRequest.class);
            confirmProvisioningResp = provisionManagementService.ConfirmProvisioning(confirmProvisioningRequestpojo);
        }catch (HCEValidationException confirmProvisioningRequestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->confirmProvisioning",confirmProvisioningRequestValidation);
            throw confirmProvisioningRequestValidation;
        }catch (HCEActionException confirmProvisioningHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->confirmProvisioning",confirmProvisioningHceActionException);
            throw confirmProvisioningHceActionException;
        }catch (Exception confirmProvisioningExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->confirmProvisioning", confirmProvisioningExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit ProvisionManagementController-> confirmProvisioning ");
        return confirmProvisioningResp;
    }

    @ResponseBody
    @RequestMapping(value = "/activeAccountManagementReplenish",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String ,Object>activeAccountManagementReplenish( @RequestBody String activeAccountManagementReplenishRequest){
        LOGGER.debug("Enter ProvisionManagementController-> activeAccountManagementReplenish ");
        ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequestpojo = null;
        Map <String,Object> replineshResp= null;
        try{
            activeAccountManagementReplenishRequestpojo = (ActiveAccountManagementReplenishRequest)hCEControllerSupport.requestFormation(activeAccountManagementReplenishRequest,ActiveAccountManagementReplenishRequest.class);
            replineshResp = provisionManagementService.ActiveAccountManagementReplenish(activeAccountManagementReplenishRequestpojo);
        }catch (HCEValidationException replinesRequestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->confirmProvisioning",replinesRequestValidation);
            throw replinesRequestValidation;
        }
        catch (HCEActionException replinesHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementReplenish",replinesHceActionException);
            throw replinesHceActionException;
        }catch (Exception replenishPanExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementReplenish", replenishPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("exit ProvisionManagementController-> activeAccountManagementReplenish ");
        return replineshResp;
    }
    @ResponseBody
    @RequestMapping(value = "/activeAccountManagementConfirmReplenishment",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String ,Object>activeAccountManagementConfirmReplenishment(@RequestBody String activeAccountManagementConfirmReplenishmentRequest){
        LOGGER.debug("Enter ProvisionManagementController-> activeAccountManagementConfirmReplenishment ");
        ActiveAccountManagementConfirmReplenishmentRequest activeAccountManagementConfirmReplenishmentRequestpojo = null;
        Map <String,Object> ConfirmReplenishmentResp= null;
        try{
            activeAccountManagementConfirmReplenishmentRequestpojo = (ActiveAccountManagementConfirmReplenishmentRequest)hCEControllerSupport.requestFormation(activeAccountManagementConfirmReplenishmentRequest,ActiveAccountManagementConfirmReplenishmentRequest.class);
            ConfirmReplenishmentResp = provisionManagementService.ActiveAccountManagementConfirmReplenishment(activeAccountManagementConfirmReplenishmentRequestpojo);
        }catch (HCEValidationException ConfirmReplenishmentRequestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->confirmProvisioning",ConfirmReplenishmentRequestValidation);
            throw ConfirmReplenishmentRequestValidation;
        }
        catch (HCEActionException ConfirmReplenishmentHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment",ConfirmReplenishmentHceActionException);
            throw ConfirmReplenishmentHceActionException;
        }catch (Exception ConfirmReplenishmentExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment", ConfirmReplenishmentExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit ProvisionManagementController-> activeAccountManagementConfirmReplenishment ");
        return ConfirmReplenishmentResp;
    }
    @ResponseBody
    @RequestMapping(value = "/replenishODAData",method = RequestMethod.POST)
    public Map<String ,Object>replenishODAData(@RequestBody  ReplenishODADataRequest replenishODADataRequest){
        return provisionManagementService.ReplenishODAData(replenishODADataRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/submitIDandVStepupMethodRequest",method =RequestMethod.POST )
    @ServiceFlowStep("paymentApp")
    public Map<String ,Object> submitIDandVStepupMethodRequest(@RequestBody String submitIDandVStepupMethodRequest){
        Map <String,Object> submitIDandVStepupMethodRequestResp= null;
        try{
            LOGGER.debug("Enter ProvisionManagementController-> submitIDandVStepupMethodRequest");
            SubmitIDandVStepupMethodRequest submitIDandVStepupMethodRequestPojo = (SubmitIDandVStepupMethodRequest)hCEControllerSupport.requestFormation(submitIDandVStepupMethodRequest,SubmitIDandVStepupMethodRequest.class);
            submitIDandVStepupMethodRequestResp = provisionManagementService.submitIDandVStepupMethod(submitIDandVStepupMethodRequestPojo);
        }
        catch (HCEValidationException ConfirmReplenishmentRequestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->confirmProvisioning",ConfirmReplenishmentRequestValidation);
            throw ConfirmReplenishmentRequestValidation;
        }
        catch (HCEActionException ConfirmReplenishmentHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment",ConfirmReplenishmentHceActionException);
            throw ConfirmReplenishmentHceActionException;
        }catch (Exception ConfirmReplenishmentExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment", ConfirmReplenishmentExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return submitIDandVStepupMethodRequestResp;
    }
    @ResponseBody
    @RequestMapping(value = "/validateOTP",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>validateOTP(@RequestBody String validateOTPRequest){
        Map <String,Object> validateOtpResp= null;
        try{
            LOGGER.debug("Enter ProvisionManagementController-> activeAccountManagementConfirmReplenishment ");
            ValidateOTPRequest validateOTPRequestPojo = (ValidateOTPRequest)hCEControllerSupport.requestFormation(validateOTPRequest,ValidateOTPRequest.class);
            validateOtpResp = provisionManagementService.validateOTP(validateOTPRequestPojo);
        }
        catch (HCEValidationException ConfirmReplenishmentRequestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->confirmProvisioning",ConfirmReplenishmentRequestValidation);
            throw ConfirmReplenishmentRequestValidation;
        }
        catch (HCEActionException ConfirmReplenishmentHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment",ConfirmReplenishmentHceActionException);
            throw ConfirmReplenishmentHceActionException;
        }catch (Exception ConfirmReplenishmentExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment", ConfirmReplenishmentExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return validateOtpResp;
    }
    @ResponseBody
    @RequestMapping(value = "/validateAuthenticationCode",method = RequestMethod.POST)
    public Map<String,Object>validateAuthenticationCode(@RequestParam ValidateAuthenticationCodeRequest validateAuthenticationCodeRequest){
        return provisionManagementService.validateAuthenticationCode(validateAuthenticationCodeRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/getStepUpOptions",method = RequestMethod.POST)
    public Map<String ,Object>getStepUpOptions(@RequestBody String getStepUpOptionsRequest){
        Map <String,Object> getStepUpOptions= null;
        try{
            LOGGER.debug("Enter ProvisionManagementController-> getStepUpOptions ");
            GetStepUpOptionsRequest getStepUpOptionsRequestPojo = (GetStepUpOptionsRequest)hCEControllerSupport.requestFormation(getStepUpOptionsRequest,GetStepUpOptionsRequest.class);
            getStepUpOptions = provisionManagementService.getStepUpOptions(getStepUpOptionsRequestPojo);
        }
        catch (HCEValidationException getSteopupOptionuestValidation){
            LOGGER.error("Exception Occured in ProvisionManagementController->getStepUpOptions",getSteopupOptionuestValidation);
            throw getSteopupOptionuestValidation; 
        }
        catch (HCEActionException getSteopupOptionuestHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->getStepUpOptions",getSteopupOptionuestHceActionException);
            throw getSteopupOptionuestHceActionException; 
        }catch (Exception getSteopupOptionuestExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->getStepUpOptions", getSteopupOptionuestExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getStepUpOptions;
  
    }
}