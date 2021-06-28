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
import com.comviva.mfs.hce.appserver.mapper.pojo.AuditLogsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConsumerReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserDeviceCardReportReq;
import com.comviva.mfs.hce.appserver.service.contract.ReportsService;
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
 * Created by rishikesh.kumar on 09-01-2019.
 */

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportsManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsManagementController.class);
    @Autowired
    private HCEControllerSupport hCEControllerSupport;
    @Autowired
    private ReportsService reportsService;

    @ResponseBody
    @RequestMapping(value = "/consumerReport", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> consumerReport(@RequestBody String consumerReport) {
        Map<String, Object> consumerReportResp = null;
        ConsumerReportReq consumerReportReqPojo = null;
        try{
            consumerReportReqPojo =(ConsumerReportReq) hCEControllerSupport.requestFormation(consumerReport,ConsumerReportReq.class);
            consumerReportResp  = reportsService.consumerReport(consumerReportReqPojo);
        }catch (HCEActionException consumerReportHceActionException){
            LOGGER.error("Exception Occured in ReportManagementController->consumerReport",consumerReportHceActionException);
            throw consumerReportHceActionException;
        }catch (Exception consumerReportExcetption) {
            LOGGER.error(" Exception Occured in ReportManagementController->consumerReport", consumerReportExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return consumerReportResp;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceReport", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> deviceReport(@RequestBody String deviceReport) {
        Map<String, Object> deviceReportResp = null;
        DeviceReportReq deviceReportReqPojo = null;
        try{
            deviceReportReqPojo =(DeviceReportReq) hCEControllerSupport.requestFormation(deviceReport,DeviceReportReq.class);
            deviceReportResp  = reportsService.deviceReport(deviceReportReqPojo);
        }catch (HCEActionException deviceReportHceActionException){
            LOGGER.error("Exception Occured in ReportManagementController->deviceReport",deviceReportHceActionException);
            throw deviceReportHceActionException;
        }catch (Exception deviceReportExcetption) {
            LOGGER.error(" Exception Occured in ReportManagementController->deviceReport", deviceReportExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return deviceReportResp;
    }

    @ResponseBody
    @RequestMapping(value = "/userDeviceCardMappingReport", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> userDeviceCardReport(@RequestBody String userDeviceCardReport) {
        Map<String, Object> userDeviceCardReportResp = null;
        UserDeviceCardReportReq userDeviceCardReportReqPojo = null;
        try{
            userDeviceCardReportReqPojo =(UserDeviceCardReportReq) hCEControllerSupport.requestFormation(userDeviceCardReport,UserDeviceCardReportReq.class);
            userDeviceCardReportResp  = reportsService.userDeviceCardReport(userDeviceCardReportReqPojo);
        }catch (HCEActionException userDeviceCardReportHceActionException){
            LOGGER.error("Exception Occured in ReportManagementController->userDeviceCardReport",userDeviceCardReportHceActionException);
            throw userDeviceCardReportHceActionException;
        }catch (Exception userDeviceCardReportExcetption) {
            LOGGER.error(" Exception Occured in ReportManagementController->userDeviceCardReport", userDeviceCardReportExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return userDeviceCardReportResp;
    }

    @ResponseBody
    @RequestMapping(value = "/auditLogs", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> auditLogs(@RequestBody String auditLogs) {
        Map<String, Object> auditLogsResp = null;
        AuditLogsRequest auditLogsRequest = null;
        try{
            auditLogsRequest =(AuditLogsRequest) hCEControllerSupport.requestFormation(auditLogs,AuditLogsRequest.class);
            auditLogsResp  = reportsService.auditLogs(auditLogsRequest);
        }catch (HCEActionException auditLogsHCEExp){
            LOGGER.error("Exception Occured in ReportManagementController->auditLogs",auditLogsHCEExp);
            throw auditLogsHCEExp;
        }catch (Exception auditLogsExp) {
            LOGGER.error(" Exception Occured in ReportManagementController->auditLogs", auditLogsExp);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return auditLogsResp;
    }
}
