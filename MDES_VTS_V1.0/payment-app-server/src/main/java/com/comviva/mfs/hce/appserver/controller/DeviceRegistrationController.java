package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegDeviceParam;

import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Map<String,Object> registerDevice(@RequestBody EnrollDeviceRequest enrollDeviceRequest) {
        return deviceDetailService.registerDevice(enrollDeviceRequest);
    }

}

