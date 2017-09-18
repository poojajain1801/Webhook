package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.ActivateUserRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/user")
public class UserRegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationController.class);


    @Autowired
    private UserDetailService userDetailService;

    @ResponseBody
    @RequestMapping(value = "/userRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        LOGGER.debug("Enter UserRegistrationController->registerUser");
        Map<String,Object> registerUser = null;
        registerUser = userDetailService.registerUser(registerUserRequest);
        LOGGER.debug("Exit UserRegistrationController->registerUser");
        return registerUser;
    }


    @ResponseBody
    @RequestMapping(value = "/activateUser", method = RequestMethod.POST)
    public Map<String,Object> activateUser(@RequestBody ActivateUserRequest activateUserRequest) {
        Map<String,Object> activateUser = null;
        LOGGER.debug("Enter UserRegistrationController->activateUser");
        activateUser =userDetailService.activateUser(activateUserRequest);
        LOGGER.debug("Exit UserRegistrationController->activateUser");
        return activateUser;
    }
}