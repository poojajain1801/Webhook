package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.mapper.vts.RequestId;
import com.comviva.mfs.hce.appserver.model.*;
import com.comviva.mfs.hce.appserver.repository.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.*;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RemoteNotification;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import com.comviva.mfs.hce.appserver.util.vts.CreateChannelSecurityContext;
import com.comviva.mfs.hce.appserver.util.vts.ValidateUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.visa.dmpd.token.JWTUtility;
import org.apache.commons.codec.binary.*;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class CardDetailServiceImpl implements CardDetailService {
    //Repository for the card details
    private final CardDetailRepository cardDetailRepository;
    //Repository for the visa card details
    private final VisaCardDetailRepository visaCardDetailRepository;
    //Repository for the user details
    private final UserDetailService userDetailService;

    //Repository for the service data
    private final ServiceDataRepository serviceDataRepository;

    private final DeviceDetailRepository deviceDetailRepository;
    private final TransactionRegDetailsRepository transactionRegDetailsRepository;
    private final UserDetailRepository userDetailRepository;
    private final HCEControllerSupport hceControllerSupport;


    @Autowired
    private Environment env;

    ServiceData serviceData;

    @Autowired
    RemoteNotificationService remoteNotificationService;

    HttpRestHandlerUtils httpRestHandlerUtils = new HttpRestHandlerImplUtils();
    private static final Logger LOGGER = LoggerFactory.getLogger(CardDetailServiceImpl.class);


    @Autowired
    public CardDetailServiceImpl(CardDetailRepository cardDetailRepository,
                                 UserDetailService userDetailService,
                                 ServiceDataRepository serviceDataRepository,
                                 DeviceDetailRepository deviceDetailRepository,
                                 VisaCardDetailRepository visaCardDetailRepository,
                                 TransactionRegDetailsRepository transactionRegDetailsRepository,
                                 UserDetailRepository userDetailRepository,
                                 HCEControllerSupport hceControllerSupport) {
        this.cardDetailRepository = cardDetailRepository;
        this.userDetailService = userDetailService;
        this.serviceDataRepository = serviceDataRepository;
        this.deviceDetailRepository = deviceDetailRepository;
        this.visaCardDetailRepository = visaCardDetailRepository;
        this.transactionRegDetailsRepository = transactionRegDetailsRepository;
        this.userDetailRepository = userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
    }


    private AddCardResponse prepareDigitizeResponse(int reasonCode, String reasonDescription) {
        return new AddCardResponse(ImmutableMap.of("reasonCode", Integer.toString(reasonCode), "reasonDescription", reasonDescription));
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
    public AddCardResponse checkDeviceEligibility(AddCardParm addCardParam) {
        try {
            Optional<DeviceInfo> deviceInfoOptional = deviceDetailRepository.findByPaymentAppInstanceId(addCardParam.getPaymentAppInstanceId());
            if(!deviceInfoOptional.isPresent()) {
                return prepareDigitizeResponse(220, "Invalid Payment App Instance Id");
            }

            // Only token type CLOUD is supported
            if (!addCardParam.getTokenType().equalsIgnoreCase("CLOUD")) {
                return prepareDigitizeResponse(211, "Token type is not supported");
            }

            // *************** Send Card Eligibility Check request to MDES ***************
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("tokenType", addCardParam.getTokenType());
            map.add("paymentAppInstanceId", addCardParam.getPaymentAppInstanceId());
            map.add("paymentAppId", addCardParam.getPaymentAppId());
            map.add("cardInfo", addCardParam.getCardInfo());
            map.add("cardletId", addCardParam.getCardId());
            map.add("consumerLanguage", addCardParam.getConsumerLanguage());
            //Store the service id and the request in the service DB for API stitching
            JSONObject requestJson = new JSONObject(addCardParam);
            // Call checkEligibility Api of MDES to check if the card is eligible for digitization.
            String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes", map);

            //Prepare Response
            JSONObject jsonResponse = new JSONObject(response);
            Map eligibilityMap = ImmutableMap.of("value", jsonResponse.getJSONObject("eligibilityReceipt").getString("value"),
                    "validForMinutes", jsonResponse.getJSONObject("eligibilityReceipt").getInt("validForMinutes"));
            Map applicableCardInfoMap = ImmutableMap.of("isSecurityCodeApplicable", jsonResponse.getJSONObject("applicableCardInfo").getBoolean("isSecurityCodeApplicable"));
            if (jsonResponse.has("eligibilityReceipt")) {
                // Store the request and response in the DB for future use
                String serviceId = Long.toString(new Random().nextLong());

               //--madan String userName = deviceInfoOptional.get().getUserName();
                serviceData = serviceDataRepository.save(new ServiceData(null,  serviceId, requestJson.toString().getBytes(), response.getBytes()));

                //Build response
                Map mapResponse = new ImmutableMap.Builder()
                        .put("message", "Success")
                        .put("responseCode", "200")
                        .put("responseHost", jsonResponse.getString("responseHost"))
                        .put("responseId", jsonResponse.getString("responseId"))
                        .put("eligibilityReceipt", eligibilityMap)
                        .put("termsAndConditionsAssetId", jsonResponse.get("termsAndConditionsAssetId"))
                        .put("applicableCardInfo", applicableCardInfoMap)
                        .put("serviceId", serviceId)
                        .put("reasonCode", Integer.toString(200))
                        .put("reasonDescription", "Card is eligible for digitization").build();
                return new AddCardResponse(mapResponse);
            } else {
                return prepareDigitizeResponse(212, "Card not eligible for digitization");
            }
        } catch (JSONException e) {
            return prepareDigitizeResponse(230, "Please check request : JSONException");
        } catch (Exception e) {
            return prepareDigitizeResponse(231, "Some error on server");
        }
    }

    public AddCardResponse addCard(DigitizationParam digitizationParam) {
        Optional<DeviceInfo> deviceInfoOptional = deviceDetailRepository.findByPaymentAppInstanceId(digitizationParam.getPaymentAppInstanceId());
        if (!deviceInfoOptional.isPresent()) {
            return prepareDigitizeResponse(220, "Invalid Payment App Instance Id");
        }

        if (!serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).isPresent()) {
            return prepareDigitizeResponse(220, "Card is not eligible for the digitization service");
        }


        String eligibilityRequest = new String(serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getRequest());
        String eligibilityResponse =new String (serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getResponse());



        JSONObject jsonRequest = new JSONObject(eligibilityRequest);
        JSONObject jsonResponse = new JSONObject(eligibilityResponse);

        // ************* Prepare request to MDES for Digitize api *************
        MultiValueMap<String, Object> digitizeReq = new LinkedMultiValueMap<>();
        digitizeReq.add("responseHost", "site1.your-server.com");
        digitizeReq.add("requestId", "123456");
        digitizeReq.add("paymentAppInstanceId", jsonRequest.getString("paymentAppInstanceId"));
        digitizeReq.add("serviceId", "ServiceIdCheckEligibility");
        digitizeReq.add("termsAndConditionsAssetId", jsonResponse.getString("termsAndConditionsAssetId"));
        digitizeReq.add("termsAndConditionsAcceptedTimestamp", "2014-07-04T12:08:56.123-07:00");
        digitizeReq.add("tokenizationAuthenticationValue", "RHVtbXkgYmFzZSA2NCBkYXRhIC0gdGhpcyBpcyBub3QgYSByZWFsIFRBViBleGFtcGxl");

        MultiValueMap<String, Object> eligibilityReceiptValue = new LinkedMultiValueMap<>();
        eligibilityReceiptValue.add("value", jsonResponse.getJSONObject("eligibilityReceipt").getString("value"));
        digitizeReq.add("eligibilityReceipt", eligibilityReceiptValue);

        MultiValueMap<String, Object> cardInfo = new LinkedMultiValueMap<>();
        cardInfo.add("encryptedData", "4545433044323232363739304532433610DE1D1461475BEB6D815F31764DDC20298BD779FBE37EE5AB3CBDA9F9825E1DDE321469537FE461E824AA55BA67BF6A");
        cardInfo.add("publicKeyFingerprint", "4c4ead5927f0df8117f178eea9308daa58e27c2b");
        cardInfo.add("encryptedKey", "A1B2C3D4E5F6112233445566");
        cardInfo.add("oaepHashingAlgorithm", "SHA512");
        digitizeReq.add("cardInfo", cardInfo);

        MultiValueMap<String, Object> decisioningData = new LinkedMultiValueMap<>();
        decisioningData.add("recommendation", "REQUIRE_ADDITIONAL_AUTHENTICATION");
        decisioningData.add("recommendationAlgorithmVersion", "01");
        decisioningData.add("deviceScore", "1");
        decisioningData.add("accountScore", "1");
        digitizeReq.add("decisioningData", decisioningData);
        String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/addCard", digitizeReq);
        JSONObject provisionRespMdes = new JSONObject(response);
        if (Integer.valueOf(provisionRespMdes.getString("reasonCode")) == 200) {
            //JSONObject mdesResp = provisionRespMdes.getJSONObject("response");
            CardDetails cardDetails = new CardDetails();
           //--madan cardDetails.setUserName(deviceDetailRepository.findByPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId")).get().getUserName());
            cardDetails.setPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId"));
            cardDetails.setTokenUniqueReference(provisionRespMdes.getString("tokenUniqueReference"));
            cardDetails.setPanUniqueReference(provisionRespMdes.getString("panUniqueReference"));
            cardDetails.setTokenInfo(provisionRespMdes.getJSONObject("tokenInfo").toString());
            cardDetails.setTokenStatus("NEW");
            cardDetailRepository.save(cardDetails);
            return prepareDigitizeResponse(provisionRespMdes);
        } else {
            return prepareDigitizeResponse(Integer.valueOf(provisionRespMdes.getString("reasonCode")), provisionRespMdes.getString("reasonDescription"));
        }
    }

    /**
     * Fetches asset.
     *
     * @param assetID asset Id of the asset requested.
     * @return Asset data
     */
    public Asset getAsset(String assetID) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject resp = new JSONObject(restTemplate.getForObject(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/digitization/1/0/asset?assetId=" + assetID, String.class));

        Map<String, Object> respMap;
        if (resp.has("mediaContents")) {
            JSONArray jArrMediaContent = resp.getJSONArray("mediaContents");
            JSONObject assetComponent;
            String type;
            ArrayList<Map> listOfAssets = new ArrayList<>();

            for (int i = 0; i < jArrMediaContent.length(); i++) {
                assetComponent = jArrMediaContent.getJSONObject(i);
                type = assetComponent.getString("type");

                if (type.equalsIgnoreCase("text/plain")) {
                    listOfAssets.add(ImmutableMap.of("type", type, "data", assetComponent.getString("data")));
                } else if (type.contains("image")) {
                    listOfAssets.add(ImmutableMap.of("type", type, "height", assetComponent.getString("height"), "width", assetComponent.getString("width"), "data", assetComponent.getString("type")));
                }
            }
            respMap = ImmutableMap.of("mediaContents", listOfAssets, "reasonCode", "200", "reasonDescription", "Successful");
        } else {
            respMap = ImmutableMap.of("reasonCode", resp.getString("reasonCode"), "reasonDescription", resp.getString("reasonDescription"));
        }
        return new Asset(respMap);
    }

    @Override
    public ActivateResp activate(ActivateReq activateReq) {
        MultiValueMap<String, String> reqMdes = new LinkedMultiValueMap<>();
        reqMdes.add("responseHost", "com.mahindracomviva.payAppServer");
        reqMdes.add("requestId", "123456");
        reqMdes.add("paymentAppInstanceId", activateReq.getPaymentAppInstanceId());
        reqMdes.add("tokenUniqueReference", activateReq.getTokenUniqueReference());
        reqMdes.add("authenticationCode", "1234");

        String resMdes = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/digitization/1/0/activate", reqMdes);
        JSONObject jsRespMdes = new JSONObject(resMdes);

        ActivateResp activateResp = new ActivateResp();
        activateResp.setResponseHost("com.mahindracomviva.payappserver");
        activateResp.setResponseId("1234567");
        activateResp.setResult(jsRespMdes.getString("result"));
        activateResp.setReasonCode("200");
        activateResp.setReasonDescription("Token Activated Successfully");

        return activateResp;
    }

    public Map<String, Object> enrollPan (EnrollPanRequest enrollPanRequest) {
        LOGGER.debug("Inside CardDetailServiceImpl->enrollPan");
       //TODO:Input Validation
        Map<String,Object> response1=new HashMap();
        try {
            List<UserDetail> userDetails = userDetailRepository.find(enrollPanRequest.getUserId());
            List<DeviceInfo> deviceInfo = deviceDetailRepository.find(enrollPanRequest.getClientDeviceID());
            ValidateUser validateUser = new ValidateUser(userDetailRepository);
            response1 = validateUser.validate(enrollPanRequest.getClientDeviceID(), userDetails, deviceInfo);
            if (!response1.get("responseCode").equals("200")) {
                return response1;
            }
            // *************** EnrollPan request to VTS ***************
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<>();
            map.put("clientAppID", env.getProperty("clientAppID"));
            map.put("clientWalletAccountID", enrollPanRequest.getClientWalletAccountId());
            //map.put("clientDeviceID", enrollPanRequest.getClientDeviceID());
            map.put("locale", enrollPanRequest.getLocale());
            map.put("panSource", enrollPanRequest.getPanSource());
            JSONObject jsonencPaymentInstrument = new JSONObject(enrollPanRequest.getEncPaymentInstrument());
            String encPaymentInstrument = JWTUtility.createSharedSecretJwe(jsonencPaymentInstrument.toString(), env.getProperty("apiKey"), env.getProperty("sharedSecret"));
            map.put("encPaymentInstrument", encPaymentInstrument);
            map.put("consumerEntryMode", enrollPanRequest.getConsumerEntryMode());
            HitVisaServices hitVisaServices = new HitVisaServices(env);
            JSONObject jsonResponse = null;
            ResponseEntity responseEntity = null;
            Map responseMap = new LinkedHashMap();
            String response = "";

            responseEntity = hitVisaServices.restfulServiceConsumerVisa(env.getProperty("visaBaseUrlSandbox") + "/vts/panEnrollments?apiKey=" + env.getProperty("apiKey"), objectMapper.writeValueAsString(map), "vts/panEnrollments", "POST");

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
            }

            if (responseEntity.getStatusCode().value() == 200 || responseEntity.getStatusCode().value() == 201) {
                //put card details in db
                //response = String.valueOf(responseEntity.getBody());
                //jsonResponse = new JSONObject(response);
                //visaCardDetails=first query
                String vPanEnrollmentId = jsonResponse.getString("vPanEnrollmentID");

                List<VisaCardDetails> visaCardDetailList = visaCardDetailRepository.findByVPanEnrollmentId(vPanEnrollmentId);

                VisaCardDetails visaCardDetails = null;
                if(visaCardDetailList!=null && !visaCardDetailList.isEmpty()){
                    visaCardDetails = visaCardDetailList.get(0);
                }else{

                    visaCardDetails = new VisaCardDetails();
                }
                visaCardDetails.setUserName(enrollPanRequest.getUserId());
                visaCardDetails.setCardnumbersuffix(jsonResponse.getJSONObject("paymentInstrument").getString("last4"));
                visaCardDetails.setvPanEnrollmentId(vPanEnrollmentId);
                visaCardDetails.setStatus("Y");
                visaCardDetailRepository.save(visaCardDetails);
                // responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.SUCCESS);
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.SUCCESS));
                LOGGER.debug("Exit CardDetailServiceImpl->enrollPan");
                return responseMap;
            } else {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit CardDetailServiceImpl->enrollPan");
                return errorMap;
            }
        }catch (Exception e)
        {
            LOGGER.debug("Exception occurred in CardDetailServiceImpl->enrollPan");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }


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
            if (responseEntity.getStatusCode().value() == 200) {
                response = JsonUtil.jsonStringToHashMap(strResponse);
                LOGGER.debug("Exit CardDetailsServiceImpl->getCardMetadata");
                return response;
            } else {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit CardDetailsServiceImpl->getCardMetadata");
                return errorMap;
            }


        }catch (Exception e)
        {
            LOGGER.debug("Exception occurred in CardDetailsServiceImpl->getCardMetadata");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
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
                response = JsonUtil.jsonStringToHashMap(strResponse);
                LOGGER.debug("Exit CardDetailsServiceImpl->getContent");
                return response;
            } else {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit CardDetailsServiceImpl->getContent");
                return errorMap;
            }

        }catch (Exception e)
        {
            LOGGER.debug("Exit CardDetailsServiceImpl->getContent");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }


    public Map<String, Object> getPANData(GetPANDataRequest getPANDataRequest) {
        if ((!userDetailService.checkIfUserExistInDb(getPANDataRequest.getUserId()))) {
            Map<String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getPANDataRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map<String, Object> response = ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        // *************** EnrollPan request to VTS ***************
        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
        // response = hitVisaServices.restfulServiceConsumerVisaGet("url","");
        // } catch (JsonProcessingException e) {
        //   e.printStackTrace();
        //}
        HashMap<String, Object> result = null;
        try {

            result = new ObjectMapper().readValue(response, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq) {
        String tokenUniqueReference = notifyTransactionDetailsReq.getTokenUniqueReference();
        String registrationCode2 = notifyTransactionDetailsReq.getRegistrationCode2();

        // Check if token unique reference is null
        if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");
        }

        // Get rnsRegistration ID of device
        Optional<DeviceInfo> oDeviceInfo = deviceDetailRepository.findByPaymentAppInstanceId(notifyTransactionDetailsReq.getPaymentAppInstanceId());
        if (!oDeviceInfo.isPresent()) {
            return ImmutableMap.of("responseId", "123456", "responseCode", "268", "message", "Incorrect PaymentAppInstanceId");
        }

        // Create Remote Notification Data
        HashMap<String, String> tdsNotificationData = new HashMap<>();
        tdsNotificationData.put("tokenUniqueReference", notifyTransactionDetailsReq.getTokenUniqueReference());
        tdsNotificationData.put("tdsUrl", notifyTransactionDetailsReq.getTdsUrl());
        tdsNotificationData.put("paymentAppInstanceId", notifyTransactionDetailsReq.getPaymentAppInstanceId());

        // If registrationCode2 is present then it's a new TDS registration.
        if (registrationCode2 != null) {
            if (registrationCode2.isEmpty()) {
                return ImmutableMap.of("responseId", "123456", "responseCode", "268", "message", "Registration Code2 is empty");
            }
            TransactionRegDetails transactionRegDetails = transactionRegDetailsRepository.findByTokenUniqueReference(notifyTransactionDetailsReq.getTokenUniqueReference()).get();
            transactionRegDetails.setRegCode2(notifyTransactionDetailsReq.getRegistrationCode2());
            transactionRegDetailsRepository.save(transactionRegDetails);
            tdsNotificationData.put("registrationCode2", notifyTransactionDetailsReq.getRegistrationCode2());
            tdsNotificationData.put(RemoteNotification.KEY_NOTIFICATION_TYPE, RemoteNotification.TYPE_TDS_REGISTRATION_NOTIFICATION);
        } else {
            // New transaction notification
            tdsNotificationData.put(RemoteNotification.KEY_NOTIFICATION_TYPE, RemoteNotification.TYPE_TDS_NOTIFICATION);
        }

        RnsGenericRequest rnsGenericRequest = new RnsGenericRequest();
        rnsGenericRequest.setIdType(UniqueIdType.MDES);
        rnsGenericRequest.setRegistrationId(oDeviceInfo.get().getRnsRegistrationId());
        rnsGenericRequest.setRnsData(tdsNotificationData);
        Map rnsResp = remoteNotificationService.sendRemoteNotification(rnsGenericRequest);
        if (rnsResp.containsKey("errorCode")) {
            return rnsResp;
        }
        return ImmutableMap.of("responseId", "123456", "responseCode", "200");
    }

    public Map getRegistrationCode(GetRegCodeReq getRegCodeReq) {
        String paymentAppInstanceId;
        String tokenUniqueRef = getRegCodeReq.getTokenUniqueReference();

        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap();
        reqMap.add("tokenUniqueReference", tokenUniqueRef);

        // Validate Token Unique reference
        if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent()) {
            paymentAppInstanceId = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get().getPaymentAppInstanceId();
        } else {
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");
        }

        // Validate PaymentAppInstanceId
        if (!paymentAppInstanceId.equalsIgnoreCase(getRegCodeReq.getPaymentAppInstanceId())) {
            return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");
        }

        // Invoke Get Registration Code API of the MDES
        String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.TDS_PATH + "/" + paymentAppInstanceId + "/getRegistrationCode", reqMap);

        JSONObject responseJson = new JSONObject(response);
        TransactionRegDetails transactionRegDetails;
        if (responseJson.getString("reasonCode").equalsIgnoreCase("200")) {
            // Store the registrationCode1
            if (transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent()) {
                transactionRegDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).get();
            } else {
                transactionRegDetails = new TransactionRegDetails();
            }
            transactionRegDetails.setPaymentAppInstanceId(paymentAppInstanceId);
            transactionRegDetails.setTokenUniqueReference(tokenUniqueRef);
            transactionRegDetails.setRegCode1(responseJson.getString("registrationCode1"));
            transactionRegDetails.setRegCode2(null);
            transactionRegDetails.setAuthCode(null);
            transactionRegDetailsRepository.save(transactionRegDetails);
        }
        return JsonUtil.jsonToMap(responseJson);


    }

    public Map registerWithTDS(TDSRegistration tdsRegistration) {
        String tokenUniqueRef = tdsRegistration.getTokenUniqueReference();
        String paymentAppInstanceId;
        String registrationHash;

        // Get the app instance ID from the DB
        if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent()) {
            paymentAppInstanceId = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get().getPaymentAppInstanceId();
        } else {
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");
        }

        // Validate PaymentAppInstanceId
        if (!paymentAppInstanceId.equalsIgnoreCase(tdsRegistration.getPaymentAppInstanceId())) {
            return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");
        }

        // Get the regcode1 and regcode2 from the DB and generate registrationHash
        TransactionRegDetails transactionRegDetails = transactionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).get();
        try {
            registrationHash = MessageDigestUtil.sha256Hasing(transactionRegDetails.getRegCode1() + transactionRegDetails.getRegCode2());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return ImmutableMap.of("reasonCode", "261", "message", "Crypto Exception");
        }

        // Create request
        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap();
        reqMap.add("tokenUniqueReference", tokenUniqueRef);
        reqMap.add("registrationHash", registrationHash);

        // Invoke Register API of the MDES
        ResponseEntity response = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.TDS_PATH + "/" + paymentAppInstanceId + "/register", reqMap);

        JSONObject responseJson = new JSONObject(String.valueOf(response.getBody()));
        if (response.getStatusCode().value() == 200) {
            // Store the AuthenticationCode and TDS Url
            transactionRegDetails.setAuthCode(responseJson.getString("authenticationCode"));
            transactionRegDetails.setTdsUrl(responseJson.getString("tdsUrl"));
            transactionRegDetailsRepository.save(transactionRegDetails);
        }
        return JsonUtil.jsonToMap(responseJson);
    }

    public Map getTransactionHistory(GetTransactionHistoryReq getTransactionHistoryReq) {
        String tokenUniqueRef = getTransactionHistoryReq.getTokenUniqueReference();
        String paymentAppInstanceId = getTransactionHistoryReq.getPaymentAppInstanceId();

        // If TokenUniqueReference is present then Check that paymentAppInstanceId and tokenUniqueReference
        if (tokenUniqueRef != null) {
            Optional<CardDetails> oCardDetails = cardDetailRepository.findByPaymentAppInstanceIdAndTokenUniqueReference(getTransactionHistoryReq.getPaymentAppInstanceId(),
                    getTransactionHistoryReq.getTokenUniqueReference());

            if (!oCardDetails.isPresent()) {
                return ImmutableMap.of("reasonCode", "260", "message", "Invalid PaymentAppInstanceId or TokenUniqueReference");
            }
        }

        // If paymentAppInstanceId is provided only then validate paymentAppInstanceId
        Optional<TransactionRegDetails> oTxnDetails = transactionRegDetailsRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
        if (!oTxnDetails.isPresent()) {
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid PaymentAppInstanceId");
        }

        TransactionRegDetails txnDetails = oTxnDetails.get();
        String authCode = txnDetails.getAuthCode();

        // Fetch Authentication Code and try to retrieve transaction details from MDES
        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap();
        reqMap.add("tokenUniqueReference", tokenUniqueRef);
        reqMap.add("authenticationCode", authCode);

        ResponseEntity response = httpRestHandlerUtils.httpPost(txnDetails.getTdsUrl() + "/" + paymentAppInstanceId + "/getTransactions", reqMap);
        String strMdesResp = String.valueOf(response.getBody());
        JSONObject jsMdesResp = new JSONObject(strMdesResp);
        if (response.getStatusCode().value() == 200) {
            // Get AuthenticationCode received from MDES and if it's different from previous one then update it.
            String mdesAuthCode = jsMdesResp.getString("authenticationCode");
            if (!mdesAuthCode.equalsIgnoreCase(authCode)) {
                txnDetails.setAuthCode(mdesAuthCode);
                transactionRegDetailsRepository.save(txnDetails);
            }
        }
        return JsonUtil.jsonStringToHashMap(strMdesResp);
    }

    public Map performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq) {
        String paymentAppInstanceID = lifeCycleManagementReq.getPaymentAppInstanceId();
        List<String> tokenUniqueRefList = lifeCycleManagementReq.getTokenUniqueReferences();
        String tokenUniqueRef = "";

        for (int i = 0; i < tokenUniqueRefList.size(); i++) {//Use foreach instade of for
            tokenUniqueRef = tokenUniqueRefList.get(i);
            //get card detail repository
            CardDetails cardDetails = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get();

            //Check if the token unique reference are valid or not
            if (!(tokenUniqueRef.equalsIgnoreCase(cardDetails.getTokenUniqueReference()))) {
                return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");
            }

            //Check if the payment appInstance ID is valid or not
            if (!(paymentAppInstanceID.equalsIgnoreCase(cardDetails.getPaymentAppInstanceId()))) {
                return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");
            }
            if (cardDetails.getTokenStatus().equalsIgnoreCase("DEACTIVATED")) {
                return ImmutableMap.of("reasonCode", "264", "message", "Card not found");
            }
        }

        //Prepare req for delete req
        MultiValueMap<String, Object> deleteReqMap = new LinkedMultiValueMap<String, Object>();
        deleteReqMap.add("responseHost", ServerConfig.RESPONSE_HOST);
        deleteReqMap.add("requestId", ArrayUtil.getHexString(ArrayUtil.getRandom(10)));
        deleteReqMap.add("paymentAppInstanceId", paymentAppInstanceID);
        deleteReqMap.add("tokenUniqueReferences", tokenUniqueRefList);
        deleteReqMap.add("causedBy", lifeCycleManagementReq.getCausedBy());
        deleteReqMap.add("reasonCode", lifeCycleManagementReq.getReasonCode());
        deleteReqMap.add("reason", lifeCycleManagementReq.getReason());

        String response = "";
        ResponseEntity responseEntity = null;
        //Call mastercard //{DELETE,SUSPEND,UNSUSPEND}
        switch (lifeCycleManagementReq.getOperation()) {
            case "DELETE":
                responseEntity = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.DIGITIZATION_PATH + "/delete", deleteReqMap);
                break;
            case "SUSPEND":
                responseEntity = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.DIGITIZATION_PATH + "/suspend", deleteReqMap);
                break;
            case "UNSUSPEND":
                responseEntity = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.DIGITIZATION_PATH + "/unsuspend", deleteReqMap);
                break;
            default:
                return ImmutableMap.of("reasonCode", "262", "message", "Invalid Operation");
        }

        response = String.valueOf(responseEntity.getBody());
        //Check if https req is 200
        if (responseEntity.getStatusCode().value() == 200) {
            //Check the status of indivisual token and update the status of the token in the DB

            JSONObject responseJson = new JSONObject(response);
            JSONArray tokensArray = responseJson.getJSONArray("tokens");
            for (int i = 0; i < tokensArray.length(); i++) {
                JSONObject j = tokensArray.getJSONObject(i);
                if (j.has("status")) {
                    tokenUniqueRef = j.getString("tokenUniqueReference");
                    String status = j.getString("status");
                    if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent()) {
                        CardDetails cardDetails = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get();
                        cardDetails.setTokenStatus(status);
                        cardDetailRepository.save(cardDetails);
                    }
                }
            }
            //Check the status of indivisual token and update the status of the token in the DB
            //Call update the card starus of the token in CMS-D
        }

        //Check the status of indivisual token and update the status of the token in the DB
        //Call update the card starus of the token in CMS-D
        //Send response
        return JsonUtil.jsonStringToHashMap(response);
    }

    public Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq) {
        Map<String, Object> response = new HashMap<>();
        response.put("responseHost", "com.mahindracomviva.payAppServer");
        response.put("requestId", "123456");

        String tokenUniqueRef = activationCodeReq.getTokenUniqueReference();
        String paymentAppInstanceId;

        // Validate tokenUniqueReference
        if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent()) {
            paymentAppInstanceId = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get().getPaymentAppInstanceId();
        } else {
            response.put("reasonCode", 260);
            response.put("message", "Invalid token UniqueReference");
            return response;
        }

        // Validate paymentAppInstanceId
        if (!paymentAppInstanceId.equalsIgnoreCase(activationCodeReq.getPaymentAppInstanceId())) {
            response.put("reasonCode", 261);
            response.put("message", "Invalid PaymentAppInstanceID");
            return response;
        }

        // Prepare Request Activation Code and call MDES
        MultiValueMap<String, Object> reqMdes = new LinkedMultiValueMap<>();
        reqMdes.add("responseHost", "com.mahindracomviva.payAppServer");
        reqMdes.add("requestId", "123456");
        reqMdes.add("paymentAppInstanceId", activationCodeReq.getPaymentAppInstanceId());
        reqMdes.add("tokenUniqueReference", activationCodeReq.getTokenUniqueReference());

        ObjectMapper mapper = new ObjectMapper();
        Map mapAuthMethod = mapper.convertValue(activationCodeReq.getAuthenticationMethod(), Map.class);
        reqMdes.add("authenticationMethod", mapAuthMethod);

        String resMdes = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/digitization/1/0/requestActivationCode", reqMdes);
        JSONObject jsRespMdes = new JSONObject(resMdes);

        return JsonUtil.jsonToMap(jsRespMdes);
    }

    @Override
    public Map unregisterTds(Map<String, String> unregisterTdsReq) {
        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap();
        if (unregisterTdsReq.containsKey("tokenUniqueReference")) {
            String tokenUniqueReference = unregisterTdsReq.get("tokenUniqueReference");
            if (!cardDetailRepository.findByTokenUniqueReference(tokenUniqueReference).isPresent()) {
                return ImmutableMap.of("reasonCode", "261", "message", "Invalid tokenUniqueReference");
            }
            reqMap.add("tokenUniqueReference", tokenUniqueReference);
        }
        String paymentAppInstanceId = unregisterTdsReq.get("paymentAppInstanceId");


        Optional<TransactionRegDetails> oTxnDetails = transactionRegDetailsRepository.findByPaymentAppInstanceId(paymentAppInstanceId);
        if (!oTxnDetails.isPresent()) {
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid PaymentAppInstanceId");
        }

        TransactionRegDetails txnDetails = oTxnDetails.get();
        String authCode = txnDetails.getAuthCode();
        reqMap.add("authenticationCode", authCode);

        ResponseEntity response = httpRestHandlerUtils.httpPost(txnDetails.getTdsUrl() + "/" + paymentAppInstanceId + "/unregister", reqMap);
        String strMdesResp = String.valueOf(response.getBody());
        JSONObject jsMdesResp = new JSONObject(strMdesResp);
        if (response.getStatusCode().value() == 200) {
            // Delete TDS Registration Details
            transactionRegDetailsRepository.delete(txnDetails);
        }
        return JsonUtil.jsonStringToHashMap(strMdesResp);
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
    public  Map getTokens(GetTokensRequest getTokensRequest)
    {
        //Check if the token unique reference is valid or not
        if(cardDetailRepository.findByTokenUniqueReference(getTokensRequest.getTokenUniqueReference()).isPresent())
        {
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");
        }
        MultiValueMap getToeknReqMap = new LinkedMultiValueMap();
        getToeknReqMap.add("tokenUniqueReference",getTokensRequest.getTokenUniqueReference());
        //call master card get token API.
        ResponseEntity responseEntity = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT +ServerConfig.DIGITIZATION_PATH+"/getToken", getToeknReqMap);
        String response;
        if (responseEntity.getStatusCode().value() == 200)
        {
            response = String.valueOf(responseEntity.getBody());
            return JsonUtil.jsonStringToHashMap(response);

        }
        return null;

    }
    public Map searchTokens(SearchTokensReq searchTokensReq)
    {
        //Check if the paymentAppInstanceId is valid or not
       if(!deviceDetailRepository.findByPaymentAppInstanceId(searchTokensReq.getPaymentAppInstanceId()).isPresent())
       {
           return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");
       }
        //call the master card searchTokens API
        MultiValueMap searchTokensReqMap = new LinkedMultiValueMap();
        searchTokensReqMap.add("paymentAppInstanceId",searchTokensReq.getPaymentAppInstanceId());
        ResponseEntity responseEntity = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT +ServerConfig.DIGITIZATION_PATH+"/searchTokens", searchTokensReqMap);
        String response;
        if (responseEntity.getStatusCode().value() == 200)
        {
            response = String.valueOf(responseEntity.getBody());
            return JsonUtil.jsonStringToHashMap(response);

        }
        return null;

    }
}