/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p/>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p/>
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
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;

import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Date;
import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@CrossOrigin(maxAge = 3600)
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
        LOGGER.info("Register device request lands --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
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
        LOGGER.info("Register device response goes --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
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

}

