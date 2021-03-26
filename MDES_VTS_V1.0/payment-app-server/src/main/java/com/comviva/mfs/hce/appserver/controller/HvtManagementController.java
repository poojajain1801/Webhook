package com.comviva.mfs.hce.appserver.controller;


import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.FcmAcknowledgementRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.HvtManagementRequest;
import com.comviva.mfs.hce.appserver.service.contract.HvtManagementService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/hvt/")
public class HvtManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HvtManagementController.class);

    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    @Autowired
    private HvtManagementService hvtManagementService;

    @ResponseBody
    @PostMapping(value = "/setNewHvtLimit")
    public Map<String, Object> setHvtlimit(@RequestBody String hvtManagmentReq) {
        LOGGER.info("request landed inside hvt limit controller {}", HCEUtil.convertDateToTimestamp(new Date()));
        Map<String, Object> hvtResponse = new HashMap<>();
        HvtManagementRequest hvtManagementRequestPojo;

        try {
            hvtManagementRequestPojo = (HvtManagementRequest) hCEControllerSupport.requestFormation(hvtManagmentReq, HvtManagementRequest.class);

            if(Integer.parseInt(hvtManagementRequestPojo.getHvtLimit()) < 0) {
                hvtResponse.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getServiceFailed());
                hvtResponse.put(HCEConstants.MESSAGE, "hvt limit can not be less than zero");
                return hvtResponse;
            }

            hvtResponse = hvtManagementService.saveHvtLimit(hvtManagementRequestPojo);
        } catch (HCEActionException hvtExecption) {
            LOGGER.error(" Exception Occured in Enter HvtManagemntController->setHvtlimit", hvtExecption);
            throw hvtExecption;
        } catch (Exception hvtLimit) {
            LOGGER.error(" Exception Occured in Enter HvtManagemntController->setHvtlimit", hvtLimit);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return hvtResponse;
    }

    /**
     * controller to store acknowledgement received from SDK
     * we had to use PostMapping due to rnsID is private info
     */
    @ResponseBody
    @PostMapping(value = "/ackHvt")
    public ResponseEntity ackHvtLimit(@RequestBody String ackHvtLimitRequest) {
        LOGGER.info("request landed inside hvt limit controller {}", HCEUtil.convertDateToTimestamp(new Date()));

        FcmAcknowledgementRequest fcmAcknowledgementRequestPojo;
        try {
            fcmAcknowledgementRequestPojo = (FcmAcknowledgementRequest) hCEControllerSupport.requestFormation(ackHvtLimitRequest,
                    FcmAcknowledgementRequest.class);
            hvtManagementService.modifySchedulerDbOnAck(fcmAcknowledgementRequestPojo);
        } catch (HCEActionException ackHvtLimitException) {
            LOGGER.error(" Exception Occured in Enter HvtManagemntController->setHvtlimit", ackHvtLimitException);
            throw ackHvtLimitException;
        } catch (Exception ackHvtLimitException) {
            LOGGER.error(" Exception Occured in Enter HvtManagemntController->setHvtlimit", ackHvtLimitException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return new ResponseEntity(HttpStatus.OK);
    }


    @ResponseBody
    @PostMapping(value = "/fetchHvtLimit")
    public Map<String, Object> fetchHvtlimit() {
        LOGGER.info("request landed inside hvt limit controller {}", HCEUtil.convertDateToTimestamp(new Date()));
        Map<String, Object> hvtResponse;

        try {
            hvtResponse = hvtManagementService.fetchHvtLimit();
        } catch (HCEActionException hvtExecption) {
            LOGGER.error(" Exception Occured in Enter HvtManagemntController->fetchHvtlimit", hvtExecption);
            throw hvtExecption;
        } catch (Exception hvtLimit) {
            LOGGER.error(" Exception Occured in Enter HvtManagemntController->fetchHvtlimit", hvtLimit);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return hvtResponse;
    }
}
