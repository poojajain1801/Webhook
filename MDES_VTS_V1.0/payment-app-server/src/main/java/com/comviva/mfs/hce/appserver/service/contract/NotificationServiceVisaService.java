package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;

import java.util.Map;

public interface NotificationServiceVisaService {
   /**
    * notifyLCMEvent
    * @param notificationServiceReq notificationService Request
    * @param apiKey ApiKey
    * @param evenType evenType
    * @return Map
    * */
    Map<String, Object> notifyLCMEvent(NotificationServiceReq notificationServiceReq,String apiKey,String evenType);

    /**
     * notifyPanMeatadtaUpdate
     * @param notificationServiceReq notificationServiceRequest
     * @param apiKey Api Key
     * @return Map
     * */
    Map<String, Object> notifyPanMetadataUpdate(NotificationServiceReq notificationServiceReq,String apiKey);

    /**
     * notifyTxnDetailsUpdate
     * @param notificationServiceReq notificationServiceRequest
     * @param apiKey Api Key
     * @return Map
     * */
    Map <String,Object> notifyTxnDetailsUpdate(NotificationServiceReq notificationServiceReq,String apiKey);
}
