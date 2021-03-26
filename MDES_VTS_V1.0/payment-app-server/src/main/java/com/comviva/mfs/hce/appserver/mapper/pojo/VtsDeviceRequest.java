package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  EnrollDevice Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VtsDeviceRequest {
    private VtsDeviceInfoRequest deviceInfo;
    private ChannelSecurityContext channelSecurityContext;
    private EnrollDeviceDasRequest dasRequest;
}