package com.comviva.mfs.hce.appserver.mapper.pojo;

import org.springframework.stereotype.Component;



@Component
public class NotificationServiceReq {
    private Long date;
    private String api;
    private String vProvisionedTokenID;
    private String vPanEnrollmentID;

    public NotificationServiceReq(Long date, String api, String vProvisionedTokenID,String vPanEnrollmentID) {
        this.date = date;
        this.api = api;
        this.vProvisionedTokenID = vProvisionedTokenID;
        this.vPanEnrollmentID = vPanEnrollmentID;
    }

    public NotificationServiceReq()
    {
        //This is a default constructor
    }

    public Long getDate() {
        return date;
    }

    public String getApi() {
        return api;
    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getvPanEnrollmentID() {
        return vPanEnrollmentID;
    }

    public void setvPanEnrollmentID(String vPanEnrollmentID) {
        this.vPanEnrollmentID = vPanEnrollmentID;
    }
}

