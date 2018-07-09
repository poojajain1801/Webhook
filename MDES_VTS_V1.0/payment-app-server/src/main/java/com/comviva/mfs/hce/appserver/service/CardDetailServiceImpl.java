package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetAssetRequest;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.TransactionRegDetails;
import com.comviva.mfs.hce.appserver.model.UserDetail;
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
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerImplUtils;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerUtils;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RemoteNotification;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import com.visa.dmpd.token.JWTUtility;
import org.apache.http.annotation.Contract;
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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static jdk.nashorn.internal.objects.NativeRegExp.ignoreCase;

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

    private HttpRestHandlerUtils httpRestHandlerUtils = new HttpRestHandlerImplUtils();
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


    private AddCardResponse prepareDigitizeResponse(int reasonCode, String reasonDescription) {
        return new AddCardResponse(ImmutableMap.of(HCEConstants.REASON_CODE, Integer.toString(reasonCode), HCEConstants.REASON_DESCRIPTION, reasonDescription));
    }

    private AddCardResponse prepareDigitizeResponse(JSONObject digitizationRespMdes) {
        Map<String, Object> mapRespMdes = JsonUtil.jsonStringToHashMap(digitizationRespMdes.toString());
        return new AddCardResponse(mapRespMdes);
    }

    /**
     * Check device/Card eligibility for digitiazation.
     *
     * @param addCardParam Parameters for check card eligibility.
     * @return Eligibility Response
     */
    public Map<String, Object> checkDeviceEligibility(AddCardParm addCardParam) {
        Map<String, Object> mapResponse = null;
        String url = null;
        JSONObject checkDeviceEligibilityRequest = null;
        Optional<DeviceInfo> deviceInfoOptional = null;
        ResponseEntity responseEntity = null;
        JSONObject jsonResponse = null;
        Map eligibilityMap = null;
        Map applicableCardInfoMap = null;
        String response = null;
        try {

            // *************** Send Card Eligibility Check request to MDES ***************
            checkDeviceEligibilityRequest = new JSONObject();
            checkDeviceEligibilityRequest.put("tokenType", addCardParam.getTokenType());
            checkDeviceEligibilityRequest.put(PAYMENT_APP_INSTANCE_ID, addCardParam.getPaymentAppInstanceId());
            checkDeviceEligibilityRequest.put("paymentAppId", addCardParam.getPaymentAppId());
            checkDeviceEligibilityRequest.put("cardInfo", addCardParam.getCardInfo());
            checkDeviceEligibilityRequest.put("cardletId", addCardParam.getCardId());
            checkDeviceEligibilityRequest.put("consumerLanguage", addCardParam.getConsumerLanguage());
            // Call checkEligibility Api of MDES to check if the card is eligible for digitization.
            // String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes", checkDeviceEligibilityRequest);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("credentialspath")+"/checkEligibility";

            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, checkDeviceEligibilityRequest.toString(), "POST");

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
            }
            //Prepare Response
            jsonResponse = new JSONObject(response);
            eligibilityMap = ImmutableMap.of("value", jsonResponse.getJSONObject("eligibilityReceipt").getString("value"),
                    "validForMinutes", jsonResponse.getJSONObject("eligibilityReceipt").getInt("validForMinutes"));
            applicableCardInfoMap = ImmutableMap.of("isSecurityCodeApplicable", jsonResponse.getJSONObject("applicableCardInfo").getBoolean("isSecurityCodeApplicable"));
            if (jsonResponse.has("eligibilityReceipt")) {
                // Store the request and response in the DB for future use
                String serviceId = Long.toString(new SecureRandom().nextLong());
                mapResponse = new HashMap<>();
                //Build response
               /* Map mapResponse = new ImmutableMap.Builder()
                        .put("message", "Success")
                        .put("responseCode", "200")
                        .put("responseHost", jsonResponse.getString("responseHost"))
                        .put("responseId", jsonResponse.getString("responseId"))
                        .put("eligibilityReceipt", eligibilityMap)
                        .put("termsAndConditionsAssetId", jsonResponse.get("termsAndConditionsAssetId"))
                        .put("applicableCardInfo", applicableCardInfoMap)
                        .put("serviceId", serviceId)
                        .put(HCEConstants.REASON_CODE, Integer.toString(HCEConstants.REASON_CODE3))
                        .put(HCEConstants.REASON_DESCRIPTION, "Card is eligible for digitization").build();*/
                mapResponse.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getSUCCESS());
                mapResponse.put(HCEConstants.MESSAGE, hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                mapResponse.put("responseHost", jsonResponse.getString("responseHost"));
                mapResponse.put("responseId", jsonResponse.getString("responseId"));
                mapResponse.put("eligibilityReceipt", eligibilityMap);
                mapResponse.put("termsAndConditionsAssetId", jsonResponse.get("termsAndConditionsAssetId"));
                mapResponse.put("applicableCardInfo", applicableCardInfoMap);
                mapResponse.put("serviceId", serviceId);
                mapResponse.put(HCEConstants.REASON_CODE, Integer.toString(HCEConstants.REASON_CODE3));
                mapResponse.put(HCEConstants.REASON_DESCRIPTION, "Card is eligible for digitization");

                return mapResponse;
            } else {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        } catch (JSONException e) {
            LOGGER.error("Exception occured", e);
            throw new HCEActionException(HCEMessageCodes.getUnableToParseRequest());
        } catch (Exception e) {
            LOGGER.error("Exception occured", e);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
    }

    public Map<String, Object> addCard(DigitizationParam digitizationParam) {
        JSONObject digitizeReq = null;
        JSONObject cardInfo = null;
        JSONObject eligibilityReceiptValue = null;
        JSONObject decisioningData = null;
        JSONObject provisionRespMdes = null;
        String url = null;
        String response = null;
        Map responseMap = null;

        try {
        /*
            deviceInfoOptional = deviceDetailRepository.findByPaymentAppInstanceId(digitizationParam.getPaymentAppInstanceId());
            if (!deviceInfoOptional.isPresent()) {
                //return prepareDigitizeResponse(HCEConstants.REASON_CODE1, "Invalid Payment App Instance Id");
                throw new HCEActionException(HCEMessageCodes.getInvalidPaymentappInstanceId());
            }


            eligibilityRequest = new String(serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getRequest());
            eligibilityResponse = new String(serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getResponse());


            jsonRequest = new JSONObject(eligibilityRequest);
            jsonResponse = new JSONObject(eligibilityResponse);
            */

            // ************* Prepare request to MDES for Digitize api *************
            digitizeReq = new JSONObject();
            digitizeReq.put("responseHost", "site1.your-server.com");
            digitizeReq.put("requestId", "123456");
            digitizeReq.put(PAYMENT_APP_INSTANCE_ID, digitizationParam.getPaymentAppInstanceId());
            digitizeReq.put("serviceId", "ServiceIdCheckEligibility");
            digitizeReq.put("termsAndConditionsAssetId", digitizationParam.getTermAndConditionAssetId());
            digitizeReq.put("termsAndConditionsAcceptedTimestamp", "2014-07-04T12:08:56.123-07:00");
            digitizeReq.put("tokenizationAuthenticationValue", "RHVtbXkgYmFzZSA2NCBkYXRhIC0gdGhpcyBpcyBub3QgYSByZWFsIFRBViBleGFtcGxl");

            eligibilityReceiptValue = new JSONObject();
            eligibilityReceiptValue.put("value", "value");
            digitizeReq.put("eligibilityReceipt", eligibilityReceiptValue);

            cardInfo = new JSONObject();
            cardInfo.put("encryptedData", "4545433044323232363739304532433610DE1D1461475BEB6D815F31764DDC20298BD779FBE37EE5AB3CBDA9F9825E1DDE321469537FE461E824AA55BA67BF6A");
            cardInfo.put("publicKeyFingerprint", "4c4ead5927f0df8117f178eea9308daa58e27c2b");
            cardInfo.put("encryptedKey", "A1B2C3D4E5F6112233445566");
            cardInfo.put("oaepHashingAlgorithm", "SHA512");
            digitizeReq.put("cardInfo", cardInfo);

            decisioningData = new JSONObject();
            decisioningData.put("recommendation", "REQUIRE_ADDITIONAL_AUTHENTICATION");
            decisioningData.put("recommendationAlgorithmVersion", "01");
            decisioningData.put("deviceScore", "1");
            decisioningData.put("accountScore", "1");
            digitizeReq.put("decisioningData", decisioningData);
            // String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/addCard", digitizeReq);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + "/addCard";
            ResponseEntity responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, digitizeReq.toString(), "POST");

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                provisionRespMdes = new JSONObject(response);
            }



            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                /*
                JSONObject mdesResp = provisionRespMdes.getJSONObject("response");
                 cardDetails = new CardDetails();
                //--madan cardDetails.setUserName(deviceDetailRepository.findByPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId")).get().getUserName());
                cardDetails.setMasterPaymentAppInstanceId(jsonRequest.getString(PAYMENT_APP_INSTANCE_ID));
                cardDetails.setMasterTokenUniqueReference(provisionRespMdes.getString("tokenUniqueReference"));
                cardDetails.setPanUniqueReference(provisionRespMdes.getString("panUniqueReference"));
                cardDetails.setMasterTokenInfo(provisionRespMdes.getJSONObject("tokenInfo").toString());
                cardDetails.setStatus("NEW");
                cardDetailRepository.save(cardDetails);
                return JsonUtil.jsonStringToHashMap(provisionRespMdes.toString());
                */
                responseMap = JsonUtil.jsonToMap(provisionRespMdes);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));


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
        return responseMap;
    }

    @Override
    public Map<String, Object> tokenize(TokenizeRequest tokenizeRequest) {
        return null;
    }

    /**
     * Fetches getAssetRequest.
     * <p>
     * //* @param getAssetRequest  object of the AssetPojo class.
     *
     * @return GetAssetRequest data
     */
    public Map getAsset(GetAssetRequest getAssetRequest) {
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        String assetID = getAssetRequest.getAssetId();
        JSONObject jsonReq = new JSONObject() ;
        ResponseEntity responseMdes = null;
        String response = null;
        JSONObject jsonResponse = null ;
        Map responseMap = null;
        String url = null ;
        try{
            jsonReq.put("assetID" , assetID);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath") + "/asset";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,jsonReq.toString(),"GET");
            if (responseMdes.hasBody())
            {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7)
            {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        }catch (HCEActionException getAssetHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getAsset", getAssetHCEactionException);
            throw getAssetHCEactionException;

        } catch (Exception getAssetException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getAsset", getAssetException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> activate(ActivateReq activateReq) {
        String paymentAppInstanceId = activateReq.getPaymentAppInstanceId();
        String tokenUniqueReference = activateReq.getTokenUniqueReference();
        String authenticationCode = activateReq.getAuthenticationCode();
        String tokenizationAuthenticationValue = activateReq.getTokenizationAuthenticationValue();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        JSONObject reqMdes = new JSONObject();
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = null;
        String response = null;
        Map responseMap = null;
        String url = null ;
        try {
            reqMdes.put(PAYMENT_APP_INSTANCE_ID, paymentAppInstanceId);
            reqMdes.put("tokenUniqueReference", tokenUniqueReference);
            reqMdes.put("authenticationCode", authenticationCode);
            reqMdes.put("tokenizationAuthenticationValue", tokenizationAuthenticationValue);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath") + "/activate" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqMdes.toString(),"POST");

            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

        }catch (HCEActionException activateHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", activateHCEactionException);
            throw activateHCEactionException;

        } catch (Exception activateException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", activateException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;
    }

    public Map<String, Object> enrollPan (EnrollPanRequest enrollPanRequest) {
        List<DeviceInfo> deviceInfoList ;
        ObjectMapper objectMapper ;
        Map<String, Object> map ;
        JSONObject jsonencPaymentInstrument ;
        String encPaymentInstrument ;
        JSONObject jsonResponse = null;
        ResponseEntity responseEntity ;
        Map responseMap ;
        String response = "";
        String vPanEnrollmentId = null;
      //  List<VisaCardDetails> visaCardDetailList = null;
        String clientWalletAccountId ;
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
                        responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                        responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
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
        Map response= new LinkedHashMap();
        JSONObject jsonResponse  = null;
        String request = "";
        ResponseEntity responseEntity =null;
        String strResponse=null;
        //JSONObject requestJson = null;
        String url = "";
        String resourcePath ="vts/panEnrollments/"+getCardMetadataRequest.getVpanEnrollmentID();
        //https://sandbox.digital.visa.com/vts/panEnrollments/{vPanEnrollmentID}?apiKey=key&platformType=platform_type
        url =  env.getProperty("visaBaseUrlSandbox")+"/vts/panEnrollments/"+getCardMetadataRequest.getVpanEnrollmentID()+"?apiKey="+env.getProperty("apiKey");

        try {
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");

            if (responseEntity.hasBody())
            {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                response = JsonUtil.jsonStringToHashMap(strResponse);
                response.put("responseCode", HCEMessageCodes.getSUCCESS());
                response.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                LOGGER.debug("Exit CardDetailsServiceImpl->getCardMetadata");
                return response;
            } else {
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
        Map response= new LinkedHashMap();
        JSONObject jsonResponse  = null;
        String request = "";
        String strResponse = null;
        ResponseEntity responseEntity =null;
        Map responseMap = null;
        String url = "";
        String resourcePath ="vts/cps/getContent/"+getContentRequest.getGuid();
        url =  env.getProperty("visaBaseUrlSandbox")+"/vts/cps/getContent/"+getContentRequest.getGuid()+"?apiKey="+env.getProperty("apiKey");

        try {
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");
            if(responseEntity.hasBody())
            {
               strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }

            if (responseEntity.getStatusCode().value() == 200 || responseEntity.getStatusCode().value() == 201) {
             //   response = JsonUtil.jsonStringToHashMap(strResponse);
                responseMap = JsonUtil.jsonStringToHashMap(strResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                LOGGER.debug("Exit CardDetailsServiceImpl->getContent");
                return responseMap;
            } else {
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


    public Map<String, Object> getPANData(GetPANDataRequest getPANDataRequest) {
       List<UserDetail>  userDetails = userDetailRepository.findByUserIdAndStatus(getPANDataRequest.getUserId(),HCEConstants.ACTIVE);
        HashMap<String, Object> result = null;
       if(userDetails!=null && !userDetails.isEmpty()){

           ObjectMapper objectMapper = new ObjectMapper();
           HitVisaServices hitVisaServices = new HitVisaServices(env);
           String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
           //  try {
           // response = hitVisaServices.restfulServiceConsumerVisaGet("url","");
           // } catch (JsonProcessingException e) {
           //   e.printStackTrace();
           //}

           try {

               result = new ObjectMapper().readValue(response, HashMap.class);
           } catch (IOException e) {
               LOGGER.error("Exception Occured" +e);
           }

       }else
       {
           Map<String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
           return response;
       }
        // *************** EnrollPan request to VTS ***************

        return result;
    }

    @Override
    public Map notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq) {
        String tokenUniqueReference = notifyTransactionDetailsReq.getTokenUniqueReference();
        String registrationCode2 = notifyTransactionDetailsReq.getRegistrationCode2();
        String tdsUrl = notifyTransactionDetailsReq.getTdsUrl();
        String paymentAppInstanceId = notifyTransactionDetailsReq.getPaymentAppInstanceId();
        JSONObject reqJson = new JSONObject();
        String url = null;
        ResponseEntity responseMdes = null;
        String response = null ;
        JSONObject jsonResponse = null;
        Map responseMap = null;
        try{

            reqJson.put("tokenUniqueReference", tokenUniqueReference);
            reqJson.put("tdsUrl", tdsUrl);
            reqJson.put(PAYMENT_APP_INSTANCE_ID, paymentAppInstanceId);
            reqJson.put("registrationCode2",registrationCode2);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("tdspath") + "/notifyTransactionDetails" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqJson.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
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

    public Map getRegistrationCode(GetRegistrationCodeReq getRegistrationCodeReq) {
        String tokenUniqueRef = getRegistrationCodeReq.getTokenUniqueReference();
        JSONObject reqJson = new JSONObject();
        String response = null;
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = null;
        Map responseMap = null;
        String url = null ;
        try{
            reqJson.put("tokenUniqueReference", tokenUniqueRef);
            // Invoke Get Registration Code API of the MDES
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("tdspath") + "/getRegistrationCode" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqJson.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

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

    public Map registerWithTDS(TDSRegistrationReq tdsRegistrationReq) {
        String tokenUniqueRef = tdsRegistrationReq.getTokenUniqueReference();
        String registrationHash = tdsRegistrationReq.getRegistrationHash();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        ResponseEntity responseMdes = null;
        String response = null ;
        JSONObject jsonResponse = null ;
        JSONObject requestJson = null;
        Map responseMap = null;
        String url = null;

        try{
            // Get the regcode1 and regcode2 from the DB and generate registrationHash
        //    transactionRegDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).get();

            // Create request
            requestJson = new JSONObject();
            requestJson.put("tokenUniqueReference", tokenUniqueRef);
            requestJson.put("registrationHash", registrationHash);

            // Invoke Register API of the MDES
            url =  env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("tdspath") + "/registerWithTDS" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

        }catch (HCEActionException registerWithTDSHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->registerWithTDS", registerWithTDSHCEactionException);
            throw registerWithTDSHCEactionException;
        } catch (Exception registerWithTDSException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->registerWithTDS",registerWithTDSException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;
    }

    public Map getTransactionHistory(GetTransactionHistoryReq getTransactionHistoryReq) {
        String tokenUniqueRef = getTransactionHistoryReq.getTokenUniqueReference();
        String authenticationCode = getTransactionHistoryReq.getAuthenticationCode();
        String lastUpdatedTag = getTransactionHistoryReq.getLastUpdatedTag();
        JSONObject reqMap = new JSONObject();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        String response = null;
        JSONObject jsonResponse = null;
        ResponseEntity responseEntity = null;
        Map responseMap = null;
        String url = null;
        try {
            reqMap.put("tokenUniqueReference", tokenUniqueRef);
            reqMap.put("authenticationCode", authenticationCode);
            reqMap.put("lastUpdatedTag", lastUpdatedTag);

            url =  env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("tdspath") + "/getTransactions" ;
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqMap.toString(),"POST");
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseEntity.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

        } catch (HCEActionException getTransactionHistoryHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getTransactionHistory", getTransactionHistoryHCEactionException);
            throw getTransactionHistoryHCEactionException;
        } catch (Exception getTransactionHistoryException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->getTransactionHistory", getTransactionHistoryException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public Map performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq) {
        String paymentAppInstanceID = lifeCycleManagementReq.getPaymentAppInstanceId();
        List<String> tokenUniqueRefList = lifeCycleManagementReq.getTokenUniqueReferences();
        String causedBy = lifeCycleManagementReq.getCausedBy();
        String reason = lifeCycleManagementReq.getReason();
        String reasonCode = lifeCycleManagementReq.getReasonCode();
        String operation = lifeCycleManagementReq.getOperation();
        JSONObject jsonResponse = null;
        String response = null;
        ResponseEntity responseEntity = null ;
        JSONObject lifeCycleManagementReqJson = new JSONObject();
        Map responseMap = null;
        String url = null;
        try {
            //Prepare req
            lifeCycleManagementReqJson.put(PAYMENT_APP_INSTANCE_ID, paymentAppInstanceID);
            lifeCycleManagementReqJson.put("tokenUniqueReferences", tokenUniqueRefList);
            lifeCycleManagementReqJson.put("causedBy", causedBy);
            lifeCycleManagementReqJson.put(HCEConstants.REASON_CODE, reasonCode);
            lifeCycleManagementReqJson.put("reason", reason);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath");
            //Call mastercard //{DELETE,SUSPEND,UNSUSPEND}
            switch (operation) {
                case "DELETE":
                    responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard( url + "/delete",lifeCycleManagementReqJson.toString(),"POST");
                    break;
                case "SUSPEND":
                    responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url + "/suspend",lifeCycleManagementReqJson.toString(),"POST");
                    break;
                case "UNSUSPEND":
                    responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url + "/unsuspend",lifeCycleManagementReqJson.toString(),"POST");
                    break;
                default:
                    throw new HCEActionException(HCEMessageCodes.getInvalidOperation());
            }

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseEntity.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        }catch(HCEActionException lifeCycleManagementHCEactionException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->lifeCycleManagement", lifeCycleManagementHCEactionException);
            throw lifeCycleManagementHCEactionException;
        }catch(Exception lifeCycleManagementException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->lifeCycleManagement", lifeCycleManagementException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq) {
        String paymentAppInstanceId = activationCodeReq.getPaymentAppInstanceId();
        String tokenUniqueRef = activationCodeReq.getTokenUniqueReference();
        AuthenticationMethod authenticationMethod = new AuthenticationMethod();
        JSONObject reqMdes = new JSONObject() ;
        ResponseEntity responseMdes = null;
        HitMasterCardService hitMasterCardService = new HitMasterCardService() ;
        String response = null;
        Map responseMap = null;
        JSONObject jsonResponse = null ;
        String url = null;
        try {
        // Prepare Request Activation Code and call MDES
        reqMdes.put("paymentAppInstanceId", paymentAppInstanceId);
        reqMdes.put("tokenUniqueReference", tokenUniqueRef );
        reqMdes.put("authenticationMethod" , authenticationMethod);
        url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath") + "/requestActivationCode" ;
        responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqMdes.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        } catch (HCEActionException activationCodeHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activationCode", activationCodeHCEactionException);
            throw activationCodeHCEactionException;
        } catch (Exception activationCodeException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activationCode", activationCodeException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;

    }

    @Override
    public Map unregisterTds(UnregisterTdsReq unregisterTdsReq) {
        String tokenUniqueReference = unregisterTdsReq.getTokenUniqueReference();
        String authenticationCode = unregisterTdsReq.getAuthenticationCode();
        JSONObject requestJson = new JSONObject();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        ResponseEntity responseMdes = null;
        String response = null;
        JSONObject jsonResponse = null;
        Map responseMap = null;
        String url = null;
        try{
            requestJson.put("tokenUniqueReference",tokenUniqueReference);
            requestJson.put("authenticationCode",authenticationCode);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("tdspath") + "/unregister" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
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
    public  Map getTokens(GetTokensRequest getTokensRequest) {
        String paymentAppInstanceId = getTokensRequest.getPaymentAppInstanceId();
        String tokenUniqueReference = getTokensRequest.getTokenUniqueReference();
        Boolean includeTokenDetail = getTokensRequest.getIncludeTokenDetail();
        JSONObject requestJson = new JSONObject();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        ResponseEntity responseMdes = null;
        String response = null;
        Map responseMap = null;
        JSONObject jsonResponse = null;
        String url = null;

        try {
            requestJson.put("paymentAppInstanceId",paymentAppInstanceId);
            requestJson.put("tokenUniqueReference",tokenUniqueReference);
            requestJson.put("includeTokenDetail",includeTokenDetail);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath") +"/getToken";
            //call master card get token API.
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        }catch(HCEActionException getTokensHCEactionException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->getTokens",getTokensHCEactionException);
            throw getTokensHCEactionException;
        }catch(Exception getTokensException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->getTokens", getTokensException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;
    }

    public Map searchTokens(SearchTokensReq searchTokensReq) {
        String paymentAppInstanceId = searchTokensReq.getPaymentAppInstanceId();
        CardInfo cardInfo = new CardInfo();
        String tokenRequestorId = searchTokensReq.getTokenRequestorId();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        JSONObject searchTokensReqJson = new JSONObject();
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = null;
        Map responseMap = null ;
        String response = null;
        String url = null;
        try {
            searchTokensReqJson.put(PAYMENT_APP_INSTANCE_ID,paymentAppInstanceId);
            searchTokensReqJson.put("cardInfo" , cardInfo);
            searchTokensReqJson.put("tokenRequestorId",tokenRequestorId);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath") +"/searchTokens" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url, searchTokensReqJson.toString(),"POST");
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));

            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }

        }catch(HCEActionException searchTokensHCEactionException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens",searchTokensHCEactionException);
            throw searchTokensHCEactionException;
        }catch(Exception searchTokensException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;
    }

    public Map getSystemHealth() {
        ResponseEntity responseMdes = null;
        JSONObject jsonResponse = null;
        String response = null;
        Map responseMap = null;
        String url = null;
        try {
            url =  env.getProperty("mdesip")  + env.getProperty("digitizationpath") + "/health";
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url, null, "GET");
            if (responseMdes == null)
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if (responseMdes.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
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
}