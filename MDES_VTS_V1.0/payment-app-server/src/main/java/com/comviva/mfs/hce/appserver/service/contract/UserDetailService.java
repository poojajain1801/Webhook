package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.GetLanguageReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetLanguageReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserLifecycleManagementReq;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface UserDetailService {
    Map<String,Object>registerUser(RegisterUserRequest registerUserRequest);
    Map<String,Object>userLifecycleManagement(UserLifecycleManagementReq userLifecycleManagementReq);
    Map<String,Object> setLanguage(SetLanguageReq setLanguageReqPojo);
    Map<String,Object> getLanguage(GetLanguageReq getLanguageReqPojo);
}
