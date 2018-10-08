package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.UserRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/user")
public class UserRegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationController.class);


    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private HCEControllerSupport hCEControllerSupport;


    @ServiceFlowStep("paymentApp")
    @ResponseBody
    @RequestMapping(value = "/userRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> registerUser(@RequestBody String registerUserRequest){
        Map<String,Object> registerUser = null;
        RegisterUserRequest registerUserRequestPojo = null;
        try{
            registerUserRequestPojo =(RegisterUserRequest) hCEControllerSupport.requestFormation(registerUserRequest,RegisterUserRequest.class);
            registerUser = userDetailService.registerUser(registerUserRequestPojo);
            LOGGER.debug("Exit UserRegistrationController->registerUser");
        }catch (HCEValidationException registerUserValidationException){
            LOGGER.error("Exception Occured in  UserRegistrationController->registerUser",registerUserValidationException);
            throw registerUserValidationException;
        }catch (HCEActionException regUserHCEActionException){
            LOGGER.error("Exception Occured in Enter UserRegistrationController->registerUser",regUserHCEActionException);
           throw regUserHCEActionException;
        }catch (Exception regUserException) {
            LOGGER.error(" Exception Occured in Enter UserRegistrationController->registerUser", regUserException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }


        return registerUser;
    }

    @ServiceFlowStep("paymentApp")

    @ResponseBody

    @RequestMapping(value = "/getLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

    public Map<String,Object> getLanguage(@RequestBody String getLanguageReq){

        Map<String,Object> getLanguageResp = null;

        GetLanguageReq getLanguageReqPojo = null;

        try{

            getLanguageReqPojo =(GetLanguageReq) hCEControllerSupport.requestFormation(getLanguageReq,GetLanguageReq.class);

            getLanguageResp = userDetailService.getLanguage(getLanguageReqPojo);

            LOGGER.debug("Exit getLanguageController->getLanguage");

        }catch (HCEValidationException getLanguageValidationException){

            LOGGER.error("Exception Occured in  UserRegistrationController->getLanguage",getLanguageValidationException);

            throw getLanguageValidationException;

        }catch (HCEActionException getLanguageHCEActionException){

            LOGGER.error("Exception Occured in Enter UserRegistrationController->getLanguage",getLanguageHCEActionException);

            throw getLanguageHCEActionException;

        }catch (Exception getLanguageException) {

            LOGGER.error(" Exception Occured in Enter UserRegistrationController->getLanguage", getLanguageException);

            throw new HCEActionException(HCEMessageCodes.getServiceFailed());

        }

        return getLanguageResp;

    }



    @ServiceFlowStep("paymentApp")

    @ResponseBody

    @RequestMapping(value = "/setLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

    public Map<String,Object> setLanguage(@RequestBody String setLanguageReq){

        Map<String,Object> setLanguageResp = null;

        SetLanguageReq setLanguageReqPojo = null;

        try{

            setLanguageReqPojo =(SetLanguageReq) hCEControllerSupport.requestFormation(setLanguageReq,SetLanguageReq.class);

            setLanguageResp = userDetailService.setLanguage(setLanguageReqPojo);

            LOGGER.debug("Exit setLanguageController->setLanguage");

        }catch (HCEValidationException setLanguageValidationException){

            LOGGER.error("Exception Occured in  UserRegistrationController->setLanguage",setLanguageValidationException);

            throw setLanguageValidationException;

        }catch (HCEActionException setLanguageHCEActionException){

            LOGGER.error("Exception Occured in Enter UserRegistrationController->setLanguage",setLanguageHCEActionException);

            throw setLanguageHCEActionException;

        }catch (Exception setLanguageException) {

            LOGGER.error(" Exception Occured in Enter UserRegistrationController->setLanguage", setLanguageException);

            throw new HCEActionException(HCEMessageCodes.getServiceFailed());

        }

        return setLanguageResp;

    }


    @ServiceFlowStep("paymentApp")
    @ResponseBody
    @RequestMapping(value = "/userLifecycleManagement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> userLifecycleManagement(@RequestBody String userLifecycleManagementRequest){
        Map<String,Object> userLifecycleManagementResp = null;
        UserLifecycleManagementReq userLifecycleManagementPojo = null;
        try{
            userLifecycleManagementPojo =(UserLifecycleManagementReq) hCEControllerSupport.requestFormation(userLifecycleManagementRequest,UserLifecycleManagementReq.class);
            userLifecycleManagementResp = userDetailService.userLifecycleManagement(userLifecycleManagementPojo);
            LOGGER.debug("Exit UserRegistrationController->registerUser");
        }catch (HCEValidationException registerUserValidationException){
            LOGGER.error("Exception Occured in  UserRegistrationController->registerUser",registerUserValidationException);
            throw registerUserValidationException;
        }catch (HCEActionException regUserHCEActionException){
            LOGGER.error("Exception Occured in Enter UserRegistrationController->registerUser",regUserHCEActionException);
            throw regUserHCEActionException;
        }catch (Exception regUserException) {
            LOGGER.error(" Exception Occured in Enter UserRegistrationController->registerUser", regUserException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }


        return userLifecycleManagementResp;
    }




}