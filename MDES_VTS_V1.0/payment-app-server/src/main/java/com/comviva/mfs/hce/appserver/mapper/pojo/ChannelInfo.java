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
    private String encryptionScheme;

    public ChannelInfo(String vCertificateID,String certUsage, String encryptionScheme) {
        this.vCertificateID=vCertificateID;
        this.certUsage=certUsage;
        this.encryptionScheme = encryptionScheme;
    }
    public ChannelInfo() {
    }
}