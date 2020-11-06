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
import com.comviva.mfs.hce.appserver.service.contract.CountManagementService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/count")
public class CountManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountManagementController.class);
    @Autowired
    private HCEControllerSupport hCEControllerSupport;
    @Autowired
    private CountManagementService countManagementService;

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
