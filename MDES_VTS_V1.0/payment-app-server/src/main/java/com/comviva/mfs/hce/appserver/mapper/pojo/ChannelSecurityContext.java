package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * ChannelSecurityContext Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class ChannelSecurityContext {

   // private ChannelInfo channelInfo;
    private VtsCerts vtsCerts;
    private DeviceCerts deviceCerts;

    public ChannelSecurityContext(VtsCerts vtsCerts,DeviceCerts deviceCerts) {
        //this.channelInfo=channelInfo;
        this.vtsCerts=vtsCerts;
        this.deviceCerts=deviceCerts;
    }

    public ChannelSecurityContext() {
    }
}