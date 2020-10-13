package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;
import com.comviva.mfs.hce.appserver.service.contract.NotificationServiceVisaService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotificationServiceControllerVisa {

    @Autowired
    private NotificationServiceVisaService notificationServiceVisaService;

    @Autowired
    private HCEControllerSupport hCEControllerSupport;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationController.class);

    @ResponseBody
    @RequestMapping(value = "/provisionedToken", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> notifyLCMEvent(@RequestBody String notificationServiceReq, @RequestParam String eventType,@RequestParam String apiKey) {
        LOGGER.debug("Enter NotificationServiceVisa->notify");
        NotificationServiceReq notificationServiceReqPojo = null;
        Map<String, Object> notifyResponse = null;
        try {
            notificationServiceReqPojo = (NotificationServiceReq) hCEControllerSupport.requestFormation(notificationServiceReq, NotificationServiceReq.class);
            notifyResponse = notificationServiceVisaService.notifyLCMEvent(notificationServiceReqPojo, apiKey, eventType);
        }catch (HCEActionException notificationServiceHCEActionException){
            LOGGER.error("Exception Occured in DeviceRegistrationController->registerDevice",notificationServiceHCEActionException);
            throw notificationServiceHCEActionException;
        }catch (Exception notificationServiceException) {
            LOGGER.error(" Exception Occured in DeviceRegistrationController->registerDevice", notificationServiceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit DeviceRegNotificationServiceVisaistrationController->notify");
        return notifyResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/panMetadata", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> notifyCardMetadataUpdateEvent(@RequestBody String notificationServiceReq ,@RequestParam String apiKey) {
        LOGGER.debug("Enter NotificationServiceVisa->notifyCardMetadataUpdateEvent");
        NotificationServiceReq notificationServiceReqpojo = null;
        Map<String,Object> notifyResponse = null;
        try {
            notificationServiceReqpojo = (NotificationServiceReq) hCEControllerSupport.requestFormation(notificationServiceReq, NotificationServiceReq.class);
            notifyResponse = notificationServiceVisaService.notifyPanMetadataUpdate(notificationServiceReqpojo, apiKey);
        }catch (HCEActionException notificationServiceHCEActionException){
            LOGGER.error("Exception Occured in DeviceRegistrationController->registerDevice",notificationServiceHCEActionException);
            throw notificationServiceHCEActionException;
        }catch (Exception notificationServiceException) {
            LOGGER.error(" Exception Occured in DeviceRegistrationController->registerDevice", notificationServiceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit DeviceRegNotificationServiceVisaistrationController->notifyCardMetadataUpdateEvent");
        return notifyResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/paymentTxns", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> notifyTxnDetailsUpdateEvent(@RequestBody String notificationServiceReq, @RequestParam String apiKey) {
        LOGGER.debug("Enter NotificationServiceVisa->notifyTxnDetailsUpdateEvent ");
        Map<String,Object> notifyResponse = null;
        NotificationServiceReq notificationServiceReqpojo = null;
        try {
            notificationServiceReqpojo = (NotificationServiceReq) hCEControllerSupport.requestFormation(notificationServiceReq, NotificationServiceReq.class);
            notifyResponse = notificationServiceVisaService.notifyTxnDetailsUpdate(notificationServiceReqpojo, apiKey);
        }catch (HCEActionException notificationServiceHCEActionException){
            LOGGER.error("Exception Occured in DeviceRegistrationController->registerDevice",notificationServiceHCEActionException);
            throw notificationServiceHCEActionException;
        }catch (Exception notificationServiceException) {
            LOGGER.error(" Exception Occured in DeviceRegistrationController->registerDevice", notificationServiceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit DeviceRegNotificationServiceVisaistrationController->notifyCardMetadataUpdateEvent");
        return notifyResponse;
    }
}
