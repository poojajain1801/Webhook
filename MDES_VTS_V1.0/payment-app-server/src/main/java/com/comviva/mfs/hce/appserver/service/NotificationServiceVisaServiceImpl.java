package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenStatusRequest;
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
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import com.google.gson.Gson;
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
    private HCEControllerSupport hceControllerSupport;
    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;
    private final CardDetailRepository cardDetailRepository;



    @Autowired
    private RemoteNotificationService remoteNotificationService;

    @Autowired
    private Environment env;

    @Autowired
    private TokenLifeCycleManagementService tokenLifeCycleManagementService ;

    @Autowired
    public NotificationServiceVisaServiceImpl(HCEControllerSupport hceControllerSupport, CardDetailRepository cardDetailRepository, UserDetailRepository userDetailRepository, DeviceDetailRepository deviceDetailRepository) {
        this.hceControllerSupport = hceControllerSupport;
        this.cardDetailRepository = cardDetailRepository;
        this.userDetailRepository = userDetailRepository;
        this.deviceDetailRepository = deviceDetailRepository;
    }



    public Map notifyLCMEvent(NotificationServiceReq notificationServiceReq,String apiKey,String eventType) {
        //Verify vProvisionID
        GetTokenStatusRequest getTokenStatusRequest = null;
        LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent");
        CardDetails cardDetails = null;
        String vprovisionedTokenId = notificationServiceReq.getvProvisionedTokenID();
        final List<CardDetails> cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(vprovisionedTokenId);
        if(cardDetailsList==null || cardDetailsList.isEmpty()){

            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl -> notifyLCMEvent");
            return hceControllerSupport.formResponse(HCEMessageCodes.getCardDetailsNotExist());
        }

        if (eventType.equalsIgnoreCase("TOKEN_STATUS_UPDATED"))
        {
            //Call getTokenStatusAPI
            getTokenStatusRequest = new GetTokenStatusRequest();
            getTokenStatusRequest.setVprovisionedTokenID(vprovisionedTokenId);
            tokenLifeCycleManagementService.getTokenStatus(getTokenStatusRequest);
        }

        //Verify API key
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey"))){
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
                    LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > TOKEN_STATUS_UPDATED");
                    return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
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

            Gson gson = new Gson();
            String json = gson.toJson(rnsGenericRequest);

            LOGGER.debug("NotificationServiceVisaServiceImpl -> Remote notification Data Send to FCM Server = "+json);
            LOGGER.debug("NotificationServiceVisaServiceImpl -> Remote notification Data Send to FCM Server = ");
            Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            LOGGER.debug("NotificationServiceVisaServiceImpl->Remote notification response receved = "+rnsResp.toString());

            if (rnsResp.containsKey("errorCode")) {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > remoteNotification Sending Failed");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyLCMEvent" );
                return rnsResp;
            } else {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyLCMEvent - > remoteNotification Send");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyLCMEvent" );
                return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
            }
        }catch (Exception e)
        {
            LOGGER.error("Exception occured" +e);
            LOGGER.debug("Exception Occored in  NotificationServiceVisaServiceImpl->-> notifyLCMEvent",e);
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
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
        //String vprovisionedTokenId = notificationServiceReq.getvProvisionedTokenID();
        String vPanEnrollmentId = notificationServiceReq.getvPanEnrollmentID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        try{
            final List<CardDetails> cardDetailsList = cardDetailRepository.findByPanUniqueReference(vPanEnrollmentId);
            String rnsRegID = null;
            if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
                for(int i =0;i<cardDetailsList.size();i++){
                    final DeviceInfo deviceInfo = cardDetailsList.get(i).getDeviceInfo();
                    rnsRegID = deviceInfo.getRnsRegistrationId();
                    RnsGenericRequest rnsGenericRequest =  preparetNotificationRequest(vPanEnrollmentId,rnsRegID);
                    sendNotification(rnsGenericRequest);
                }
            }
            else{
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl -> notifyPanMetadataUpdate");
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }
            return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());

        }catch (Exception e){
            LOGGER.error("Exception occured",e);
            LOGGER.debug("Exception Occored in  NotificationServiceVisaServiceImpl->-> notifyPanMetadataUpdate",e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

    }

    public RnsGenericRequest preparetNotificationRequest(String panUniqueReference,String rnsId){
        LOGGER.debug("Inside preparetNotificationRequest");
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        rnsGenericRequest.setIdType(UniqueIdType.VTS);
        rnsGenericRequest.setRegistrationId(rnsId);
        lcmNotificationData.put("vPanEnrollmentId", panUniqueReference);
        lcmNotificationData.put(HCEConstants.OPERATION,HCEConstants.UPDATE_CARD_METADATA);
        rnsGenericRequest.setRnsData(lcmNotificationData);
        LOGGER.debug("Exit preparetNotificationRequest");
        return rnsGenericRequest;
    }

    public void sendNotification(RnsGenericRequest rnsGenericRequest) throws Exception{
        Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
        if (rnsResp.containsKey("errorCode")) {
            LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> sendNotification-> notifyPanMetadataUpdate - > remoteNotification Sending Failed");
            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->sendNotification-> notifyPanMetadataUpdate" );
        //    throw new HCEActionException(HCEConstants.SERVICE_FAILED);
        } else {
            LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> sendNotification->notifyPanMetadataUpdate - > remoteNotification Sent");
            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->sendNotification-> notifyPanMetadataUpdate" );
           // throw new HCEActionException(HCEMessageCodes.SUCCESS);
        }
    }

    public Map notifyTxnDetailsUpdate (NotificationServiceReq notificationServiceReq,String apiKey) {
        Map rnsResp = new HashMap();
        LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyTxnDetailsUpdate");
        if (apiKey.equalsIgnoreCase(env.getProperty("apiKey")))
        {
            //Return Invalid apiKey
        }
        //is.txnnotification.requires

        String vprovisionedTokenId = notificationServiceReq.getvProvisionedTokenID();
        HashMap<String, String> lcmNotificationData = new HashMap<>();
        try{
            if(env.getProperty("is.txnnotification.requires").equals("N"))
            {
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate->Not supported" );
                return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
            }
            RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
            rnsGenericRequest.setIdType(UniqueIdType.VTS);
            rnsGenericRequest.setRegistrationId(getRnsRegId(vprovisionedTokenId));
            lcmNotificationData.put("vprovisionedTokenId", vprovisionedTokenId);
            lcmNotificationData.put(HCEConstants.OPERATION,HCEConstants.UPDATE_TXN_HISTORY);
            rnsGenericRequest.setRnsData(lcmNotificationData);

            Gson gson = new Gson();
            String json = gson.toJson(rnsGenericRequest);

            LOGGER.debug("NotificationServiceVisaServiceImpl -> Remote notification Data Send to FCM Server = "+json);
            LOGGER.debug("NotificationServiceVisaServiceImpl -> Remote notification Data Send to FCM Server = ");
            if(env.getProperty("is.txnnotification.requires").equals("Y"))
            {
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate-> supported" );
                rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
            }
            //Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);

            LOGGER.debug("NotificationServiceVisaServiceImpl->Remote notification response receved = "+rnsResp.toString());

            LOGGER.debug("Response Recived from ");
            if (rnsResp.containsKey("errorCode")) {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyTxnDetailsUpdate - > remoteNotification Sending Failed");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate" );
                return rnsResp;
            } else {
                LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> notifyTxnDetailsUpdate - > remoteNotification Sent");
                LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate" );
                return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
            }

        }catch (Exception e){
            LOGGER.error("Exception occured" +e);
            LOGGER.debug("Exception Occored in  NotificationServiceVisaServiceImpl->-> notifyTxnDetailsUpdate",e);
            return  hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
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
