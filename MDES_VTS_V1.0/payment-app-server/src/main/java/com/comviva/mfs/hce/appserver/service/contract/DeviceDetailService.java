package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface DeviceDetailService {
    /**
     * registerDevice
     * @param enrollDeviceRequest enrollDeviceRequest
     * @return map
     */
    Map<String,Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest);

    /**
     * unRegisterDevice
     * @param unRegisterReq unRegisterRequest
     * @return map
     * */
    Map<String,Object> unRegisterDevice(UnRegisterReq unRegisterReq);

}
