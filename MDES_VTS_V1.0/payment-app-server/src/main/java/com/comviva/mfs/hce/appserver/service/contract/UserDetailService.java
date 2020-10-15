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
    /**
     * registerUser
     * @param registerUserRequest registerUserRequest
     * @return map
     * */
    Map<String,Object>registerUser(RegisterUserRequest registerUserRequest);

    /**
     * userLifecycleManagement
     * @param userLifecycleManagementReq userLifeCycleManagementRequest
     * @return map
     * */
    Map<String,Object>userLifecycleManagement(UserLifecycleManagementReq userLifecycleManagementReq);

    /**
     * setLanguage
     * @param setLanguageReqPojo setLanguagePojo
     * @return map
     * */
    Map<String,Object> setLanguage(SetLanguageReq setLanguageReqPojo);


    /**
     * getLanguage
     * @param getLanguageReqPojo setLanguagePojo
     * @return map
     * */
    Map<String,Object> getLanguage(GetLanguageReq getLanguageReqPojo);
}
