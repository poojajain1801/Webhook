package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegDeviceParam;

import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/device/")
public class DeviceRegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationController.class);

    @Autowired
    private DeviceDetailService deviceDetailService;

    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    public DeviceRegistrationController(DeviceDetailService deviceDetailService) {
        this.deviceDetailService = deviceDetailService;
    }
    @ResponseBody
    @RequestMapping(value = "/deviceRegistration", method = RequestMethod.POST)
    public Map<String,Object> registerDevice(@RequestBody String  enrollDeviceRequest , HttpServletRequest httpRequest) {
        Map<String,Object> registerDeviceResponse = null;
        EnrollDeviceRequest enrollDeviceRequestPojo = null;
        try{
            LOGGER.debug("Enter DeviceRegistrationController->registerDevice"+enrollDeviceRequest);
            enrollDeviceRequestPojo =(EnrollDeviceRequest) hCEControllerSupport.requestFormation(enrollDeviceRequest,EnrollDeviceRequest.class);
            registerDeviceResponse = deviceDetailService.registerDevice(enrollDeviceRequestPojo);
            LOGGER.debug("Exit DeviceRegistrationController->registerDevice");
        }catch (HCEValidationException registerDeviceValidationException){
            LOGGER.error("Exception Occured in  DeviceRegistrationController->registerDevice",registerDeviceValidationException);
            registerDeviceResponse = hCEControllerSupport.formResponse(registerDeviceValidationException.getMessageCode(),registerDeviceValidationException.getMessage());
        }catch (HCEActionException regDeviceHCEActionException){
            LOGGER.error("Exception Occured in Enter DeviceRegistrationController->registerDevice",regDeviceHCEActionException);
            registerDeviceResponse = hCEControllerSupport.formResponse(regDeviceHCEActionException.getMessageCode());
        }catch (Exception regDeviceException) {
            LOGGER.error(" Exception Occured in Enter DeviceRegistrationController->registerDevice", regDeviceException);
            registerDeviceResponse = hCEControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
        hCEControllerSupport.prepareRequest(enrollDeviceRequest,registerDeviceResponse,httpRequest);
        return registerDeviceResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/unRegister", method = RequestMethod.POST)
    public Map<String,Object> unRegister(@RequestBody UnRegisterReq unRegisterReq) {
        LOGGER.debug("Enter DeviceRegistrationController->unRegister");
        Map<String,Object> unRegisterResponse = null;
        unRegisterResponse =  deviceDetailService.unRegisterDevice(unRegisterReq);
        LOGGER.debug("Exit DeviceRegistrationController->unRegister");
        return unRegisterResponse;
    }

}

