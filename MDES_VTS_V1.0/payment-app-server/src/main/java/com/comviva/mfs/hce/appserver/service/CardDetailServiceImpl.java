package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.mapper.vts.RequestId;
import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import com.comviva.mfs.hce.appserver.repository.VisaCardDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerImplUtils;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerUtils;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.ServiceData;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.ServiceDataRepository;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    @Autowired
    private Environment env;

    ServiceData serviceData;

    HttpRestHandlerUtils httpRestHandlerUtils = new HttpRestHandlerImplUtils();

    @Autowired
    public CardDetailServiceImpl(CardDetailRepository cardDetailRepository,
                                 UserDetailService userDetailService,
                                 ServiceDataRepository serviceDataRepository,
                                 DeviceDetailRepository deviceDetailRepository,
                                 VisaCardDetailRepository visaCardDetailRepository) {
        this.cardDetailRepository = cardDetailRepository;
        this.userDetailService = userDetailService;
        this.serviceDataRepository = serviceDataRepository;
        this.deviceDetailRepository = deviceDetailRepository;
        this.visaCardDetailRepository=visaCardDetailRepository;
    }


    private AddCardResponse prepareDigitizeResponse(int reasonCode, String reasonDescription) {
        return new AddCardResponse(ImmutableMap.of("reasonCode", Integer.toString(reasonCode), "reasonDescription", reasonDescription));
    }

    private AddCardResponse prepareDigitizeResponse() {
        // ************* Prepare Response *************
        ImmutableMap.Builder addCardResponse = new ImmutableMap.Builder();
        addCardResponse.put("responseHost", "mahindracomviva.paymentAppServer");
        addCardResponse.put("responseId", "123456");
        addCardResponse.put("decision", "APPROVED");
        addCardResponse.put("tokenUniqueReference", "DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45");
        addCardResponse.put("panUniqueReference", "FWSPMC000000000159f71f703d2141efaf04dd26803f922b");
        addCardResponse.put("tdsRegistrationUrl", "tds.mastercard.com");

        // Prepare tokenInfo object
        ImmutableMap.Builder productConfig = new ImmutableMap.Builder();
        productConfig.put("tokenPanSuffix", "1234");
        productConfig.put("accountPanSuffix", "6789");
        productConfig.put("tokenExpiry", "1018");
        productConfig.put("dsrpCapable", false);
        addCardResponse.put("tokenInfo", productConfig.build());

        productConfig = new ImmutableMap.Builder();
        productConfig.put("brandLogoAssetId", "800200c9-629d-11e3-949a-0739d27e5a66");
        productConfig.put("isCoBranded", "true");
        productConfig.put("coBrandName", "Co brand partner");
        productConfig.put("coBrandLogoAssetId", "dbc55444-496a-4896-b41c-5d5e2dd431e2");
        productConfig.put("cardBackgroundCombinedAssetId", "739d27e5-629d-11e3-949a-0800200c9a66");
        productConfig.put("foregroundColor", "000000");
        productConfig.put("issuerName", "Issuing Bank");
        productConfig.put("shortDescription", "Bank Rewards MasterCard");
        productConfig.put("longDescription", "Bank Rewards MasterCard with the super duper rewards program");
        productConfig.put("customerServiceUrl", "https://bank.com/customerservice");
        productConfig.put("termsAndConditionsUrl", "https://bank.com/termsAndConditions");
        productConfig.put("privacyPolicyUrl", "https://bank.com/privacy");
        productConfig.put("issuerProductConfigCode", "123456");

        ImmutableMap.Builder openIssuerMobileAppAndroidIntent = new ImmutableMap.Builder();
        openIssuerMobileAppAndroidIntent.put("action", "com.mybank.bankingapp.action.OPEN_ISSUER_MOBILE_APP");
        openIssuerMobileAppAndroidIntent.put("packageName", "com.mybank.bankingapp");
        openIssuerMobileAppAndroidIntent.put("extraTextValue", "ew0KICAgICJwYXltZW50QXBwUHJvdmlkZXJJZCI6ICIxMjM0NTY3ODkiLA0KICAgICJwYXltZW50QXBwSWQiOiAiV2FsbGV0QXBwMSIsDQogICAgInBheW1lbnRBcHBJbnN0YW5jZUlkIjogIjEyMzQ1Njc4OSIsDQogICAgInRva2VuVW5pcXVlUmVmZXJlbmNlIjogIkRXU1BNQzAwMDAwMDAwMGZjYjJmNDEzNmIyZjQxMzZhMDUzMmQyZjQxMzZhMDUzMiINCn0=");

        ImmutableMap.Builder issuerMobileApp = new ImmutableMap.Builder();
        issuerMobileApp.put("openIssuerMobileAppAndroidIntent", openIssuerMobileAppAndroidIntent.build());
        productConfig.put("issuerMobileApp", issuerMobileApp.build());

        addCardResponse.put("productConfig", productConfig.build());
        return new AddCardResponse(addCardResponse.build());
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

                String userName = deviceInfoOptional.get().getUserName();
                serviceData = serviceDataRepository.save(new ServiceData(null, userName, serviceId, requestJson.toString(), response));

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
        if(!deviceInfoOptional.isPresent()) {
            return prepareDigitizeResponse(220, "Invalid Payment App Instance Id");
        }

        if (!serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).isPresent()) {
            return prepareDigitizeResponse(220, "Card is not eligible for the digitization service");
        }
        String eligibilityRequest = serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getRequest();
        String eligibilityResponse = serviceDataRepository.findByServiceId(digitizationParam.getServiceId()).get().getResponse();
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
            cardDetails.setUserName(deviceDetailRepository.findByPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId")).get().getUserName());
            cardDetails.setPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId"));
            cardDetails.setTokenUniqueReference(provisionRespMdes.getString("tokenUniqueReference"));
            cardDetails.setPanUniqueReference(provisionRespMdes.getString("panUniqueReference"));
            cardDetails.setTokenInfo(provisionRespMdes.getJSONObject("tokenInfo").toString());
            cardDetails.setTokenStatus("NEW");
            cardDetailRepository.save(cardDetails);
            return prepareDigitizeResponse();
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
        if (resp.has("mediaContent")) {
            JSONArray jArrMediaContent = resp.getJSONArray("mediaContent");
            JSONObject assetComponent;
            String type;
            ArrayList<Map> listOfAssets = new ArrayList<>();

            for (int i = 0; i < jArrMediaContent.length(); i++) {
                assetComponent = jArrMediaContent.getJSONObject(i);
                type = assetComponent.getString("type");

                if (type.equalsIgnoreCase("text")) {
                    listOfAssets.add(ImmutableMap.of("type", type, "data", assetComponent.getString("data")));
                } else if (type.contains("image")) {
                    listOfAssets.add(ImmutableMap.of("type", type, "height", assetComponent.getString("height"), "width", assetComponent.getString("width"), "data", assetComponent.getString("type")));
                }
            }
            respMap = ImmutableMap.of("mediaContent", listOfAssets, "reasonCode", "200", "reasonDescription", "Successful");
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
        if ((!userDetailService.checkIfUserExistInDb(enrollPanRequest.getUserId()))) {
             Map <String, Object> response =ImmutableMap.of("message", "Invalid User", "responseCode", "205");
             return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(enrollPanRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }

        // *************** EnrollPan request to VTS ***************

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("clientAppID", enrollPanRequest.getClientAppId());
        map.add("clientWalletAccountID", enrollPanRequest.getClientWalletAccountId());
        map.add("clientDeviceID", enrollPanRequest.getClientDeviceId());
        map.add("locale", enrollPanRequest.getLocale());
        map.add("panSource", enrollPanRequest.getPanSource());
        map.add("consumerEntryMode", enrollPanRequest.getConsumerEntryMode());
        map.add("encPaymentInstrument", enrollPanRequest.getEncPaymentInstrument());
        map.add("encryptionMetaData",enrollPanRequest.getEncryptionMetaData());
        map.add("platformType",enrollPanRequest.getPlatformType());
        map.add("channelSecurityContext",enrollPanRequest.getChannelSecurityContext());
        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
       String response ="";
          try {
              response = hitVisaServices.restfulServiceConsumerVisa("url",objectMapper.writeValueAsString(map), map);
         } catch (JsonProcessingException e) {
           e.printStackTrace();
       }
       JSONObject jsonResponse=new JSONObject(response);
          if("200".equals(jsonResponse.getString("responseCode"))){
            //put card details in db
              VisaCardDetails visaCardDetails=new VisaCardDetails();
              visaCardDetails.setUserName(enrollPanRequest.getUserId());
              visaCardDetails.setCardnumberSuffix(enrollPanRequest.getEncPaymentInstrument().getAccountNumber());
              visaCardDetails.setVpanenrollmentid((String) jsonResponse.get("vPanEnrollmentID"));
              visaCardDetails.setStatus("Y");
              visaCardDetailRepository.save(visaCardDetails);
          }
         HashMap<String,Object> result =null;
         try {

           result =   new ObjectMapper().readValue(response, HashMap.class);
         } catch (IOException e) {
           e.printStackTrace();
        }
          return result;
    }


    public Map<String, Object> getCardMetadata (GetCardMetadataRequest getCardMetadataRequest) {
        if ((!userDetailService.checkIfUserExistInDb(getCardMetadataRequest.getUserId()))) {
            Map <String, Object> response =ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getCardMetadataRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
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
        HashMap<String,Object> result =null;
        try {

            result =   new ObjectMapper().readValue(response, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> getContent(GetContentRequest getContentRequest){
        if ((!userDetailService.checkIfUserExistInDb(getContentRequest.getUserId()))) {
            Map <String, Object> response =ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getContentRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        // *************** EnrollPan request to VTS ***************
        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
         //response = hitVisaServices.restfulServiceConsumerVisaGet("url","");
        // } catch (JsonProcessingException e) {
        //   e.printStackTrace();
        //}
        HashMap<String,Object> result =null;
        try {

            result =   new ObjectMapper().readValue(response, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object>getPANData(GetPANDataRequest getPANDataRequest){
        if ((!userDetailService.checkIfUserExistInDb(getPANDataRequest.getUserId()))) {
            Map <String, Object> response =ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getPANDataRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
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
        HashMap<String,Object> result =null;
        try {

            result =   new ObjectMapper().readValue(response, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}