package com.comviva.mfs.promotion.modules.mpamanagement.service.contract;

import com.comviva.mfs.promotion.modules.mpamanagement.model.DeviceRegParam;
import com.comviva.mfs.promotion.modules.mpamanagement.model.DeviceRegisterResp;

import java.util.Map;

public interface RegisterDeviceService {
    /**
     * Registers new device into CMS-Dedicated.
     * @param regDeviceParam    Regiistration parameters
     * @return  Response
     */
    Map registerDevice(DeviceRegParam regDeviceParam);
}
