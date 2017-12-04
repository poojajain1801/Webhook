package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ChannelInfo Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class ChannelInfo {

    private String  vCertificateID;
    private String certUsage;
    public ChannelInfo(String vCertificateID,String certUsage) {
        this.vCertificateID=vCertificateID;
        this.certUsage=certUsage;
    }
    public ChannelInfo() {
    }
}