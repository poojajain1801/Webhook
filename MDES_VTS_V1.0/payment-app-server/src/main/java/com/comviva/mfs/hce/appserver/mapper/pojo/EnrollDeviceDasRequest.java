package com.comviva.mfs.hce.appserver.mapper.pojo;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
