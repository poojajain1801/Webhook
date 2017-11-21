package com.comviva.hceservice.common;

import com.comviva.hceservice.fcm.RnsInfo;

public class InitializationData {
    private boolean initState;
    private String paymentAppInstanceId;
    private String mobileKeysetid;
    private String transportKey;
    private String macKey;
    private String dataEncryptionKey;
    private String remoteManagementUrl;
    private RnsInfo rnsInfo;

    public boolean isInitState() {
        return initState;
    }

    public void setInitState(boolean initState) {
        this.initState = initState;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getMobileKeysetid() {
        return mobileKeysetid;
    }

    public void setMobileKeysetid(String mobileKeysetid) {
        this.mobileKeysetid = mobileKeysetid;
    }

    public String getTransportKey() {
        return transportKey;
    }

    public void setTransportKey(String transportKey) {
        this.transportKey = transportKey;
    }

    public String getMacKey() {
        return macKey;
    }

    public RnsInfo getRnsInfo() {
        return rnsInfo;
    }

    public void setRnsInfo(RnsInfo rnsInfo) {
        this.rnsInfo = rnsInfo;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public String getDataEncryptionKey() {
        return dataEncryptionKey;
    }

    public void setDataEncryptionKey(String dataEncryptionKey) {
        this.dataEncryptionKey = dataEncryptionKey;
    }

    public String getRemoteManagementUrl() {
        return remoteManagementUrl;
    }

    public void setRemoteManagementUrl(String remoteManagementUrl) {
        this.remoteManagementUrl = remoteManagementUrl;
    }


}
