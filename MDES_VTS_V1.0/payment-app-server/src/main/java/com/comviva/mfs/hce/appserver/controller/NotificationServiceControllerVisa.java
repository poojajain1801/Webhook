package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;
import com.comviva.mfs.hce.appserver.service.contract.NotificationServiceVisaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationServiceControllerVisa {

    @Autowired
    NotificationServiceVisaService notificationServiceVisaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationController.class);

    @ResponseBody
    @RequestMapping(value = "/provisionedToken", method = RequestMethod.POST)

    public Map<String,Object> notifyLCMEvent(@RequestBody NotificationServiceReq notificationServiceReq,@PathVariable String eventType,@PathVariable String apiKey) {
        LOGGER.debug("Enter NotificationServiceVisa->notify");
        Map<String,Object> notifyResponse = null;
        notifyResponse =  notificationServiceVisaService.notifyLCMEvent(notificationServiceReq,apiKey,eventType);
        LOGGER.debug("Exit DeviceRegNotificationServiceVisaistrationController->notify");
        return notifyResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/panMetadata", method = RequestMethod.POST)

    public Map<String,Object> notifyCardMetadataUpdateEvent(@RequestBody NotificationServiceReq notificationServiceReq,@PathVariable String apiKey) {
        LOGGER.debug("Enter NotificationServiceVisa->notifyCardMetadataUpdateEvent");
        Map<String,Object> notifyResponse = null;
        notifyResponse =  notificationServiceVisaService.notifyPanMetadataUpdate(notificationServiceReq,apiKey);
        LOGGER.debug("Exit DeviceRegNotificationServiceVisaistrationController->notifyCardMetadataUpdateEvent");
        return notifyResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/paymentTxns", method = RequestMethod.POST)

    public Map<String,Object> notifyTxnDetailsUpdateEvent(@RequestBody NotificationServiceReq notificationServiceReq,@PathVariable String apiKey) {
        LOGGER.debug("Enter NotificationServiceVisa->notifyTxnDetailsUpdateEvent ");
        Map<String,Object> notifyResponse = null;
        notifyResponse =  notificationServiceVisaService.notifyTxnDetailsUpdate(notificationServiceReq,apiKey);
        LOGGER.debug("Exit DeviceRegNotificationServiceVisaistrationController->notifyCardMetadataUpdateEvent");
        return notifyResponse;
    }


}
