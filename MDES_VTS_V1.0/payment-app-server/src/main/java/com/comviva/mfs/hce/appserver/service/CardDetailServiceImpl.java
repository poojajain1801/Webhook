package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.*;
import com.comviva.mfs.hce.appserver.repository.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerImplUtils;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandlerUtils;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.comviva.mfs.hce.appserver.util.vts.CreateChannelSecurityContext;
import com.comviva.mfs.hce.appserver.util.vts.ValidateUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.visa.dmpd.token.JWTUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private final TransctionRegDetailsRepository transctionRegDetailsRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UserDetailRepository userDetailRepository;
    @Autowired
    private Environment env;

    ServiceData serviceData;

    HttpRestHandlerUtils httpRestHandlerUtils = new HttpRestHandlerImplUtils();

    @Autowired
    public CardDetailServiceImpl(CardDetailRepository cardDetailRepository,
                                 UserDetailService userDetailService,
                                 ServiceDataRepository serviceDataRepository,
                                 DeviceDetailRepository deviceDetailRepository,
                                 VisaCardDetailRepository visaCardDetailRepository,
                                 TransctionRegDetailsRepository transctionRegDetailsRepository,
                                 TransactionHistoryRepository transactionHistoryRepository, UserDetailRepository userDetailRepository) {
        this.cardDetailRepository = cardDetailRepository;
        this.userDetailService = userDetailService;
        this.serviceDataRepository = serviceDataRepository;
        this.deviceDetailRepository = deviceDetailRepository;
        this.visaCardDetailRepository = visaCardDetailRepository;
        this.transctionRegDetailsRepository = transctionRegDetailsRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.userDetailRepository = userDetailRepository;
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
            if (!deviceInfoOptional.isPresent()) {
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
                serviceData = serviceDataRepository.save(new ServiceData(null, serviceId, requestJson.toString(), response));

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
            //--madan cardDetails.setUserName(deviceDetailRepository.findByPaymentAppInstanceId(jsonRequest.getString("paymentAppInstanceId")).get().getUserName());
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

    public Map<String, Object> enrollPan(EnrollPanRequest enrollPanRequest) {

        Map<String, Object> response1 = new HashMap();
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
        map.put("clientAppID", "111b95f4-06d9-b032-00c1-178ec0fd7201");
        map.put("clientWalletAccountID", userDetails.get(0).getClientWalletAccountid());
        map.put("clientDeviceID", enrollPanRequest.getClientDeviceID());
        map.put("locale", enrollPanRequest.getLocale());
        map.put("panSource", enrollPanRequest.getPanSource());

        enrollPanRequest.getEncPaymentInstrument().getProvider().setClientWalletProvider(userDetails.get(0).getClientWalletAccountid());
        enrollPanRequest.getEncPaymentInstrument().getProvider().setClientWalletAccountID(userDetails.get(0).getClientWalletAccountid());
        enrollPanRequest.getEncPaymentInstrument().getProvider().setClientDeviceID(enrollPanRequest.getClientDeviceID());
        enrollPanRequest.getEncPaymentInstrument().getProvider().setClientAppID("111b95f4-06d9-b032-00c1-178ec0fd7201");

        map.put("encPaymentInstrument", JWTUtility.createSharedSecretJwe(enrollPanRequest.getEncPaymentInstrument().toString(), env.getProperty("apiKey"), env.getProperty("sharedSecret")));
        map.put("consumerEntryMode", enrollPanRequest.getConsumerEntryMode());
        map.put("platformType", enrollPanRequest.getPlatformType());
        //*********************create channelSecurityContext start*****************
        CreateChannelSecurityContext createChannelSecurityContext = new CreateChannelSecurityContext();
        Map<String, Object> securityContext = createChannelSecurityContext.visaChannelSecurityContext(deviceInfo);
        map.put("channelSecurityContext", securityContext);
        //******************************end***********************************
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(hitVisaServices.restfulServiceConsumerVisa(env.getProperty("visaBaseUrlSandbox") + "/vts/panEnrollments?apiKey=" + env.getProperty("apiKey"), objectMapper.writeValueAsString(map), "vts/panEnrollments", "POST"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if ("200".equals(jsonResponse.getString("responseCode"))) {
            //put card details in db
            VisaCardDetails visaCardDetails = new VisaCardDetails();
            visaCardDetails.setUserName(enrollPanRequest.getUserId());
            visaCardDetails.setCardnumberSuffix(enrollPanRequest.getEncPaymentInstrument().getAccountNumber());
            visaCardDetails.setVpanenrollmentid((String) jsonResponse.get("vPanEnrollmentID"));
            visaCardDetails.setStatus("Y");
            visaCardDetailRepository.save(visaCardDetails);
        }
        return null;
    }


    public Map<String, Object> getCardMetadata(GetCardMetadataRequest getCardMetadataRequest) {
        if ((!userDetailService.checkIfUserExistInDb(getCardMetadataRequest.getUserId()))) {
            Map<String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getCardMetadataRequest.getUserId()).equalsIgnoreCase("userActivated");
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

    public Map<String, Object> getContent(GetContentRequest getContentRequest) {
        if ((!userDetailService.checkIfUserExistInDb(getContentRequest.getUserId()))) {
            Map<String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getContentRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map<String, Object> response = ImmutableMap.of("message", "User is not active", "responseCode", "207");
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
        HashMap<String, Object> result = null;
        try {

            result = new ObjectMapper().readValue(response, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
        //Check if tokenn unique referance is null
        JSONObject reqObj = new JSONObject(notifyTransactionDetailsReq);

        if ((!reqObj.has("tokenUniqueReference")) || (reqObj.getString("tokenUniqueReference").isEmpty()))
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");

        //Check if the registration code is there
        if ((reqObj.has("registrationCode2")) && (!reqObj.getString("registrationCode2").isEmpty())) {
            TransctionRegDetails transctionRegDetails = transctionRegDetailsRepository.findByTokenUniqueReference(notifyTransactionDetailsReq.getTokenUniqueReference()).get();
            transctionRegDetails.setRegCode2(notifyTransactionDetailsReq.getRegistrationCode2());
            transctionRegDetailsRepository.save(transctionRegDetails);
        } else {
            return ImmutableMap.of("responseId", "123456", "responseCode", "268", "message", "Regcode2 not present");
            //notify the Mobile payment app to call getTransctionDetail API
        }

        return ImmutableMap.of("responseId", "123456", "responseCode", "200");
    }

    public Map getRegistrationCode(GetregCodeReq getregCodeReq) {
        String tokenUniqueRef = getregCodeReq.getTokenUniqueReference();
        String paymentAppInstanceId = "";
        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap<String, String>();
        reqMap.add("tokenUniqueReference", tokenUniqueRef);
        //Get the app instanse ID from the DB
        if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent())
            paymentAppInstanceId = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get().getPaymentAppInstanceId();
        else
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");

        if (!paymentAppInstanceId.equalsIgnoreCase(getregCodeReq.getPaymentAppInstanceId()))
            return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");

        //Call getregstration Code API of the Master card
        String response = httpRestHandlerUtils.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.TDS_PATH + "/" + paymentAppInstanceId + "/getRegistrationCode", reqMap);

        JSONObject responseJson = new JSONObject(response);
        TransctionRegDetails transctionRegDetails;
        if (responseJson.getString("reasonCode").equalsIgnoreCase("200")) {
            //Strore the regcode1 to the data base
            //TransctionRegDetails transctionRegDetails = transctionRegDetailsRepository.save(new TransctionRegDetails(null,tokenUniqueRef,responseJson.getString("registrationCode1"),null,null,null));
            // transctionRegDetails.setId(null);
            if (!transctionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent())
                transctionRegDetails = new TransctionRegDetails();
            else
                transctionRegDetails = transctionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).get();
            transctionRegDetails.setTokenUniqueReference(tokenUniqueRef);
            transctionRegDetails.setRegCode1(responseJson.getString("registrationCode1"));
            transctionRegDetails.setRegCode2(null);
            transctionRegDetails.setAuthCode(null);
            transctionRegDetails.setAuthCodeExpiry(null);
            transctionRegDetailsRepository.save(transctionRegDetails);

        }
       /* MultiValueMap<String,String> responseMap = new LinkedMultiValueMap<String,String>();
        responseMap.add("reasonCode",responseJson.getString("reasonCode"));
        responseMap.add("result",responseJson.getString("result"));
        responseMap.add("message","SUCCESS");*/


        return JsonUtil.jsonToMap(responseJson);


    }

    public Map registerWithTDS(TDSRegistration tdsRegistration) {
        String tokenUniqueRef = tdsRegistration.getTokenUniqueReference();
        String paymentAppInstanceId = "";
        String registrationHash = "";

        //Get the app instanse ID from the DB
        if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent())
            paymentAppInstanceId = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get().getPaymentAppInstanceId();
        else
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");

        if (!paymentAppInstanceId.equalsIgnoreCase(tdsRegistration.getPaymentAppInstanceId()))
            return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");

        //Get the regcode1 and regcode2 from the DB and generate registrationHash
        TransctionRegDetails transctionRegDetails = transctionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).get();
        String regcode1 = transctionRegDetails.getRegCode1();
        String regCode2 = transctionRegDetails.getRegCode2();

        try {
            registrationHash = MessageDigestUtil.sha256Hasing(regcode1 + regCode2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Creat request
        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap<String, String>();
        reqMap.add("tokenUniqueReference", tokenUniqueRef);
        reqMap.add("registrationHash", registrationHash);

        //Call getregstration API of the Master card
        ResponseEntity response = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.TDS_PATH + "/" + paymentAppInstanceId + "/register", reqMap);

        JSONObject responseJson = new JSONObject(String.valueOf(response.getBody()));
        if (response.getStatusCode().value() == 200) {
            //Strore the Authcode in DB
            transctionRegDetails.setAuthCode(responseJson.getString("authenticationCode"));
            //TODO:Generate the authcodeExpity here and update the same in the DB
            transctionRegDetails.setAuthCodeExpiry(null);
            transctionRegDetailsRepository.save(transctionRegDetails);
        }

        // TODO: Set the  RegURL In ServerCinfig file
        /*MultiValueMap<String,String> responseMap = new LinkedMultiValueMap<String,String>();
        responseMap.add("reasonCode",responseJson.getString("reasonCode"));
        responseMap.add("result",responseJson.getString("result"));
        responseMap.add("message","SUCCESS");*/
        return JsonUtil.jsonToMap(responseJson);
    }

    public Map getTransctionHistory(GetTransactionHistoryReq getTransactionHistoryReq) {
        String tokenUniqueRef = getTransactionHistoryReq.getTokenUniqueReference();
        String paymentAppInstanceId = "";

        if (cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent())
            paymentAppInstanceId = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get().getPaymentAppInstanceId();
        else
            return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");

        if (!paymentAppInstanceId.equalsIgnoreCase(getTransactionHistoryReq.getPaymentAppInstanceId()))
            return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");

        //TODO:if required Get the authcode from DB and Validate the auth code expiry


        String authCode = transctionRegDetailsRepository.findByTokenUniqueReference(tokenUniqueRef).get().getAuthCode();
        MultiValueMap<String, String> reqMap = new LinkedMultiValueMap<String, String>();
        reqMap.add("tokenUniqueReference", tokenUniqueRef);
        reqMap.add("authenticationCode", authCode);

        //Call getTransctions Code API of the Master card
        ResponseEntity response = httpRestHandlerUtils.httpPost(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + ServerConfig.TDS_PATH + "/" + paymentAppInstanceId + "/getTransactions", reqMap);
        JSONObject responseJson = new JSONObject(String.valueOf(response.getBody()));
        TransactionHistory transactionHistory;
        if (response.getStatusCode().value() == 200) {
            //store the Transaction History to the Data Base
            if (transactionHistoryRepository.findByTokenUniqueReference(tokenUniqueRef).isPresent()) {
                transactionHistory = transactionHistoryRepository.findByTokenUniqueReference(tokenUniqueRef).get();
                transactionHistory.setTransactionDetails(responseJson.getJSONArray("transactions").toString());
                transactionHistoryRepository.save(transactionHistory);
            } else {
                transactionHistory = new TransactionHistory();
                transactionHistory.setTokenUniqueReference(tokenUniqueRef);
                transactionHistory.setTransactionDetails(responseJson.getJSONArray("transactions").toString());
                transactionHistoryRepository.save(transactionHistory);

            }
        }

        return JsonUtil.jsonStringToHashMap(String.valueOf(response.getBody()));
    }

    public Map deleteCard(LifeCycleManagementReq lifeCycleManagementReq) {
        String paymentAppInstanseID = lifeCycleManagementReq.getPaymentAppInstanceId();
        List<String> tokenUniqueRefList = lifeCycleManagementReq.getTokenUniqueReferences();
        String tokenUniqueRef = "";

        for (int i = 0; i < tokenUniqueRefList.size(); i++)//Use foreach instade of for
        {
            tokenUniqueRef = tokenUniqueRefList.get(i);
            //get card detail repository
            CardDetails cardDetails = cardDetailRepository.findByTokenUniqueReference(tokenUniqueRef).get();

            //Check if the token unique reference are valid or not
            if (!(tokenUniqueRef.equalsIgnoreCase(cardDetails.getTokenUniqueReference())))
                return ImmutableMap.of("reasonCode", "260", "message", "Invalid token UniqueReference");

            //Check if the payment appInstance ID is valid or not
            if (!(paymentAppInstanseID.equalsIgnoreCase(cardDetails.getPaymentAppInstanceId())))
                return ImmutableMap.of("reasonCode", "261", "message", "Invalid PaymentAppInstanceID");
            if (cardDetails.getTokenStatus().equalsIgnoreCase("DEACTIVATED"))
                return ImmutableMap.of("reasonCode", "264", "message", "Card not found");
        }
        //Prepare req for delete req
        MultiValueMap<String, Object> deleteReqMap = new LinkedMultiValueMap<String, Object>();
        deleteReqMap.add("responseHost", ServerConfig.RESPONSE_HOST);
        deleteReqMap.add("requestId", ArrayUtil.getHexString(ArrayUtil.getRandom(10)));
        deleteReqMap.add("paymentAppInstanceId", paymentAppInstanseID);
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
        String paymentAppInstanceId = "";

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
}