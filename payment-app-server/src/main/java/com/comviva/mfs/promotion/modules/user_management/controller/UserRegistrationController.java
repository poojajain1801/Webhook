package com.comviva.mfs.promotion.modules.user_management.controller;

import com.comviva.mfs.promotion.modules.user_management.model.UserRegistrationResponse;
import com.comviva.mfs.promotion.modules.user_management.service.contract.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/user")
public class UserRegistrationController {

    @Autowired
    private UserDetailService userDetailService;

    @ResponseBody
    @RequestMapping(value = "/userRegistration", method = RequestMethod.POST)
    public UserRegistrationResponse registerUser(@RequestParam(required = true, name = "userName") String userName) {
        return userDetailService.registerUser(userName);
    }

    @ResponseBody
    @RequestMapping(value = "/activateUser", method = RequestMethod.POST)
    public UserRegistrationResponse activateUser(@RequestParam(required = true, name = "userName") String userName,@RequestParam(required = true, name = "activationCode") String activationCode) {
        return userDetailService.activateUser(userName,activationCode);
    }
}
