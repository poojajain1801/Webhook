package com.comviva.mfs.promotion.modules.mpamanagement.controller;

import com.comviva.mfs.promotion.modules.mpamanagement.model.DeviceRegParam;
import com.comviva.mfs.promotion.modules.mpamanagement.model.DeviceRegisterResp;
import com.comviva.mfs.promotion.modules.mpamanagement.service.contract.RegisterDeviceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contains all MPA Management APIs.
 */
@RestController
@RequestMapping("mdes/mpamanagement/1/0")
public class MpaManagementController {

    @Autowired
    private RegisterDeviceService registerDeviceService;

    public MpaManagementController(RegisterDeviceService deviceDetailService) {
        this.registerDeviceService = deviceDetailService;
    }

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map registerDevice(@RequestBody DeviceRegParam regDeviceParam) {
        return registerDeviceService.registerDevice(regDeviceParam);
    }
}

