package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotificationServiceReq;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.repository.VisaCardDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.NotificationServiceVisaService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;

@Service
public class NotificationServiceVisaServiceImpl implements NotificationServiceVisaService{

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceVisaServiceImpl.class);
    HCEControllerSupport hceControllerSupport;
    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;
    private final CardDetailRepository cardDetailRepository;



    @Autowired
    RemoteNotificationService remoteNotificationService;

    @Autowired
    private Environment env;

    @Autowired
    public NotificationServiceVisaServiceImpl(HCEControllerSupport hceControllerSupport, CardDetailRepository cardDetailRepository, UserDetailRepository userDetailRepository, DeviceDetailRepository deviceDetailRepository) {
        this.hceControllerSupport = hceControllerSupport;
        this.cardDetailRepository = cardDetailRepository;
        this.userDetailRepository = userDetailRepository;
        this.deviceDetailRepository = deviceDetailRepository;
    }



    public Map notifyLCMEvent(NotificationServiceReq notificationServiceReq,String apiKey,String eventType)
    {
        //Verify vProvisionID
        String vprovisionedTokenId = notificationServiceReq.getvProvisionedTokenID();
        final List<CardDetails> cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(vprovisionedTokenId);
        if(cardDetailsList==null || cardDetailsList.isEmpty()){

            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl -> notifyLCMEvent");
            return hceControllerSupport.formResponse(HCEMessageCodes.CARD_DETAILS_NOT_EXIST);
        }

        LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent");
        //Verify API key
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }

        HashMap<String, String> lcmNotificationData = new HashMap<>();

        try {
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.VTS);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);


            switch (eventType) {
                case HCEConstants.TOKEN_CREATED:
                case HCEConstants.TOKEN_STATUS_UPDATED:
                    //Send remote notification to call getTokenStatus
                    LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > TOKEN_STATUS_UPDATED");
                    lcmNotificationData.put(HCEConstants.OPERATION, HCEConstants.TOKEN_STATUS_UPDATED);
                    break;
                case HCEConstants.KEY_STATUS_UPDATED:
                    //Send remote notification to start replinish operation
                    LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > KEY_STATUS_UPDATED");
                    lcmNotificationData.put(HCEConstants.OPERATION, HCEConstants.KEY_STATUS_UPDATED);
                    //TODO:Update card Status
                    break;
                default:
                    //return invalid event Type


            }

            rnsGenericRequest.setRnsData(lcmNotificationData);
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            if (rnsResp.containsKey("errorCode")) {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > remoteNotification Sending Failed");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyLCMEvent" );
                return rnsResp;
            } else {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > remoteNotification Send");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyLCMEvent" );
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            LOGGER.debug("Exception Occored in  NotificationServiceVisaServiceImpl->-> notifyLCMEvent",e);
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }



    }
    public Map notifyPanMetadataUpdate (NotificationServiceReq notificationServiceReq,String apiKey)
    {
        LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyPanMetadataUpdate");
        //Verify vProvisionID
        //Verify API key
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }
        String vprovisionedTokenId = notificationServiceReq.getvProvisionedTokenID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        try{
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.VTS);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);
            lcmNotificationData.put(HCEConstants.OPERATION,HCEConstants.UPDATE_CARD_METADATA);
            rnsGenericRequest.setRnsData(lcmNotificationData);
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            if (rnsResp.containsKey("errorCode")) {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyPanMetadataUpdate - > remoteNotification Sending Failed");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyPanMetadataUpdate" );
                return rnsResp;
            } else {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyPanMetadataUpdate - > remoteNotification Sent");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyPanMetadataUpdate" );
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);

            }

        }catch (Exception e){
            e.printStackTrace();
            LOGGER.debug("Exception Occored in  NotificationServiceVisaServiceImpl->-> notifyPanMetadataUpdate",e);
            return  hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }

    public Map notifyTxnDetailsUpdate (NotificationServiceReq notificationServiceReq,String apiKey)
    {
        LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyTxnDetailsUpdate");
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }
        String vprovisionedTokenId = notificationServiceReq.getvProvisionedTokenID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        try{
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.VTS);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);
            lcmNotificationData.put(HCEConstants.OPERATION,HCEConstants.UPDATE_TXN_HISTORY);
            rnsGenericRequest.setRnsData(lcmNotificationData);
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            if (rnsResp.containsKey("errorCode")) {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyTxnDetailsUpdate - > remoteNotification Sending Failed");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate" );
                return rnsResp;
            } else {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyTxnDetailsUpdate - > remoteNotification Sent");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate" );
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);
            }

        }catch (Exception e){
            e.printStackTrace();
            LOGGER.debug("Exception Occored in  NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate",e);
            return  hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }
    private String getRnsRegId(String vProvisionedID)
    {
        String rnsRegID = null;
        final List<CardDetails> cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(vProvisionedID);
        if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
           final DeviceInfo deviceInfo = cardDetailsList.get(0).getDeviceInfo();
            rnsRegID = deviceInfo.getRnsRegistrationId();
        }
        return rnsRegID;
    }



}
