package com.comviva.mfs.hce.appserver.service;


import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.FcmAcknowledgementRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.HvtManagementRequest;
import com.comviva.mfs.hce.appserver.model.FcmSchedulerLog;
import com.comviva.mfs.hce.appserver.model.HvtManagement;
import com.comviva.mfs.hce.appserver.model.HvtManagementPK;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.FcmSchedulerLogRepository;
import com.comviva.mfs.hce.appserver.repository.HvtManagementRepository;
import com.comviva.mfs.hce.appserver.service.contract.HvtManagementService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HvtMangementServiceImpl implements HvtManagementService{

    public static final Logger LOGGER = LoggerFactory.getLogger(HvtMangementServiceImpl.class);

    @Autowired
    private FcmSchedulerLogRepository fcmSchedulerLogRepository;

    @Autowired
    private HvtManagementRepository hvtManagementRepository;

    @Autowired
    HCEControllerSupport hceControllerSupport;

    @Autowired
    DeviceDetailRepository deviceDetailRepository;

    @Autowired
    private RemoteNotificationService remoteNotificationService;

    @Autowired
    protected Environment env;



    //change it according to the Bank paymentAppId = SBICARDS
    private static String paymentAppId = HCEConstants.PAYMENT_APP_INSTANCE_ID;

    /**
     * SaveHvtLimit -> save or update hvt limit in hvt_management table
     * @param hvtManagementRequest request containing hvtLimit and isHvtSupported values
     * @return map
     * */
    @Override
    public Map<String, Object> saveHvtLimit(HvtManagementRequest hvtManagementRequest) {
        HvtManagement hvtManagement;
        HvtManagementPK hvtManagementPK;
        String isSupported;
        String hvtLimit;
        Map<String, Object> responseMap = new HashMap<>();
        HvtManagement storedRecord = null;
        HvtManagement storedNoRecord = null;

        try {
            isSupported = hvtManagementRequest.getIsHvtSupported();
            hvtLimit = hvtManagementRequest.getHvtLimit();
            hvtManagement = new HvtManagement();
            hvtManagementPK = new HvtManagementPK();

            HvtManagement storedHvt = hvtManagementRepository.findByPaymentAppId(paymentAppId);

            if("Y".equalsIgnoreCase(isSupported) && !hvtLimit.isEmpty()) {
                //if paymentAppId is already there, just update the limit
                if(storedHvt != null) {
                    hvtManagementRepository.update(storedHvt.getId().getRequestId(), paymentAppId, "Y",
                            hvtLimit);
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("message", "Successfully Updated");
                    sendRnsNotification();
                    return responseMap;
                } else  {
                    LOGGER.info("inserting when isHvtSupported Yes *******************************");
                    hvtManagementPK.setPaymentAppId(paymentAppId);
                    hvtManagement.setId(hvtManagementPK);
                    hvtManagement.setIsHvtSupported("Y");
                    hvtManagement.setHvtLimit(hvtLimit);
                    hvtManagement.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    hvtManagement.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    storedRecord = hvtManagementRepository.save(hvtManagement);
                }
            } else if("N".equalsIgnoreCase(isSupported)) {
                if(storedHvt != null) {
                    hvtManagement.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    hvtManagementRepository.update(storedHvt.getId().getRequestId(), paymentAppId, "N", "0");
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("message", "Successfully Updated");
                    sendRnsNotification();
                    return responseMap;
                } else {
                    LOGGER.info("inserting when isHvtSupported No ************************************");
                    hvtManagementPK.setPaymentAppId(paymentAppId);
                    hvtManagement.setId(hvtManagementPK);
                    hvtManagement.setIsHvtSupported("N");
                    hvtManagement.setHvtLimit("0");
                    hvtManagement.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    hvtManagement.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    storedNoRecord = hvtManagementRepository.save(hvtManagement);
                }
            }

            if((storedRecord != null || storedNoRecord != null)) {
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", "Successfully Updated");
                sendRnsNotification();
            } else {
                responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getServiceFailed());
                responseMap.put(HCEConstants.MESSAGE, "Updating Failed");
            }

        } catch(HCEActionException setHvtLimitException){
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> modifySchedulerException",setHvtLimitException);
            throw setHvtLimitException;
        }catch(Exception setHvtLimitException){
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> modifySchedulerDbException", setHvtLimitException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }


    /**
     * fetchRnsList
     * fetches all the distinct RNS_registration_Ids from db
     */
    public List<Object[]> fetchRnsList(int limit, int offset) {
        List<Object[]> rnsRegistrationIds = null;
        try {
            if(env.getProperty("spring.jpa.database").equals("POSTGRESQL")) {
                rnsRegistrationIds = deviceDetailRepository.fetchRnsRegistrationId(limit, offset);
            } else if(env.getProperty("spring.jpa.database").equals("ORACLE")) {
                rnsRegistrationIds = deviceDetailRepository.fetchRnsRegistrationIdOracle(limit, offset);
            }
        } catch(HCEActionException Exception) {
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> fetchRnsList",Exception);
            throw Exception;
        }
        return rnsRegistrationIds;
    }


    /**
     * sendRnsNotification
     * prepare Rns Object
     * CompletableFuture<Void>
     * return CompletableFuture.completedFuture(null);
     */
    @Async
    public void sendRnsNotification() {
        List<Object[]> rnsRegistrationIds;
        int limit = 10;
        try {
            int countOfRnsIds = deviceDetailRepository.countOfRnsIds();
//            String newHvtLimit = hvtManagementRepository.findByPaymentAppId(paymentAppId).getHvtLimit();
            int totalCount = (int)(countOfRnsIds / limit);
            for(int i=0;i<=totalCount+1;i++) {
                rnsRegistrationIds = fetchRnsList(limit, limit * i);
                if (rnsRegistrationIds != null && !rnsRegistrationIds.isEmpty()) {
                    for (int j = 0; j < rnsRegistrationIds.size(); j++) {
                        String rnsId = (String) rnsRegistrationIds.get(j)[0];
                        if (rnsId != null) {
                            RnsGenericRequest rnsGenericRequest = preparetNotificationRequest(rnsId);
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


    /**
     * saveToFcmSchedulerLog -> save status and rnsId TO FcmSchedulerLog table
     * @param rnsId rns_registration_id
     * */
    private void saveToFcmSchedulerLog(String rnsId) {
        FcmSchedulerLog fcmSchedulerLog = new FcmSchedulerLog();
        fcmSchedulerLog.setStatus("I");
        fcmSchedulerLog.setRnsRegistrationId(rnsId);
        fcmSchedulerLog.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
        fcmSchedulerLog.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
        fcmSchedulerLogRepository.save(fcmSchedulerLog);

    }


    /**
     * prepareNotificationRequest - preapares rns notification msg
     * @param rnsId rns_registratoin_id
     * @return RnsGenericRequest
     * */
    public RnsGenericRequest preparetNotificationRequest(String rnsId){
        LOGGER.debug("Inside preparetNotificationRequest");
        HashMap<String, String> rnsNotificationData = new HashMap<>();
        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        rnsGenericRequest.setIdType(UniqueIdType.ALL);
        rnsGenericRequest.setRegistrationId(rnsId);
        // find way to get newHvtLimit
        // operation - hvt limit updated
        rnsNotificationData.put(HCEConstants.OPERATION, "HVT LIMIT UPDATED");
        rnsGenericRequest.setRnsData(rnsNotificationData);
        LOGGER.debug("Exit preparetNotificationRequest");
        return rnsGenericRequest;
    }


    /**
     * sendNotification - sends rns notification msg
     * @param rnsGenericRequest rns request
     * */
    public void sendNotification(RnsGenericRequest rnsGenericRequest) throws Exception{
        Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
        if (rnsResp.containsKey("responseData")) {
            String response = rnsResp.get("responseData").toString();

            if(response.indexOf("message_id") > 0) {
                saveToFcmSchedulerLog(rnsGenericRequest.getRegistrationId());
            }

            LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> sendNotification-> notifyPanMetadataUpdate" +
                    " - > remoteNotification Sending Failed");
            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->sendNotification-> notifyPanMetadataUpdate" );
            //    throw new HCEActionException(HCEConstants.SERVICE_FAILED);
        } else {
            LOGGER.debug("Inside NotificationServiceVisaServiceImpl -> sendNotification->notifyPanMetadataUpdate " +
                    "- > remoteNotification Sent");
            LOGGER.debug("EXIT NotificationServiceVisaServiceImpl->sendNotification-> notifyPanMetadataUpdate" );
            // throw new HCEActionException(HCEMessageCodes.SUCCESS);
        }
    }


    /**
     * modifySchedulerDbOnAck - modifies the status in fcm_scheduler_log table against rnsId when ack is sent by sdk
     * @param fcmAcknowledgementRequestPojo contains rnsId
     * */
    @Override
    public void modifySchedulerDbOnAck(FcmAcknowledgementRequest fcmAcknowledgementRequestPojo) {
        String rns_registration_id;
         try {
             rns_registration_id = fcmAcknowledgementRequestPojo.getRnsRegistrationId();
             fcmSchedulerLogRepository.updateRecord(rns_registration_id, "S");
         } catch(HCEActionException modifySchedularDbException){
             LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> modifySchedulerException",
                     modifySchedularDbException);
             throw modifySchedularDbException;
         }catch(Exception modifySchedularDbException){
             LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> modifySchedulerDbException",
                     modifySchedularDbException);
             throw new HCEActionException(HCEMessageCodes.getServiceFailed());
         }
    }


    /**
     * fetchHvtLimit - returns current hvt limit in hvt_management table
     * @return map
     * */
    @Override
    public Map<String, Object> fetchHvtLimit() {
        Map<String, Object> responseMap = new HashMap<>();
        HvtManagement hvtManagement = hvtManagementRepository.findByPaymentAppId(paymentAppId);
        if(hvtManagement != null) {
            responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
            responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            responseMap.put("isHvtSupported", "Y".equalsIgnoreCase(hvtManagement.getIsHvtSupported()));
            responseMap.put("hvtLimit", Double.parseDouble(hvtManagement.getHvtLimit()));
        } else {
            responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getServiceFailed());
            responseMap.put(HCEConstants.MESSAGE, "No hvt limit found");
        }

        return responseMap;
    }



}
