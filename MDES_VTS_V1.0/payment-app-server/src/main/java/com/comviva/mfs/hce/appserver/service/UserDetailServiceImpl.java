package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.mapper.pojo.ActivateUserRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegisterUserRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
    String devicestatus;

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
        List<UserDetail> userDetails = userDetailRepository.find(registerUserRequest.getUserId());
        List<DeviceInfo> deviceInfo=deviceDetailRepository.find(registerUserRequest.getClientDeviceID());
        if ((null == userDetails || userDetails.isEmpty()) && (null==deviceInfo || deviceInfo.isEmpty())) {
            userstatus = "userRegistered";
            String activationCode = generateActivationCode();
            String clientWalletAccountid =generatelCientWalletAccountid(registerUserRequest.getUserId());
            savedUser = userDetailRepository.save(new UserDetail(null,registerUserRequest.getUserId(),activationCode, userstatus,
                    clientWalletAccountid,registerUserRequest.getClientDeviceID(), null));
            deviceDetailRepository.save(new DeviceInfo(null,null,null, null,registerUserRequest.getOs_name(),null,null,registerUserRequest.getImei(),registerUserRequest.getClientDeviceID(),null,registerUserRequest.getDevice_model(), null,"N","N","Not Registered with visa","Not Registered with Master Card","deviceRegistered",null,null,null,null,null,null,null,null,null,null,null));
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "200",
                    "message", "User has been successfully registered in the system,Please Activate using below activation code",
                   "userDetails", savedUser,
                    "activationCode", activationCode);
            return  response;
        }
        else if((null != userDetails || !userDetails.isEmpty()) && (null==deviceInfo || deviceInfo.isEmpty())){
            deviceDetailRepository.save(new DeviceInfo(null,null,null, null,registerUserRequest.getOs_name(),null,null,registerUserRequest.getImei(),registerUserRequest.getClientDeviceID(),null,registerUserRequest.getDevice_model(), null,"N","N","Not Registered with visa","Not Registered with Master Card","deviceRegistered",null,null,null,null,null,null,null,null,null,null,null));
            userDetails.get(0).setClientDeviceId(registerUserRequest.getClientDeviceID());
            userDetailRepository.save(userDetails.get(0));
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "200",
                    "message", "User has been successfully registered in the system,Please Activate using below activation code",
                    "userDetails", userDetails.get(0),
                    "activationCode", userDetails.get(0).getActivationCode());
            return  response;
        }else if ((null == userDetails || userDetails.isEmpty()) && (null !=deviceInfo || !deviceInfo.isEmpty())){
            userstatus = "userRegistered";
            String activationCode = generateActivationCode();
            String clientWalletAccountid =generatelCientWalletAccountid(registerUserRequest.getUserId());
            savedUser = userDetailRepository.save(new UserDetail(null,registerUserRequest.getUserId(),activationCode, userstatus,
                    clientWalletAccountid,registerUserRequest.getClientDeviceID(), deviceInfo.get(0).getPaymentAppInstanceId()));
            Map <String, Object> response = ImmutableMap.of(
                    "responseCode", "200",
                    "message", "User has been successfully registered in the system ,Activate account with below Activaction code",
                    "userDetails", savedUser,
                    "activationCode", activationCode);
            return  response;
        }
        else {
            if("userRegistered".equals(userDetails.get(0).getUserstatus())&& "deviceRegistered".equals(deviceInfo.get(0).getDeviceStatus())){
                Map <String, Object> response = ImmutableMap.of(
                        "responseCode", "200",
                        "message", "User already  registered in the system ,Activate account with below Activaction code",
                        "userDetails", userDetails.get(0),
                        "activationCode", userDetails.get(0).getActivationCode());
                return  response;
            }else if("userActivated".equals(userDetails.get(0).getUserstatus())&& "deviceRegistered".equals(deviceInfo.get(0).getDeviceStatus())){
                Map <String, Object> response = ImmutableMap.of(
                        "responseCode", "200",
                        "message", "User already  registered in the system ,Activate account with below Activaction code",
                        "userDetails", userDetails.get(0),
                        "activationCode", userDetails.get(0).getActivationCode());
                return  response;
            }else if("userRegistered".equals(userDetails.get(0).getUserstatus())&& "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
                Map <String, Object> response = ImmutableMap.of(
                        "responseCode", "200",
                        "message", "User already  registered in the system ,Activate account with below Activaction code",
                        "userDetails", userDetails.get(0),
                        "activationCode", userDetails.get(0).getActivationCode());
                return  response;
            }else{
                if(userDetails.get(0).getClientDeviceId().equals(deviceInfo.get(0).getClientDeviceId()) && "userActivated".equals(userDetails.get(0).getUserstatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus()) ){
                    Map <String, Object> response = ImmutableMap.of(
                            "responseCode", "200",
                            "message", "User already  registered in the system",
                            "userDetails", userDetails.get(0),
                            "activationCode", userDetails.get(0).getActivationCode());
                    return  response;

                }else{
                    Map <String, Object> response = ImmutableMap.of(
                            "responseCode", "200",
                            "message", "User already  registered in the system ,Activate account with below Activaction code",
                            "userDetails", userDetails.get(0),
                            "activationCode", userDetails.get(0).getActivationCode());
                    return  response;
                }
            }
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
        List<UserDetail> userDetails = userDetailRepository.find(activateUserRequest.getUserId());
        List<DeviceInfo> deviceInfo=deviceDetailRepository.find(activateUserRequest.getClientDeviceID());
        userstatus = "userActivated";
        devicestatus="deviceActivated";
        if((null == userDetails || userDetails.isEmpty()) || (null==deviceInfo || deviceInfo.isEmpty())) {
            Map<String, Object> response = ImmutableMap.of("message", "Invalid User please Register or device not registred in system", "responseCode", "203");
            return response;
        }
        else {
            if(!userDetails.get(0).getActivationCode().equals(activateUserRequest.getActivationCode())){
                //activaction code problem.
                Map <String, Object> response =ImmutableMap.of("message", "Wrong activaction Code", "responseCode", "202");
                return response;
            }else{
            List<UserDetail> userDevice = userDetailRepository.findByClientDeviceId(activateUserRequest.getClientDeviceID());
           // if("userActivated".equals(userDetails.get(0).getUserstatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
               if(null !=userDevice && !userDevice.isEmpty()) {
                   for (int i = 0; i <userDetails.size(); i++){
                       if (!userDevice.get(i).getUserName().equals(userDetails.get(0).getUserName())) {
                           userDevice.get(i).setClientDeviceId("CD");
                           userDetailRepository.save(userDevice.get(i));
                       }
               }
                }
                userDetails.get(0).setUserstatus(userstatus);
                userDetails.get(0).setClientDeviceId(activateUserRequest.getClientDeviceID());
                userDetailRepository.save(userDetails.get(0));
                deviceInfo.get(0).setDeviceStatus(devicestatus);
                deviceDetailRepository.save(deviceInfo.get(0));
        }
        }
        Map <String, Object> response =ImmutableMap.of("message", "User is activated", "responseCode", "200");
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