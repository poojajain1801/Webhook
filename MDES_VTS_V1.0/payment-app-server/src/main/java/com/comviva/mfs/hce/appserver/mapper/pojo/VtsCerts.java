package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ChannelSecurityContext Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VtsCerts {
    private String vCertificateID;
    private String certUsage;
}