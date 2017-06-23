package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.mapper.UserRegistrationResponse;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.util.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class UserDetailServiceImpl implements UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;

    @Autowired
    private Environment env;
    @Autowired
    public UserDetailServiceImpl(UserDetailRepository userDetailRepository,DeviceDetailRepository deviceDetailRepository) {
        this.userDetailRepository = userDetailRepository;
        this.deviceDetailRepository=deviceDetailRepository;
    }
    UserDetail savedUser;
    String userstatus;

    @Override
    @Transactional
    public Map<String,Object> registerUser(RegisterUserRequest registerUserRequest) {
        if(registerUserRequest.getUserId()==null || registerUserRequest.getUserId().isEmpty()
                || registerUserRequest.getClientDeviceID()==null || registerUserRequest.getClientDeviceID().isEmpty()){
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "300",
                    "message", "insufficient data");
            return  response;
        }
      //  boolean isUserPresentInDb =checkIfUserExistInDb(registerUserRequest.getUserId());
        List<UserDetail> userDetails = userDetailRepository.find(registerUserRequest.getUserId());
        boolean isClientDeviceIDPresentInDb=checkIfClientDeviceIDExistInDb(registerUserRequest.getClientDeviceID());
        if ((null == userDetails || userDetails.isEmpty()) && ! isClientDeviceIDPresentInDb ) {
            userstatus = "userRegistered";
            String activationCode = generateActivationCode();
            String clientWalletAccountid =generatelCientWalletAccountid(registerUserRequest.getUserId());
            savedUser = userDetailRepository.save(new UserDetail(null,registerUserRequest.getUserId(),activationCode, userstatus,
                    clientWalletAccountid,registerUserRequest.getClientDeviceID()));
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "200",
                    "message", "User has been successfully registered in the system",
                    "userDetails", savedUser,
                    "activationCode", activationCode);
            return  response;
        }
        else if((null != userDetails && !userDetails.isEmpty()) && ! isClientDeviceIDPresentInDb){


            return null;

        }
        else {
         Map<String,Object> response=ImmutableMap.of("message", "User is already registered in the system", "responseCode", "201");
            return  response;
        }
    }

    public Map<String,Object> activateUser(ActivateUserRequest activateUserRequest) {


        if(activateUserRequest.getUserId()==null || activateUserRequest.getActivationCode()==null ||
                activateUserRequest.getUserId().isEmpty() || activateUserRequest.getActivationCode().isEmpty()){
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "300",
                    "message", "insufficient data");
            return  response;
        }
        userstatus = "userActivated";

        boolean isUserPresentInDb = userDetailRepository.findByUserName(activateUserRequest.getUserId()).isPresent();
        //boolean checkUserStatus = getUserstatus(userName).equalsIgnoreCase(userstatus);

        if(!isUserPresentInDb)
        {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "203");
            return response;
        }
        savedUser = userDetailRepository.findByUserName(activateUserRequest.getUserId()).get();
        if(getUserstatus(activateUserRequest.getUserId()).equalsIgnoreCase(userstatus))
        {
            Map <String, Object> response =ImmutableMap.of("message", "User is Already Activated", "responseCode", "204");
            return response;
        }

            String strActivationCode = getActivationCode(activateUserRequest.getUserId());
            if (strActivationCode.equalsIgnoreCase(activateUserRequest.getActivationCode())) {

                savedUser.setUserstatus(userstatus);
                userDetailRepository.save(savedUser);
                Map <String, Object> response =ImmutableMap.of("message", "User is activated", "responseCode", "200");
                return response;
            }
            else {
                Map<String, Object> response = ImmutableMap.of("message", "Invalid Acivation Code", "responseCode", "202");
                return response;
            }
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

    private  String generatelCientWalletAccountid(String id){
           String cientWalletAccountid = String.format("%032X", Calendar.getInstance().getTime().getTime());
        cientWalletAccountid = id+ArrayUtil.getHexString(ArrayUtil.getRandom(5))+cientWalletAccountid ;
            return cientWalletAccountid.substring(0, Math.min(cientWalletAccountid.length(), 23));
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
    public boolean checkIfClientDeviceIDExistInDb(String clientDeviceID){
        boolean isClientDeviceIDPresentInDb=deviceDetailRepository.findByClientDeviceId(clientDeviceID).isPresent();
        return isClientDeviceIDPresentInDb;
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