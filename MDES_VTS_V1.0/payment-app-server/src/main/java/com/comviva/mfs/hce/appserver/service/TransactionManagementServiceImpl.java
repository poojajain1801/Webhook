package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.ServiceData;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.ServiceDataRepository;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerImplUtils;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerUtils;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.visa.dmpd.token.JWTUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * Created by tanmay.patel on 5/10/2017.
 */
@Service
public class TransactionManagementServiceImpl implements TransactionManagementService {
    @Autowired
    private Environment env;
    private final HCEControllerSupport hceControllerSupport;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementServiceImpl.class);

    public TransactionManagementServiceImpl(HCEControllerSupport hceControllerSupport) {
        this.hceControllerSupport = hceControllerSupport;
    }
    public Map<String, Object> getTransactionHistory(GetTransactionHistoryRequest getTransactionHistoryRequest) {
        LOGGER.debug("Enter TransactionManagementServiceImpl->getTransactionHistory");
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        Map response = new LinkedHashMap();
        JSONObject jsonResponse = null;
        String request = "";
        ResponseEntity responseEntity = null;
        //JSONObject requestJson = null;
        String url = "";
        String resourcePath = "vts/paymentTxns";
        //https://sandbox.digital.visa.com/vts/paymentTxns?apiKey=key&vProvisionedTokenID=token_ID
        url = env.getProperty("visaBaseUrlSandbox") + "/vts/paymentTxns" + "?apiKey=" + env.getProperty("apiKey") + "&vProvisionedTokenID=" + getTransactionHistoryRequest.getVprovisionedTokenID();

        try {
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");

            if (responseEntity.hasBody()) {
                String strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
                //response = JsonUtil.jsonStringToHashMap(strResponse);
            } else {
                response = null;
            }
            JSONObject decryptedJsonObj = null;
            Map decArray = null;
            List<Map<String,Object>> decMapList =null;
            String decString = null;
            if (responseEntity.getStatusCode().value() == 200) {
                JSONArray txnHistory;

                if (null !=jsonResponse) {
                    txnHistory = jsonResponse.getJSONArray("transactionDetails");
                    decMapList = new ArrayList();
                    for (int i = 0; i < txnHistory.length(); i++) {
                        decArray = new LinkedHashMap();
                        decString = txnHistory.getJSONObject(i).getString("encTransactionInfo");
                        decString = JWTUtility.decryptJwe(decString, env.getProperty("sharedSecret"));
                        //   decryptedJsonObj = new JSONObject(decString);
                        decArray.put("txnHistory", decString);
                        decMapList.add(decArray);
                    }
                }
                if (null !=response) {
                    response.put("transactionDetails", decMapList);
                    response.put("responseCode", HCEMessageCodes.getSUCCESS());
                    response.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                }
                LOGGER.debug("Exit Exit TransactionManagementServiceImpl->getTransactionHistory");
                return response;

            } else {
                Map errorMap = new LinkedHashMap();
                if (null !=jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit TransactionManagementServiceImpl->getTransactionHistory");
                return errorMap;
            }


        }catch (Exception e)
        {
            LOGGER.debug("Exception Occored in  TransactionManagementServiceImpl->getTransactionHistory"+e);
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
    }


}