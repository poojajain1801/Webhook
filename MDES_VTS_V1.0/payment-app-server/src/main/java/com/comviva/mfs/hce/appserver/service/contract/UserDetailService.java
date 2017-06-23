package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface UserDetailService {
    Map<String,Object>registerUser(RegisterUserRequest registerUserRequest);
    Map<String,Object> activateUser(ActivateUserRequest activateUserRequest);
    boolean checkIfUserExistInDb(String userName);
    boolean checkIfClientDeviceIDExistInDb(String clientDeviceID);
    String getActivationCode(String userName);
    String getUserstatus(String userName);
}
