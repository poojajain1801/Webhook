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
import com.comviva.mfs.hce.appserver.mapper.pojo.ApproveHvtRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetHvtValueRequest;
import com.comviva.mfs.hce.appserver.service.contract.ConfigurationService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/configuration")
public class ConfigurationManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagementController.class);

    @Autowired
    private HCEControllerSupport hCEControllerSupport;
    @Autowired
    private ConfigurationService configurationService ;

    @ResponseBody
    @RequestMapping(value = "/setHvtValue", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> setHvtValue(@RequestBody String setHvtValueReq) {
        SetHvtValueRequest setHvtValueRequestpojo = null;
        Map <String,Object>setHvtValueResponse= null;
        try {
            setHvtValueRequestpojo = (SetHvtValueRequest)hCEControllerSupport.requestFormation(setHvtValueReq,SetHvtValueRequest.class);
            setHvtValueResponse =  configurationService.setHvtValue(setHvtValueRequestpojo);
        }catch (HCEActionException setHvtValueHceActionException){
            LOGGER.error("Exception Occured in ConfigurationManagementController->setHvtValue",setHvtValueHceActionException);
            throw setHvtValueHceActionException;
        }catch (Exception setHvtValueExcetption) {
            LOGGER.error(" Exception Occured in ConfigurationManagementController->setHvtValue", setHvtValueExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return setHvtValueResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/getPendingRequests", method = RequestMethod.GET)
    public Map<String,Object> getPendingRequests() {
        Map <String,Object>getPendingRequestsResp= null;
        try {
            getPendingRequestsResp =  configurationService.getPendingRequests();
        }catch (HCEActionException getPendingRequestsHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getPendingRequests",getPendingRequestsHceActionException);
            throw getPendingRequestsHceActionException;
        }catch (Exception getPendingRequestsExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getPendingRequests", getPendingRequestsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getPendingRequestsResp;
    }

    @ResponseBody
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> approveHvt(@RequestBody String approveHvtRequest) {
        ApproveHvtRequest approveHvtRequestPojo = null;
        Map <String,Object>approveHvtResponse= null;
        try {
            approveHvtRequestPojo = (ApproveHvtRequest)hCEControllerSupport.requestFormation(approveHvtRequest,ApproveHvtRequest.class);
            approveHvtResponse =  configurationService.approveHvt(approveHvtRequestPojo);
        }catch (HCEActionException approveHvtHceActionException){
            LOGGER.error("Exception Occured in ConfigurationManagementController->approveHvt",approveHvtHceActionException);
            throw approveHvtHceActionException;
        }catch (Exception approveHvtExcetption) {
            LOGGER.error(" Exception Occured in ConfigurationManagementController->approveHvt", approveHvtExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return approveHvtResponse;
    }

}
