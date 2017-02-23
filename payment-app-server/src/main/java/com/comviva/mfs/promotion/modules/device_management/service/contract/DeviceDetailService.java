package com.comviva.mfs.promotion.modules.device_management.service.contract;

import com.comviva.mfs.promotion.modules.device_management.model.DeviceRegistrationResponse;

import com.comviva.mfs.promotion.modules.device_management.model.RegDeviceParam;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface DeviceDetailService {
    /**
     * @param regDeviceParam
     * @return
     */
    DeviceRegistrationResponse registerDeviece(RegDeviceParam regDeviceParam);

    /**
     * @param regDeviceParam
     * @return
     */
    boolean checkDeviceEligibility(RegDeviceParam regDeviceParam);

    /**
     * @param regDeviceParam
     * @return
     */
    String registerDeviceWithCMSD(RegDeviceParam regDeviceParam);

}
