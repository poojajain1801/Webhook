package com.comviva.mfs.promotion.modules.device_management.controller;

import com.comviva.mfs.promotion.modules.device_management.model.DeviceRegistrationResponse;

import com.comviva.mfs.promotion.modules.device_management.model.RegDeviceParam;
import com.comviva.mfs.promotion.modules.device_management.service.contract.DeviceDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/device")
public class DeviceRegistrationController {

    @Autowired
    private DeviceDetailService deviceDetailService;

    public DeviceRegistrationController(DeviceDetailService deviceDetailService) {
        this.deviceDetailService = deviceDetailService;
    }
    @ResponseBody
    @RequestMapping(value = "/deviceRegistration", method = RequestMethod.POST)
    public DeviceRegistrationResponse registerDevice(@RequestBody RegDeviceParam regDeviceParam) {
        return deviceDetailService.registerDevice(regDeviceParam);
    }

}

