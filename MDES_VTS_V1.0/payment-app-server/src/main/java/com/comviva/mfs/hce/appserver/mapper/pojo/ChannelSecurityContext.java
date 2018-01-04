package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ChannelSecurityContext Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class ChannelSecurityContext {

    private List<VtsCerts> vtsCerts;
    private List<DeviceCerts> deviceCerts;

    public ChannelSecurityContext(List<VtsCerts> vtsCerts,List<DeviceCerts> deviceCerts) {
        this.vtsCerts = (vtsCerts);
        this.deviceCerts = (deviceCerts);
    }

    public ChannelSecurityContext() {
    }
}