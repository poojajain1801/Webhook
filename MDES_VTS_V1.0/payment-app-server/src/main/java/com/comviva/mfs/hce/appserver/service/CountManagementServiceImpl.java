package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.CountManagementService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@Service
public class CountManagementServiceImpl implements CountManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountManagementServiceImpl.class);
    @Autowired
    private UserDetailRepository userDetailRepository;
    @Autowired
    private DeviceDetailRepository deviceDetailRepository;
    @Autowired
    private CardDetailRepository cardDetailRepository;


    @Override
    public Map<String, Object> getUserCount() {
        List<UserDetail> userDetailList = null;
        int userCount = 0;
        Map responsemap = new HashMap();
        try{
            LOGGER.info("Entering inside getUserCount *******  ");
            userDetailList = userDetailRepository.findAll();
            userCount = userDetailList.size();
            responsemap.put("userCount",userCount);
        }catch(HCEActionException getUserCountHCEactionException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getUserCount", getUserCountHCEactionException);
            throw getUserCountHCEactionException;

        }catch(Exception getUserCountException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getUserCount", getUserCountException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responsemap;
    }

    @Override
    public Map<String, Object> getDeviceCount() {
        List<DeviceInfo> deviceInfoList = null;
        Map responseMap = new HashMap();
        int deviceCount = 0;
        try {
            deviceInfoList = deviceDetailRepository.findAll();
            deviceCount = deviceInfoList.size();
            responseMap.put("deviceCount",deviceCount);
        }catch(HCEActionException getDeviceCountHCEactionException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getDeviceCount", getDeviceCountHCEactionException);
            throw getDeviceCountHCEactionException;

        }catch(Exception getDeviceCountCountException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getDeviceCount", getDeviceCountCountException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> getTokenCount() {
        List<CardDetails> cardDetailsList = null;
        Map responseMap = new HashMap();
        int tokenCount = 0;
        try {
            cardDetailsList = cardDetailRepository.findAll();
            tokenCount = cardDetailsList.size();
            responseMap.put("tokenCount",tokenCount);
        }catch(HCEActionException getTokenCountHCEactionException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getTokenCount", getTokenCountHCEactionException);
            throw getTokenCountHCEactionException;

        }catch(Exception getTokenCountException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getTokenCount", getTokenCountException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> getActiveTokenCount() {
        List<CardDetails> cardDetailsList = null;
        Map responseMap = new HashMap();
        int tokenCount = 0;
        try {
            cardDetailsList = cardDetailRepository.findByStatus(HCEConstants.ACTIVE);
            tokenCount = cardDetailsList.size();
            responseMap.put("activeTokenCount",tokenCount);
        }catch(HCEActionException getActiveTokenCountHCEactionException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getActiveTokenCount", getActiveTokenCountHCEactionException);
            throw getActiveTokenCountHCEactionException;

        }catch(Exception getActiveTokenCountException){
            LOGGER.error("Exception occured in CountManagementServiceImpl->getActiveTokenCount", getActiveTokenCountException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

}
