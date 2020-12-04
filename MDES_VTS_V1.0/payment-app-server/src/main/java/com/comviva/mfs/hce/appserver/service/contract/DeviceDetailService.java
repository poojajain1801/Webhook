package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface DeviceDetailService {
    /**
     * register Device
     * @param enrollDeviceRequest enrollDeviceRequest
     * @return map
     */
    Map<String,Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest);
    Map<String,Object> unRegisterDevice(UnRegisterReq unRegisterReq);
    Map<String,Object> enrollDeviceDas(EnrollDeviceDasRequest enrollDeviceDasReqPojo, String clientDeviceId);
}
