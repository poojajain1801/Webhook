package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Column;

@Getter
@Setter
@Component
public class NotificationServiceReq {
    private String date;
    private String api;
    private String vProvisionedTokenID;

    public NotificationServiceReq(String date, String api, String vProvisionedTokenID) {
        this.date = date;
        this.api = api;
        this.vProvisionedTokenID = vProvisionedTokenID;
    }
    public NotificationServiceReq()
    {
        //This is a default constructor
    }

}
