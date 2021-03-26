package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.repository.FcmSchedulerLogRepository;
import com.comviva.mfs.hce.appserver.repository.HvtManagementRepository;
import com.comviva.mfs.hce.appserver.service.contract.HvtSchedulerService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "spring.scheduling", name = "enabled", havingValue="true")
public class HvtSchedulerServiceImpl implements HvtSchedulerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(HvtSchedulerServiceImpl.class);

    @Autowired
    private FcmSchedulerLogRepository fcmSchedulerLogRepository;

    @Autowired
    private HvtManagementRepository hvtManagementRepository;

    @Autowired
    HCEControllerSupport hceControllerSupport;

    @Autowired
    private RemoteNotificationService remoteNotificationService;

    @Autowired
    protected Environment env;


    //change it according to the Bank paymentAppId = SBICARDS
    private static String paymentAppId = HCEConstants.PAYMENT_APP_INSTANCE_ID;

    /**
     * retrySendingRnsNotification
     * prepare Rns Object
     * spring cron is different from unix cron
     * Second, minute, hour, day of month, month, day(s) of week
     * */
    @Override
    @Scheduled(cron = "0 35 10 * * *")
    public void retrySendingRnsNotification() {
        List<Object[]> rnsRegistrationIds;
        int limit = 10;
        try {
            int countOfRnsIds = fcmSchedulerLogRepository.countOfRnsIds();
            String newHvtLimit = hvtManagementRepository.findByPaymentAppId(paymentAppId).getHvtLimit();
            int totalCount = (int)(countOfRnsIds / limit);
            for(int i=0;i<=totalCount+1;i++) {
                rnsRegistrationIds = fetchRnsListFromFcmLog(limit, limit * i);
                if (rnsRegistrationIds != null && !rnsRegistrationIds.isEmpty()) {
                    for (int j = 0; j < rnsRegistrationIds.size(); j++) {
                        String rnsId = (String) rnsRegistrationIds.get(j)[0];
                        if (rnsId != null) {
                            RnsGenericRequest rnsGenericRequest = preparetNotificationRequest(rnsId, newHvtLimit);
                            // no need to save again??
                            sendNotification(rnsGenericRequest);
                        }
                    }
                }
                Thread.sleep(10000);
            }
        } catch(HCEActionException exception) {
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> sendRnsNotification",exception);
            throw exception;
        } catch (Exception exception) {
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> sendRnsNotification", exception);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
    }


    private List<Object[]> fetchRnsListFromFcmLog(int limit, int offset) {
        List<Object[]> rnsRegistrationIds = null;
        try {
            if(env.getProperty("spring.jpa.database").equals("POSTGRESQL")) {
                rnsRegistrationIds = fcmSchedulerLogRepository.fetchRnsIdWithStatusI(limit, offset);
            } else if(env.getProperty("spring.jpa.database").equals("ORACLE")) {
                rnsRegistrationIds = fcmSchedulerLogRepository.fetchRnsRegistrationIdOracle(limit, offset);
            }
        } catch(HCEActionException Exception) {
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> fetchRnsList",Exception);
            throw Exception;
        }
        return rnsRegistrationIds;
    }


    /**
     * prepareNotificationRequest - preapares rns notification msg
     * @param rnsId rns_registratoin_id
     * @param newHvtLimit newHvtLimit
     * @return RnsGenericRequest
     * */
    private RnsGenericRequest preparetNotificationRequest(String rnsId, String newHvtLimit){
        LOGGER.debug("Inside preparetNotificationRequest");
        HashMap<String, String> rnsNotificationData = new HashMap<>();
        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        rnsGenericRequest.setIdType(UniqueIdType.ALL);
        rnsGenericRequest.setRegistrationId(rnsId);
        // find way to get newHvtLimit
        rnsNotificationData.put(HCEConstants.OPERATION, newHvtLimit);
        rnsGenericRequest.setRnsData(rnsNotificationData);
        LOGGER.debug("Exit preparetNotificationRequest");
        return rnsGenericRequest;
    }


    /**
     * sendNotification - sends rns notification msg
     * @param rnsGenericRequest rns request
     * we can make use of hceManagementService.sendNotification instead of this redundant function
     * for the sake of testing scheduler, had to repeat the same function same with prepare notification request function
     * */
    private void sendNotification(RnsGenericRequest rnsGenericRequest) throws Exception{
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
}
