package com.comviva.mfs.hce.appserver.service;


import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoFlowRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoTokenRequest;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.RePersoService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import java.util.HashMap;
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

    @Autowired
    private CardDetailRepository cardDetailRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsServiceImpl.class);

    @Override
    public Map<String, Object> rePersoTokenDataRequest(RePersoTokenRequest rePersoTokenRequest) {
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String resourcePath,strResponse, url;
        Map<String, Object> responseMap = new HashMap<>();
        ResponseEntity responseEntity = null;
        JSONObject jsonResponse = new JSONObject();

        try {

            String vProvisionedTokenID = rePersoTokenRequest.getVProvisionedTokenID();

            JSONObject requestJson = new JSONObject();

            requestJson.put("clientDeviceID", rePersoTokenRequest.getClientDeviceID());
            requestJson.put("clientAppID", env.getProperty("clientAppID"));
            requestJson.put("clientWalletAccountID", rePersoTokenRequest.getClientWalletAccountId());
            requestJson.put("vNotificationID", rePersoTokenRequest.getvNotificationID());
            requestJson.put("fullReperso", rePersoTokenRequest.isFullReperso());
//            requestJson.put("vProvisionedTokenID", vProvisionedTokenID);


            resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/reperso";


            url = "https://cert.api.visa.com/vts/provisionedTokens/"+vProvisionedTokenID+"/reperso?apiKey="+ env.getProperty("apiKey");

            LOGGER.debug("\n ***************************************** \n before hitting reperso api url RepersoServiceImpl -> repersoTokenRequest "+url + " "+requestJson.toString()+" ");
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestJson.toString(), resourcePath, "POST");
            LOGGER.debug("\n ***************************************** \nafter hitting reperso api url RepersoServiceImpl -> repersoTokenRequest");

            if (responseEntity.hasBody()) {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }

            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                cardDetailRepository.updateRepersoStatus(vProvisionedTokenID, "S");
            } else {
                responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
                cardDetailRepository.updateRepersoStatus(vProvisionedTokenID, "N");
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

    public Map<String,Object> rePersoFlow(RePersoFlowRequest rePersoFlowRequest) {
        Map<String, Object> responseMap = new HashMap<>();
        ResponseEntity responseEntity;
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String resourcePath,strResponse, url;
        JSONObject jsonResponse = new JSONObject();

        try {
            JSONObject requestJson = new JSONObject();

            String vProvisionedTokenID = rePersoFlowRequest.getvProvisionedTokenID();

            resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/repersoFlow";

            url = "https://cert.api.visa.com/vts/provisionedTokens/"+vProvisionedTokenID+"/repersoFlow?apiKey="+ env.getProperty("apiKey");

            LOGGER.debug("\n *************************************** \n after hitting repersoFlow api url RepersoServiceImpl -> repersoFlow");
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, new JSONObject().toString(), resourcePath, "POST");
            LOGGER.debug("\n *************************************** \n after hitting repersoFlow api url RepersoServiceImpl -> repersoFlow");

            if (responseEntity.hasBody()) {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }



            LOGGER.debug("\n ***************************************** \n response from repersoFlow " + jsonResponse.toString());
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                cardDetailRepository.updateRepersoStatus(vProvisionedTokenID, "I");
            } else {
                responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
            }

            LOGGER.debug("\n exiting reperso flow \n ****************************************************");

        } catch(HCEActionException rePersoTokenException){
            LOGGER.error("Exception occured in RePersoServiceImpl -> rePersoTokenException",rePersoTokenException);
            throw rePersoTokenException;
        }catch(Exception rePersoFlowException){
            LOGGER.error("Exception occured in RePersoServiceImpl -> rePersoTokenException", rePersoFlowException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;


    }


}
