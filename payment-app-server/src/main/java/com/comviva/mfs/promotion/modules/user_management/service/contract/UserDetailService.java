package com.comviva.mfs.promotion.modules.user_management.service.contract;

import com.comviva.mfs.promotion.modules.user_management.model.UserRegistrationResponse;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface UserDetailService {
    UserRegistrationResponse registerUser(String userName);

    UserRegistrationResponse activateUser(String userName, String activationCode);

    boolean checkIfUserExistInDb(String userName);

    String getActivationCode(String userName);
    String getUserstatus(String userName);
}
