package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoteNotificationRequest {
    private String paymentAppProviderId;
    private String paymentAppInstanceId;
    private String notificationData;

    public RemoteNotificationRequest(){
    }

    public RemoteNotificationRequest(String paymentAppProviderId, String paymentAppInstanceId, String notificationData){
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.notificationData = notificationData;
    }
}
