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

    // read from property file
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

        try {
            isSupported = hvtManagementRequest.getIsHvtSupported();
            hvtLimit = hvtManagementRequest.getHvtLimit();
            hvtManagement = new HvtManagement();
            hvtManagementPK = new HvtManagementPK();

            HvtManagement storedHvt = hvtManagementRepository.findByPaymentAppId(paymentAppId);

            if(storedHvt == null) {
               return insertNewHvtValue(isSupported, hvtLimit,
                       hvtManagement, hvtManagementPK,
                       hvtManagementRequest);
            } else if(!storedHvt.getHvtLimit().equals(hvtLimit)) {
                if ("Y".equalsIgnoreCase(isSupported) && !hvtLimit.isEmpty()) {
                    //if paymentAppId is already there, just update the limit
//                    hvtManagementRepository.update(storedHvt.getId().getRequestId(), paymentAppId,
//                            "Y",
//                            hvtLimit);
                    storedHvt.setHvtLimit(hvtLimit);
                    storedHvt.setIsHvtSupported("Y");
                } else if ("N".equalsIgnoreCase(isSupported)) {
//                    hvtManagement.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
//                    hvtManagementRepository.update(storedHvt.getId().getRequestId(), paymentAppId,
//                            "N", "0");
                    storedHvt.setHvtLimit("0");
                    storedHvt.setIsHvtSupported("N");
                }
                storedHvt.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
            }
            return upsertRecord(storedHvt, hvtManagementRequest);
        } catch(HCEActionException setHvtLimitException){
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> modifySchedulerException",setHvtLimitException);
            throw setHvtLimitException;
        }catch(Exception setHvtLimitException){
            LOGGER.error("Exception occurred in HvtMgmtServiceImpl -> modifySchedulerDbException", setHvtLimitException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
    }

    private Map<String, Object> insertNewHvtValue(String isSupported, String hvtLimit, HvtManagement hvtManagement,
                                   HvtManagementPK hvtManagementPK, HvtManagementRequest hvtManagementRequest) {
        hvtManagementPK.setPaymentAppId(paymentAppId);
        hvtManagement.setId(hvtManagementPK);
        if("Y".equalsIgnoreCase(isSupported) && !hvtLimit.isEmpty()) {
            LOGGER.info("inserting when isHvtSupported Yes *******************************");
            hvtManagement.setIsHvtSupported("Y");
            hvtManagement.setHvtLimit(hvtLimit);
        } else if("N".equalsIgnoreCase(isSupported)) {
            LOGGER.info("inserting when isHvtSupported No ************************************");
            hvtManagement.setIsHvtSupported("N");
            hvtManagement.setHvtLimit("0");
        }
        hvtManagement.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
        hvtManagement.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
        return upsertRecord(hvtManagement, hvtManagementRequest);
    }

    private Map<String, Object> upsertRecord(HvtManagement storedHvt, HvtManagementRequest hvtManagementRequest) {
        Map<String, Object> responseMap = new HashMap<>();
        if(null != hvtManagementRequest.getThemeColor() && !hvtManagementRequest.getThemeColor().isEmpty()) {
            storedHvt.setColorValue(hvtManagementRequest.getThemeColor());
        }

        String transactionTime = env.getProperty("transactionTime");
        if(null != hvtManagementRequest.getTransactionTime() && !hvtManagementRequest.getTransactionTime().isEmpty()) {
            if(Integer.parseInt(hvtManagementRequest.getTransactionTime()) > Integer.parseInt(transactionTime)) {
                responseMap.put("responseCode", HCEMessageCodes.getServiceFailed());
                responseMap.put("message", "transaction time should not exceed" + transactionTime);
                return responseMap;
            }
            storedHvt.setTransactionTime(hvtManagementRequest.getTransactionTime());
        }
        hvtManagementRepository.save(storedHvt);
//        sendRnsNotification();
        responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
        responseMap.put("message", "Successfully Updated");
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
            LOGGER.info("no of rnsIds "+ countOfRnsIds);
//            String newHvtLimit = hvtManagementRepository.findByPaymentAppId(paymentAppId).getHvtLimit();
            int totalCount = (countOfRnsIds / limit);
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
                Thread.sleep(1000);
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

    /**
     * fetchHvtLimit - returns current hvt limit in hvt_management table
     * @return map
     * */
    @Override
    public Map<String, Object> fetchConfiguration() {
        Map<String, Object> responseMap = new HashMap<>();
        HvtManagement hvtManagement = hvtManagementRepository.findByPaymentAppId(paymentAppId);
        if(hvtManagement != null) {
            responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
            responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            responseMap.put("isHvtSupported", "Y".equalsIgnoreCase(hvtManagement.getIsHvtSupported()));
            responseMap.put("hvtLimit", Double.parseDouble(hvtManagement.getHvtLimit()));
            responseMap.put("colorValue", hvtManagement.getColorValue());
            responseMap.put("transactionDuration", hvtManagement.getTransactionTime());
        } else {
            responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getServiceFailed());
            responseMap.put(HCEConstants.MESSAGE, "No hvt limit found");
        }

        return responseMap;
    }



}
