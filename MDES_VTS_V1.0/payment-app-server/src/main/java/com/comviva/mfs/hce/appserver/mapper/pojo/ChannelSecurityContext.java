package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ChannelSecurityContext Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSecurityContext {
    private List<VtsCerts> vtsCerts;
    private List<DeviceCerts> deviceCerts;
    private ChannelInfo channelInfo;
}