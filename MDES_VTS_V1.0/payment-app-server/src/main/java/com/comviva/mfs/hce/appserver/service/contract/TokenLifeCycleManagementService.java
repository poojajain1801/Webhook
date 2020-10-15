package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenListRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenStatusRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;

import java.util.Map;

/**
 * Created by Madan.Amgoth on 5/9/2017.
 */
public interface TokenLifeCycleManagementService {
    /**
     * getTokenstatus
     * @param getTokenStatusRequest getTokenStatusRequest
     * @return map
     * */
    Map<String,Object>getTokenStatus(GetTokenStatusRequest getTokenStatusRequest);

    /**
     * lifeCycleManagementVisa
     * @param lifeCycleManagementVisaRequest lifeCycleMgmtVisa
     * @return map
     * */
    Map<String,Object>lifeCycleManagementVisa(LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest);

    /**
     * getTokenList
     * @param getTokenListRequest getTokenListRequest
     * @return map
     * */
    Map<String,Object> getTokenList(GetTokenListRequest getTokenListRequest);
}
