package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class  RePersoFlowRequest {
    private String clientDeviceID;
    private String vProvisionedTokenID;

    public RePersoFlowRequest(String vProvisionedTokenID, String clientDeviceID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
        this.clientDeviceID = clientDeviceID;
    }

    public String getClientDeviceID() {
        return clientDeviceID;
    }

    public void setClientDeviceID(String clientDeviceID) {
        this.clientDeviceID = clientDeviceID;
    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }
}
