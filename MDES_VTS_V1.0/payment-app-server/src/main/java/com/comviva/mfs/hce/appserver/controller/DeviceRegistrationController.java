package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegDeviceParam;

import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    public DeviceRegistrationController(DeviceDetailService deviceDetailService) {
        this.deviceDetailService = deviceDetailService;
    }
    @ResponseBody
    @RequestMapping(value = "/deviceRegistration", method = RequestMethod.POST)
    public Map<String,Object> registerDevice(@RequestBody EnrollDeviceRequest enrollDeviceRequest) {
        LOGGER.debug("Enter DeviceRegistrationController->registerDevice");
        Map<String,Object> registerDeviceResponse = null;
        registerDeviceResponse =  deviceDetailService.registerDevice(enrollDeviceRequest);
        LOGGER.debug("Exit DeviceRegistrationController->registerDevice");
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

