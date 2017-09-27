package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.UserRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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


    @ResponseBody
    @RequestMapping(value = "/userRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> registerUser(@RequestBody String registerUserRequest, HttpServletRequest httpRequest){
        Map<String,Object> registerUser = null;
        RegisterUserRequest registerUserRequestPojo = null;
        try{
            LOGGER.debug("Enter UserRegistrationController->registerUser");
            registerUserRequestPojo =(RegisterUserRequest) hCEControllerSupport.requestFormation(registerUserRequest,RegisterUserRequest.class);
            registerUser = userDetailService.registerUser(registerUserRequestPojo);
        }catch (HCEValidationException registerUserValidationException){
            LOGGER.error("Exception Occured in  UserRegistrationController->registerUser",registerUserValidationException);
            registerUser = hCEControllerSupport.formResponse(registerUserValidationException.getMessageCode(),registerUserValidationException.getMessage());
        }catch (HCEActionException regUserHCEActionException){
            LOGGER.error("Exception Occured in Enter UserRegistrationController->registerUser",regUserHCEActionException);
            registerUser = hCEControllerSupport.formResponse(regUserHCEActionException.getMessageCode());
        }catch (Exception regUserException) {
            LOGGER.error(" Exception Occured in Enter UserRegistrationController->registerUser", regUserException);
            registerUser = hCEControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
        hCEControllerSupport.prepareRequest(registerUserRequest,registerUser,httpRequest);
        LOGGER.debug("Exit UserRegistrationController->registerUser");

        return registerUser;
    }


    @ResponseBody
    @RequestMapping(value = "/activateUser", method = RequestMethod.POST)
    public Map<String,Object> activateUser(@RequestBody String activateUserRequest, HttpServletRequest httpServletRequest) {
        Map<String,Object> activateUser = null;
        ActivateUserRequest activateUserRequestPojo = null;
        try{
            LOGGER.debug("Enter UserRegistrationController->activateUser");
            activateUserRequestPojo =(ActivateUserRequest) hCEControllerSupport.requestFormation(activateUserRequest,ActivateUserRequest.class);
            activateUser = userDetailService.activateUser(activateUserRequestPojo);
        }catch (HCEValidationException registerUserValidationException){
            LOGGER.error("Exception Occured in  UserRegistrationController->activateUser",registerUserValidationException);
            activateUser = hCEControllerSupport.formResponse(registerUserValidationException.getMessageCode(),registerUserValidationException.getMessage());
        }catch (HCEActionException regUserHCEActionException){
            LOGGER.error("Exception Occured in Enter UserRegistrationController->activateUser",regUserHCEActionException);
            activateUser = hCEControllerSupport.formResponse(regUserHCEActionException.getMessageCode());
        }catch (Exception regUserException) {
            LOGGER.error(" Exception Occured in Enter UserRegistrationController->activateUser", regUserException);
            activateUser = hCEControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
        hCEControllerSupport.prepareRequest(activateUserRequest,activateUser,httpServletRequest);
        LOGGER.debug("Exit UserRegistrationController->registerUser");

        return activateUser;
    }
}