package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.ApproveHvtRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetHvtValueRequest;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
public interface ConfigurationService {
    /**
     * setHvtValue
     * @param setHvtValueRequestpojo setHvtValueReuPojo
     * @return map
     * */
    Map<String,Object> setHvtValue(SetHvtValueRequest setHvtValueRequestpojo);

    /**
     * getPendingRequests
     * @return map
     * */
    Map<String,Object> getPendingRequests();

    /**
     * approveHvt
     * @param approveHvtRequest aprroveHvtRequest
     * @return map
     * */
    Map<String,Object> approveHvt(ApproveHvtRequest approveHvtRequest);
}
