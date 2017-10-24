package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.repository.VisaCardDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.ProvisionManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.comviva.mfs.hce.appserver.util.vts.CreateChannelSecurityContext;
import com.comviva.mfs.hce.appserver.util.vts.ValidateUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

/**
 * Created by Amgoth.madan on 2/5/2017.
 */
@Service
public class ProvisionManagementServiceImpl implements ProvisionManagementService {
    @Autowired
    private Environment env;

    private final UserDetailService userDetailService;
    private final UserDetailRepository userDetailRepository;
    private final DeviceDetailRepository deviceDetailRepository;
    private final HCEControllerSupport hceControllerSupport;
    private final VisaCardDetailRepository visaCardDetailRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionManagementServiceImpl.class);


    @Autowired
    public ProvisionManagementServiceImpl(UserDetailService userDetailService,UserDetailRepository userDetailRepository,
                                          DeviceDetailRepository deviceDetailRepository,HCEControllerSupport hceControllerSupport,
                                          VisaCardDetailRepository visaCardDetailRepository) {
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.deviceDetailRepository=deviceDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.visaCardDetailRepository = visaCardDetailRepository;
    }

    public Map<String, Object> ProvisionTokenGivenPanEnrollmentId (ProvisionTokenGivenPanEnrollmentIdRequest provisionTokenGivenPanEnrollmentIdRequest) {

        LOGGER.debug("Enter ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
        //TODO: Validate clintdevice ID and clint account id
        //TODO get the vPanEnrollmentID from DB

        //Calculate Email Hash
        String emailAdress = provisionTokenGivenPanEnrollmentIdRequest.getEmailAddress();
        String emailHash = "";
        JSONObject reqest = new JSONObject();
        JSONArray presentationType = new JSONArray();
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
        VisaCardDetails visaCardDetails= null;
        Map<String,Object> responseMap = null;

        try {
            emailHash = MessageDigestUtil.getEmailHashAlgorithmValue(emailAdress);
            byte[] b64data = org.apache.commons.codec.binary.Base64.encodeBase64(emailHash.getBytes());
            emailAdress = new String(b64data);
            emailAdress = emailAdress.substring(0, 43);

            /**********Construct ProvisionTokenGivenPanEnrollmentId Request************************/

            reqest.put("clientAppID", provisionTokenGivenPanEnrollmentIdRequest.getClientAppId());
            reqest.put("clientWalletAccountID", provisionTokenGivenPanEnrollmentIdRequest.getClientWalletAccountId());
            reqest.put("clientWalletAccountEmailAddressHash", emailAdress);
            reqest.put("clientDeviceID", provisionTokenGivenPanEnrollmentIdRequest.getClientDeviceID());
            reqest.put("protectionType", provisionTokenGivenPanEnrollmentIdRequest.getProtectionType());

            presentationType.put(provisionTokenGivenPanEnrollmentIdRequest.getPresentationType());
            reqest.put("presentationType", presentationType);
            JSONObject termandCondition = new JSONObject();
            termandCondition.put("id", provisionTokenGivenPanEnrollmentIdRequest.getTermsAndConditionsID());
            long unixTimestamp = Instant.now().getEpochSecond();
            termandCondition.put("date", unixTimestamp);
            reqest.put("termsAndConditions", termandCondition);

            hitVisaServices = new HitVisaServices(env);
            String vPanEnrollmentID = provisionTokenGivenPanEnrollmentIdRequest.getPanEnrollmentID();
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/panEnrollments/" + vPanEnrollmentID + "/provisionedTokens" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/panEnrollments/" + vPanEnrollmentID + "/provisionedTokens";
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, reqest.toString(), resourcePath, "POST");
            if (responseEntity.hasBody())
            {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
            }
            if (responseEntity.getStatusCode().value() == 200 || responseEntity.getStatusCode().value() == 201) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
                visaCardDetails = new VisaCardDetails();
                visaCardDetails = visaCardDetailRepository.findByVPanEnrollmentId(provisionTokenGivenPanEnrollmentIdRequest.getPanEnrollmentID()).get(0);
                visaCardDetails.setvProvisionedTokenId(jsonResponse.getString("vProvisionedTokenID"));
                visaCardDetailRepository.save(visaCardDetails);
                responseMap= JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.SUCCESS);
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.SUCCESS));
                return responseMap;
            }
            else
            {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
                return errorMap;

            }


        }catch (Exception e)
        {
            LOGGER.debug("Exception occurred in ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
    }



    public Map<String, Object> ProvisionTokenWithPanData (ProvisionTokenWithPanDataRequest provisionTokenWithPanDataRequest) {
        if ((!userDetailService.checkIfUserExistInDb(provisionTokenWithPanDataRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(provisionTokenWithPanDataRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("encryptionMetaData", provisionTokenWithPanDataRequest.getEncryptionMetaData());
        map.add("clientAppID", provisionTokenWithPanDataRequest.getClientAppID());
        map.add("clientWalletAccountID", provisionTokenWithPanDataRequest.getClientWalletAccountID());
        map.add("ip4address", provisionTokenWithPanDataRequest.getIp4address());
        map.add("location",provisionTokenWithPanDataRequest.getLocation());
        map.add("local", provisionTokenWithPanDataRequest.getLocal());
        map.add("issuerAuthCode", provisionTokenWithPanDataRequest.getIssuerAuthCode());
        map.add("emailAddressHash", provisionTokenWithPanDataRequest.getEmailAddressHash());
        map.add("emailAddress", provisionTokenWithPanDataRequest.getEmailAddress());
        map.add("protectionType", provisionTokenWithPanDataRequest.getProtectionType());
        map.add("clientDeviceID", provisionTokenWithPanDataRequest.getClientDeviceID());
        map.add("panSource", provisionTokenWithPanDataRequest.getPanSource());
        map.add("consumerEntryMode", provisionTokenWithPanDataRequest.getConsumerEntryMode());
        map.add("encRiskDataInfo", provisionTokenWithPanDataRequest.getEncRiskDataInfo());
        map.add("encPaymentInstrument", provisionTokenWithPanDataRequest.getEncPaymentInstrument());
        map.add("presentationType", provisionTokenWithPanDataRequest.getPresentationType());
        map.add("accountType", provisionTokenWithPanDataRequest.getAccountType());
        map.add("encRiskDataInfo", provisionTokenWithPanDataRequest.getEncRiskDataInfo());
        map.add("ssdData", provisionTokenWithPanDataRequest.getSsdData());
        map.add("channelSecurityContext", provisionTokenWithPanDataRequest.getChannelSecurityContext());
        map.add("platformType", provisionTokenWithPanDataRequest.getPlatformType());

        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
        //      response = hitVisaServices.restfulServieceConsumerVisa("url",objectMapper.writeValueAsString(enrollPanRequest), map);
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


    public Map<String, Object> ConfirmProvisioning (ConfirmProvisioningRequest confirmProvisioningRequest) {
       //TODO:Check vProvisonID is valid or not
        LOGGER.debug("Enter ProvisionManagementServiceImpl->ConfirmProvisioning");
        String provisonStatus = confirmProvisioningRequest.getProvisioningStatus();
        String failureReason = confirmProvisioningRequest.getFailureReason();
        String vProvisionedTokenID = confirmProvisioningRequest.getVprovisionedTokenId();
        JSONObject requestMap = new JSONObject();
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
        try {
            requestMap.put("api", confirmProvisioningRequest.getApi());
            requestMap.put("provisioningStatus", provisonStatus);

            if (provisonStatus.equalsIgnoreCase("FAILURE") && (!(failureReason.equalsIgnoreCase("null")) || (failureReason.isEmpty())))
                requestMap.put("failureReason", confirmProvisioningRequest.getFailureReason());

            hitVisaServices = new HitVisaServices(env);

            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/confirmProvisioning" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/confirmProvisioning";
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestMap.toString(), resourcePath, "PUT");
            if (responseEntity.hasBody())
            {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
            }

            if (responseEntity.getStatusCode().value() == 200) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ConfirmProvisioning");
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);

            }
            else
            {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ConfirmProvisioning");
                return errorMap;

            }


        }catch (Exception e)
        {
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ConfirmProvisioning");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }

    public Map<String, Object> ActiveAccountManagementReplenish (ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest) {
        //TODO:Check vProvisonID is valid or not
        if (!validatevProvisionedID(activeAccountManagementReplenishRequest.getVprovisionedTokenID()))
            //return Invalid vprovisonID.

        LOGGER.debug("Enter ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
        String vProvisionedTokenID = "";
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
        JSONObject requestMap = new JSONObject();
        JSONObject signature = new JSONObject();
        JSONObject tokenInfo = new JSONObject();
        JSONObject hceData = new JSONObject();
        JSONObject dynParams = new JSONObject();
        JSONArray tvl = new JSONArray();
        Map responseMap = new LinkedHashMap();
        Array[] tvlData = activeAccountManagementReplenishRequest.getTvl();

        try{

            signature.put("mac",activeAccountManagementReplenishRequest.getMac());
            requestMap.put("signature" ,signature);
            dynParams.put("api",activeAccountManagementReplenishRequest.getApi());
            dynParams.put("sc",activeAccountManagementReplenishRequest.getSc());

            for(int i=0;i<tvlData.length;i++)
            {
                tvl.put(tvlData[i]);
            }
            dynParams.put("tvl",tvl);
            hceData.put("dynParams",dynParams);
            tokenInfo.put("hceData",hceData);
            requestMap.put("tokenInfo",tokenInfo);
            vProvisionedTokenID = activeAccountManagementReplenishRequest.getVprovisionedTokenID();
            //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/replenish?apiKey=key
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/replenish" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/replenish";
            hitVisaServices = new HitVisaServices(env);
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestMap.toString(), resourcePath, "POST");
            if (responseEntity.hasBody())
            {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());

            }

            if (responseEntity.getStatusCode().value() == 200) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
                responseMap.put("responseCode", HCEMessageCodes.SUCCESS);
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.SUCCESS));
                return responseMap;

            }
            else
            {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
                return errorMap;

            }


        }catch (Exception e)
        {
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }

    public Map<String, Object> ActiveAccountManagementConfirmReplenishment(ActiveAccountManagementConfirmReplenishmentRequest activeAccountManagementConfirmReplenishmentRequest) {

        //TODO:Check vProvisonID is valid or not
        LOGGER.debug("Enter ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
        String vProvisionedTokenID = "";
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
        JSONObject requestMap = new JSONObject();
        JSONObject tokenInfo = new JSONObject();
        JSONObject hceData = new JSONObject();
        JSONObject dynParams = new JSONObject();
        JSONArray tvl = new JSONArray();
        Map responseMap = new LinkedHashMap();

        try{
            dynParams.put("api",activeAccountManagementConfirmReplenishmentRequest.getApi());
            dynParams.put("sc",activeAccountManagementConfirmReplenishmentRequest.getSc());
            hceData.put("dynParams",dynParams);
            tokenInfo.put("hceData",hceData);
            requestMap.put("tokenInfo",tokenInfo);

            vProvisionedTokenID = activeAccountManagementConfirmReplenishmentRequest.getVprovisionedTokenID();
            //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/replenish?apiKey=key
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/confirmReplenishment" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/confirmReplenishment";
            hitVisaServices = new HitVisaServices(env);
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestMap.toString(), resourcePath, "PUT");

            if (responseEntity.hasBody())
            {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);

            }
            if (responseEntity.getStatusCode().value() == 200) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);

            }
            else
            {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
                return errorMap;

            }


        }
        catch (Exception e)
        {
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

    }
    public Map<String, Object> ReplenishODAData(ReplenishODADataRequest replenishODADataRequest) {
        LOGGER.debug("Enter ProvisionManagementServiceImpl->ReplenishODAData");
        String vProvisionedTokenID = "";
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
        String  request = "";
        Map responseMap = new LinkedHashMap();

        try{

            vProvisionedTokenID = replenishODADataRequest.getVprovisionedTokenID();
            //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/replenish?apiKey=key
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/replenishODA" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/replenishODA";
            hitVisaServices = new HitVisaServices(env);
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "POST");

            if (responseEntity.hasBody())
            {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());

            }

            if (responseEntity.getStatusCode().value() == 200) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ReplenishODAData");
                responseMap.put("responseCode", HCEMessageCodes.SUCCESS);
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.SUCCESS));
                return responseMap;

            }            else
            {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ReplenishODAData");
                return errorMap;

            }


        }
        catch (Exception e)
        {
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }


    }
    public Map<String, Object> submitIDandVStepupMethod(SubmitIDandVStepupMethodRequest submitIDandVStepupMethodRequest) {
        if ((!userDetailService.checkIfUserExistInDb(submitIDandVStepupMethodRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(submitIDandVStepupMethodRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("vProvisionedTokenID", submitIDandVStepupMethodRequest.getVProvisionedTokenID());
        map.add("stepUpRequestID", submitIDandVStepupMethodRequest.getStepUpRequestID());
        map.add("date", submitIDandVStepupMethodRequest.getDate());

        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);

        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
        // response = hitVisaServices.restfulServieceConsumerVisa("url",objectMapper.writeValueAsString(enrollPanRequest), map);
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


    public Map<String, Object> validateOTP(ValidateOTPRequest validateOTPRequest) {
        if ((!userDetailService.checkIfUserExistInDb(validateOTPRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(validateOTPRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("vProvisionedTokenID", validateOTPRequest.getVProvisionedTokenID());
        map.add("otpValue", validateOTPRequest.getOtpValue());
        map.add("date", validateOTPRequest.getDate());

        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);

        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
        // response = hitVisaServices.restfulServieceConsumerVisa("url",objectMapper.writeValueAsString(enrollPanRequest), map);
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


    public Map<String, Object> validateAuthenticationCode(ValidateAuthenticationCodeRequest validateAuthenticationCodeRequest) {
        if ((!userDetailService.checkIfUserExistInDb(validateAuthenticationCodeRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(validateAuthenticationCodeRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("vProvisionedTokenID", validateAuthenticationCodeRequest.getVProvisionedTokenID());
        map.add("issuerAuthCode", validateAuthenticationCodeRequest.getIssuerAuthCode());
        map.add("date", validateAuthenticationCodeRequest.getDate());

        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);

        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
        // response = hitVisaServices.restfulServieceConsumerVisa("url",objectMapper.writeValueAsString(enrollPanRequest), map);
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


    public Map<String, Object> getStepUpOptions(GetStepUpOptionsRequest getStepUpOptionsRequest) {
        if ((!userDetailService.checkIfUserExistInDb(getStepUpOptionsRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(getStepUpOptionsRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);

        String response = "{ \t\"vPanEnrollmentID\": \"c9b61bd49a52597a3d0a18f6535df201\", \t\"encryptionMetaData\": \" base 64 encoded\", \t\"paymentInstrument\": { \t\t\"last4\": \"3018\", \t\t\"accountStatus\": \"N\", \t\t\"isTokenizable\": \"Y\", \t\t\"expirationDate\": { \t\t\t\"month\": \"12\", \t\t\t\"year\": \"2015\" \t\t}, \t\t\"indicators\": [\"PRIVATE_LABEL\"], \t\t\"expDatePrintedInd\": \"Y\", \t\t\"cvv2PrintedInd\": \"Y\", \t\t\"paymentAccountReference\": \"V0010013816180398947326400396\" \t}, \t\"cardMetaData\": { \t\t\"backgroundColor\": \"0x009602\", \t\t\"foregroundColor\": \"0x1af0f0\", \t\t\"labelColor\": \"0x195501\", \t\t\"contactWebsite\": \"www.thebank.com\", \t\t\"contactEmail\": \"goldcustomer@thebank.com\", \t\t\"contactNumber\": \"18001234567\", \t\t\"contactName\": \"TheBank\", \t\t\"privacyPolicyURL\": \"www.thebank.com/privacy\", \t\t\"bankAppName\": \"TheBankApp\", \t\t\"bankAppAddress\": \"com.sampleIssuer.thebankapp\", \t\t\"termsAndConditionsURL\": \"www.thebank.com/termsAndConditionsURL\", \t\t\"termsAndConditionsID\": \"3456548509876567...\", \t\t\"shortDescription\": \"The Bank Card\", \t\t\"longDescription\": \"The Bank Card Platinum Rewards\", \t\t\"cardData\": [{ \t\t\t\"guid\": \"5591f1c00bba420484ad9aa5b48c66d3\", \t\t\t\"contentType\": \"cardSymbol\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"100\", \t\t\t\t\"height\": \"100\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"c20bd324315b4788ab1399f482537afb\", \t\t\t\"contentType\": \"digitalCardArt\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"image/png\", \t\t\t\t\"width\": \"1536\", \t\t\t\t\"height\": \"968\" \t\t\t}] \t\t}, { \t\t\t\"guid\": \"4a9469ba5fbe4e739281cbdc8de7a898\", \t\t\t\"contentType\": \"termsAndConditions\", \t\t\t\"content\": [{ \t\t\t\t\"mimeType\": \"text/plain\", \t\t\t\t\"width\": \"0\", \t\t\t\t\"height\": \"0\" \t\t\t}] \t\t}] \t}, \t\"aidInfo\": [{ \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}, { \t\t\"aid\": \"A0000000031010\", \t\t\"priority\": \"01\" \t}] }";
        //  try {
        // response = hitVisaServices.restfulServieceConsumerVisa("url",objectMapper.writeValueAsString(enrollPanRequest), map);
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
    public boolean validatevProvisionedID(String vProvisionedTokenID)
    {
        if (visaCardDetailRepository.findByVProvisionedTokenId(vProvisionedTokenID).isPresent())
            return true;
        else
            return false;
    }
}