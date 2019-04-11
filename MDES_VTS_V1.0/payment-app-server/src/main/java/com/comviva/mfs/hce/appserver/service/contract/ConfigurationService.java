package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.ApproveHvtRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetHvtValueRequest;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
public interface ConfigurationService {
    Map<String,Object> setHvtValue(SetHvtValueRequest setHvtValueRequestpojo);
    Map<String,Object> getPendingRequests();
    Map<String,Object> approveHvt(ApproveHvtRequest approveHvtRequest);
}
