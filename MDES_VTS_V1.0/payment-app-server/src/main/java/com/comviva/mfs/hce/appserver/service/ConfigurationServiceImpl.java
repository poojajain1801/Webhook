package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ApproveHvtRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetHvtValueRequest;
import com.comviva.mfs.hce.appserver.model.ConfigurationManagement;
import com.comviva.mfs.hce.appserver.model.ConfigurationManagementM;
import com.comviva.mfs.hce.appserver.repository.ConfigurationManagementMRepository;
import com.comviva.mfs.hce.appserver.repository.ConfigurationManagementRepository;
import com.comviva.mfs.hce.appserver.service.contract.ConfigurationService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    private ConfigurationManagementMRepository configurationManagementMRepository;
    @Autowired
    private ConfigurationManagementRepository configurationManagementRepository;

    @Override
    public Map<String, Object> setHvtValue(SetHvtValueRequest setHvtValueRequest) {
        String userName = null;
        String requestId = null;
        String hvtLimit = null;
        String isHvtSupported = null;
        Map responseMap = new HashMap();
        ConfigurationManagementM configManagementM = null;
        ConfigurationManagementM configurationManagementM = new ConfigurationManagementM();
        try {
            userName = setHvtValueRequest.getUserName();
            requestId = setHvtValueRequest.getRequestId();
            hvtLimit = setHvtValueRequest.getHvtLimit();
            isHvtSupported = setHvtValueRequest.getIsHvtSupported();
            if (userName == null || userName.isEmpty() || requestId == null || requestId.isEmpty() || isHvtSupported == null|| isHvtSupported.isEmpty()){
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }else if((hvtLimit == null || hvtLimit.isEmpty()) && isHvtSupported.equals("Y")){
                throw new HCEActionException((HCEMessageCodes.getInsufficientData()));
            }
            configManagementM = configurationManagementMRepository.findByRequestId(requestId);
            if (configManagementM != null){
                throw new HCEActionException(HCEMessageCodes.getDuplicateRequest());
            }
            configurationManagementM.setUserId(userName);
            configurationManagementM.setRequestId(requestId);
            configurationManagementM.setHvtLimit(hvtLimit);
            configurationManagementM.setIsHvtSupported(isHvtSupported);
            configurationManagementM.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
            configurationManagementM.setStatus(HCEConstants.INITIATE);
            configurationManagementMRepository.save(configurationManagementM);
            responseMap.put(HCEConstants.RESPONSE_CODE,HCEMessageCodes.getSUCCESS());

        }catch (HCEActionException setHvtValueHCEactionException) {
            LOGGER.error("Exception occured in ConfigurationServiceImpl ->setHvtValue", setHvtValueHCEactionException);
            throw setHvtValueHCEactionException;

        } catch (Exception setHvtValueException) {
            LOGGER.error("Exception occured in ConfigurationServiceImpl ->setHvtValue", setHvtValueException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> getPendingRequests() {
        List<ConfigurationManagementM> configurationManagementMList = null;
        Map responseMap = new HashMap();
        org.json.JSONArray jsonArray = new org.json.JSONArray();
        JSONObject responseJson = new JSONObject();
        try {
            configurationManagementMList = configurationManagementMRepository.findByStatus(HCEConstants.INITIATE);
            int count = configurationManagementMList.size();
            for (int i = 0 ; i<count ; i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("requestId",configurationManagementMList.get(i).getRequestId());
                jsonObject.put("userName",configurationManagementMList.get(i).getUserId());
                jsonObject.put("hvtLimit",configurationManagementMList.get(i).getHvtLimit());
                jsonObject.put("isHvtSupported",configurationManagementMList.get(i).getIsHvtSupported());
                jsonObject.put("createdOn",configurationManagementMList.get(i).getCreatedOn());
                jsonArray.put(jsonObject);
            }
            responseJson.put("pendingRequestList",jsonArray);
            responseMap = JsonUtil.jsonToMap(responseJson);
            responseMap.put(HCEConstants.RESPONSE_CODE,HCEMessageCodes.getSUCCESS());

        }catch (HCEActionException getPendingRequestsHCEactionException) {
            LOGGER.error("Exception occured in ConfigurationServiceImpl ->getPendingRequests", getPendingRequestsHCEactionException);
            throw getPendingRequestsHCEactionException;

        } catch (Exception getPendingRequestsException) {
            LOGGER.error("Exception occured in ConfigurationServiceImpl ->getPendingRequests", getPendingRequestsException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> approveHvt(ApproveHvtRequest approveHvtRequest) {
        String userName = null;
        String requestId = null;
        String hvtLimit = null;
        String decision = null;
        String isHvtSupported = null;
        Map responseMap = new HashMap();
        ConfigurationManagementM configurationManagementM = new ConfigurationManagementM();
        ConfigurationManagement configurationManagement = new ConfigurationManagement();
        try {
            userName = approveHvtRequest.getUserName();
            requestId = approveHvtRequest.getRequestId();
            decision = approveHvtRequest.getDecision();
            configurationManagementM = configurationManagementMRepository.findByRequestId(requestId);
            if (configurationManagementM == null){
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            hvtLimit = configurationManagementM.getHvtLimit();
            isHvtSupported = configurationManagementM.getIsHvtSupported();
            configurationManagementM.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
            configurationManagementMRepository.save(configurationManagementM);

            if (decision.equals(HCEConstants.APPROVED) ){
                configurationManagementRepository.delete(configurationManagementRepository.findAll());
                configurationManagement.setRequestId(requestId);
                configurationManagement.setUserId(userName);
                configurationManagement.setHvtLimit(hvtLimit);
                configurationManagement.setIsHvtSupported(isHvtSupported);
                configurationManagement.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
                configurationManagementRepository.save(configurationManagement);
                configurationManagementM.setStatus("A");
                configurationManagementMRepository.save(configurationManagementM);
                responseMap.put(HCEConstants.RESPONSE_CODE,HCEMessageCodes.getSUCCESS());
            }else if (decision.equals(HCEConstants.DECLINED)) {
                configurationManagementM.setStatus("D");
                configurationManagementMRepository.save(configurationManagementM);
                responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getSUCCESS());
            }else
                throw new HCEActionException(HCEMessageCodes.getInvalidOperation());

        }catch (HCEActionException approveHvtHCEactionException) {
            LOGGER.error("Exception occured in ConfigurationServiceImpl ->approveHvt", approveHvtHCEactionException);
            throw approveHvtHCEactionException;

        } catch (Exception approveHvtException) {
            LOGGER.error("Exception occured in ConfigurationServiceImpl ->approveHvt", approveHvtException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }
}
