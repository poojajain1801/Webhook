package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by tarkeshwar.v on 7/7/2017.
 */
@RestController
@RequestMapping("/mpamanagement/1/0")
public class RemoteNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardManagementController.class);
    @Autowired
    private RemoteNotificationService remoteNotificationService;

    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    @ResponseBody
    @RequestMapping(value = "/sendRemoteNotificationMessage", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map sendRemoteNotificationMessage(@RequestBody String remoteNotificationReq) {
        LOGGER.debug("Inside RemoteNotificationController----------->sendRemoteNotificationMessage");
        Map <String,Object> sendRemoteNotificationMessageResponse= null;
        RemoteNotificationRequest remoteNotificationRequestPojo = null;
        try{
            remoteNotificationRequestPojo =(RemoteNotificationRequest) hCEControllerSupport.requestFormation(remoteNotificationReq ,RemoteNotificationRequest.class);
            sendRemoteNotificationMessageResponse = remoteNotificationService.sendRemoteNotificationMessage(remoteNotificationRequestPojo);
        }catch (HCEActionException sendRemoteNotificationMessageHceActionException){
            LOGGER.error("Exception Occured in RemoteNotificationController->sendRemoteNotificationMessage",sendRemoteNotificationMessageHceActionException);
            throw sendRemoteNotificationMessageHceActionException;
        }catch (Exception sendRemoteNotificationMessageExcetption) {
            LOGGER.error(" Exception Occured in RemoteNotificationController->sendRemoteNotificationMessage", sendRemoteNotificationMessageExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return sendRemoteNotificationMessageResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/sendGenericRemoteNotificationMessage", method = RequestMethod.POST)
    public Map sendGenericRns(@RequestBody RnsGenericRequest rnsGenericRequest) {
        return remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
    }
}

