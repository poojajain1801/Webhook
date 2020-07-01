package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.AuditLogsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConsumerReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserDeviceCardReportReq;
import com.comviva.mfs.hce.appserver.model.AuditTrail;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.AuditTrailRepository;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.ReportsService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by rishikesh.kumar on 09-01-2019.
 */
@Service
public class ReportsServiceImpl implements ReportsService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);
    @Autowired
    private UserDetailRepository userDetailRepository;
    @Autowired
    private DeviceDetailRepository deviceDetailRepository;
    @Autowired
    private CardDetailRepository cardDetailRepository;
    @Autowired
    private HCEControllerSupport hceControllerSupport;
    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Override
    public Map<String, Object> consumerReport(ConsumerReportReq consumerReportReq) {
        Date fromDate = consumerReportReq.getFromDate();
        Date toDate = consumerReportReq.getToDate();
        String userId = consumerReportReq.getUserId();
        String userStatus = consumerReportReq.getStatus();
        List<UserDetail> userDetailList = null;
        Map <String, Object> response;
        JSONObject responseJson = new JSONObject();
        try {
            if (userId == null || userId.isEmpty()){
                userId = "-";
            }
            if (userStatus == null || userStatus.isEmpty()){
                userStatus = "-";
            }
            if (fromDate == null || toDate == null){
                userDetailList = userDetailRepository.findConsumerReportNoDate(userId);
            }else {
                Calendar c = Calendar.getInstance();
                c.setTime(toDate);
                c.add(Calendar.DATE, 1);
                toDate = c.getTime();
                userDetailList = userDetailRepository.findConsumerReport(fromDate, toDate, userId, userStatus);
            }
            responseJson = responseJsonUser(userDetailList);
            response = JsonUtil.jsonToMap(responseJson);
            response.put("responseCode", HCEMessageCodes.getSUCCESS());
            response.put("message", "Transaction Success");
        }catch (HCEActionException consumerReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->consumerReport", consumerReportException);
            throw consumerReportException;
        }catch (Exception consumerReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->consumerReport", consumerReportException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
    }

    @Override
    public Map<String, Object> deviceReport(DeviceReportReq deviceReportReq) {
        Date fromDate;
        Date toDate;
        String userId;
        String userStatus;
        String deviceId;
        String deviceStatus;
        int size = 0;
        Map <String, Object> response = null;
        JSONObject responseJson  = new JSONObject();
        List<Object[]> deviceUserList = null;
        try {
            fromDate = deviceReportReq.getFromDate();
            toDate = deviceReportReq.getToDate();
            userId = deviceReportReq.getUserId();
            if (userId == null || userId.isEmpty()){
                userId = "-";
            }
            userStatus = deviceReportReq.getUserStatus();
            if (userStatus == null || userStatus.isEmpty()){
                userStatus = "-";
            }
            deviceId = deviceReportReq.getDeviceId();
            if (deviceId == null || deviceId.isEmpty()){
                deviceId = "-";
            }
            deviceStatus = deviceReportReq.getDeviceStatus();
            if (deviceStatus == null || deviceStatus.isEmpty()){
                deviceStatus = "-";
            }
            if (fromDate == null || toDate == null){
                deviceUserList = deviceDetailRepository.findDeviceReportNoDate(userId, deviceId, userStatus, deviceStatus);
            }else {
                Calendar c = Calendar.getInstance();
                c.setTime(toDate);
                c.add(Calendar.DATE, 1);
                toDate = c.getTime();
                deviceUserList = deviceDetailRepository.findDeviceReport(fromDate, toDate, userId, deviceId, userStatus, deviceStatus);
            }
            size = deviceUserList.size();
            responseJson = responseJsonDevice(deviceUserList , size);
            LOGGER.info("List of queried deviceList ******** "+responseJson);
            response = JsonUtil.jsonToMap(responseJson);
            response.put("responseCode", HCEMessageCodes.getSUCCESS());
            response.put("message", "Transaction Success");
        }catch (HCEActionException deviceReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->deviceReport", deviceReportException);
            throw deviceReportException;
        }catch (Exception deviceReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->deviceReport", deviceReportException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
    }

    @Override
    public Map<String, Object> userDeviceCardReport(UserDeviceCardReportReq userDeviceCardReportReq) {
        Date fromDate;
        Date toDate;
        String userId;
        String userStatus;
        String deviceId;
        String deviceStatus;
        int size = 0;
        Map <String, Object> response = null;
        JSONObject responseJson  = new JSONObject();
        List<Object[]> cardList = null;
        try {
            fromDate = userDeviceCardReportReq.getFromDate();
            toDate = userDeviceCardReportReq.getToDate();
            userId = userDeviceCardReportReq.getUserId();
            if (userId == null || userId.isEmpty()){
                userId = "-";
            }
            userStatus = userDeviceCardReportReq.getUserStatus();
            if (userStatus == null || userStatus.isEmpty()){
                userStatus = "-";
            }
            deviceId = userDeviceCardReportReq.getDeviceId();
            if (deviceId == null || deviceId.isEmpty()){
                deviceId = "-";
            }
            deviceStatus = userDeviceCardReportReq.getDeviceStatus();
            if (deviceStatus == null || deviceStatus.isEmpty()){
                deviceStatus = "-";
            }
            if (fromDate == null || toDate == null){
                cardList = cardDetailRepository.findUserDeviceCardReportWithoutDate(userId, deviceId, userStatus, deviceStatus);
            }else {
                Calendar c = Calendar.getInstance();
                c.setTime(toDate);
                c.add(Calendar.DATE, 1);
                toDate = c.getTime();
                cardList = cardDetailRepository.findUserDeviceCardReport(fromDate, toDate, userId, deviceId, userStatus, deviceStatus);
            }
            size = cardList.size();
            responseJson = responseJsonCard(cardList , size);
            LOGGER.info("List of queried CardList ******** "+responseJson);
            response = JsonUtil.jsonToMap(responseJson);
            response.put("responseCode", HCEMessageCodes.getSUCCESS());
            response.put("message", "Transaction Success");
        }catch (HCEActionException userDeviceCardReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->userDeviceCardReport", userDeviceCardReportException);
            throw userDeviceCardReportException;
        }catch (Exception userDeviceCardReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->userDeviceCardReport", userDeviceCardReportException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
    }

    @Override
    public Map<String, Object> auditLogs(AuditLogsRequest auditLogsRequest) {
        Date fromDate = auditLogsRequest.getFromDate();
        Date toDate = auditLogsRequest.getToDate();
        Map <String, Object> response = null;
        JSONObject responseJson  = new JSONObject();
        int size = 0;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(toDate);
            c.add(Calendar.DATE, 1);
            toDate = c.getTime();
            List<AuditTrail> auditTrailList = auditTrailRepository.findAuditTrailReport(fromDate, toDate);
            responseJson = responseJsonAudit(auditTrailList);
            response = JsonUtil.jsonToMap(responseJson);
            response.put("responseCode", HCEMessageCodes.getSUCCESS());
            response.put("message", "Transaction Success");
        }catch (HCEActionException auditLogsReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->auditLogs", auditLogsReportException);
            throw auditLogsReportException;
        }catch (Exception auditLogsException){
            LOGGER.error("Exception occured in ReportsServiceImpl->auditLogs", auditLogsException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
    }

    private JSONObject responseJsonUser (List<UserDetail> userDetailList){
        int userCount = userDetailList.size();
        JSONObject responseJson = new JSONObject();
        JSONArray jArray = new JSONArray();
        for(int i=0;i<userCount;i++) {
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            jsonObject.put("userId", userDetailList.get(i).getUserId());
            jsonObject.put("status", userDetailList.get(i).getStatus());
            jsonObject.put("createdOn", userDetailList.get(i).getCreatedOn());
            jsonObject.put("modifiedOn", userDetailList.get(i).getModifiedOn());
            jArray.put(jsonObject);
        }
        responseJson.put("consumerReport", jArray);
        return responseJson;
    }

    private JSONObject responseJsonAudit (List<AuditTrail> auditTrailList){
        int userCount = auditTrailList.size();
        JSONObject responseJson = new JSONObject();
        JSONArray jArray = new JSONArray();
        for(int i=0;i<userCount;i++) {
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            jsonObject.put("requestId", auditTrailList.get(i).getRequestId());
            jsonObject.put("createdBy", auditTrailList.get(i).getCreatedBy());
            jsonObject.put("clientDeviceId", auditTrailList.get(i).getClientDeviceId());
            jsonObject.put("createdOn", auditTrailList.get(i).getCreatedOn());
            jsonObject.put("serviceType", auditTrailList.get(i).getServiceType());
            jsonObject.put("responseCode", auditTrailList.get(i).getResponseCode());
            jArray.put(jsonObject);
        }
        responseJson.put("auditLogsReport", jArray);
        return responseJson;
    }

    private JSONObject responseJsonDevice (List<Object[]> deviceDetailList , int deviceCount ){
        JSONObject responseJson = new JSONObject();
        JSONArray jArray = new JSONArray();
        for(int i=0;i<deviceCount;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", (deviceDetailList.get(i))[0]);
            jsonObject.put("userRegistrationDate", deviceDetailList.get(i)[1]);
            jsonObject.put("userStatus", deviceDetailList.get(i)[2]);
            jsonObject.put("imei",deviceDetailList.get(i)[3]);
            jsonObject.put("deviceName",deviceDetailList.get(i)[4]);
            jsonObject.put("deviceModel",deviceDetailList.get(i)[5]);
            jsonObject.put("OSVersion",deviceDetailList.get(i)[6]);
            jsonObject.put("deviceStatus",deviceDetailList.get(i)[7]);
            jsonObject.put("deviceRegistrationDate",deviceDetailList.get(i)[8]);
            jArray.put(jsonObject);
        }
        responseJson.put("deviceReport", jArray);
        return responseJson;
    }

    private JSONObject responseJsonCard (List<Object[]> cardlList , int cardCount ) throws ParseException {
        JSONObject responseJson = new JSONObject();
        JSONArray jArray = new JSONArray();
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        for(int i=0;i<cardCount;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", (cardlList.get(i))[0]);
            jsonObject.put("userRegistrationDate", cardlList.get(i)[1]);
            jsonObject.put("userStatus", cardlList.get(i)[2]);
            jsonObject.put("imei",cardlList.get(i)[3]);
            jsonObject.put("deviceName",cardlList.get(i)[4]);
            jsonObject.put("deviceModel",cardlList.get(i)[5]);
            jsonObject.put("OSVersion",cardlList.get(i)[6]);
            jsonObject.put("deviceStatus",cardlList.get(i)[7]);
            jsonObject.put("deviceRegistrationDate",cardlList.get(i)[8]);
            jsonObject.put("cardSuffix",cardlList.get(i)[9]);
            jsonObject.put("tokenSuffix",cardlList.get(i)[10]);
            jsonObject.put("cardAddedDate",cardlList.get(i)[11]);
            Date cardAddedDate = (Date) cardlList.get(i)[11];
            Date todaysDate = HCEUtil.convertDateToTimestamp(new Date());
            long difference = todaysDate.getTime() - cardAddedDate.getTime();
            int daysBetween = (int) (difference / (1000*60*60*24));
            jsonObject.put("tokenStatus",cardlList.get(i)[12]);
            jsonObject.put("replenishOn",cardlList.get(i)[13]);
            jsonObject.put("paymentAppInstanceId",cardlList.get(i)[14]);
            jsonObject.put("schemeType",cardlList.get(i)[15]);
            jsonObject.put("tokenUniqueReference",cardlList.get(i)[16]);
            jsonObject.put("visaProvisionTokenId",cardlList.get(i)[17]);
            jsonObject.put("addedSince",daysBetween+" Days");
            jArray.put(jsonObject);
        }
        responseJson.put("userDeviceCardMappingReport", jArray);
        return responseJson;
    }
}
