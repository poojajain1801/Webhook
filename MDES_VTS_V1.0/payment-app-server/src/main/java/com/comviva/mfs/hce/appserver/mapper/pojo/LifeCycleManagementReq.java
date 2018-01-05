package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;

import java.util.List;

/**
 * Created by Tanmay.Patel on 5/16/2017.
 */
@Getter
public class LifeCycleManagementReq {
    private String paymentAppInstanceId;
    private List<String> tokenUniqueReferences;
    private String causedBy;
    private String reason;
    private String reasonCode;
    private String operation;//{DELETE,SUSPEND,UNSUSPEND}

    public LifeCycleManagementReq(String paymentAppInstanceId, List<String> tokenUniqueReferences, String causedBy, String reason, String reasonCode, String operation) {
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReferences = tokenUniqueReferences;
        this.causedBy = causedBy;
        this.reason = reason;
        this.reasonCode = reasonCode;
        this.operation = operation;
    }

    public LifeCycleManagementReq()
    {
        //This is a default constructor
    }
}
