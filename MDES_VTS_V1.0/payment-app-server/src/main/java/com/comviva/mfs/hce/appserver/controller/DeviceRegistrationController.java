package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @ResponseBody
    @RequestMapping(value = "/deviceRegistration", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> registerDevice(@RequestBody String enrollDeviceRequest) {

        Map<String,Object> registerDeviceResponse = null;
        EnrollDeviceRequest enrollDeviceRequestPojo = null;
        try{
            enrollDeviceRequestPojo =(EnrollDeviceRequest) hCEControllerSupport.requestFormation(enrollDeviceRequest,EnrollDeviceRequest.class);
            registerDeviceResponse = deviceDetailService.registerDevice(enrollDeviceRequestPojo);
        }catch (HCEActionException regDeviceHCEActionException){
            LOGGER.error("Exception Occured in DeviceRegistrationController->registerDevice",regDeviceHCEActionException);
            throw regDeviceHCEActionException;
        }catch (Exception regDeviceException) {
            LOGGER.error(" Exception Occured in DeviceRegistrationController->registerDevice", regDeviceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return registerDeviceResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/deRegister", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> unRegister(@RequestBody String unRegisterReq) {
        Map<String,Object> unRegisterResponse = null;
        UnRegisterReq unRegisterReqPojo = null;
        try{
            unRegisterReqPojo =(UnRegisterReq) hCEControllerSupport.requestFormation(unRegisterReq,UnRegisterReq.class);
            unRegisterResponse = deviceDetailService.unRegisterDevice(unRegisterReqPojo);
        }catch (HCEActionException deRegHCEActionException){
            LOGGER.error("Exception Occured in Enter DeviceRegistrationController->registerDevice",deRegHCEActionException);
            throw deRegHCEActionException;
        }catch (Exception deRegException) {
            LOGGER.error(" Exception Occured in Enter DeviceRegistrationController->registerDevice", deRegException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return unRegisterResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/getDeviceInfo", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> getDeviceInfo(@RequestBody String getDeviceInfoRequest) {

        Map<String,Object> getDeviceInfoResponse = null;
        GetDeviceInfoRequest getDeviceInfoRequestPojo = null;
        try{
            getDeviceInfoRequestPojo =(GetDeviceInfoRequest) hCEControllerSupport.requestFormation(getDeviceInfoRequest,GetDeviceInfoRequest.class);
            getDeviceInfoResponse = deviceDetailService.getDeviceInfo(getDeviceInfoRequestPojo);
        }catch (HCEActionException getDeviceInfoHCEActionException){
            LOGGER.error("Exception Occured in DeviceRegistrationController->getDeviceInfo",getDeviceInfoHCEActionException);
            throw getDeviceInfoHCEActionException;
        }catch (Exception getDeviceInfoException) {
            LOGGER.error(" Exception Occured in DeviceRegistrationController->getDeviceInfo", getDeviceInfoException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getDeviceInfoResponse;
    }
}

