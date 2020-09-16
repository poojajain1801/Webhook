package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface DeviceDetailService {
    /**
     * @param enrollDeviceRequest
     * @return
     */
    Map<String,Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest);
    Map<String,Object> unRegisterDevice(UnRegisterReq unRegisterReq);
    Map<String,Object> enrollDeviceDas(EnrollDeviceDasRequest enrollDeviceDasReqPojo);
}
