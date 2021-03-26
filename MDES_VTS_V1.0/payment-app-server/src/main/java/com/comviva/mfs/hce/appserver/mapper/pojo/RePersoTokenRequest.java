package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RePersoTokenRequest {
    private String clientAppId;
    private String vProvisionedTokenID;
    private String clientDeviceID;
    private String clientWalletAccountId;
    private String vNotificationID;
    private String fullReperso;

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getvNotificationID() {
        return vNotificationID;
    }

    public void setvNotificationID(String vNotificationID) {
        this.vNotificationID = vNotificationID;
    }

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

    public String getClientWalletAccountId() {
        return clientWalletAccountId;
    }

    public void setClientWalletAccountId(String clientWalletAccountId) {
        this.clientWalletAccountId = clientWalletAccountId;
    }

    public String isFullReperso() {
        return fullReperso;
    }

    public void setFullReperso(String fullReperso) {
        this.fullReperso = fullReperso;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }
}
