package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.Getter;
import lombok.Setter;

public class ConfirmReplenishmenRequest {
    private String api;
    private String sc;
    private String vProvisionedTokenID;

    public ConfirmReplenishmenRequest(String api, String sc, String vProvisionedTokenID) {
        this.api = api;
        this.sc = sc;
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public ConfirmReplenishmenRequest() {
    }
}
