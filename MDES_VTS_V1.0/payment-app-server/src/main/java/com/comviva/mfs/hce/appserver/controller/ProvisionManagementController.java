package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActiveAccountManagementReplenishRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmProvisioningRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmReplenishmenRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetStepUpOptionsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ProvisionTokenGivenPanEnrollmentIdRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ReplenishODADataRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SubmitIDandVStepupMethodRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ValidateOTPRequest;
import com.comviva.mfs.hce.appserver.service.contract.ProvisionManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


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
    @RequestMapping(value = "/confirmProvisioning",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object>confirmProvisioning(@RequestBody String confirmProvisioningRequest){
        LOGGER.debug("Enter ProvisionManagementController-> confirmProvisioning ");
        ConfirmProvisioningRequest confirmProvisioningRequestpojo = null;
        Map <String,Object>confirmProvisioningResp= null;
        try{
            confirmProvisioningRequestpojo = (ConfirmProvisioningRequest)hCEControllerSupport.requestFormation(confirmProvisioningRequest,ConfirmProvisioningRequest.class);
            confirmProvisioningResp = provisionManagementService.ConfirmProvisioning(confirmProvisioningRequestpojo);
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
        try {
            activeAccountManagementReplenishRequestpojo = (ActiveAccountManagementReplenishRequest) hCEControllerSupport.requestFormation(activeAccountManagementReplenishRequest, ActiveAccountManagementReplenishRequest.class);
            replineshResp = provisionManagementService.ActiveAccountManagementReplenish(activeAccountManagementReplenishRequestpojo);
        }catch (HCEActionException replinesHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementReplenish",replinesHceActionException);
            throw replinesHceActionException;
        }catch (Exception replenishPanExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementReplenish", replenishPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("exit ProvisionManagementController-> activeAccountManagementReplenish ");
        return replineshResp;
    }

    /*@ResponseBody
    @RequestMapping(value = "/activeAccountManagementConfirmReplenishment",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String ,Object>activeAccountManagementConfirmReplenishment(@RequestBody String activeAccountManagementConfirmReplenishmentRequest){
        LOGGER.debug("Enter ProvisionManagementController-> activeAccountManagementConfirmReplenishment ");
        ActiveAccountManagementConfirmReplenishmentRequest activeAccountManagementConfirmReplenishmentRequestpojo = null;
        Map <String,Object> ConfirmReplenishmentResp= null;
        try {
            activeAccountManagementConfirmReplenishmentRequestpojo = (ActiveAccountManagementConfirmReplenishmentRequest) hCEControllerSupport.requestFormation(activeAccountManagementConfirmReplenishmentRequest, ActiveAccountManagementConfirmReplenishmentRequest.class);
            ConfirmReplenishmentResp = provisionManagementService.ActiveAccountManagementConfirmReplenishment(activeAccountManagementConfirmReplenishmentRequestpojo);
        }
        catch (HCEActionException ConfirmReplenishmentHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment",ConfirmReplenishmentHceActionException);
            throw ConfirmReplenishmentHceActionException;
        }catch (Exception ConfirmReplenishmentExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment", ConfirmReplenishmentExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit ProvisionManagementController-> activeAccountManagementConfirmReplenishment");
        return ConfirmReplenishmentResp;
    }*/

    @ResponseBody
    @RequestMapping(value = "/activeAccountManagementConfirmReplenishment",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String ,Object>confirmReplenish(@RequestBody String confirmReplenish){
        LOGGER.debug("Enter ProvisionManagementController-> confirmReplenish ");
        ConfirmReplenishmenRequest confirmReplenishmenRequestPojo = null;
        Map <String,Object> ConfirmReplenishmentResp= null;
        try {
            confirmReplenishmenRequestPojo = (ConfirmReplenishmenRequest) hCEControllerSupport.requestFormation(confirmReplenish, ConfirmReplenishmenRequest.class);
            ConfirmReplenishmentResp = provisionManagementService.ActiveAccountManagementConfirmReplenishment(confirmReplenishmenRequestPojo);
        }
        catch (HCEActionException ConfirmReplenishmentHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment",ConfirmReplenishmentHceActionException);
            throw ConfirmReplenishmentHceActionException;
        }catch (Exception ConfirmReplenishmentExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementConfirmReplenishment", ConfirmReplenishmentExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit ProvisionManagementController-> activeAccountManagementConfirmReplenishment");
        return ConfirmReplenishmentResp;
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
        try {
            LOGGER.debug("Enter ProvisionManagementController-> activeAccountManagementConfirmReplenishment ");
            ValidateOTPRequest validateOTPRequestPojo = (ValidateOTPRequest) hCEControllerSupport.requestFormation(validateOTPRequest, ValidateOTPRequest.class);
            validateOtpResp = provisionManagementService.validateOTP(validateOTPRequestPojo);
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
    @RequestMapping(value = "/getStepUpOptions",method = RequestMethod.POST)
    public Map<String ,Object>getStepUpOptions(@RequestBody String getStepUpOptionsRequest){
        Map <String,Object> getStepUpOptions= null;
        try{
            LOGGER.debug("Enter ProvisionManagementController-> getStepUpOptions ");
            GetStepUpOptionsRequest getStepUpOptionsRequestPojo = (GetStepUpOptionsRequest)hCEControllerSupport.requestFormation(getStepUpOptionsRequest,GetStepUpOptionsRequest.class);
            getStepUpOptions = provisionManagementService.getStepUpOptions(getStepUpOptionsRequestPojo);
        }catch(HCEActionException getSteopupOptionuestHceActionException){
            LOGGER.error("Exception Occured in ProvisionManagementController->getStepUpOptions",getSteopupOptionuestHceActionException);
            throw getSteopupOptionuestHceActionException;
        }catch (Exception getSteopupOptionuestExcetption) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->getStepUpOptions", getSteopupOptionuestExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getStepUpOptions;
    }

    @ResponseBody
    @RequestMapping(value = "/replenishODAData",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String ,Object>replenishODAData( @RequestBody String replenishODADataRequest){
        LOGGER.debug("Enter ProvisionManagementController-> replenishODAData ");
        ReplenishODADataRequest replenishODADataRequestPojo = null;
        Map <String,Object> replineshODADataResp= null;
        try {
            replenishODADataRequestPojo = (ReplenishODADataRequest) hCEControllerSupport.requestFormation(replenishODADataRequest, ReplenishODADataRequest.class);
            replineshODADataResp = provisionManagementService.replenishODAData(replenishODADataRequestPojo);
        }catch (HCEActionException replenishODAData){
            LOGGER.error("Exception Occured in ProvisionManagementController->activeAccountManagementReplenish",replenishODAData);
            throw replenishODAData;
        }catch (Exception replenishODAData) {
            LOGGER.error(" Exception Occured in ProvisionManagementController->activeAccountManagementReplenish", replenishODAData);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("exit ProvisionManagementController-> activeAccountManagementReplenish ");
        return replineshODADataResp;
    }
}