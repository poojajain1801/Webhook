package com.comviva.mfs.hce.appserver.service;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.*;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.ServiceDataRepository;
import com.comviva.mfs.hce.appserver.repository.TransactionRegDetailsRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.*;
import com.comviva.mfs.hce.appserver.util.mdes.CryptoUtils;
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
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class CardDetailServiceImpl implements CardDetailService {
    //Repository for the card details
    private final CardDetailRepository cardDetailRepository;
    //Repository for the service data
    private final ServiceDataRepository serviceDataRepository;
    private  static final String PAYMENT_APP_INSTANCE_ID = "paymentAppInstanceId";
    private final DeviceDetailRepository deviceDetailRepository;
    private final TransactionRegDetailsRepository transactionRegDetailsRepository;
    private final UserDetailRepository userDetailRepository;
    private final HCEControllerSupport hceControllerSupport;

    @Autowired
    private Environment env;

    @Autowired
    private RemoteNotificationService remoteNotificationService;

    @Autowired
    private HitMasterCardService hitMasterCardService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CardDetailServiceImpl.class);


    @Autowired
    public CardDetailServiceImpl(CardDetailRepository cardDetailRepository,
                                 ServiceDataRepository serviceDataRepository,
                                 DeviceDetailRepository deviceDetailRepository,
                                 TransactionRegDetailsRepository transactionRegDetailsRepository,
                                 UserDetailRepository userDetailRepository,
                                 HCEControllerSupport hceControllerSupport) {
        this.cardDetailRepository = cardDetailRepository;
        this.serviceDataRepository = serviceDataRepository;
        this.deviceDetailRepository = deviceDetailRepository;
        this.transactionRegDetailsRepository = transactionRegDetailsRepository;
        this.userDetailRepository = userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
    }

    /**
     * Check device/Card eligibility for digitiazation.
     *
     * @param addCardParam Parameters for check card eligibility.
     * @return Eligibility Response
     */
    public Map<String, Object> checkDeviceEligibility(AddCardParm addCardParam) {
        Map<String, Object> mapResponse = null;
        String paymentAppInstanceId = "";
        Optional<DeviceInfo> deviceDetail;
        String url = null;
        JSONObject jsonResp = new JSONObject();
        JSONObject checkDeviceEligibilityRequest = null;
        Optional<DeviceInfo> deviceInfoOptional = null;
        ResponseEntity responseEntity = null;
        JSONObject jsonResponse = null;
        JSONObject eligibilityResponse = null;
        Map eligibilityMap = null;
        Map applicableCardInfoMap = null;
        String response = "";
        String id = null;
        String requestId;
        try {
           /* deviceInfoOptional = deviceDetailRepository.findByPaymentAppInstanceId(addCardParam.getPaymentAppInstanceId());
            if (!deviceInfoOptional.isPresent()) {
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }*/

            // Only token type CLOUD is supported
            /*if (!ignoreCase("CLOUD").equals(addCardParam.getTokenType())) {
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }*/

            // *************** Send Card Eligibility Check request to MDES ***************
            JSONObject cardInfoJsonObj = new JSONObject();
            CardInfo cardInfoObj = addCardParam.getCardInfo();
            cardInfoJsonObj.put("publicKeyFingerprint",cardInfoObj.getPublicKeyFingerprint());
            cardInfoJsonObj.put("encryptedKey",cardInfoObj.getEncryptedKey());
            //cardInfoJsonObj.put("OeapHashingAlgorithim","SHA512");
            cardInfoJsonObj.put("iv",cardInfoObj.getIv());
            cardInfoJsonObj.put("encryptedData",cardInfoObj.getEncryptedData());

            //Get DeviceInfo
            JSONObject deviceInfo = new JSONObject(addCardParam.getDeviceInfo());
            DeviceInfoRequest deviceInfoRequest = addCardParam.getDeviceInfo();

            checkDeviceEligibilityRequest = new JSONObject();
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            checkDeviceEligibilityRequest.put("requestId", requestId);
            //checkDeviceEligibilityRequest.put("responseHost",env.getProperty("responseHost"));
            checkDeviceEligibilityRequest.put("tokenType", addCardParam.getTokenType());
            checkDeviceEligibilityRequest.put(PAYMENT_APP_INSTANCE_ID, addCardParam.getPaymentAppInstanceId());
            checkDeviceEligibilityRequest.put("paymentAppId", addCardParam.getPaymentAppId());
            checkDeviceEligibilityRequest.put("cardInfo", cardInfoJsonObj);
            checkDeviceEligibilityRequest.put("deviceInfo", deviceInfo);
            checkDeviceEligibilityRequest.put("cardletId",this.env.getProperty("cardletId"));
            checkDeviceEligibilityRequest.put("consumerLanguage", "en");
            // Call checkEligibility Api of MDES to check if the card is eligible for digitization.
            url = this.env.getProperty("mdesip") +this.env.getProperty("digitizationpath");
            id = "checkEligibility";
            LOGGER.info("Check card eligibility before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, checkDeviceEligibilityRequest.toString(), "POST",id);
            LOGGER.info("Check card eligibility after hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
            //Prepare Response
            if ((responseEntity.hasBody()) && (responseEntity.getStatusCode().value() == 200)) {
                if (responseEntity.hasBody()) {
                    response = String.valueOf(responseEntity.getBody());
                    jsonResp = new JSONObject(response);
                    paymentAppInstanceId = addCardParam.getPaymentAppInstanceId();
                    deviceDetail = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
                    if (deviceDetail.isPresent()){
                        deviceDetail.get().setNfcCapable(jsonResp.getString("responseHost"));
                        deviceDetailRepository.save(deviceDetail.get());
                    }
                }
                //response =  String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
                eligibilityResponse = new JSONObject();
                if (jsonResponse.has("errors")) {
                    jsonResponse.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                    //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                    jsonResponse.put("message",jsonResponse.getString("errorDescription"));
                }
                else {
                    LOGGER.info("Check card eligibility before DB operation  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
                    ServiceData serviceData = serviceDataRepository.save(new ServiceData(null,  requestId, checkDeviceEligibilityRequest.toString().getBytes(), response.getBytes()));
                    LOGGER.info("Check card eligibility after DB operation  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
                    jsonResponse.put("statusCode",HCEMessageCodes.getSUCCESS());
                    jsonResponse.put("message","Success");
                    jsonResponse.put("serviceId",requestId);
                }

                eligibilityMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
            }
            else
            {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        } catch (JSONException e) {
            LOGGER.error("Exception occured", e);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
        } catch (Exception e) {
            LOGGER.error("Exception occured", e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return eligibilityMap;
    }

    public Map<String, Object> addCard(DigitizationParam digitizationParam) {
        Optional<DeviceInfo> deviceInfoList = null;
        String eligibilityRequest = null;
        Optional<DeviceInfo> deviceDetail;
        String eligibilityResponse = null;
        JSONObject jsonRequest = null;
        JSONObject jsonResponse = null;
        JSONObject digitizeReq = null;
        JSONObject cardInfo = null;
        JSONObject eligibilityReceiptValue = null;
        JSONObject decisioningData = null;
        JSONObject provisionRespMdes = null;
        String url = null;
        String response = null;
        CardDetails cardDetails = null;
        String decision = "";
        String paymentAppInstanceId ="";
        String urlHost = null;
        try {
           // deviceInfoOptional = deviceDetailRepository.findByPaymentAppInstanceId(digitizationParam.getPaymentAppInstanceId());
            /*if (!deviceInfoOptional.isPresent()) {
                //return prepareDigitizeResponse(HCEConstants.REASON_CODE1, "Invalid Payment App Instance Id");
                throw new HCEActionException(HCEMessageCodes.getInvaildPaymentappInstanceId());
            }

            if (!serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).isPresent()) {
                //return prepareDigitizeResponse(HCEConstants.REASON_CODE1, "Card is not eligible for the digitization service");
                throw new HCEActionException(HCEMessageCodes.getCardNotEligible());
            }*/

            eligibilityRequest = new String(serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getRequest());
            eligibilityResponse = new String(serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getResponse());
            jsonRequest = new JSONObject(eligibilityRequest);
            jsonResponse = new JSONObject(eligibilityResponse);

            // ************* Prepare request to MDES for Digitize api *************

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            digitizeReq = new JSONObject();
            //digitizeReq.put("responseHost", "site1.your-server.com");
            String requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            digitizeReq.put("requestId",requestId);
            digitizeReq.put(PAYMENT_APP_INSTANCE_ID, digitizationParam.getPaymentAppInstanceId());
            //digitizeReq.put("serviceId", "ServiceIdCheckEligibility");
            digitizeReq.put("termsAndConditionsAssetId", digitizationParam.getTermsAndConditionsAssetId());
            digitizeReq.put("termsAndConditionsAcceptedTimestamp", nowAsISO);
            digitizeReq.put("taskId", ArrayUtil.getRequestId());

            eligibilityReceiptValue = new JSONObject();
            eligibilityReceiptValue.put("value", jsonResponse.getJSONObject("eligibilityReceipt").getString("value"));
            digitizeReq.put("eligibilityReceipt", eligibilityReceiptValue);

            cardInfo = jsonRequest.getJSONObject("cardInfo");
            /*cardInfo.put("encryptedData", "4545433044323232363739304532433610DE1D1461475BEB6D815F31764DDC20298BD779FBE37EE5AB3CBDA9F9825E1DDE321469537FE461E824AA55BA67BF6A");
            cardInfo.put("publicKeyFingerprint", "4c4ead5927f0df8117f178eea9308daa58e27c2b");
            cardInfo.put("encryptedKey", "A1B2C3D4E5F6112233445566");
            cardInfo.put("oaepHashingAlgorithm", "SHA512");*/
            digitizeReq.put("cardInfo", cardInfo);

            // String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/addCard", digitizeReq);
            paymentAppInstanceId = digitizationParam.getPaymentAppInstanceId();
            deviceDetail = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
            if (deviceDetail.isPresent() && !(this.env.getProperty("mdeshost")).equalsIgnoreCase("mtf")){
                urlHost = deviceDetail.get().getNfcCapable();
            }
            if (urlHost == null) {
                url = this.env.getProperty("mdesip") + this.env.getProperty("digitizationpath");
            }else {
                url = "https://"+urlHost + this.env.getProperty("digitizationpath");
            }
            String id = "digitize";
            ResponseEntity responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, digitizeReq.toString(), "POST",id);

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
            }

            provisionRespMdes = new JSONObject(response);
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                //JSONObject mdesResp = provisionRespMdes.getJSONObject("response");
                if (provisionRespMdes.has("errors")) {
                    provisionRespMdes.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                    //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                    provisionRespMdes.put("message",provisionRespMdes.getString("errorDescription"));
                }
                else {
                    provisionRespMdes.put("responseCode",HCEMessageCodes.getSUCCESS());
                    provisionRespMdes.put("message","Success");
                    if (provisionRespMdes.has("decision"))
                        decision = provisionRespMdes.getString("decision");

                    if(!decision.equalsIgnoreCase("DECLINED")) {
                        cardDetails = new CardDetails();
                        //--madan cardDetails.setUserName(deviceDetailRepository.findByPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId")).get().getUserName());
                        cardDetails.setMasterPaymentAppInstanceId(jsonRequest.getString(PAYMENT_APP_INSTANCE_ID));
                        deviceInfoList = deviceDetailRepository.findByPaymentAppInstanceId(jsonRequest.getString(PAYMENT_APP_INSTANCE_ID));
                        if (deviceInfoList.isPresent()) {
                            cardDetails.setDeviceInfo(deviceInfoList.get());
                        }
                        cardDetails.setMasterTokenUniqueReference(provisionRespMdes.getString("tokenUniqueReference"));
                        cardDetails.setPanUniqueReference(provisionRespMdes.getString("panUniqueReference"));
                        cardDetails.setMasterTokenInfo(provisionRespMdes.getJSONObject("tokenInfo").toString());
                        cardDetails.setStatus(HCEConstants.INITIATE);
                        cardDetails.setCardType(HCEConstants.MASTERCARD);
                        cardDetails.setCardId(ArrayUtil.getHexString(ArrayUtil.getRandom(8)));
                        cardDetails.setCardIdentifier(ArrayUtil.getHexString(ArrayUtil.getRandom(8)));
                        cardDetails.setCardSuffix(provisionRespMdes.getJSONObject("tokenInfo").getString("accountPanSuffix"));
                        cardDetails.setTokenSuffix(provisionRespMdes.getJSONObject("tokenInfo").getString("tokenPanSuffix"));
                        cardDetails.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
                        cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                        cardDetailRepository.save(cardDetails);
                    }else {
                        provisionRespMdes.put(HCEConstants.RESPONSE_CODE , HCEMessageCodes.getFailedAtThiredParty());
                        provisionRespMdes.put(HCEConstants.MESSAGE, HCEConstants.GENERIC_ERROR);
                    }
                }

            } else {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        } catch (HCEActionException addCardHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->addCard", addCardHCEactionException);
            throw addCardHCEactionException;

        } catch (Exception addCardException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->addCard", addCardException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return JsonUtil.jsonStringToHashMap(provisionRespMdes.toString());
    }

    public Map<String,Object> tokenize (TokenizeRequest tokenizeRequest) {
        JSONObject requestJson = new JSONObject();
        String url = null;
        ResponseEntity responseEntity = null;
        String response = null;
        JSONObject responseJson = null;
        try{
            //TODO:Get the tokenRequesteriD from property file
            requestJson.put("tokenRequestorId","98765432101");
            requestJson.put("tokenType",tokenizeRequest.getTokenType());
            requestJson.put("cardInfo",tokenizeRequest.getCardInfo());
            //TODO:Generate the taskID
            requestJson.put("taskId","123456");
            requestJson.put("paymentAppId",tokenizeRequest.getPaymentAppId());
            url = "";
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST",null);
            if(responseEntity.hasBody())
            {
                response = String.valueOf(responseEntity.getBody());
                responseJson = new JSONObject(response);

            }
            if (responseEntity.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                //TODO: Insert the data to the data base
                return JsonUtil.jsonToMap(responseJson);
            }
            else {
                hceControllerSupport.formResponse(HCEMessageCodes.getFailedAtThiredParty());
            }


        }catch (HCEActionException tokenizeHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->addCard", tokenizeHCEactionException);
            throw tokenizeHCEactionException;

        } catch (Exception tokenizeException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->addCard", tokenizeException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return null;
    }

    /**
     * Fetches asset.
     *
     * @param assetID asset Id of the asset requested.
     * @return Asset data
     */
    public Map<String,Object> getAsset(GetAssetPojo assetID) {
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = new JSONObject();
        String response = null;
        Map responseMap = null;
        String url = null;
        String id = "";
        try {
            //https://mtf.services.mastercard.com/mtf/mdes/digitization/1/0/asset?AssetId=95d4cd38-36fc-4b26-8795-06a3b00acf3b
            url =  env.getProperty("mdesip")+ "/mdes/assets/1/0/asset";
            id = assetID.getAssetId();
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url, null, "GET",id);
            if (responseMdes == null)
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
           /* if (responseMdes.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }*/ if (jsonResponse.has("errors")) {
                jsonResponse.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                jsonResponse.put("message",jsonResponse.getString("errorDescription"));
            }
            else
            {
                jsonResponse.put("responseCode",HCEMessageCodes.getSUCCESS());
                jsonResponse.put("message","Success");
            }
            responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
        } catch (HCEActionException getSystemHealthHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getSystemHealth", getSystemHealthHCEactionException);
            throw getSystemHealthHCEactionException;
        } catch (Exception getSystemHealthException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getSystemHealth", getSystemHealthException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;
    }

    @Override
    public  Map<String,Object> activate(ActivateReq activateReq) {
        JSONObject reqMdes = null;
        ResponseEntity responseEntity = null;
        String url = null;
        String response = null;
        JSONObject jsRespMdes = new JSONObject();
        String requestId = "";
        String id = "";
        String result = "";
        try {
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            reqMdes  = new JSONObject();
            //reqMdes.put("responseHost", "com.mahindracomviva.payAppServer");
            reqMdes.put("requestId", requestId);
            reqMdes.put(PAYMENT_APP_INSTANCE_ID, activateReq.getPaymentAppInstanceId());
            reqMdes.put("tokenUniqueReference", activateReq.getTokenUniqueReference());
            reqMdes.put("authenticationCode", activateReq.getAuthenticationCode());
            url = this.env.getProperty("mdesip")  +this.env.getProperty("digitizationpath");
            id = "activate";
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqMdes.toString(),"POST",id);
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsRespMdes = new JSONObject(response);
            }
           if(jsRespMdes.has("result")) {
               result = jsRespMdes.getString("result");
           }
           switch (result){
               case "SUCCESS":
                   jsRespMdes.put(HCEConstants.RESPONSE_CODE ,String.valueOf(HCEConstants.REASON_CODE7));
                   break;

               case "INCORRECT_CODE":

               case "EXPIRED_CODE":
                   jsRespMdes.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getIncorrectOtp());
                   break;

               case  "INCORRECT_CODE_RETRIES_EXCEEDED":
                   jsRespMdes.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getFailedAtThiredParty());
                   break;
               default:
                   jsRespMdes.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getFailedAtThiredParty());

           }
           /*
           *//*if (jsRespMdes.has("errors")) {
                jsRespMdes.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                jsRespMdes.put("message",jsRespMdes.getString("errorDescription"));
           }*//*
           if (!result.equalsIgnoreCase("SUCCESS")){
               jsRespMdes.put("responseCode", HCEMessageCodes.getIncorrectOtp());
                //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
               jsRespMdes.put("message","You have entered Invalid OTP");
           }
           else {
               jsRespMdes.put("responseCode",String.valueOf(HCEConstants.REASON_CODE7));
               jsRespMdes.put("message",HCEConstants.SUCCESS);
           }*/

        }catch (HCEActionException activateHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", activateHCEactionException);
            throw activateHCEactionException;

        } catch (Exception activateException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", activateException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return JsonUtil.jsonToMap(jsRespMdes);
    }

    public Map<String, Object> enrollPan (EnrollPanRequest enrollPanRequest) {
        List<DeviceInfo> deviceInfoList;
        ObjectMapper objectMapper ;
        Map<String, Object> map ;
        JSONObject jsonencPaymentInstrument;
        String encPaymentInstrument ;
        JSONObject jsonResponse = null;
        ResponseEntity responseEntity ;
        Map responseMap;
        String response = "";
        String vPanEnrollmentId = null;
        // List<VisaCardDetails> visaCardDetailList = null;
        String clientWalletAccountId;
        String clientDeviceId ;
        HitVisaServices hitVisaServices ;
        String accountNubmer ;
        String cardIdentifier ;
        List<CardDetails> cardDetailsList ;
        DeviceInfo deviceInfo ;
        try{
            LOGGER.debug("Enter CardDetailServiceImpl->enrollPan");
            clientWalletAccountId = enrollPanRequest.getClientWalletAccountId();
            clientDeviceId = enrollPanRequest.getClientDeviceID();
            accountNubmer = enrollPanRequest.getEncPaymentInstrument().getAccountNumber();

            if(accountNubmer!=null && !accountNubmer.isEmpty()){
                cardIdentifier = MessageDigestUtil.sha256Hasing(accountNubmer);
            }else{
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            deviceInfoList = deviceDetailRepository.findDeviceDetails(clientDeviceId,clientWalletAccountId,HCEConstants.ACTIVE);
            if(deviceInfoList!=null && !deviceInfoList.isEmpty()){
                cardDetailsList = cardDetailRepository.findCardDetailsByIdentifier(cardIdentifier,clientWalletAccountId,clientDeviceId,HCEConstants.ACTIVE,HCEConstants.SUSUPEND);
                if(cardDetailsList!= null && !cardDetailsList.isEmpty()){
                    throw new HCEActionException(HCEMessageCodes.getCardAlreadyRegistered());
                }
                objectMapper = new ObjectMapper();
                map = new HashMap<>();
                map.put("clientAppID", env.getProperty("clientAppID"));
                map.put("clientWalletAccountID", clientWalletAccountId);
                map.put("locale", enrollPanRequest.getLocale());
                map.put("panSource", enrollPanRequest.getPanSource());
                jsonencPaymentInstrument = new JSONObject(enrollPanRequest.getEncPaymentInstrument());
                encPaymentInstrument = JWTUtility.createSharedSecretJwe(jsonencPaymentInstrument.toString(), env.getProperty("apiKey"), env.getProperty("sharedSecret"));
                map.put("encPaymentInstrument", encPaymentInstrument);
                map.put("consumerEntryMode", enrollPanRequest.getConsumerEntryMode());
                hitVisaServices = new HitVisaServices(env);
                responseMap = new LinkedHashMap();
                responseEntity = hitVisaServices.restfulServiceConsumerVisa(env.getProperty("visaBaseUrlSandbox") + "/vts/panEnrollments?apiKey=" + env.getProperty("apiKey"), objectMapper.writeValueAsString(map), "vts/panEnrollments", "POST");
                CardDetails  cardDetails = new CardDetails();
                if (responseEntity.hasBody()) {
                    response = String.valueOf(responseEntity.getBody());
                    jsonResponse = new JSONObject(response);
                    responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                }

                if (responseEntity.getStatusCode().value() == 200 || responseEntity.getStatusCode().value() == 201) {
                    if(null != jsonResponse) {
                        vPanEnrollmentId = jsonResponse.getString("vPanEnrollmentID");
                    }
                    //cardDetailsList = cardDetailRepository.findByPanUniqueReference(vPanEnrollmentId);
                    //CardDetails cardDetails = null;


                    cardDetails.setDeviceInfo(deviceInfoList.get(0));
                    cardDetails.setCardId(HCEUtil.generateRandomId(HCEConstants.CARD_PREFIX));

                    if (null != jsonResponse) {
                        cardDetails.setCardSuffix(jsonResponse.getJSONObject("paymentInstrument").getString("last4"));
                    }
                   // cardDetails.setVisaProvisionTokenId(vPanEnrollmentId);
                    cardDetails.setPanUniqueReference(vPanEnrollmentId);
                    cardDetails.setCardIdentifier(cardIdentifier);
                    cardDetails.setStatus(HCEConstants.INITIATE);
                    cardDetails.setCreatedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    cardDetails.setCardType(HCEConstants.VISA);
                    cardDetailRepository.save(cardDetails);
                    responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getSUCCESS());
                    responseMap.put(HCEConstants.MESSAGE, hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                } else {
                    if (null != jsonResponse) {
                        /*responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                        responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));*/
                        responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getFailedAtThiredParty());
                        responseMap.put(HCEConstants.MESSAGE, HCEConstants.GENERIC_ERROR);
                    }
                }

            }else{
                throw new HCEActionException(HCEMessageCodes.getDeviceNotRegistered());
            }
        }catch(HCEActionException enrollPanHCEactionException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->enrollPan", enrollPanHCEactionException);
            throw enrollPanHCEactionException;

        }catch(Exception enrollPanException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->enrollPan", enrollPanException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.debug("Exit CardDetailServiceImpl->enrollPan");
        return responseMap;
    }


    public Map<String, Object> getCardMetadata (GetCardMetadataRequest getCardMetadataRequest) {
        LOGGER.debug("Inside CardDetailsServiceImpl->getCardMetadata");
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String response= null;
        JSONObject jsonResponse  = null;
        String request = "";
        ResponseEntity responseEntity = null;
        String strResponse = null;
        Map responseMap = null;
        //JSONObject requestJson = null;
        String url = "";
        String resourcePath ="vts/panEnrollments/"+getCardMetadataRequest.getVpanEnrollmentID();
        //https://sandbox.digital.visa.com/vts/panEnrollments/{vPanEnrollmentID}?apiKey=key&platformType=platform_type
        url =  env.getProperty("visaBaseUrlSandbox")+"/vts/panEnrollments/"+getCardMetadataRequest.getVpanEnrollmentID()+"?apiKey="+env.getProperty("apiKey");
        try {
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");
            if (responseEntity.hasBody()) {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                return responseMap;

            }else {
                Map errorMap = new LinkedHashMap();
                if(null !=jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit CardDetailsServiceImpl->getCardMetadata");
                return errorMap;
            }


        }catch (Exception e) {
            LOGGER.error("Exception occured",e);
            LOGGER.debug("Exception occurred in CardDetailsServiceImpl->getCardMetadata");
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
    }

    public Map<String, Object> getContent(GetContentRequest getContentRequest){
        LOGGER.debug("Inside CardDetailsServiceImpl->getContent");
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String response= null;
        JSONObject jsonResponse  = null;
        String request = "";
        String strResponse = null;
        ResponseEntity responseEntity =null;
        String url = "";
        Map responseMap = null;
        String resourcePath ="vts/cps/getContent/"+getContentRequest.getGuid();
        url =  env.getProperty("visaBaseUrlSandbox")+"/vts/cps/getContent/"+getContentRequest.getGuid()+"?apiKey="+env.getProperty("apiKey");

        try {
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseEntity.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                return responseMap;

            }
            else {
                Map errorMap = new LinkedHashMap();
                if (null != jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit CardDetailsServiceImpl->getContent");
                return errorMap;
            }

        }catch (Exception e) {
            LOGGER.error("Exception occured",e);
            LOGGER.debug("Exit CardDetailsServiceImpl->getContent");
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
    }

    @Override
    public Map notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq) {
        String tokenUniqueReference = notifyTransactionDetailsReq.getTokenUniqueReference();
        String registrationCode2 = notifyTransactionDetailsReq.getRegistrationCode2();
        String tdsUrl = notifyTransactionDetailsReq.getTdsUrl();
        String paymentAppInstanceId = notifyTransactionDetailsReq.getPaymentAppInstanceId();
        Optional<TransactionRegDetails> transactionDetails;
        TransactionRegDetails transactionRegDetails = null;
        JSONObject reqJson = new JSONObject();
        String response = null;
        JSONObject jsonResponse = null;
        Map responseMap = new HashMap();
        Map registerRespose = null;
        String responseId = "";
        TDSRegistrationReq tdsRegistrationReq = null;
        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        HashMap notificationDataMap = null;
        try{
            transactionDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueReference);
            if (!transactionDetails.isPresent()) {
                LOGGER.error("invalid tokenUniqueReference ");
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }
            transactionRegDetails = transactionDetails.get();
            if(registrationCode2 !=null){
                transactionRegDetails.setRegCode2(registrationCode2);
                LOGGER.debug("registration code2 ******** :"+registrationCode2);
                transactionRegDetailsRepository.save(transactionRegDetails);
            }
            responseId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            responseMap.put("responseId" , responseId);

            //Register
            LOGGER.debug("*****************************Register with TDS Started****************************");
            tdsRegistrationReq = new TDSRegistrationReq();
            tdsRegistrationReq.setPaymentAppInstanceId(notifyTransactionDetailsReq.getPaymentAppInstanceId());
            tdsRegistrationReq.setTokenUniqueReference(notifyTransactionDetailsReq.getTokenUniqueReference());
            registerRespose = registerWithTDS(tdsRegistrationReq);
            String strregisterRespose = (String) registerRespose.get("responseCode");
            if(!strregisterRespose.equalsIgnoreCase(HCEMessageCodes.getSUCCESS())){
                LOGGER.debug("*****************************Register with TDS Failed****************************");
                throw new HCEActionException(HCEMessageCodes.getServiceFailed());

            }
            LOGGER.debug("*****************************Register with TDS Completed****************************");

            //Send Remote Notification to device

            notificationDataMap = new HashMap();
            notificationDataMap.put("registrationStatus",HCEConstants.ACTIVE);
            notificationDataMap.put("tokenUniqueReference",notifyTransactionDetailsReq.getTokenUniqueReference());

            //Prepare FCM Request
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(notifyTransactionDetailsReq.getPaymentAppInstanceId()));
            rnsGenericRequest.setRnsData(notificationDataMap);

            Map rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());
            rnsData.put("SUBTYPE","MDES_TXN");

            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority", "high");
            payloadObject.put("time_to_live", 2160000);

            //Send Remote Notification
            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse fcmresponse = rns.sendRns(payloadObject.toString().getBytes());
            Gson gson = new Gson();
            String json = gson.toJson(fcmresponse);
            LOGGER.debug("CardDetailServiceImpl -> notifyTokenUpdated->Raw response from FCM server" + json);
            if (Integer.valueOf(fcmresponse.getErrorCode()) != 200) {
                return ImmutableMap.of("errorCode", "720",
                        "errorDescription", "UNABLE_TO_DELIVER_MESSAGE");
            }


        }catch (HCEActionException notifyTransactionDetailsHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->notifyTransactionDetails", notifyTransactionDetailsHCEactionException);
            throw notifyTransactionDetailsHCEactionException;
        } catch (Exception notifyTransactionDetailsException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->notifyTransactionDetails", notifyTransactionDetailsException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }



    /*public Map<String,Object> getRegistrationCode(GetRegistrationCodeReq getRegistrationCodeReq) {
        String tokenUniqueRef = getRegistrationCodeReq.getTokenUniqueReference();
        String paymentAppInstanceId = getRegistrationCodeReq.getPaymentAppInstanceId();
        JSONObject reqJson = new JSONObject();
        TransactionRegDetails transactionRegDetails = new TransactionRegDetails();
        Optional<TransactionRegDetails> txnDetails = null;
        String response = null;
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = null;
        Map responseMap = null;
        String url = null ;
        String id = null;
        try{
            txnDetails= transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef);
            reqJson.put("tokenUniqueReference", tokenUniqueRef);
            url = env.getProperty("mdesip")  + env.getProperty("tdspath") + "/" + paymentAppInstanceId ;
            id = "getRegistrationCode";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqJson.toString(),"POST",id);
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                if(jsonResponse.has("errors") || jsonResponse.has("errorCode")) {
                    responseMap.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());

                }else {
                    if (txnDetails!=null && txnDetails.isPresent()){
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
    }*/


    public Map registerWithTDS(TDSRegistrationReq tdsRegistrationReq ) {
        String tokenUniqueRef = tdsRegistrationReq.getTokenUniqueReference();
        String registrationHash = null;
        Optional<TransactionRegDetails> transactionDetails = null;
        TransactionRegDetails transactionRegDetails = null;
        JSONObject jsonResponse = new JSONObject();
        String response = null;
        JSONObject requestJson = new JSONObject();
        ResponseEntity responseMdes = null;
        Map responseMap = new HashMap();
        String url = null;
        String id = null;
        try {
            transactionDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef);
            if (!transactionDetails.isPresent()) {
                LOGGER.error(" invalid tokenUniqueReference ");
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }
            transactionRegDetails = transactionDetails.get();
            try {
                // Get the regcode1 and regcode2 from the DB and generate registrationHash
                LOGGER.debug("Reg code1 ----------------------------------------------------- "+transactionRegDetails.getRegCode1());
                LOGGER.debug("Reg code2 ----------------------------------------------------- "+transactionRegDetails.getRegCode2());
                registrationHash = MessageDigestUtil.sha256Hasing(transactionRegDetails.getRegCode1() + transactionRegDetails.getRegCode2());
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                LOGGER.error("Exception occured", e);
                throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
            }
            requestJson = new JSONObject();
            requestJson.put("tokenUniqueReference", tokenUniqueRef);
            requestJson.put("registrationHash", registrationHash);

            // Invoke Register API of the MDES
            url = env.getProperty("mdesip") + env.getProperty("tdspath")+"/"+tdsRegistrationReq.getPaymentAppInstanceId();
            id = "register";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url, requestJson.toString(), "POST", id);
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if (responseMdes.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                if (!jsonResponse.has("errorCode") && !jsonResponse.has("errors")){
                    transactionRegDetails.setAuthCode(jsonResponse.getString("authenticationCode"));
                    transactionRegDetails.setTdsUrl(jsonResponse.getString("tdsUrl"));
                    transactionRegDetails.setStatus(HCEConstants.ACTIVE);
                    transactionRegDetailsRepository.save(transactionRegDetails);
                    responseMap = JsonUtil.jsonToMap(jsonResponse);
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                }
                else{
                    responseMap = JsonUtil.jsonToMap(jsonResponse);
                    responseMap.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                    //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                }
            } else
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());

        } catch (HCEActionException registerWithTDSHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->registerWithTDS", registerWithTDSHCEactionException);
            throw registerWithTDSHCEactionException;
        } catch (Exception registerWithTDSException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->registerWithTDS",registerWithTDSException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public Map performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq) {
        String paymentAppInstanceID = lifeCycleManagementReq.getPaymentAppInstanceId();
        List<String> tokenUniqueRefList = lifeCycleManagementReq.getTokenUniqueReferences();
        String tokenUniqueRef = "";
        String response = "";
        JSONObject lifecycleJsonRequest = null;
        String url = "";
        JSONObject responseJson = null;
        String statusFromMastercard = "";
        String status = "";
        boolean error = false;
        JSONArray tokens = null;
        JSONObject tokensJsonObj = null;
        try {
            if (tokenUniqueRefList.isEmpty() || (tokenUniqueRefList.size() == 0)) {
                throw new HCEActionException(HCEMessageCodes.getInsufficientData());
            }
            for (int i = 0; i < tokenUniqueRefList.size(); i++) {
                tokenUniqueRef = tokenUniqueRefList.get(i);
                //get card detail repository
                CardDetails cardDetails = cardDetailRepository.findByMasterTokenUniqueReference(tokenUniqueRef).get();

                //Check if the token unique reference are valid or not
                if (!(tokenUniqueRef.equalsIgnoreCase(cardDetails.getMasterTokenUniqueReference()))) {
                    // return ImmutableMap.of(HCEConstants.REASON_CODE, "260", "message", "Invalid token UniqueReference");
                    throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
                }

                //Check if the payment appInstance ID is valid or not
                if (!(paymentAppInstanceID.equalsIgnoreCase(cardDetails.getMasterPaymentAppInstanceId()))) {
                    throw new HCEActionException(HCEMessageCodes.getInvalidPaymentAppInstanceId());
                }
                //Check the status of the card if it is deactivated than thwor error
            }

            //Prepare req for delete req
            lifecycleJsonRequest = new JSONObject();
            lifecycleJsonRequest.put("requestId", ArrayUtil.getHexString(ArrayUtil.getRandom(10)));
            lifecycleJsonRequest.put(PAYMENT_APP_INSTANCE_ID, paymentAppInstanceID);
            lifecycleJsonRequest.put("tokenUniqueReferences", tokenUniqueRefList);
            lifecycleJsonRequest.put("causedBy", lifeCycleManagementReq.getCausedBy());
            lifecycleJsonRequest.put(HCEConstants.REASON_CODE, lifeCycleManagementReq.getReasonCode());
            lifecycleJsonRequest.put("reason", lifeCycleManagementReq.getReason());
            ResponseEntity responseEntity = null;
            url = this.env.getProperty("mdesip") + this.env.getProperty("digitizationpath");
            String id = "";
            //Call mastercard //{DELETE,SUSPEND,UNSUSPEND}
            switch (lifeCycleManagementReq.getOperation()) {
                case "DELETE":
                    id = "delete";
                    responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, lifecycleJsonRequest.toString(), "POST", id);
                    break;
                case "SUSPEND":
                    id = "suspend";
                    responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, lifecycleJsonRequest.toString(), "POST", id);
                    break;
                case "UNSUSPEND":
                    id = "unsuspend";
                    responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, lifecycleJsonRequest.toString(), "POST", id);
                    break;
                default:
                    return ImmutableMap.of(HCEConstants.REASON_CODE, "262", "message", "Invalid Operation");
            }
            if (responseEntity == null) {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                responseJson = new JSONObject(response);
            }
            //Check if https req is 200
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                //Check the status of indivisual token and update the status of the token in the DB
                if (responseJson.has("tokens")) {
                    tokens = responseJson.getJSONArray("tokens");
                    tokensJsonObj = (JSONObject) tokens.get(0);
                }

                if (responseJson.has("errors")) {
                    responseJson.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                    //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                    responseJson.put("message", responseJson.getString("errorDescription"));
                } else if (tokensJsonObj.has("errorCode")) {
                    responseJson.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                    //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                    responseJson.put("message", tokensJsonObj.getString("errorDescription"));
                } else {
                    responseJson.put("responseCode", HCEMessageCodes.getSUCCESS());
                    JSONArray tokensArray = responseJson.getJSONArray("tokens");
                    for (int i = 0; i < tokensArray.length(); i++) {
                        JSONObject j = tokensArray.getJSONObject(i);
                        if (j.has("status")) {
                            tokenUniqueRef = j.getString("tokenUniqueReference");
                            statusFromMastercard = j.getString("status");
                            switch (statusFromMastercard) {
                                case "DEACTIVATED":
                                    status = HCEConstants.INACTIVE;
                                    break;
                                case "SUSPENDED":
                                    status = HCEConstants.SUSUPEND;
                                    break;
                                case "ACTIVE":
                                    status = HCEConstants.ACTIVE;
                                    break;
                            }
                            if (cardDetailRepository.findByMasterTokenUniqueReference(tokenUniqueRef).isPresent()) {
                                CardDetails cardDetails = cardDetailRepository.findByMasterTokenUniqueReference(tokenUniqueRef).get();
                                cardDetails.setStatus(status);
                                cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                                cardDetailRepository.save(cardDetails);
                            }
                        }
                    }
                }
                //Check the status of indivisual token and update the status of the token in the DB
                //Call update the card starus of the token in CMS-D
            }
        } catch (HCEActionException enrollPanHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->performCardLifeCycleManagement", enrollPanHCEactionException);
            throw enrollPanHCEactionException;

        } catch (Exception enrollPanException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->performCardLifeCycleManagement", enrollPanException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        LOGGER.debug("Exit CardDetailServiceImpl->enrollPan");

        //Check the status of indivisual token and update the status of the token in the DB
        //Call update the card starus of the token in CMS-D
        //Send response
        return JsonUtil.jsonStringToHashMap(responseJson.toString());
    }

    public Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq) {
        JSONObject reqMdes = null;
        ResponseEntity responseEntity = null;
        String url = null;
        String response = null;
        JSONObject jsRespMdes = new JSONObject();
        String requestId = "";
        JSONObject authenticationCode = null;
        String paymentAppInstanceId = null;
        String id = "";
        String urlHost = null;
        Optional<DeviceInfo> deviceDetail = null;
        try {
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            reqMdes  = new JSONObject();
            //reqMdes.put("responseHost", "com.mahindracomviva.payAppServer");
            reqMdes.put("requestId", requestId);
            reqMdes.put(PAYMENT_APP_INSTANCE_ID, activationCodeReq.getPaymentAppInstanceId());
            reqMdes.put("tokenUniqueReference", activationCodeReq.getTokenUniqueReference());
            authenticationCode = new JSONObject();
            authenticationCode.put("id",activationCodeReq.getAuthenticationCodeId());
            reqMdes.put("authenticationMethod", authenticationCode);

            paymentAppInstanceId = activationCodeReq.getPaymentAppInstanceId();
            deviceDetail = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
            if (deviceDetail.isPresent() && !(this.env.getProperty("mdeshost")).equalsIgnoreCase("mtf")){
                urlHost = deviceDetail.get().getNfcCapable();
            }
            if (urlHost == null) {
                url = this.env.getProperty("mdesip") + this.env.getProperty("digitizationpath");
            }else {
                url = "https://"+urlHost + this.env.getProperty("digitizationpath");
            }

            //url = this.env.getProperty("mdesip")  +this.env.getProperty("digitizationpath");
            id = "requestActivationCode";
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqMdes.toString(),"POST",id);
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsRespMdes = new JSONObject(response);
            }
            if (jsRespMdes.has("errors")) {
                jsRespMdes.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                jsRespMdes.put("message",jsRespMdes.getString("errorDescription"));
            }
            else {
                jsRespMdes.put("responseCode",String.valueOf(HCEConstants.REASON_CODE7));
                jsRespMdes.put("reasonDescription","Token Activated Successfully");
            }

        }catch (HCEActionException activateHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", activateHCEactionException);
            throw activateHCEactionException;

        } catch (Exception activateException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", activateException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return JsonUtil.jsonToMap(jsRespMdes);
    }

    public Map searchTokens(SearchTokensReq searchTokensReq) {
        JSONObject searchTokenReq = null;
        String url = "";
        String id = "";
        String trId = "";
        String response;
        JSONObject jsRespMdes = new JSONObject();
        String requestId = "";
        //Check if the paymentAppInstanceId is valid or not
        if(!deviceDetailRepository.findByPaymentAppInstanceId(searchTokensReq.getPaymentAppInstanceId()).isPresent()) {
           throw new HCEActionException(HCEMessageCodes.getDeviceNotRegistered());
        }
        try {
            //call the master card searchTokens API
            searchTokenReq = new JSONObject();
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            searchTokenReq.put("requestId", requestId);
            searchTokenReq.put(PAYMENT_APP_INSTANCE_ID, searchTokensReq.getPaymentAppInstanceId());
            /*trId = this.env.getProperty("tokenrequestorid");
            searchTokenReq.put("tokenRequestorId",trId);
*/
            url = this.env.getProperty("mdesip")  +this.env.getProperty("digitizationpath");
            id = "searchTokens";
            ResponseEntity responseEntity =  hitMasterCardService.restfulServiceConsumerMasterCard(url,searchTokenReq.toString(),"POST",id);

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsRespMdes = new JSONObject(response);
            }
            if (jsRespMdes.has("errors")) {
                jsRespMdes.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                jsRespMdes.put("message",jsRespMdes.getString("errorDescription"));
            }
            else {
                jsRespMdes.put("reasonCode",String.valueOf(HCEConstants.REASON_CODE7));
                jsRespMdes.put("reasonDescription",HCEConstants.SUCCESS);
            }
        }catch (HCEActionException searchTokensHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensHCEactionException);
            throw searchTokensHCEactionException;

        } catch (Exception searchTokensException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return JsonUtil.jsonToMap(jsRespMdes);
    }

    public  Map getTokens(GetTokensRequest getTokensRequest) {
        JSONObject getTokenReq = null;
        String url = "";
        String id = "";
        String trId = "";
        String response;
        JSONObject jsRespMdes = new JSONObject();
        String requestId = "";
        //Check if the token unique reference is valid or not
        if(!cardDetailRepository.findByMasterTokenUniqueReference(getTokensRequest.getTokenUniqueReference()).isPresent())
        {
            throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
        }

        try {
            getTokenReq = new JSONObject();
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            getTokenReq.put("requestId", requestId);
            getTokenReq.put("tokenUniqueReference", getTokensRequest.getTokenUniqueReference());
            getTokenReq.put(PAYMENT_APP_INSTANCE_ID, getTokensRequest.getPaymentAppInstanceId());
            /*trId = this.env.getProperty("tokenrequestorid");
            searchTokenReq.put("tokenRequestorId",trId);
*/

            url = this.env.getProperty("mdesip")  +this.env.getProperty("digitizationpath");
            id = "getToken";
            ResponseEntity responseEntity =  hitMasterCardService.restfulServiceConsumerMasterCard(url,getTokenReq.toString(),"POST",id);

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsRespMdes = new JSONObject(response);
            }
            if (jsRespMdes.has("errors")) {
                jsRespMdes.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                //jsonResponse.put("message",jsonResponse.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));errorDescription
                jsRespMdes.put("message",jsRespMdes.getString("errorDescription"));
            }
            else
            {
                jsRespMdes.put("reasonCode",String.valueOf(HCEConstants.REASON_CODE7));
                jsRespMdes.put("reasonDescription",HCEConstants.SUCCESS);
            }
        }catch (HCEActionException getTokensHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", getTokensHCEactionException);
            throw getTokensHCEactionException;

        } catch (Exception getTokensException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", getTokensException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return JsonUtil.jsonToMap(jsRespMdes);
    }

    @Override
    public Map unregisterTds(UnregisterTdsReq unregisterTdsReq) {
        String tokenUniqueReference = unregisterTdsReq.getTokenUniqueReference();
        String paymentAppInstanceId = unregisterTdsReq.getPaymentAppInstanceId();
        String authenticationCode = null;
        JSONObject requestJson = new JSONObject();
        Optional<TransactionRegDetails> transactionDetails = null;
        TransactionRegDetails transactionRegDetails = new TransactionRegDetails();
        List<TransactionRegDetails> transactionDetailsList = null;
        int size = 0;
        ResponseEntity responseMdes = null;
        String response = null;
        JSONObject jsonResponse = new JSONObject();
        Map responseMap = null;
        String url = null;
        String id = null;
        try{
            transactionDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueReference);
            if (!transactionDetails.isPresent()) {
                LOGGER.error("invalid tokenUniqueReference ");
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }
            transactionRegDetails = transactionDetails.get();
            if (!isActive(transactionRegDetails)){
                LOGGER.error("This token has not been registered");
                throw new HCEActionException(HCEMessageCodes.getServiceFailed());
            }
            authenticationCode = transactionDetails.get().getAuthCode();
            requestJson.put("authenticationCode",authenticationCode);
            requestJson.put("tokenUniqueReference",tokenUniqueReference);
                /*transactionDetailsList = transactionRegDetailsRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
                if (!transactionDetailsList.isEmpty()) {
                    size = transactionDetailsList.size();
                }*/
            url = env.getProperty("mdesip") + env.getProperty("tdspath") + "/" + paymentAppInstanceId;
            id = "unregister";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST",id);
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                if (jsonResponse.has("errorCode")){
                    responseMap = JsonUtil.jsonToMap(jsonResponse);
                    responseMap.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                }else {
                    transactionRegDetails.setStatus(HCEConstants.INACTIVE);
                    transactionRegDetailsRepository.save(transactionRegDetails);
                /*if (tokenUniqueReference==null) {
                    for (int i=0 ; i<size;i++) {
                        transactionRegDetails = transactionDetailsList.get(i);
                        transactionRegDetails.setStatus(HCEConstants.INACTIVE);
                    }
                }*/
                    responseMap = JsonUtil.jsonToMap(jsonResponse);
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                }
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

        }catch(HCEActionException unregisterTdsHCEactionException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->unregisterTds",unregisterTdsHCEactionException);
            throw unregisterTdsHCEactionException;
        }catch(Exception unregisterTdsException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->unregisterTds", unregisterTdsException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    private boolean isActive(TransactionRegDetails transactionRegDetails){
        boolean isActive = false;
        String status = transactionRegDetails.getStatus();
        if (status != null){
            if (status.equals(HCEConstants.ACTIVE)){
                isActive =  true;
            }
        }
        return isActive;
    }
/*
    private Map<String,Object> validate(EnrollPanRequest enrollPanRequest,List<UserDetail> userDetails,List<DeviceInfo> deviceInfo) {
        Map<String,Object> result=new HashMap();
        if ((null==userDetails || userDetails.isEmpty()) || (null==deviceInfo || deviceInfo.isEmpty())) {
            result.put("message", "Invalid User please register");
            result.put("responseCode", "205");
            return result;
        }else if("userActivated".equals(userDetails.get(0).getUserstatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
            List<UserDetail> userDevice = userDetailRepository.findByClientDeviceId(enrollPanRequest.getClientDeviceID());
            if(null !=userDevice && !userDevice.isEmpty()) {
                for (int i = 0; i <userDetails.size(); i++){
                    if (!userDevice.get(i).getUserName().equals(userDetails.get(0).getUserName())) {
                        userDevice.get(i).setClientDeviceId("CD");
                        userDetailRepository.save(userDevice.get(i));
                    }
                }
            }
            userDetails.get(0).setClientDeviceId(enrollPanRequest.getClientDeviceID());
            userDetailRepository.save(userDetails.get(0));
            result.put("message", "Active User");
            result.put("responseCode", "200");
            return result;
        }else{
            result.put("message", "User not active");
            result.put("responseCode", "205");
            return result;
        }
    }*/

    @Override
    public Map getSystemHealth() {
        ResponseEntity responseMdes = null;
        Map responseMap = new HashMap();
        String url = null;
        String id = "";
        try {
            url =  env.getProperty("mdesip")  + env.getProperty("digitizationpath");
            id = "health";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url, null, "GET",id);
            if (responseMdes.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            } else {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

        } catch (HCEActionException getSystemHealthHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getSystemHealth", getSystemHealthHCEactionException);
            throw getSystemHealthHCEactionException;
        } catch (Exception getSystemHealthException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getSystemHealth", getSystemHealthException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public Object getPublicKeyCertificate() {
        ResponseEntity responseMdes = null;
        Object response = null;
        String url = null;
        try {
            url = env.getProperty("mdesip") + env.getProperty("mpamanagementPath");
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url, null, "GET", "pkCertificate");
            if (responseMdes == null)
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            if (responseMdes.hasBody()) {
                response = responseMdes.getBody();
            }
        } catch (HCEActionException getPublicKeyCertificateException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getPublicKeyCertificate", getPublicKeyCertificateException);
            throw getPublicKeyCertificateException;

        } catch (Exception getPublicKeyCertificateException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getPublicKeyCertificate", getPublicKeyCertificateException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
    }

    @Override
    public Map<String, Object> notifyTokenUpdated(NotifyTokenUpdatedReq notifyTokenUpdatedReq) {
        LOGGER.debug("Enter CardDetailServiceImpl ----------------------------------------------notifyTokenUpdated-------");
        EncryptedPayload encryptedPayload = new EncryptedPayload();
        HashMap notifyTokenUpdatedMap = null;
        String responseId = null;
        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        JSONObject requestJson = null;
        String strRequest = "";
        String rgk = "";
        try {
            //Decrypt notification Data
            rgk = CryptoUtils.privateKeydecryption(getPrivateKeyFromKeyStore(), notifyTokenUpdatedReq.getEncryptedPayload().getEncryptedKey());
            strRequest = CryptoUtils.AESEncryption(notifyTokenUpdatedReq.getEncryptedPayload().getEncryptedData(), rgk, Cipher.DECRYPT_MODE, notifyTokenUpdatedReq.getEncryptedPayload().getIv());
            requestJson = new JSONObject(strRequest);
            notifyTokenUpdatedMap = (HashMap)JsonUtil.jsonStringToHashMap(strRequest);
            //Prepare FCM Request
            rnsGenericRequest.setIdType(UniqueIdType.MDES);
            rnsGenericRequest.setRegistrationId(getRnsRegId(requestJson.getString("paymentAppInstanceId")));
            rnsGenericRequest.setRnsData(notifyTokenUpdatedMap);
            Map rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());
            rnsData.put("SUBTYPE","MDES_LCM");
            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority", "high");
            payloadObject.put("time_to_live", 2160000);
            //Send Remote Notification
            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());
            Gson gson = new Gson();
            String json = gson.toJson(response);
            responseId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            LOGGER.debug("CardDetailServiceImpl -> notifyTokenUpdated->Raw response from FCM server" + json);
            if (Integer.valueOf(response.getErrorCode()) != 200) {
                return ImmutableMap.of("errorCode", "720",
                        "errorDescription", "UNABLE_TO_DELIVER_MESSAGE");
            }
        } catch (HCEActionException notifyTokenUpdatedHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->notifyTokenUpdated", notifyTokenUpdatedHCEactionException);
            throw notifyTokenUpdatedHCEactionException;
        } catch (Exception notifyTokenUpdatedException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->notifyTokenUpdated", notifyTokenUpdatedException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return ImmutableMap.of("responseHost","nbkewallet.nbkpilot.com",
                "responseId", responseId);
    }

    @Override
    public Map<String, Object> getCustomerCareContact() {
        String customerCareContact = null;
        Map responseMap = new HashMap();
        try {
            customerCareContact = env.getProperty("customerCareContact");
            responseMap.put("customerCareContact",customerCareContact);
            responseMap.put(HCEConstants.RESPONSE_CODE,HCEMessageCodes.getSUCCESS());
            responseMap.put(HCEConstants.MESSAGE,"Transaction Success");
        } catch (HCEActionException getCustomerCareContactHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getCustomerCareContact", getCustomerCareContactHCEactionException);
            throw getCustomerCareContactHCEactionException;
        } catch (Exception getCustomerCareContactException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getCustomerCareContact", getCustomerCareContactException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public String getRnsRegId(String paymentAppInstanceId) {
        String rnsRegID = null;
        final Optional<DeviceInfo> deviceDetailsList = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
        if(deviceDetailsList.isPresent() ){
            final DeviceInfo deviceInfo = deviceDetailsList.get();
            rnsRegID = deviceInfo.getRnsRegistrationId();
        }
        return rnsRegID;
    }

    public  PrivateKey getPrivateKeyFromKeyStore() throws Exception{
        ResourceLoader resourceLoader = null;
        Resource resource = null;
        InputStream inputStream = null;
        String fileName = null;
        String password = null;
        String alias = "";
        try{
            //InputStream ins = DecryptPayload.class.getResourceAsStream("/keystore.
            //
            // jks");
            //fileName = env.getProperty("end.to.end.keystore.filename");
            fileName = env.getProperty("outboundtruststore");
            password = env.getProperty("outboundtruststorepass");
            alias = env.getProperty("outboundtruststorealias");
            resourceLoader = new FileSystemResourceLoader();
            resource = resourceLoader.getResource("classpath:"+fileName);
            inputStream  = resource.getInputStream();

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(inputStream, password.toCharArray());   //Keystore password
            KeyStore.PasswordProtection keyPassword =       //Key password
                    new KeyStore.PasswordProtection(password.toCharArray());

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPassword);

            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            return privateKey;
        }catch (Exception ex) {
            //LOGGER.error("Error in AESEncrypt getPrivateKeyFromKeyStore : " + ex.getMessage(), ex);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
        }

    }
}