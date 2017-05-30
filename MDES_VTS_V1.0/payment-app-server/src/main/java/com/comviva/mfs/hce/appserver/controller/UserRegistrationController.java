package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.UserRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/user")
public class UserRegistrationController {

    @Autowired
    private UserDetailService userDetailService;

    @ResponseBody
    @RequestMapping(value = "/userRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object>registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        return userDetailService.registerUser(registerUserRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/activateUser", method = RequestMethod.POST)
    public Map<String,Object> activateUser(@RequestBody ActivateUserRequest activateUserRequest) {
        return userDetailService.activateUser(activateUserRequest);
    }
}