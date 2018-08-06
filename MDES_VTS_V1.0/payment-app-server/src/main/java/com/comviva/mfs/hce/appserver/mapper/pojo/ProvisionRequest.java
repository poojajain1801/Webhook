package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 03-08-2018.
 */
@Getter
@Setter
public class ProvisionRequest {
    private String paymentAppProviderId ;
    private String paymentAppId ;
    private String paymentAppInstanceId ;
    private String tokenUniqueReference ;
    private String tokenType ;
    private String taskId ;
    private String apduCommands ;
    private TokenCredential tokenCredential ;

    public ProvisionRequest(String paymentAppProviderId, String paymentAppId, String paymentAppInstanceId, String tokenUniqueReference, String tokenType, String taskId, String apduCommands, TokenCredential tokenCredential) {
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppId = paymentAppId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.tokenType = tokenType;
        this.taskId = taskId;
        this.apduCommands = apduCommands;
        this.tokenCredential = tokenCredential;
    }
}
