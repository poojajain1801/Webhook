package com.comviva.mfs.hce.appserver.service;

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
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import com.comviva.mfs.hce.appserver.util.vts.CreateChannelSecurityContext;
import com.comviva.mfs.hce.appserver.util.vts.ValidateUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @Autowired
    public ProvisionManagementServiceImpl(UserDetailService userDetailService,UserDetailRepository userDetailRepository,DeviceDetailRepository deviceDetailRepository) {
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.deviceDetailRepository=deviceDetailRepository;
    }

    public Map<String, Object> ProvisionTokenGivenPanEnrollmentId (ProvisionTokenGivenPanEnrollmentIdRequest provisionTokenGivenPanEnrollmentIdRequest) {


        //TODO: Validate clintdevice ID and clint account id
        //TODO get the vPanEnrollmentID from DB
        String emailAdress = provisionTokenGivenPanEnrollmentIdRequest.getEmailAddress();
        String emailHash = "";
        /*try {
            emailHash = MessageDigestUtil.sha256Hasing(emailAdress);
            byte[] b64data = org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(emailHash.getBytes());
            emailAdress = new String(b64data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        emailAdress = "ZtoaslwtWW6FQEvuOQ-GWvABWf-VInFJUmGQgm4AdtY";
       /* Map<String, Object> map = new HashMap<>();
        map.put("clientAppID",provisionTokenGivenPanEnrollmentIdRequest.getClientAppId());
        map.put("clientWalletAccountID", provisionTokenGivenPanEnrollmentIdRequest.getClientWalletAccountId());
        map.put("clientWalletAccountEmailAddressHash", emailAdress);
        map.put("protectionType", provisionTokenGivenPanEnrollmentIdRequest.getProtectionType());
        map.put("clientDeviceID",provisionTokenGivenPanEnrollmentIdRequest.getClientDeviceID());
        Map<String, Object> mapPra = new HashMap<>();
        //mapPra.put("presentationType",provisionTokenGivenPanEnrollmentIdRequest.getPresentationType());
        JSONArray presentationType = new JSONArray();
        presentationType.put(provisionTokenGivenPanEnrollmentIdRequest.getPresentationType());
        map.put("presentationType", presentationType);
*/
       JSONObject reqest = new JSONObject();
       reqest.put("clientAppID",provisionTokenGivenPanEnrollmentIdRequest.getClientAppId());
       reqest.put("clientWalletAccountID", provisionTokenGivenPanEnrollmentIdRequest.getClientWalletAccountId());
       reqest.put("clientWalletAccountEmailAddressHash", emailAdress);
       reqest.put("clientDeviceID",provisionTokenGivenPanEnrollmentIdRequest.getClientDeviceID());
       reqest.put("protectionType", provisionTokenGivenPanEnrollmentIdRequest.getProtectionType());
        JSONArray presentationType = new JSONArray();
        presentationType.put(provisionTokenGivenPanEnrollmentIdRequest.getPresentationType());
        reqest.put("presentationType", presentationType);
        JSONObject termandCondition =  new JSONObject();
        termandCondition.put("id",provisionTokenGivenPanEnrollmentIdRequest.getTermsAndConditionsID());
       // long utcTimestamp = System.currentTimeMillis() / 1000L;
        long unixTimestamp = Instant.now().getEpochSecond();
        //String date = Long.toString(utcTimestamp);
        termandCondition.put("date",unixTimestamp);
        reqest.put("termsAndConditions",termandCondition);
        //ObjectMapper objectMapper = new ObjectMapper();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String vPanEnrollmentID = provisionTokenGivenPanEnrollmentIdRequest.getPanEnrollmentID();
        String url = env.getProperty("visaBaseUrlSandbox")+"/vts/panEnrollments/"+vPanEnrollmentID+"/provisionedTokens"+"?apiKey="+env.getProperty("apiKey");
        String resourcePath = "vts/panEnrollments/"+vPanEnrollmentID+"/provisionedTokens";
        responseEntity = hitVisaServices.restfulServiceConsumerVisa(url,reqest.toString(), resourcePath,"POST");


        return null;
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
        if ((!userDetailService.checkIfUserExistInDb(confirmProvisioningRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(confirmProvisioningRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("api", confirmProvisioningRequest.getApi());
        map.add("provisioningStatus", confirmProvisioningRequest.getProvisioningStatus());
        map.add("failureReason", confirmProvisioningRequest.getFailureReason());
        map.add("reperso", confirmProvisioningRequest.getReperso());
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

    public Map<String, Object> ActiveAccountManagementReplenish (ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest) {
        if ((!userDetailService.checkIfUserExistInDb(activeAccountManagementReplenishRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(activeAccountManagementReplenishRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("mac", activeAccountManagementReplenishRequest.getMac());
        map.add("api", activeAccountManagementReplenishRequest.getApi());
        map.add("sc", activeAccountManagementReplenishRequest.getSc());
        map.add("tvl", activeAccountManagementReplenishRequest.getTvl());
        map.add("encryptionMetaData", activeAccountManagementReplenishRequest.getEncryptionMetaData());
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

    public Map<String, Object> ActiveAccountManagementConfirmReplenishment(ActiveAccountManagementConfirmReplenishmentRequest activeAccountManagementConfirmReplenishmentRequest) {
        if ((!userDetailService.checkIfUserExistInDb(activeAccountManagementConfirmReplenishmentRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(activeAccountManagementConfirmReplenishmentRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("tokenInfo", activeAccountManagementConfirmReplenishmentRequest.getTokenInfo());
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
    public Map<String, Object> ReplenishODAData(ReplenishODADataRequest replenishODADataRequest) {
        if ((!userDetailService.checkIfUserExistInDb(replenishODADataRequest.getUserId()))) {
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(replenishODADataRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            Map <String, Object> response =ImmutableMap.of("message", "User is not active", "responseCode", "207");
            return response;
        }
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
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
}