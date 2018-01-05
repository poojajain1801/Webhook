package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by tarkeshwar.v on 7/7/2017.
 */
@RestController
@RequestMapping("/mdes/mpamanagement/1/0")
public class RemoteNotificationController {
    @Autowired
    private RemoteNotificationService remoteNotificationService;

    @ResponseBody
    @RequestMapping(value = "/sendRemoteNotificationMessage", method = RequestMethod.POST)
    public Map sendRemoteNotificationMessage(@RequestBody RemoteNotificationRequest remoteNotificationReq) {
        return remoteNotificationService.sendRemoteNotificationMessage(remoteNotificationReq);
    }

    @ResponseBody
    @RequestMapping(value = "/sendGenericRemoteNotificationMessage", method = RequestMethod.POST)
    public Map sendGenericRns(@RequestBody RnsGenericRequest rnsGenericRequest) {
        return remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
    }
}
