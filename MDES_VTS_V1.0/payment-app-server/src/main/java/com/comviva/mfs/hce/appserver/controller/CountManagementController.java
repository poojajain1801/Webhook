package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConsumerReportReq;
import com.comviva.mfs.hce.appserver.service.contract.CountManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/count")
public class CountManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountManagementController.class);
    @Autowired
    private HCEControllerSupport hCEControllerSupport;
    @Autowired
    CountManagementService countManagementService;

    @ResponseBody
    @RequestMapping(value = "/userCount", method = RequestMethod.GET)
    public Map<String, Object> getUserCount() {
        Map<String, Object> userCountResp = null;
        try{
            userCountResp  = countManagementService.getUserCount();
        }catch (HCEActionException getUserCountHceActionException){
            LOGGER.error("Exception Occured in CountManagementController->getUserCount",getUserCountHceActionException);
            throw getUserCountHceActionException;
        }catch (Exception getUserCountExcetption) {
            LOGGER.error(" Exception Occured in CountManagementController->getUserCount", getUserCountExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return userCountResp;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceCount", method = RequestMethod.GET)
    public Map<String, Object> getDeviceCount() {
        Map<String, Object> deviceCountResp = null;
        try{
            deviceCountResp  = countManagementService.getDeviceCount();
        }catch (HCEActionException getDeviceCountHceActionException){
            LOGGER.error("Exception Occured in CountManagementController->getDeviceCount",getDeviceCountHceActionException);
            throw getDeviceCountHceActionException;
        }catch (Exception getDeviceCountExcetption) {
            LOGGER.error(" Exception Occured in CountManagementController->getDeviceCountExcetption", getDeviceCountExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return deviceCountResp;
    }

    @ResponseBody
    @RequestMapping(value = "/tokenCount", method = RequestMethod.GET)
    public Map<String, Object> getTokenCount() {
        Map<String, Object> tokenCountResp = null;
        try{
            tokenCountResp  = countManagementService.getTokenCount();
        }catch (HCEActionException getTokenCountHceActionException){
            LOGGER.error("Exception Occured in CountManagementController->getTokenCount",getTokenCountHceActionException);
            throw getTokenCountHceActionException;
        }catch (Exception getTokenCountExcetption) {
            LOGGER.error(" Exception Occured in CountManagementController->getTokenCount", getTokenCountExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return tokenCountResp;
    }

    @ResponseBody
    @RequestMapping(value = "/activeTokenCount", method = RequestMethod.GET)
    public Map<String, Object> getActiveTokenCount() {
        Map<String, Object> ActiveTokenCountResp = null;
        try{
            ActiveTokenCountResp  = countManagementService.getActiveTokenCount();
        }catch (HCEActionException getActiveTokenCountHceActionException){
            LOGGER.error("Exception Occured in CountManagementController->getActiveTokenCount",getActiveTokenCountHceActionException);
            throw getActiveTokenCountHceActionException;
        }catch (Exception getActiveTokenCountExcetption) {
            LOGGER.error(" Exception Occured in CountManagementController->getActiveTokenCount", getActiveTokenCountExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return ActiveTokenCountResp;
    }
}
