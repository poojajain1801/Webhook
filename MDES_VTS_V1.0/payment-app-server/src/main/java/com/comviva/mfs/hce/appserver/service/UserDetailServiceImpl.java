package com.comviva.mfs.hce.appserver.service;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.CardDetail;
import com.comviva.mfs.hce.appserver.mapper.PerformUserLifecycle;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserLifecycleManagementReq;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Date;

import com.comviva.mfs.hce.appserver.exception.*;
import sun.rmi.runtime.Log;

/**
 * Perform user registration and activation
 */
@Service
public class UserDetailServiceImpl implements UserDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;
    private final HCEControllerSupport hceControllerSupport;
    private final CardDetailRepository cardDetailRepository;
    @Autowired
    private Environment env;

    @Autowired
    private TokenLifeCycleManagementService tokenLifeCycleManagementService;
    @Autowired
    PerformUserLifecycle performLCMobj;
    private LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest;
    @Autowired
    public UserDetailServiceImpl(UserDetailRepository userDetailRepository,DeviceDetailRepository deviceDetailRepository, HCEControllerSupport hceControllerSupport,CardDetailRepository cardDetailRepository) {
        this.userDetailRepository = userDetailRepository;
        this.deviceDetailRepository=deviceDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.cardDetailRepository = cardDetailRepository;
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
            userId = registerUserRequest.getUserId();
            imei = registerUserRequest.getImei();

            if(isClientDeviceIdExist(registerUserRequest.getClientDeviceID())){
                throw new HCEActionException(HCEMessageCodes.getClientDeviceidExist());
            }

            userDetails = userDetailRepository.findByUserIdAndStatus(userId,HCEConstants.ACTIVE);

            if(userDetails!=null && !userDetails.isEmpty()){

                userDetail = userDetails.get(0);

                deviceInfos = deviceDetailRepository.findByImeiAndStatus(imei,HCEConstants.ACTIVE);
                if(deviceInfos!=null && !deviceInfos.isEmpty()){

                        deviceInfo = deviceInfos.get(0);
                        deactivateDevice(deviceInfo);
                        deviceInfo.setStatus(HCEConstants.INACTIVE);
                        deviceDetailRepository.save(deviceInfo);
                        updateUserStatusIfOneDeviceIsLinked(deviceInfo,userId);

                        deviceInfo = saveDeviceInfo(registerUserRequest,userDetail);
                        deviceInfo.setUserDetail(userDetail);
                        deviceDetailRepository.save(deviceInfo);
                        // Register New Device
                        //update Old device with N and if owner of that user is having one device then make user status N too. and register device.

                }else{

                    deviceInfo = saveDeviceInfo(registerUserRequest,userDetail);
                    deviceInfo.setUserDetail(userDetail);
                    deviceDetailRepository.save(deviceInfo);
                    //Register Device

                }

            }else{

                deviceInfos = deviceDetailRepository.findByImeiAndStatus(imei,HCEConstants.ACTIVE);
                if(deviceInfos!=null && !deviceInfos.isEmpty()){
                    deviceInfo = deviceInfos.get(0);
                    deactivateDevice(deviceInfo);
                    deviceInfo.setStatus(HCEConstants.INACTIVE);
                    deviceDetailRepository.save(deviceInfo);
                    updateUserStatusIfOneDeviceIsLinked(deviceInfo,userId);
                    userDetail = saveUserDetails(registerUserRequest);
                    userDetailRepository.save(userDetail);
                    deviceInfo = saveDeviceInfo(registerUserRequest,userDetail);
                    deviceInfo.setUserDetail(userDetail);
                    deviceDetailRepository.save(deviceInfo);


                    //update Old device with N and if owner of that user is having one device then make user status N too. and register device and user.

                }else{

                    userDetail = saveUserDetails(registerUserRequest);
                    userDetailRepository.saveAndFlush(userDetail);
                    deviceInfo = saveDeviceInfo(registerUserRequest,userDetail);
                    deviceInfo.setUserDetail(userDetail);
                    deviceDetailRepository.saveAndFlush(deviceInfo);
                    // RegisterUser and Register Device

                }
            }
            response = prepareResponseMap(HCEMessageCodes.getSUCCESS(),userDetail,null);

        }catch(HCEActionException regUserHCEactionException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", regUserHCEactionException);
            throw regUserHCEactionException;

        }catch(Exception regUserException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", regUserException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
    }

    /**
     *
     * @param userLifecycleManagementReq
     * @return
     */
    @Override
    @Transactional
    public Map<String,Object>userLifecycleManagement(UserLifecycleManagementReq userLifecycleManagementReq){
        UserDetail userDetails;
        LOGGER.debug("Inside userLifecycleManagement");
        try{
            if(userLifecycleManagementReq.getUserId().isEmpty() || userLifecycleManagementReq.getUserId().equalsIgnoreCase("null"))
            {
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }

           userDetails = userDetailRepository.findByUserId(userLifecycleManagementReq.getUserId());
            if(userDetails==null ||userDetails.getUserId().isEmpty())
            {
                throw new HCEActionException(HCEMessageCodes.getInvalidUser());
            }
            performLCMobj.performLCM(userLifecycleManagementReq,userDetails);

            //Update the user satatus
            //Update all the card status
            LOGGER.debug("Exit userLifecycleManagement");
            return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());



        }catch(HCEActionException userLifecycleManagementException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", userLifecycleManagementException);
            throw userLifecycleManagementException;

        }catch(Exception userLifecycleManageException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", userLifecycleManageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        //Check if user is valid or not

    // return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
    }



    private void performMastercardLifecycle(List<CardDetails> masterCardList,String operation)
    {

    }
    private UserDetail saveUserDetails(RegisterUserRequest registerUserRequest) throws Exception{

        UserDetail userDetail = null;
        UserDetail oldUserDetail = userDetailRepository.findByUserId(registerUserRequest.getUserId());
        if(oldUserDetail!=null ){
            userDetail = oldUserDetail;
        }else{
            userDetail = new UserDetail();
            userDetail.setClientWalletAccountId(HCEUtil.generateRandomId(HCEConstants.USER_PREFIX));
        }
        userDetail.setStatus(HCEConstants.ACTIVE);
        userDetail.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
        userDetail.setUserId(registerUserRequest.getUserId());
        return userDetail;
    }
    private DeviceInfo saveDeviceInfo(RegisterUserRequest registerUserRequest,UserDetail userDetail){
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setStatus(HCEConstants.INITIATE);
        deviceInfo.setIsVisaEnabled(HCEConstants.INACTIVE);
        deviceInfo.setIsMastercardEnabled(HCEConstants.INACTIVE);
        deviceInfo.setClientDeviceId(registerUserRequest.getClientDeviceID());
        deviceInfo.setDeviceModel(registerUserRequest.getDevice_model());
        deviceInfo.setOsName(registerUserRequest.getOs_name());
        deviceInfo.setImei(registerUserRequest.getImei());
        deviceInfo.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
        deviceInfo.setUserDetail(userDetail);
        return deviceInfo;

    }

    private void updateUserStatusIfOneDeviceIsLinked(DeviceInfo deviceInfo, String userId) {
        List<UserDetail> userDetails;
        UserDetail userDetail;
        userDetail =  deviceInfo.getUserDetail();
        if(userDetail!=null){
            if(userDetail.getDeviceInfos().size()==1 && !userId.equals(userDetail.getUserId())){
                userDetail.setStatus(HCEConstants.INACTIVE);
                userDetailRepository.save(userDetail);
            }
        }
    }


    public boolean isClientDeviceIdExist(String clientDeviceId){
        List<DeviceInfo> deviceInfoList = deviceDetailRepository.findByClientDeviceIdAndStatus(clientDeviceId,HCEConstants.ACTIVE);
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
                HCEConstants.CLIENT_WALLET_ACCOUNT_ID, userDetail.getClientWalletAccountId());
        return responseMap;
    }
}