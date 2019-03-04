package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ChannelSecurityContext Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class DeviceCerts {

    private String certUsage;
    private String certFormat;
    private String certValue;

    public DeviceCerts(String certUsage,String certFormat,String certValue)
    {
        this.certUsage=certUsage;
        this.certFormat=certFormat;
        this.certValue=certValue;
    }
    public DeviceCerts() {
    }
}