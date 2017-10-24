package com.comviva.mfs.hce.appserver.service;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActivateUserRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.comviva.mfs.hce.appserver.exception.*;

/**
 * Perform user registration and activation
 */
@Service
public class UserDetailServiceImpl implements UserDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;
    private final HCEControllerSupport hceControllerSupport;

    @Autowired
    private Environment env;
    @Autowired
    public UserDetailServiceImpl(UserDetailRepository userDetailRepository,DeviceDetailRepository deviceDetailRepository, HCEControllerSupport hceControllerSupport) {
        this.userDetailRepository = userDetailRepository;
        this.deviceDetailRepository=deviceDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
    }


    @Override
    @Transactional
    public Map<String,Object> registerUser(RegisterUserRequest registerUserRequest) {


        Map <String, Object> response;
        List<UserDetail> userDetails;
        List<DeviceInfo> deviceInfos;
        String activationCode;
        String clientWalletAccountid;
        UserDetail userDetail;
        DeviceInfo deviceInfo;
        String userstatus;
        String devicestatus;
        String userId ;
        String imei;


        try{
            LOGGER.debug("Enter UserDetailServiceImpl->registerUser");
            userId = registerUserRequest.getUserId();
            imei = registerUserRequest.getImei();

            if(isClientDeviceIdExist(registerUserRequest.getClientDeviceID())){
                throw new HCEActionException(HCEMessageCodes.CLIENT_DEVICEID_EXIST);
            }

            userDetails = userDetailRepository.findByUserIdandStatus(userId,HCEConstants.ACTIVE);
            if(userDetails!=null && !userDetails.isEmpty()){
                userDetail = userDetails.get(0);
                deviceInfos = deviceDetailRepository.findByImeiandStatus(imei,HCEConstants.ACTIVE);
                if(deviceInfos!=null && !deviceInfos.isEmpty()){
                    deviceInfo = deviceInfos.get(0);
                    if(userDetail.getClientWalletAccountId().equals(deviceInfo.getUserDetail().getClientWalletAccountId())){
                        throw new HCEActionException(HCEMessageCodes.USER_ALREADY_REGISTERED);
                    }else{

                        deactivateDevice(deviceInfo);
                        deviceInfo.setStatus(HCEConstants.INACTIVE);
                        deviceDetailRepository.save(deviceInfo);
                        updateUserStatusIfOneDeviceIsLinked(deviceInfo);
                        deviceInfos = saveDeviceInfo(registerUserRequest);
                        userDetail.setDeviceInfos(deviceInfos);
                        userDetailRepository.save(userDetail);
                        // Register New Device
                        //update Old device with N and if owner of that user is having one device then make user status N too. and register device.
                    }
                }else{

                    deviceInfos = saveDeviceInfo(registerUserRequest);
                    userDetail.setDeviceInfos(deviceInfos);
                    userDetailRepository.save(userDetail);
                    //Register Device

                }

            }else{

                deviceInfos = deviceDetailRepository.findByImeiandStatus(imei,HCEConstants.ACTIVE);
                if(deviceInfos!=null && !deviceInfos.isEmpty()){
                    deviceInfo = deviceInfos.get(0);
                    deactivateDevice(deviceInfo);
                    deviceInfo.setStatus(HCEConstants.INACTIVE);
                    deviceDetailRepository.save(deviceInfo);
                    updateUserStatusIfOneDeviceIsLinked(deviceInfo);
                    userDetail = saveUserDetails(registerUserRequest);
                    deviceInfos = saveDeviceInfo(registerUserRequest);
                    userDetail.setDeviceInfos(deviceInfos);
                    userDetailRepository.save(userDetail);


                    //update Old device with N and if owner of that user is having one device then make user status N too. and register device and user.

                }else{

                    userDetail = saveUserDetails(registerUserRequest);
                    deviceInfos = saveDeviceInfo(registerUserRequest);
                    userDetail.setDeviceInfos(deviceInfos);
                    userDetailRepository.save(userDetail);
                    // RegisterUser and Register Device

                }
            }
            response = prepareResponseMap(HCEMessageCodes.SUCCESS,userDetail,null);

            LOGGER.debug("Exit UserDetailServiceImpl->registerUser");

        }catch(HCEActionException regUserHCEactionException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", regUserHCEactionException);
            throw regUserHCEactionException;

        }catch(Exception regUserException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", regUserException);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }

        LOGGER.debug("Exit UserDetailServiceImpl->registerUser");
        return response;
    }



    private UserDetail saveUserDetails(RegisterUserRequest registerUserRequest) throws Exception{

        UserDetail userDetail = new UserDetail();
        userDetail.setStatus(HCEConstants.ACTIVE);
        userDetail.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
        userDetail.setClientWalletAccountId(HCEUtil.generateRandomId(HCEConstants.USER_PREFIX));
        userDetail.setUserId(registerUserRequest.getUserId());
        return userDetail;
    }
    private List<DeviceInfo> saveDeviceInfo(RegisterUserRequest registerUserRequest){
        DeviceInfo deviceInfo = new DeviceInfo();
        List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();
        deviceInfo.setStatus(HCEConstants.INITIATE);
        deviceInfo.setIsVisaEnabled(HCEConstants.INACTIVE);
        deviceInfo.setIsMastercardEnabled(HCEConstants.INACTIVE);
        deviceInfo.setClientDeviceId(registerUserRequest.getClientDeviceID());
        deviceInfo.setDeviceModel(registerUserRequest.getDevice_model());
        deviceInfo.setOsName(registerUserRequest.getOs_name());
        deviceInfo.setImei(registerUserRequest.getImei());
        deviceInfo.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
        deviceInfoList.add(deviceInfo);
        return deviceInfoList;

    }

    private void updateUserStatusIfOneDeviceIsLinked(DeviceInfo deviceInfo) {
        List<UserDetail> userDetails;
        UserDetail userDetail;
        userDetails = (List<UserDetail>) deviceInfo.getUserDetail();
        if(userDetails!=null && !userDetails.isEmpty()){
            userDetail = userDetails.get(0);
            if(userDetail.getDeviceInfos().size()==1){
                userDetail.setStatus(HCEConstants.INACTIVE);
                userDetailRepository.save(userDetail);
            }
        }
    }


    public boolean isClientDeviceIdExist(String clientDeviceId){
        List<DeviceInfo> deviceInfoList = deviceDetailRepository.findByClientDeviceIdandStatus(clientDeviceId,HCEConstants.ACTIVE);
        if(deviceInfoList!=null && !deviceInfoList.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @param deviceInfo
     */
    public void deactivateDevice(DeviceInfo deviceInfo){

        // call deactivate User
    }
    /**
     *
     * @param responseCode
     * @param userDetail
     * @param activationCode
     * @return Map
     */

    public Map<String,Object> prepareResponseMap(String responseCode,UserDetail userDetail,String activationCode ) throws Exception{

        Map<String,Object> responseMap = null;

        responseMap = ImmutableMap.of(
                HCEConstants.RESPONSE_CODE, responseCode,
                HCEConstants.MESSAGE, hceControllerSupport.prepareMessage(responseCode),
                HCEConstants.USER_DETAILS, userDetail,
                HCEConstants.ACTIVATION_CODE, activationCode);
        return responseMap;
    }
}