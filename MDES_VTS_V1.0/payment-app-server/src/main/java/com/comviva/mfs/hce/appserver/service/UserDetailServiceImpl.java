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

import java.util.Calendar;
import java.util.List;
import java.util.Map;
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
        List<DeviceInfo> deviceInfo;
        String activationCode;
        String clientWalletAccountid;
        UserDetail savedUser;
        String userstatus;
        String devicestatus;


        try{
            LOGGER.debug("Enter UserDetailServiceImpl->registerUser");

            if(registerUserRequest.getUserId()==null || registerUserRequest.getUserId().isEmpty()
                    || registerUserRequest.getClientDeviceID()==null || registerUserRequest.getClientDeviceID().isEmpty()){

               throw  new HCEActionException(HCEMessageCodes.INSUFFICIENT_DATA);
            }

            userDetails = userDetailRepository.find(registerUserRequest.getUserId());
            deviceInfo=deviceDetailRepository.find(registerUserRequest.getClientDeviceID());


            if ((null == userDetails || userDetails.isEmpty()) && (null==deviceInfo || deviceInfo.isEmpty())) {
                 userstatus = "userActivated";
                 activationCode = generateActivationCode();
                 clientWalletAccountid =generatelCientWalletAccountid(registerUserRequest.getUserId());
                 savedUser = userDetailRepository.save(new UserDetail(null,registerUserRequest.getUserId(),activationCode, userstatus,
                        clientWalletAccountid,registerUserRequest.getClientDeviceID(), null));
                 deviceDetailRepository.save(new DeviceInfo(null,null,null, null,registerUserRequest.getOs_name(),null,null,registerUserRequest.getImei(),registerUserRequest.getClientDeviceID(),null,registerUserRequest.getDevice_model(), null,"N","N","Not Registered with visa","Not Registered with Master Card","deviceActivated",null,null,null,null,null,null,null,null,null,null,null));
                response =  prepareResponseMap(HCEMessageCodes.SUCCESS,savedUser,activationCode);
            }else if((null != userDetails || !userDetails.isEmpty()) && (null==deviceInfo || deviceInfo.isEmpty())){
                deviceDetailRepository.save(new DeviceInfo(null,null,null, null,registerUserRequest.getOs_name(),null,null,registerUserRequest.getImei(),registerUserRequest.getClientDeviceID(),null,registerUserRequest.getDevice_model(), null,"N","N","Not Registered with visa","Not Registered with Master Card","deviceActivated",null,null,null,null,null,null,null,null,null,null,null));
                userDetails.get(0).setClientDeviceId(registerUserRequest.getClientDeviceID());
                userDetailRepository.save(userDetails.get(0));
                response =  prepareResponseMap(HCEMessageCodes.SUCCESS,userDetails.get(0),userDetails.get(0).getActivationCode());
            }else if ((null == userDetails || userDetails.isEmpty()) && (null !=deviceInfo || !deviceInfo.isEmpty())){
                userstatus = "userActivated";
                 activationCode = generateActivationCode();
                 clientWalletAccountid =generatelCientWalletAccountid(registerUserRequest.getUserId());
                savedUser = userDetailRepository.save(new UserDetail(null,registerUserRequest.getUserId(),activationCode, userstatus,
                        clientWalletAccountid,registerUserRequest.getClientDeviceID(), deviceInfo.get(0).getPaymentAppInstanceId()));
                response =  prepareResponseMap(HCEMessageCodes.SUCCESS,savedUser,activationCode);
            }
            else {
                if("userActivated".equals(userDetails.get(0).getUserStatus())&& "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
                    response =  prepareResponseMap(HCEMessageCodes.USER_ACTIVATION_REQUIRED,userDetails.get(0),userDetails.get(0).getActivationCode());
                }else if("userActivated".equals(userDetails.get(0).getUserStatus())&& "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
                    response =  prepareResponseMap(HCEMessageCodes.USER_ACTIVATION_REQUIRED,userDetails.get(0),userDetails.get(0).getActivationCode());
                }else if("userActivated".equals(userDetails.get(0).getUserStatus())&& "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
                    response =  prepareResponseMap(HCEMessageCodes.USER_ACTIVATION_REQUIRED,userDetails.get(0),userDetails.get(0).getActivationCode());
                }else{
                    if(userDetails.get(0).getClientDeviceId().equals(deviceInfo.get(0).getClientDeviceId()) && "userActivated".equals(userDetails.get(0).getUserStatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus()) ){
                        response =  prepareResponseMap(HCEMessageCodes.USER_ALREADY_REGISTERED,userDetails.get(0),userDetails.get(0).getActivationCode());
                    }else{
                        response =  prepareResponseMap(HCEMessageCodes.USER_ACTIVATION_REQUIRED,userDetails.get(0),userDetails.get(0).getActivationCode());
                    }
                }
            }
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

    public Map<String,Object> activateUser(ActivateUserRequest activateUserRequest) {
        String userstatus ;
        String devicestatus;
        List<UserDetail> userDetails;
        List<DeviceInfo> deviceInfo;
        Map<String, Object> response;
        List<UserDetail> userDevice;

        try{

            LOGGER.debug("Enter UserDetailServiceImpl->activateUser");
            if(activateUserRequest.getUserId()==null || activateUserRequest.getActivationCode()==null ||
                    activateUserRequest.getUserId().isEmpty() || activateUserRequest.getActivationCode().isEmpty()){
                throw new HCEActionException(HCEMessageCodes.INSUFFICIENT_DATA);

            }
             userDetails = userDetailRepository.find(activateUserRequest.getUserId());
             deviceInfo=deviceDetailRepository.find(activateUserRequest.getClientDeviceID());
             userstatus = "userActivated";
             devicestatus="deviceActivated";

            if((null == userDetails || userDetails.isEmpty()) || (null==deviceInfo || deviceInfo.isEmpty())) {
               throw new HCEActionException(HCEMessageCodes.INVALID_USER_AND_DEVICE);
            }
            else {
                if(!userDetails.get(0).getActivationCode().equals(activateUserRequest.getActivationCode())){
                    //activaction code problem.
                   throw new HCEActionException(HCEMessageCodes.INVALID_ACTIVATION_CODE);
                }else{
                     userDevice = userDetailRepository.findByClientDeviceId(activateUserRequest.getClientDeviceID());
                    if(null !=userDevice && !userDevice.isEmpty()) {
                        for (int i = 0; i <userDetails.size(); i++){
                            if (!userDevice.get(i).getUserName().equals(userDetails.get(0).getUserName())) {
                                userDevice.get(i).setClientDeviceId(HCEConstants.CHANGE_DEVICE);
                                userDetailRepository.save(userDevice.get(i));
                            }
                        }
                    }
                    userDetails.get(0).setUserStatus(userstatus);
                    userDetails.get(0).setClientDeviceId(activateUserRequest.getClientDeviceID());
                    userDetailRepository.save(userDetails.get(0));
                    deviceInfo.get(0).setDeviceStatus(devicestatus);
                    deviceDetailRepository.save(deviceInfo.get(0));
                }
            }
             response =hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);
        }catch(HCEActionException actUserHCEactionException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->activateUser", actUserHCEactionException);
          throw actUserHCEactionException;

        }catch(Exception actUserException){
            LOGGER.error("Exception occured in UserDetailServiceImpl->activateUser", actUserException);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }

        LOGGER.debug("Exit UserDetailServiceImpl->activateUser");

        return response;
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
        String userStaus = userDetailRepository.findByUserName(userName).get().getUserStatus();
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