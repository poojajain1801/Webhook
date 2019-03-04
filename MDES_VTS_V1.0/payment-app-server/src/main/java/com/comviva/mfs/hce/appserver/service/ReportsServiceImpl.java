package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConsumerReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserDeviceCardReportReq;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.ReportsService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
            if (fromDate == null || toDate == null){
                LOGGER.info("Please Enter the fromDate and toDdate properly ");
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            if (userId == null){
                userId = "-";
            }
            if (userStatus == null){
                userStatus = "-";
            }
            userDetailList = userDetailRepository.findConsumerReport(fromDate,toDate,userId,userStatus);
            responseJson = responseJsonUser(userDetailList);
            response = JsonUtil.jsonToMap(responseJson);
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
            if (fromDate == null || toDate == null){
                LOGGER.info("Please Enter the fromDate and toDdate properly ");
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            userId = deviceReportReq.getUserId();
            if (userId == null){
                userId = "-";
            }
            userStatus = deviceReportReq.getUserStatus();
            if (userStatus == null){
                userStatus = "-";
            }
            deviceId = deviceReportReq.getDeviceId();
            if (deviceId == null){
                deviceId = "-";
            }
            deviceStatus = deviceReportReq.getDeviceStatus();
            if (deviceStatus == null){
                deviceStatus = "-";
            }
            deviceUserList = deviceDetailRepository.findDeviceReport(fromDate, toDate, userId, deviceId, userStatus, deviceStatus);
            size = deviceUserList.size();
            responseJson = responseJsonDevice(deviceUserList , size);
            LOGGER.info("List of queried deviceList ******** "+responseJson);
            response = JsonUtil.jsonToMap(responseJson);
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
    public Map<String, Object> userDeviceCardReport(UserDeviceCardReportReq userDeviceCardReportReqReq) {
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
            fromDate = userDeviceCardReportReqReq.getFromDate();
            toDate = userDeviceCardReportReqReq.getToDate();
            if (fromDate == null || toDate == null){
                LOGGER.info("Please Enter the fromDate and toDdate properly ");
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            userId = userDeviceCardReportReqReq.getUserId();
            if (userId == null){
                userId = "-";
            }
            userStatus = userDeviceCardReportReqReq.getUserStatus();
            if (userStatus == null){
                userStatus = "-";
            }
            deviceId = userDeviceCardReportReqReq.getDeviceId();
            if (deviceId == null){
                deviceId = "-";
            }
            deviceStatus = userDeviceCardReportReqReq.getDeviceStatus();
            if (deviceStatus == null){
                deviceStatus = "-";
            }
            cardList = cardDetailRepository.findUserDeviceCardReport(fromDate, toDate, userId, deviceId, userStatus, deviceStatus);
            size = cardList.size();
            responseJson = responseJsonCard(cardList , size);
            LOGGER.info("List of queried CardList ******** "+responseJson);
            response = JsonUtil.jsonToMap(responseJson);
        }catch (HCEActionException userDeviceCardReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->userDeviceCardReport", userDeviceCardReportException);
            throw userDeviceCardReportException;
        }catch (Exception userDeviceCardReportException){
            LOGGER.error("Exception occured in ReportsServiceImpl->userDeviceCardReport", userDeviceCardReportException);
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
            jArray.put(jsonObject);
        }
        responseJson.put("consumerReport", jArray);
        return responseJson;
    }

    private JSONObject responseJsonDevice (List<Object[]> deviceDetailList , int deviceCount ){
        JSONObject responseJson = new JSONObject();
        JSONArray jArray = new JSONArray();
        for(int i=0;i<deviceCount;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", (deviceDetailList.get(i))[0]);
            jsonObject.put("userRegistrationDate", deviceDetailList.get(i)[1]);
            jsonObject.put("Userstatus", deviceDetailList.get(i)[2]);
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

    private JSONObject responseJsonCard (List<Object[]> cardlList , int cardCount ){
        JSONObject responseJson = new JSONObject();
        JSONArray jArray = new JSONArray();
        for(int i=0;i<cardCount;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", (cardlList.get(i))[0]);
            jsonObject.put("userRegistrationDate", cardlList.get(i)[1]);
            jsonObject.put("Userstatus", cardlList.get(i)[2]);
            jsonObject.put("imei",cardlList.get(i)[3]);
            jsonObject.put("deviceName",cardlList.get(i)[4]);
            jsonObject.put("deviceModel",cardlList.get(i)[5]);
            jsonObject.put("OSVersion",cardlList.get(i)[6]);
            jsonObject.put("deviceStatus",cardlList.get(i)[7]);
            jsonObject.put("deviceRegistrationDate",cardlList.get(i)[8]);
            jsonObject.put("cardSuffix",cardlList.get(i)[9]);
            jsonObject.put("tokenSuffix",cardlList.get(i)[10]);
            jsonObject.put("cardAddedDate",cardlList.get(i)[11]);
            jsonObject.put("tokenStatus",cardlList.get(i)[12]);
            jsonObject.put("replenishOn",cardlList.get(i)[13]);
            jArray.put(jsonObject);
        }
        responseJson.put("deviceReport", jArray);
        return responseJson;
    }
}
