package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;

import java.util.Map;

public interface NotificationServiceVisaService {
    Map<String, Object> notifyLCMEvent(NotificationServiceReq notificationServiceReq,String apiKey,String evenType);
    Map<String, Object> notifyPanMetadataUpdate(NotificationServiceReq notificationServiceReq,String apiKey);
    Map <String,Object> notifyTxnDetailsUpdate(NotificationServiceReq notificationServiceReq,String apiKey);
}
