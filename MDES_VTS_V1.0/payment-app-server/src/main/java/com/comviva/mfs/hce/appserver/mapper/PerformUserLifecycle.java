package com.comviva.mfs.hce.appserver.mapper;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserLifecycleManagementReq;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.UserDetailServiceImpl;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import org.hibernate.cfg.Environment;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PerformUserLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    private final CardDetailRepository cardDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;
    private final UserDetailRepository userDetailRepository;
    @Autowired
    private TokenLifeCycleManagementService tokenLifeCycleManagementService;

    @Autowired
    private org.springframework.core.env.Environment env;

    @Autowired
    private HitMasterCardService hitMasterCardService;

    @Autowired
    private RemoteNotificationService remoteNotificationService;

    private LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest;

    public PerformUserLifecycle(CardDetailRepository cardDetailRepository, DeviceDetailRepository deviceDetailRepository, UserDetailRepository userDetailRepository) {
        this.cardDetailRepository = cardDetailRepository;
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailRepository = userDetailRepository;
    }

    @Async
    @Transactional
    public void performLCM(UserLifecycleManagementReq userLifecycleManagementReq, UserDetail userDetails) {
        LOGGER.debug("Inside PerformLCM");
        List<DeviceInfo> deviceInfoList = null;
        DeviceInfo deviceInfo = null;
        List<String> rnsIdList = null;
        String userSatus = null;
        String updatedUserStatus = null;
        String operation = null;
        try {
            operation = userLifecycleManagementReq.getOperation();

            switch (userLifecycleManagementReq.getOperation()) {
                case HCEConstants.SUSUPEND_USER:
                    userSatus = HCEConstants.ACTIVE;
                    updatedUserStatus = HCEConstants.SUSUPEND;
                    break;
                case HCEConstants.UNSUSPEND_USER:
                    userSatus = HCEConstants.SUSUPEND;
                    updatedUserStatus = HCEConstants.ACTIVE;
                    break;
                case HCEConstants.DELETE_USER:
                    userSatus = HCEConstants.ACTIVE;
                    updatedUserStatus = HCEConstants.INACTIVE;
                    break;
                default:
                    break;
            }

            //Get the list of device info associated with the userID
            deviceInfoList = deviceDetailRepository.findByClientWalletAccountIdAndStatus(userDetails.getClientWalletAccountId(), userSatus);
            if (deviceInfoList.isEmpty()) {
                throw new HCEActionException(HCEMessageCodes.getDeviceNotRegistered());
            }

            if(userLifecycleManagementReq.getOperation().equalsIgnoreCase(HCEConstants.DELETE_USER))
            {
                for (int i = 0; i < deviceInfoList.size(); i++) {
                    deviceInfo = deviceInfoList.get(i);
                    performDeleteUser(userLifecycleManagementReq,deviceInfo.getPaymentAppInstanceId(),deviceInfo);
                }

            }
            //Get all cards for the device and
            rnsIdList = new ArrayList<>();
            for (int i = 0; i < deviceInfoList.size(); i++) {
                deviceInfo = deviceInfoList.get(i);
                rnsIdList.add(deviceInfo.getRnsRegistrationId());
                deviceInfo.setStatus(updatedUserStatus);
                deviceDetailRepository.save(deviceInfo);

                //Suspend or resume All the cards for this device

            }

            sendRnsMessage(rnsIdList, operation);

            userDetails.setStatus(updatedUserStatus);
            userDetailRepository.save(userDetails);
        } catch (HCEActionException userLifecycleManagementException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", userLifecycleManagementException);
            throw userLifecycleManagementException;

        } catch (Exception userLifecycleManageException) {
            LOGGER.error("Exception occured in UserDetailServiceImpl->registerUser", userLifecycleManageException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit userLifecycleManagement");
    }

    private void sendRnsMessage(List<String> rnsIdList, String operation) {
        LOGGER.debug("Inside sendRnsMessage");
        HashMap<String, String> notificationData = new HashMap<>();
        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        try {
            rnsGenericRequest.setIdType(UniqueIdType.ALL);
            for (int i = 0; i < rnsIdList.size(); i++) {
                rnsGenericRequest.setRegistrationId(rnsIdList.get(i));
                notificationData.put(HCEConstants.OPERATION, operation);
                rnsGenericRequest.setRnsData(notificationData);
                sendNotification(rnsGenericRequest);
            }
        } catch (Exception e) {
            LOGGER.error("Exception in sendRnsMessage", e);
        }

        // notificationData.put("vPanEnrollmentId", panUniqueReference);

        LOGGER.debug("Exit performLCM");

    }

    public void sendNotification(RnsGenericRequest rnsGenericRequest) throws Exception {

        Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
        if (rnsResp.containsKey("errorCode")) {
            LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> sendNotification-> notifyPanMetadataUpdate - > remoteNotification Sending Failed");
            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->sendNotification-> notifyPanMetadataUpdate");
            //    throw new HCEActionException(HCEConstants.SERVICE_FAILED);
        } else {
            LOGGER.debug("Inside performLCM -> sendNotification-> remoteNotification Sent");
            LOGGER.debug("EXIT  performLCM -> sendNotification-> remoteNotification Sent");
            // throw new HCEActionException(HCEMessageCodes.SUCCESS);
        }
    }

    private void performDeleteUser(UserLifecycleManagementReq userLifecycleManagementReq,String paymentAppInstanceId, DeviceInfo deviceInfo) {
        //Get all the active and suspended card
        List<CardDetails> cardDetails = null;
        CardDetails cardDetailobj = null;
        List<CardDetails> masterCardList = null;
        List<CardDetails> visaCardList = null;
        cardDetails = cardDetailRepository.getCardList(userLifecycleManagementReq.getUserId(), HCEConstants.ACTIVE, HCEConstants.ACTIVE, HCEConstants.SUSUPEND);
        masterCardList = new ArrayList<>();
        visaCardList = new ArrayList<>();
        for (int i = 0; i < cardDetails.size(); i++) {
            cardDetailobj = cardDetails.get(i);
            if (cardDetailobj.getCardType().equalsIgnoreCase(HCEConstants.VISA)) {
                visaCardList.add(cardDetailobj);
            } else {
                masterCardList.add(cardDetailobj);

            }
        }
        performVisaLifecycle(visaCardList,"DELETE");
        unregisterMdes(paymentAppInstanceId);
        if(deviceInfo!=null)
        {
            cardDetailRepository.updateCardDetails(deviceInfo.getClientDeviceId(),HCEConstants.INACTIVE);
            deviceInfo.setStatus(HCEConstants.INACTIVE);
            deviceDetailRepository.save(deviceInfo);

        }else
        {
            throw new HCEActionException(HCEMessageCodes.getDeviceNotRegistered());

        }
    }

    private void performVisaLifecycle(List<CardDetails> visaCardList, String operation) {
        CardDetails cardDetailobj;
        Map<String, Object> response = null;
        lifeCycleManagementVisaRequest = new LifeCycleManagementVisaRequest();
        for (int i = 0; i < visaCardList.size(); i++) {
            cardDetailobj = visaCardList.get(i);
            lifeCycleManagementVisaRequest.setOperation(operation);
            lifeCycleManagementVisaRequest.setReasonCode("CUSTOMER_CONFIRMED");
            lifeCycleManagementVisaRequest.setReasonDesc("Customer Initiated");
            lifeCycleManagementVisaRequest.setVprovisionedTokenID(cardDetailobj.getVisaProvisionTokenId());
            response = tokenLifeCycleManagementService.lifeCycleManagementVisa(lifeCycleManagementVisaRequest);
            LOGGER.debug("Suspend Response = ", response);
        }

    }
    private void unregisterMdes(String paymentAppInstanceID)
    {
        JSONObject requestJson;
        String url;
        ResponseEntity responseEntitye;
        String id = null;
        try {
            requestJson = new JSONObject();
            requestJson.put("responseHost","Wallet.mahindracomviva.com");
            requestJson.put("requestId","12344");
            requestJson.put("paymentAppInstanceId",paymentAppInstanceID);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport")+"mdes"+"mpamanagement"+"1/0";
            id = "unregister";
            responseEntitye = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST",id);
            if ((responseEntitye==null) || (responseEntitye.getStatusCode().value()!=HCEConstants.REASON_CODE7))
            {
               LOGGER.error("Mstercard Unregister failed...");

            }
        } catch (HCEActionException e) {
            LOGGER.error("Error in unregisterMdes",e);
        }
    }
}
