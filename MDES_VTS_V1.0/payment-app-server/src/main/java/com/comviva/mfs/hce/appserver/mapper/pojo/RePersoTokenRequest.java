package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RePersoTokenRequest {
    private String vProvisionedTokenID;
    private String clientDeviceID;
    private String clientWalletAccountID;

    public String getVProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setVProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getClientDeviceID() {
        return clientDeviceID;
    }

    public void setClientDeviceID(String clientDeviceID) {
        this.clientDeviceID = clientDeviceID;
    }

    public String getClientWalletAccountID() {
        return clientWalletAccountID;
    }

    public void setClientWalletAccountID(String clientWalletAccountID) {
        this.clientWalletAccountID = clientWalletAccountID;
    }
}
