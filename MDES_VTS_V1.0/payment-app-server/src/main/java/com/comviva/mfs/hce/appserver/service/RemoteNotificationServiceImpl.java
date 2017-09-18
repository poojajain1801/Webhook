package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RemoteNotification;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsFactory;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsResponse;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class RemoteNotificationServiceImpl implements com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService {
    private final DeviceDetailRepository deviceDetailRepository;


    @Autowired
    public RemoteNotificationServiceImpl(DeviceDetailRepository deviceDetailRepository) {
        this.deviceDetailRepository = deviceDetailRepository;
    }

    public Map sendRemoteNotificationMessage(RemoteNotificationRequest remoteNotificationReq) {
        // Create Remote Notification Data
        RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM);

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
        Map rnsData = rnsGenericRequest.getRnsData();
        rnsData.put("TYPE", rnsGenericRequest.getIdType().name());

        JSONObject payloadObject = new JSONObject();
        payloadObject.put("data", new JSONObject(rnsData));
        payloadObject.put("to", rnsGenericRequest.getRegistrationId());

        RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM);
        RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());
        if (Integer.valueOf(response.getErrorCode()) != 200) {
            return ImmutableMap.of("errorCode", response.getErrorCode(),
                    "errorDescription", /*"RNS Unavailable"*/response.getResponse());
        }
        return ImmutableMap.of("responseHost", "localhost",
                "responseId", "12345678");
    }
}
