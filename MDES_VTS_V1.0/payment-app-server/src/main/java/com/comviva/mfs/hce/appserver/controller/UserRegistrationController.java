/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetLanguageReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetLanguageReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserLifecycleManagementReq;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */

@CrossOrigin(maxAge = 3600)
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
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> registerUser(@RequestBody String registerUserRequest){
        Map<String,Object> registerUser = null;
        RegisterUserRequest registerUserRequestPojo = null;
        try{
            registerUserRequestPojo =(RegisterUserRequest) hCEControllerSupport.requestFormation(registerUserRequest,RegisterUserRequest.class);
            registerUser = userDetailService.registerUser(registerUserRequestPojo);
            LOGGER.debug("Exit UserRegistrationController->registerUser");
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
        }catch (HCEActionException getLanguageHCEActionException){
            LOGGER.error("Exception Occured in Enter UserRegistrationController->getLanguage",getLanguageHCEActionException);
            throw getLanguageHCEActionException;
        }catch (Exception getLanguageException) {
            LOGGER.error(" Exception Occured in Enter UserRegistrationController->getLanguage", getLanguageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getLanguageResp;
    }




    @ResponseBody
    @RequestMapping(value = "/setLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> setLanguage(@RequestBody String setLanguageReq){
        Map<String,Object> setLanguageResp = null;
        SetLanguageReq setLanguageReqPojo = null;
        try{
            setLanguageReqPojo =(SetLanguageReq) hCEControllerSupport.requestFormation(setLanguageReq,SetLanguageReq.class);
            setLanguageResp = userDetailService.setLanguage(setLanguageReqPojo);
            LOGGER.debug("Exit setLanguageController->setLanguage");
        }catch (HCEActionException setLanguageHCEActionException){
            LOGGER.error("Exception Occured in Enter UserRegistrationController->setLanguage",setLanguageHCEActionException);
            throw setLanguageHCEActionException;
        }catch (Exception setLanguageException) {
            LOGGER.error(" Exception Occured in Enter UserRegistrationController->setLanguage", setLanguageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return setLanguageResp;
    }


    @ResponseBody
    @RequestMapping(value = "/userLifecycleManagement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> userLifecycleManagement(@RequestBody String userLifecycleManagementRequest){
        Map<String,Object> userLifecycleManagementResp = null;
        UserLifecycleManagementReq userLifecycleManagementPojo = null;
        try{
            userLifecycleManagementPojo =(UserLifecycleManagementReq) hCEControllerSupport.requestFormation(userLifecycleManagementRequest,UserLifecycleManagementReq.class);
            userLifecycleManagementResp = userDetailService.userLifecycleManagement(userLifecycleManagementPojo);
            LOGGER.debug("Exit UserRegistrationController->registerUser");
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
