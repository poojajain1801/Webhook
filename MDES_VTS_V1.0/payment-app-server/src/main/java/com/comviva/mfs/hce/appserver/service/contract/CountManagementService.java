package com.comviva.mfs.hce.appserver.service.contract;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
public interface CountManagementService {
    Map<String,Object> getUserCount();
    Map<String,Object> getDeviceCount();
    Map<String,Object> getTokenCount();
    Map<String,Object> getActiveTokenCount();
}
