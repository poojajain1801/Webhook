package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.ServiceData;
import com.comviva.mfs.hce.appserver.model.TransactionRegDetails;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.ServiceDataRepository;
import com.comviva.mfs.hce.appserver.repository.TransactionRegDetailsRepository;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.*;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tanmay.patel on 5/10/2017.
 */
@Service
public class TransactionManagementServiceImpl implements TransactionManagementService {
    @Autowired
    private Environment env;
    @Autowired
    private CardDetailRepository cardDetailRepository;
    @Autowired
    private TransactionRegDetailsRepository transactionRegDetailsRepository;
    @Autowired
    private DeviceDetailRepository deviceDetailRepository;
    @Autowired
    private HitMasterCardService hitMasterCardService;
    private final HCEControllerSupport hceControllerSupport;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementServiceImpl.class);

    public TransactionManagementServiceImpl(HCEControllerSupport hceControllerSupport) {
        this.hceControllerSupport = hceControllerSupport;
    }
    public Map<String, Object> getTransactionHistoryVisa(GetTransactionHistoryRequest getTransactionHistoryRequest) {
        LOGGER.debug("Enter TransactionManagementServiceImpl->getTransactionHistory");
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        Map response = new LinkedHashMap();
        String localTime = null;
        JSONObject jsonResponse = null;
        String request = "";
        ResponseEntity responseEntity = null;
        //JSONObject requestJson = null;
        String url = "";
        String resourcePath = "vts/paymentTxns";
        //https://sandbox.digital.visa.com/vts/paymentTxns?apiKey=key&vProvisionedTokenID=token_ID
        url = env.getProperty("visaBaseUrlSandbox") + "/vts/paymentTxns" + "?apiKey=" + env.getProperty("apiKey") + "&vProvisionedTokenID=" + getTransactionHistoryRequest.getvProvisionedTokenID();

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
                        decryptedJsonObj = new JSONObject(decString);
                        localTime = utcToLocalTime(decryptedJsonObj.getString("transactionDate"));
                        decryptedJsonObj.put("transactionDate",localTime);
                        decArray.put("txnHistory", decryptedJsonObj.toString());
                        decMapList.add(decArray);
                    }
                }
                if (null !=response) {
                    //LOGGER.debug("txnHistory",decArray.toString());
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

    public String utcToLocalTime(String inputTime) {
        DateFormat utcFormat = null;
        Date date = null;
        DateFormat kwdTime = null;
        String localtime ="";
        String timeZone = "";
        try {
            utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = utcFormat.parse(inputTime);
            kwdTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            timeZone = env.getProperty("timezone");
            kwdTime.setTimeZone(TimeZone.getTimeZone(timeZone));
            localtime = (String)kwdTime.format(date);
            System.out.println(kwdTime.format(date));
            LOGGER.debug("Local time is *************************** ",kwdTime.format(date));
        }catch (Exception e ) {
            LOGGER.error("Exception occored in date convertion",e);
        }

        return  localtime;
    }

    public Map<String, Object> pushTransctionDetails(PushTransctionDetailsReq pushTransctionDetailsReq) {
        String requestId = pushTransctionDetailsReq.getRequestId();
        List<Transactions> transactions = pushTransctionDetailsReq.getTransactions();
        String tokenUniqueReference = pushTransctionDetailsReq.getTransactions().get(0).getTokenUniqueReference();
        HashMap rnsNotificationData = new HashMap();
        Map responseMap = new HashMap();
        String responseId = null;
        LOGGER.debug("Inside TransactionManagementService---------->pushTransctionDetails");
        RnsGenericRequest rnsGenericRequest ;
        try {
            /*rnsGenericRequest = new RnsGenericRequest();
            rnsNotificationData.put("requestId",requestId);
            rnsNotificationData.put("transactions",transactions);
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(tokenUniqueReference));
            rnsGenericRequest.setRnsData(rnsNotificationData);

            Map rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());
            rnsData.put("SUBTYPE","TXN");

            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority","high");
            payloadObject.put("time_to_live",2160000);

            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());

            Gson gson = new Gson();
            String json = gson.toJson(response);
            LOGGER.debug("pushTransctionDetails -> pushTransctionDetails->Raw response from FCM server"+json);

            if (Integer.valueOf(response.getErrorCode()) != 200) {
                responseMap.put(HCEConstants.ERROR_CODE,HCEConstants.REASON_CODE_234);
                responseMap.put("errorDescription","RNS Unavailable");
            }*/
            requestId = env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            responseMap.put("responseId",requestId );
            responseMap.put(HCEConstants.RESPONSE_CODE,HCEMessageCodes.getSUCCESS());
            responseMap.put(HCEConstants.MESSAGE,"Transaction Success");


        }catch (HCEActionException pushTransctionDetailsHCEactionException) {
            LOGGER.error("Exception occured in TransactionManagementServiceImpl->pushTransctionDetails", pushTransctionDetailsHCEactionException);
            throw pushTransctionDetailsHCEactionException;

        } catch (Exception pushTransctionDetailsException) {
            LOGGER.error("Exception occured in TransactionManagementServiceImpl->pushTransctionDetails", pushTransctionDetailsException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }


    private String getRnsRegId(String tokenUniqueReference) {
        String rnsRegID = null;
        DeviceInfo deviceInfo = null;
        String paymentAppInstanceId = null;
        Optional<CardDetails> cardDetailsList = cardDetailRepository.findByMasterTokenUniqueReference(tokenUniqueReference);
        if(cardDetailsList.isPresent() ){
            deviceInfo = cardDetailsList.get().getDeviceInfo();
            if (deviceInfo != null) {
                rnsRegID = deviceInfo.getRnsRegistrationId();
            }else {
                paymentAppInstanceId = cardDetailsList.get().getMasterPaymentAppInstanceId();
                Optional<DeviceInfo> deviceInfo1 = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
                if (deviceInfo1.isPresent())
                    rnsRegID = deviceInfo1.get().getRnsRegistrationId();
            }
        }
        return rnsRegID;
    }

    @Override
    public Map<String, Object> getTransactionsMasterCard(GetTransactionsRequest getTransactionsReq) {
        String tokenUniqueRef = getTransactionsReq.getTokenUniqueReference();
        String authenticationCode = null;
        String paymentAppInstanceId = getTransactionsReq.getPaymentAppInstanceId();
        JSONObject reqJson = new JSONObject();
        TransactionRegDetails transactionRegDetails = new TransactionRegDetails();
        Optional<CardDetails> oCardDetails = null;
        Optional<TransactionRegDetails> oTxnDetails = null;
        String authorizationStatus = null;
        List<TransactionRegDetails> transactionList = null;
        String response = null;
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = null;
        Map responseMap = null;
        String url = null;
        String id = null;
        int transactionStatus = 0;
        try{
            if (tokenUniqueRef != null) {
                oCardDetails = cardDetailRepository.findByMasterPaymentAppInstanceIdAndMasterTokenUniqueReference(paymentAppInstanceId,tokenUniqueRef);
                if (!oCardDetails.isPresent()) {
                    throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
                }
            }
            oTxnDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef);
            if (!oTxnDetails.isPresent()) {
                throw new HCEActionException(HCEMessageCodes.getInvalidTokenUniqueReference());
            }
            transactionList = transactionRegDetailsRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
            TransactionRegDetails txnDetails = oTxnDetails.get();
            authenticationCode = txnDetails.getAuthCode();
            if (authenticationCode == null || authenticationCode.isEmpty()){
                throw new HCEActionException(HCEMessageCodes.getDeviceNotRegistered());
            }
            reqJson.put("tokenUniqueReference", tokenUniqueRef);
            reqJson.put("authenticationCode",authenticationCode);
            url = env.getProperty("mdesip")  + env.getProperty("tdspath") + "/" + paymentAppInstanceId;
            id = "getTransactions";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqJson.toString(),"POST",id);
            LOGGER.info("Response recieved from masterCard ");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
                if (jsonResponse.has("transactions")){
                    JSONArray originalArray = jsonResponse.getJSONArray("transactions");
                    JSONArray duplicateArray = new JSONArray();
                    int k = 0;
                    int j = 0;
                    int m = 0;
                    int length = originalArray.length();
                    for (int i = length-1; i>=0 ; i--){
                        duplicateArray.put(k++,originalArray.get(i));
                        authorizationStatus = duplicateArray.getJSONObject(m++).get("authorizationStatus").toString();
                        switch (authorizationStatus){
                            case "AUTHORIZED" :
                                transactionStatus = 1;
                                break;
                            case "DECLINED" :
                                transactionStatus = 3;
                                break;
                            case "CLEARED" :
                                transactionStatus = 4;
                                break;
                            case "REVERSED" :
                                transactionStatus = 2;
                                break;
                            default:
                        }
                        duplicateArray.getJSONObject(j++).put("transactionStatus",transactionStatus);
                    }
                    jsonResponse.put("transactions",duplicateArray);
                }
            }

            if(responseMdes.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                if (jsonResponse != null) {
                    if (jsonResponse.has("errors") || jsonResponse.has("errorCode")) {
                        responseMap = JsonUtil.jsonToMap(jsonResponse);
                    } else {
                        int size = transactionList.size();
                        for (int i = 0 ; i<size ; i++){
                            transactionList.get(i).setAuthCode(jsonResponse.getString("authenticationCode"));
                        }
                        transactionRegDetailsRepository.save(txnDetails);
                        responseMap = JsonUtil.jsonToMap(jsonResponse);
                        responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    }
                }
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        }
        catch (HCEActionException getTransactionsHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getTransactions", getTransactionsHCEactionException);
            throw getTransactionsHCEactionException;
        }catch (Exception getTransactionsException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getTransactions", getTransactionsException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public Map<String,Object> getRegistrationCode(GetRegistrationCodeReq getRegistrationCodeReq) {
        String tokenUniqueRef = getRegistrationCodeReq.getTokenUniqueReference();
        String paymentAppInstanceId = getRegistrationCodeReq.getPaymentAppInstanceId();
        JSONObject reqJson = new JSONObject();
        TransactionRegDetails transactionRegDetails = new TransactionRegDetails();
        Optional<TransactionRegDetails> txnDetails = null;
        String response = null;
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = new JSONObject();
        Map responseMap = null;
        String url = null;
        String id = null;
        try{
            txnDetails= transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef);
            reqJson.put("tokenUniqueReference", tokenUniqueRef);
            url = env.getProperty("mdesip")  + env.getProperty("tdspath") + "/" + paymentAppInstanceId;
            id = "getRegistrationCode";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqJson.toString(),"POST",id);
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                if(jsonResponse.has("errors") || jsonResponse.has("errorCode")) {
                    responseMap = JsonUtil.jsonToMap(jsonResponse);
                    responseMap.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());

                }else {
                    if (txnDetails.isPresent()){
                        txnDetails.get().setRegCode1(jsonResponse.getString("registrationCode1"));
                        txnDetails.get().setPaymentAppInstanceId(paymentAppInstanceId);
                        transactionRegDetailsRepository.save(txnDetails.get());
                    }else {
                        transactionRegDetails.setRegCode1(jsonResponse.getString("registrationCode1"));
                        transactionRegDetails.setPaymentAppInstanceId(paymentAppInstanceId);
                        transactionRegDetails.setTokenUniqueReference(tokenUniqueRef);
                        transactionRegDetailsRepository.save(transactionRegDetails);
                    }
                    responseMap = JsonUtil.jsonToMap(jsonResponse);
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("registrationStatus",HCEConstants.INITIATE);
                }
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        }catch (HCEActionException getRegistrationCodeHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getRegistrationCode", getRegistrationCodeHCEactionException);
            throw getRegistrationCodeHCEactionException;
        } catch (Exception getRegistrationCodeException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getRegistrationCode", getRegistrationCodeException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }


}