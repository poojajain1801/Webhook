package com.comviva.mfs.hce.appserver.service.contract;


import com.comviva.mfs.hce.appserver.mapper.pojo.FcmAcknowledgementRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.HvtManagementRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import java.util.Map;

public interface HvtManagementService {
    Map<String, Object> saveHvtLimit(HvtManagementRequest hvtManagementRequest);
    Map<String, Object> fetchHvtLimit();
    void modifySchedulerDbOnAck(FcmAcknowledgementRequest fcmAcknowledgementRequestPojo);
    RnsGenericRequest preparetNotificationRequest(String rnsId);
    void sendNotification(RnsGenericRequest rnsGenericRequest) throws Exception;

    Map<String, Object> fetchConfiguration();
}
