package com.comviva.mfs.hce.appserver.service;


import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoTokenRequest;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.RePersoService;

import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RePersoServiceImpl implements RePersoService {

    @Autowired
    private Environment env;

    @Autowired
    private HitMasterCardService hitMasterCardService;

    @Autowired
    private HCEControllerSupport hceControllerSupport;

    @Autowired
    private DeviceDetailRepository deviceDetailRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);

    @Override
    public Map<String, Object> rePersoTokenDataRequest(RePersoTokenRequest rePersoTokenRequest) {
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String resourcePath,strResponse, url;
        Map<String, Object> responseMap = null;
        ResponseEntity responseEntity = null;
        JSONObject jsonResponse = new JSONObject();

        try {
            JSONObject requestJson = new JSONObject();
            String clientDeviceID = rePersoTokenRequest.getClientDeviceID();
            List<DeviceInfo> deviceInfos = deviceDetailRepository.find(clientDeviceID);
            String clientWalletAccountID = "";
            if(null != deviceInfos) {
                for(DeviceInfo deviceInfo:deviceInfos) {
                    if(clientDeviceID.equalsIgnoreCase(deviceInfo.getClientDeviceId())) {
                        clientWalletAccountID = deviceInfo.getUserDetail().getClientWalletAccountId();
                    }
                }
            }

            requestJson.put("clientDeviceID", clientDeviceID);
            rePersoTokenRequest.setClientWalletAccountID(clientWalletAccountID);
            requestJson.put("clientAppID", env.getProperty("clientAppID"));
            requestJson.put("clientWalletAccountID", rePersoTokenRequest.getClientWalletAccountID());

            String vProvisionedTokenID = rePersoTokenRequest.getVProvisionedTokenID();

            resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/reperso";


            url = "https://digital.visa.com/vts/provisionedTokens/"+vProvisionedTokenID+"/reperso?apiKey="+ env.getProperty("apiKey");


            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestJson.toString(), resourcePath, "PUT");

            if (responseEntity.hasBody()) {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }

            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            } else {
//                if(responseEntity.getStatusCode().value() == 409) {
                    responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                    responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
//                }
            }

        } catch(HCEActionException rePersoTokenException){
            LOGGER.error("Exception occured in RePersoServiceImpl -> rePersoTokenException",rePersoTokenException);
            throw rePersoTokenException;
        }catch(Exception rePersoTokenException){
            LOGGER.error("Exception occured in RePersoServiceImpl -> rePersoTokenException", rePersoTokenException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }
}
