package com.comviva.mfs.promotion.modules.user_management.service;

import com.comviva.mfs.promotion.modules.user_management.domain.UserDetail;
import com.comviva.mfs.promotion.modules.user_management.model.UserRegistrationResponse;
import com.comviva.mfs.promotion.modules.user_management.repository.UserDetailRepository;
import com.comviva.mfs.promotion.modules.user_management.service.contract.UserDetailService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class UserDetailServiceImpl implements UserDetailService {

    private final UserDetailRepository userDetailRepository;

    @Autowired
    public UserDetailServiceImpl(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }
    UserDetail savedUser;
    String userstatus;
    @Override
    @Transactional

    public UserRegistrationResponse registerUser(String userName) {
        boolean isUserPresentInDb =checkIfUserExistInDb(userName);
        if (!isUserPresentInDb) {

            userstatus = "userRegistered";
            String activationCode = generateActivationCode();
            savedUser = userDetailRepository.save(new UserDetail(null,userName,activationCode, userstatus));
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "200",
                    "message", "User has been successfully registered in the system",
                    "userDetails", savedUser,
                    "activationCode", activationCode);
            return new UserRegistrationResponse(response);
        } else {
            return new UserRegistrationResponse(ImmutableMap.of("message", "User is already registered in the system", "responseCode", "201"));
        }
    }

    public UserRegistrationResponse activateUser(String userName, String activationCode) {
        userstatus = "userActivated";

        boolean isUserPresentInDb = userDetailRepository.findByUserName(userName).isPresent();
        //boolean checkUserStatus = getUserstatus(userName).equalsIgnoreCase(userstatus);

        if(!isUserPresentInDb)
        {
            return new UserRegistrationResponse(ImmutableMap.of("message", "Invalid User", "responseCode", "203"));
        }
        savedUser = userDetailRepository.findByUserName(userName).get();
        if(getUserstatus(userName).equalsIgnoreCase(userstatus))
        {
            return new UserRegistrationResponse(ImmutableMap.of("message", "User is Already Activated", "responseCode", "204"));
        }

            String strActivationCode = getActivationCode(userName);
            if (strActivationCode.equalsIgnoreCase(activationCode)) {

                savedUser.setUserstatus(userstatus);
                userDetailRepository.save(savedUser);
                return new UserRegistrationResponse(ImmutableMap.of("message", "User is activated", "responseCode", "200"));
            }

            else
                return new UserRegistrationResponse(ImmutableMap.of("message", "Invalid Acivation Code", "responseCode", "202"));

    }

    /**
     * Implementation for generating activation code is required
     *
     * @return
     */
    private String generateActivationCode() {
        String activationCode = "40";
        return activationCode;
    }


    /**
     * Implementation for checking if user is exist in DB
     *
     * @return
     */
    public boolean checkIfUserExistInDb(String userName) {
        boolean isUserPresentInDb = userDetailRepository.findByUserName(userName).isPresent();
        return isUserPresentInDb;
    }
    public String getUserstatus(String userName) {
        String userStaus = userDetailRepository.findByUserName(userName).get().getUserstatus();
        return userStaus;
    }

    /**
     * Implementation for checking if user is exist in DB
     *
     * @return
     */
    public String getActivationCode(String userName) {
        String strActivationCode = userDetailRepository.findByUserName(userName).get().getActivationCode();
        return strActivationCode;
    }
}
