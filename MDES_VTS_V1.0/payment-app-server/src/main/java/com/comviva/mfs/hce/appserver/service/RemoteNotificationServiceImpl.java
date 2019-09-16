package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.*;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RemoteNotificationServiceImpl implements com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService {
    private final DeviceDetailRepository deviceDetailRepository;
    private HCEControllerSupport hceControllerSupport;

    @Autowired
    public RemoteNotificationServiceImpl(DeviceDetailRepository deviceDetailRepository,HCEControllerSupport hceControllerSupport) {
        this.deviceDetailRepository = deviceDetailRepository;
    }
    @Autowired
    private Environment env;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNotificationServiceImpl.class);

    public Map sendRemoteNotificationMessage(RemoteNotificationRequest remoteNotificationReq) {
        Map responseMap = new HashMap();
        String paymentAppProviderId = remoteNotificationReq.getPaymentAppProviderId();
        String paymentAppInstanceId = remoteNotificationReq.getPaymentAppInstanceId();
        String notificationData = remoteNotificationReq.getNotificationData();
        HashMap rnsNotificationData = new HashMap();
        LOGGER.debug("Inside RemoteNotificationServiceImpl---------->sendRemoteNotificationMessage");
        RnsGenericRequest rnsGenericRequest ;
        try {

        /*    //Temp
            String notificationDataTemp = notificationData;
            notificationDataTemp = new String(Base64.getDecoder().decode(notificationData.getBytes()));
            JSONObject notificationJson = new JSONObject(notificationDataTemp);
            String responseHost = notificationJson.getString("responseHost");
            String []part1 = responseHost.split("//",2);
            responseHost = part1[1];
            notificationJson.put("responseHost",responseHost);
            notificationData = Base64.getEncoder().encodeToString(notificationJson.toString().getBytes());
            //endTemp*/
            rnsGenericRequest = new RnsGenericRequest();
            rnsNotificationData.put("paymentAppProviderId",paymentAppProviderId);
            rnsNotificationData.put("notificationData",notificationData);
            rnsNotificationData.put("paymentAppInstanceId",paymentAppInstanceId);
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(paymentAppInstanceId));
            rnsGenericRequest.setRnsData(rnsNotificationData);
            Map rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());
            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority","high");
            payloadObject.put("time_to_live",2160000);
            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());
            Gson gson = new Gson();
            String json = gson.toJson(response);
            LOGGER.debug("RemoteNotificationServiceImpl -> sendRemoteNotification->Raw response from FCM server"+json);


      /*
        byte[] rnsPostData = Base64.getDecoder().decode(remoteNotificationReq.getNotificationData().getBytes());
        RnsResponse response = rns.sendRns(rnsPostData);*/

            if (Integer.valueOf(response.getErrorCode()) != 200) {
                return ImmutableMap.of("errorCode", "234",
                        "errorDescription", "RNS Unavailable");
            }
            responseMap.put("responseHost", "wallet.mahindracomviva.com");
            responseMap.put("responseId",ArrayUtil.getHexString(ArrayUtil.getRandom(8)));
        }catch (HCEActionException remoteNotificationHCEactionException) {
            LOGGER.error("Exception occured in RemoteNotificationServiceImpl->sendRemoteNotificationMessage", remoteNotificationHCEactionException);
            throw remoteNotificationHCEactionException;
        }catch (Exception remoteNotificationException) {
            LOGGER.error("Exception occured in RemoteNotificationServiceImpl->sendRemoteNotificationMessage", remoteNotificationException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public Map sendRemoteNotification(RnsGenericRequest rnsGenericRequest) {
        Map responseMap = new HashMap();
        try {
            LOGGER.debug("Inside RemoteNotificationServiceImpl -> sendRemoteNotification");
            // Create Remote Notification Data
            Map rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());
            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority","high");
            payloadObject.put("time_to_live",2160000);
            LOGGER.debug("RemoteNotificationServiceImpl -> sendRemoteNotification->Request payload send to FCM : ",payloadObject.toString());
            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());
            Gson gson = new Gson();
            String json = gson.toJson(response);
            LOGGER.debug("RemoteNotificationServiceImpl -> sendRemoteNotification->Raw response from FCM server"+json);
            if (Integer.valueOf(response.getErrorCode()) != 200) {
                return ImmutableMap.of("errorCode", "720",
                        "errorDescription", "UNABLE_TO_DELIVERFCM_MESSAGE");
            }
            responseMap.put("responseHost", "wallet.mahindracomviva.com");
            responseMap.put("responseId",ArrayUtil.getHexString(ArrayUtil.getRandom(16)));
        }catch (HCEActionException remoteNotificationHCEactionException) {
            LOGGER.error("Exception occured in RemoteNotificationServiceImpl->sendRemoteNotification", remoteNotificationHCEactionException);
            throw remoteNotificationHCEactionException;
        }catch (Exception remoteNotificationException) {
            LOGGER.error("Exception occured in RemoteNotificationServiceImpl->sendRemoteNotification", remoteNotificationException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());

        }
        return responseMap;
    }

    @Override
    public Map<String, Object> getDeviceInfo(GetDeviceInfoRequest getDeviceInfoRequest) {
        String tokenUniqueReference = null;
        String paymentAppInstanceId = null;
        Optional<DeviceInfo> deviceDetailList = null;
        Map responseMap = new HashMap();
        Map deviceInfo = new HashMap();

        try {
            tokenUniqueReference = getDeviceInfoRequest.getTokenUniqueReference();
            paymentAppInstanceId = getDeviceInfoRequest.getPaymentAppInstanceId();
            deviceDetailList = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
            if (deviceDetailList.isPresent()){
                deviceInfo.put("deviceName",deviceDetailList.get().getDeviceName());
                deviceInfo.put("serialNumber",deviceDetailList.get().getImei());
                responseMap.put("deviceInfo",deviceInfo);
                responseMap.put("responseHost", "wallet.mahindracomviva.com");
                responseMap.put("responseId",ArrayUtil.getHexString(ArrayUtil.getRandom(8)));
            }else {
                throw new HCEActionException(HCEMessageCodes.getInvalidPaymentAppInstanceId());
            }

        }catch (HCEActionException getDeviceInfoHCEactionException) {
            LOGGER.error("Exception occured in RemoteNotificationServiceImpl->getDeviceInfo", getDeviceInfoHCEactionException);
            throw getDeviceInfoHCEactionException;

        } catch (Exception getDeviceInfoException) {
            LOGGER.error("Exception occured in RemoteNotificationServiceImpl->getDeviceInfo", getDeviceInfoException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    private String getRnsRegId(String paymentAppInstanceId) {
        String rnsRegID = null;
        final Optional<DeviceInfo> deviceDetailsList = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
        if(deviceDetailsList.isPresent() ){
            final DeviceInfo deviceInfo = deviceDetailsList.get();
            rnsRegID = deviceInfo.getRnsRegistrationId();
        }
        return rnsRegID;
    }
}
