package com.comviva.mfs.promotion.modules.mpamanagement.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public class DeviceRegisterResp {

    private String responseHost;
    private String responseId;
    private String mobileKeysetId;
    private MobileKeys mobileKeys;
    private String remoteManagementUrl;
    private String responseCode;
    private String message;

   public DeviceRegisterResp(String responseHost,String responseId,String mobileKeysetId,MobileKeys mobileKeys,String remoteManagementUrl,String responseCode,String message)
   {
        this.responseHost=responseHost;
        this.responseId=responseId;
        this.mobileKeysetId=mobileKeysetId;
        this.mobileKeys=mobileKeys;
        this.remoteManagementUrl=remoteManagementUrl;
        this.responseCode=responseCode;
        this.message=message;
    }
}