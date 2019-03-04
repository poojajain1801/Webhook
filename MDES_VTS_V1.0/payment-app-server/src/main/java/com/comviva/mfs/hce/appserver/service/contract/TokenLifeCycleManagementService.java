package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by Madan.Amgoth on 5/9/2017.
 */
public interface TokenLifeCycleManagementService {
    Map<String,Object>getTokenStatus(GetTokenStatusRequest getTokenStatusRequest);
    Map<String,Object>lifeCycleManagementVisa(LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest);
    public  Map<String,Object> getTokenList(GetTokenListRequest getTokenListRequest);
}
