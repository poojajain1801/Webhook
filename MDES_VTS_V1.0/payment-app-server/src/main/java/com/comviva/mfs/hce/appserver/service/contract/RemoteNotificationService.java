package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.GetDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;

import java.util.Map;

/**
 * Created by tarkeshwar.v on 7/7/2017.
 */
public interface RemoteNotificationService {
    /**
     * This API is used by MDES to send a Remote Notification Message to the Mobile Payment App via a server.
     * @param remoteNotificationReq
     * @return Response
     */
    Map<String, Object> sendRemoteNotificationMessage(RemoteNotificationRequest remoteNotificationReq);

    /**
     * Generic purpose API to send remote notification data to Mobile Application. Can be used by MDES/VTS
     * @return Response
     */
    Map<String, Object> sendRemoteNotification(RnsGenericRequest rnsGenericRequest);

    Map<String,Object> getDeviceInfo(GetDeviceInfoRequest getDeviceInfoRequest);
}
