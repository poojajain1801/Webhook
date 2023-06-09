package com.comviva.mfs.hce.appserver.service;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.CardDetail;
import com.comviva.mfs.hce.appserver.mapper.PerformUserLifecycle;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
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
    private PerformUserLifecycle performLCMobj;
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
        String languageCode =null;
        UserDetail userDetail;
        DeviceInfo deviceInfo;
        String userId ;
        String imei;
        try{
            LOGGER.debug("Register user service *****************");
            userId = registerUserRequest.getUserId();
            imei = registerUserRequest.getImei();
            languageCode = registerUserRequest.getLanguageCode();
            if(languageCode == null || languageCode.isEmpty()){
                registerUserRequest.setLanguageCode("1");
            }
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
    public Map<String, Object> userLifecycleManagement(UserLifecycleManagementReq userLifecycleManagementReq) {
        UserDetail userDetails;
        Map responseMap = null;
        Map userMap = null;
        LOGGER.debug("Inside userLifecycleManagement");
        List<String> userIdlist = null;
        List<Map> userMapList = null;
        String message = null;
        String messageCode = null;
        try {
            userIdlist = userLifecycleManagementReq.getUserIdList();
            if (userIdlist.size() <= 0) {
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            responseMap = new LinkedHashMap();

            userMapList = new ArrayList<>();
            for (int i = 0; i < userIdlist.size(); i++) {
                userMap = new LinkedHashMap();
                userDetails = userDetailRepository.findByUserId(userIdlist.get(i));

                if (userDetails == null || userDetails.getUserId().isEmpty()) {
                    message = "User ID does not exist";
                    messageCode = HCEMessageCodes.getInvalidUser();
                    userMap.put("UserId", userIdlist.get(i));
                    userMap.put("Status", HCEConstants.INACTIVE);
                    userMap.put("Message", message);
                    userMap.put("MessageCode",messageCode);
                } else {
                    if (userDetails.getStatus().equalsIgnoreCase(HCEConstants.ACTIVE)) {
                        message = "User ID avilable";
                        messageCode = HCEMessageCodes.getSUCCESS();
                    } else {
                        message = "User ID avilable but Inactive";
                        messageCode = HCEMessageCodes.getInvalidUser();
                    }
                    userMap.put("UserId", userIdlist.get(i));
                    userMap.put("Status", userDetails.getStatus());
                    userMap.put("Message", message);
                    userMap.put("MessageCode",messageCode);
                }
                userMapList.add(userMap);
               /* if ((userDetails!=null)&&(!userDetails.getStatus().equalsIgnoreCase(HCEConstants.INACTIVE))) {
                    performLCMobj.performLCM(userIdlist.get(i), userLifecycleManagementReq.getOperation(), userDetails);
                }
*/

            }
            performLCMobj.performUserLCM(userIdlist,userLifecycleManagementReq.getOperation());

            responseMap.put("UserStaus", userMapList);
            responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getSUCCESS());
            responseMap.put(HCEConstants.MESSAGE, "SUCSSES");
            // userDetails = userDetailRepository.findByUserId(userLifecycleManagementReq.getUserId());


            //Update the user satatus
            //Update all the card status
            LOGGER.debug("Exit userLifecycleManagement");
            return responseMap;

        } catch (HCEActionException userLifecycleManagementException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", userLifecycleManagementException);
            throw userLifecycleManagementException;

        } catch (Exception userLifecycleManageException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", userLifecycleManageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

    }

    @Override
    public Map<String, Object> getLanguage(GetLanguageReq getLanguageReq) {
        String userId = null;
        Map languageResp = new HashMap();
        UserDetail userDetail = null;
        String languageCode = null;

        try {
            userId = getLanguageReq.getUserId();
            userDetail = userDetailRepository.findByUserId(userId);
            if (userDetail == null ){
                LOGGER.info("No user is registered with userId :"+userId);
                throw new HCEActionException(HCEMessageCodes.getInvalidUser());
            }
            languageCode = userDetail.getLanguageCode();
            languageResp.put("languageCode",languageCode);

        }catch (HCEActionException getLanguageException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->getLanguage", getLanguageException);
            throw getLanguageException;

        } catch (Exception getLanguageException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", getLanguageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return languageResp;
    }

    @Override
    public Map<String, Object> setLanguage(SetLanguageReq setLanguageReq) {
        String userId = null;
        String languageCode = null;
        UserDetail userDetail;
        Map languageResponse = new HashMap();
        try {
            userId = setLanguageReq.getUserId();
            languageCode = setLanguageReq.getLanguageCode();
            userDetail = userDetailRepository.findByUserId(userId);
            if (userDetail == null) {
                LOGGER.info("no user is registered with userId : " + userId);
                throw new HCEActionException(HCEMessageCodes.getInvalidUser());
            }
            userDetail.setLanguageCode(languageCode);
            userDetailRepository.save(userDetail);
            languageResponse.put("responseCode", HCEMessageCodes.getSUCCESS());

        } catch (HCEActionException setLanguageException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->setLanguage", setLanguageException);

            throw setLanguageException;

        } catch (Exception setLanguageException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", setLanguageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return languageResponse;
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
        userDetail.setLanguageCode(registerUserRequest.getLanguageCode());
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

