package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RemoteNotification;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsFactory;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsResponse;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class RemoteNotificationServiceImpl implements com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService {
    private final DeviceDetailRepository deviceDetailRepository;
    private HCEControllerSupport hceControllerSupport;

    @Autowired
    public RemoteNotificationServiceImpl(DeviceDetailRepository deviceDetailRepository,HCEControllerSupport hceControllerSupport) {
        this.deviceDetailRepository = deviceDetailRepository;
    }
    @Autowired
    private Environment env;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNotificationServiceImpl.class);

    public Map sendRemoteNotificationMessage(RemoteNotificationRequest remoteNotificationReq) {
        // Create Remote Notification Data
        RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM,env);

        byte[] rnsPostData = Base64.getDecoder().decode(remoteNotificationReq.getNotificationData().getBytes());

        RnsResponse response = rns.sendRns(rnsPostData);
        if (Integer.valueOf(response.getErrorCode()) != 200) {
            return ImmutableMap.of("errorCode", "234",
                    "errorDescription", "RNS Unavailable");
        }
        return ImmutableMap.of("responseHost", "localhost",
                "responseId", "12345678");
    }

    public Map sendRemoteNotification(RnsGenericRequest rnsGenericRequest) {
        // Create Remote Notification Data
        LOGGER.debug("Inside RemoteNotificationServiceImpl -> sendRemoteNotification");
        try {
            Map rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());

            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority","high");
            payloadObject.put("time_to_live",2160000);

            LOGGER.debug("RemoteNotificationServiceImpl -> sendRemoteNotification->Request payload send to FCM : ",payloadObject.toString());

            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());

            Gson gson = new Gson();
            String json = gson.toJson(response);
            LOGGER.debug("RemoteNotificationServiceImpl -> sendRemoteNotification->Raw response from FCM server"+json);

            if (Integer.valueOf(response.getErrorCode()) != 200) {
                return ImmutableMap.of("errorCode", "720",
                        "errorDescription", "UNABLE_TO_DELIVERFCM_MESSAGE");
            }
            return ImmutableMap.of("responseHost", "localhost",
                    "responseId", "12345678");
        }catch (Exception e)
        {
            LOGGER.debug("Exception occored in RemoteNotificationServiceImpl-> sendRemoteNotification",e);
            return  ImmutableMap.of("errorCode","500",
                "errorDescription","Servicefaild");

        }
    }
}
