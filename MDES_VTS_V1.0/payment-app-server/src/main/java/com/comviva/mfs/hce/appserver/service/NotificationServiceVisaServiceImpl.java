package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;
import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.repository.VisaCardDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.NotificationServiceVisaService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;

@Service
public class NotificationServiceVisaServiceImpl implements NotificationServiceVisaService{
    HCEControllerSupport hceControllerSupport;
    private final VisaCardDetailRepository visaCardDetailRepository;
    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;



    @Autowired
    RemoteNotificationService remoteNotificationService;

    @Autowired
    private Environment env;

    @Autowired
    public NotificationServiceVisaServiceImpl(HCEControllerSupport hceControllerSupport, VisaCardDetailRepository visaCardDetailRepository, UserDetailRepository userDetailRepository, DeviceDetailRepository deviceDetailRepository) {
        this.hceControllerSupport = hceControllerSupport;
        this.visaCardDetailRepository = visaCardDetailRepository;
        this.userDetailRepository = userDetailRepository;
        this.deviceDetailRepository = deviceDetailRepository;
    }



    public Map notifyLCMEvent(NotificationServiceReq notificationServiceReq,String apiKey,String eventType)
    {
        //Verify vProvisionID

        //Verify API key
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }
        String vprovisionedTokenId = notificationServiceReq.getVProvisionedTokenID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();

        try {
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);


            switch (eventType) {
                case HCEConstants.TOKEN_CREATED:
                case HCEConstants.TOKEN_STATUS_UPDATED:
                    //Send remote notification to call getTokenStatus
                    lcmNotificationData.put(HCEConstants.OPERATION, HCEConstants.TOKEN_STATUS_UPDATED);
                    break;
                case HCEConstants.KEY_STATUS_UPDATED:
                    //Send remote notification to start replinish operation
                    lcmNotificationData.put(HCEConstants.OPERATION, HCEConstants.KEY_STATUS_UPDATED);
                    break;
                default:
                    //return invalid event Type


            }
            rnsGenericRequest.setRnsData(lcmNotificationData);
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            if (rnsResp.containsKey("errorCode")) {
                return rnsResp;
            } else
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);
        }catch (Exception e)
        {
            e.printStackTrace();
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }



    }
    public Map notifyPanMetadataUpdate (NotificationServiceReq notificationServiceReq,String apiKey)
    {
        //Verify vProvisionID
        //Verify API key
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }
        String vprovisionedTokenId = notificationServiceReq.getVProvisionedTokenID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        try{
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);
            lcmNotificationData.put(HCEConstants.OPERATION,HCEConstants.UPDATE_CARD_METADATA);
            rnsGenericRequest.setRnsData(lcmNotificationData);
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            if (rnsResp.containsKey("errorCode")) {
                return rnsResp;
            } else
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);

        }catch (Exception e){
            e.printStackTrace();
            return  hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }

    public Map notifyTxnDetailsUpdate (NotificationServiceReq notificationServiceReq,String apiKey)
    {
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }
        String vprovisionedTokenId = notificationServiceReq.getVProvisionedTokenID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        try{
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);
            lcmNotificationData.put(HCEConstants.OPERATION,HCEConstants.UPDATE_TXN_HISTORY);
            rnsGenericRequest.setRnsData(lcmNotificationData);
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            if (rnsResp.containsKey("errorCode")) {
                return rnsResp;
            } else
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);

        }catch (Exception e){
            e.printStackTrace();
            return  hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }
    private String getRnsRegId(String vProvisionedID)
    {
        String userName = visaCardDetailRepository.findByVProvisionedTokenId(vProvisionedID).get().getUserName();
        String clintDeviceId = userDetailRepository.findByUserName(userName).get().getClientDeviceId();
        String rnsRegID = deviceDetailRepository.findByClientDeviceId(clintDeviceId).get().getRnsRegistrationId();
        return rnsRegID;
    }


}
