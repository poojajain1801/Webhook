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
            LOGGER.debug("Enter UserRegistrationController->registerUser");
            String s = MDC.get("tenantId");
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
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }


        return registerUser;
    }


}