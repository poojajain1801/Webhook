package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ChannelInfo Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfo {
    private String  vCertificateID;
    private String certUsage;
}