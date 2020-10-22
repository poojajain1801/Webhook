package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollDeviceDasRequest {
    private List<String> deviceCertList;
    private String profileAppID;
    private VtsDeviceInfoRequest deviceInfo;
    private DeviceProfile deviceProfile;
    private List<VtsCerts> visaCertReferenceList;
    private String clientDeviceID;
}
