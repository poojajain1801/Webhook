package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  VtsDeviceInfo Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VtsDeviceInfoRequest {

    private String osType;
    private String osVersion;
    private String osBuildID;
    private String deviceType;
    private String deviceIDType;
    private String deviceManufacturer;
    private String deviceBrand;
    private String deviceModel;
    private String deviceName;
    private String hostDeviceID;
    private String phoneNumber;

}