package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Madan amgoth on 5/10/2017.
 */
@Service
public class TokenLifeCycleManagementServiceImpl implements TokenLifeCycleManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenLifeCycleManagementServiceImpl.class);

    @Autowired
    private Environment env;
    private final UserDetailService userDetailService;
    private final HCEControllerSupport hceControllerSupport;
    private final UserDetailRepository userDetailRepository;
    private final CardDetailRepository cardDetailRepository;


    @Autowired
    public TokenLifeCycleManagementServiceImpl(UserDetailService userDetailService,HCEControllerSupport hceControllerSupport,UserDetailRepository userDetailRepository,CardDetailRepository cardDetailRepository) {
        this.hceControllerSupport = hceControllerSupport;
        this.userDetailService = userDetailService;
        this.userDetailRepository = userDetailRepository;
        this.cardDetailRepository = cardDetailRepository;
    }

    public Map<String, Object> getPaymentDataGivenTokenID (GetPaymentDataGivenTokenIDRequest getPaymentDataGivenTokenIDRequest) {

        List<UserDetail>  userDetails = userDetailRepository.findByUserIdAndStatus(getPaymentDataGivenTokenIDRequest.getUserId(), HCEConstants.ACTIVE);
        if(userDetails == null){
            Map <String, Object> response = ImmutableMap.of("message", "Invalid User", "responseCode", "205");
            return response;
        }
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


    public Map<String, Object>getTokenStatus(GetTokenStatusRequest getTokenStatusRequest) {
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        Map response= new LinkedHashMap();
        JSONObject jsonResponse  = null;
        String request = "";
        ResponseEntity responseEntity =null;
        String strResponse=null;
        String url = "";
        String resourcePath ="vts/provisionedTokens/"+getTokenStatusRequest.getVprovisionedTokenID();

        //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}?apiKey=key

        url =  env.getProperty("visaBaseUrlSandbox")+"/vts/provisionedTokens/"+getTokenStatusRequest.getVprovisionedTokenID()+"?apiKey="+env.getProperty("apiKey");
        try {
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");
            if (responseEntity.hasBody())
            {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
                response = JsonUtil.jsonStringToHashMap(strResponse);

            }
            if(responseEntity.getStatusCode().value()==200)
            {

                LOGGER.debug("Exit TokenLifeCycleManagementService->getTokenStatus");
                response.put("responseCode", HCEMessageCodes.SUCCESS);
                response.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.SUCCESS));

                return response;
            }
            else
            {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit TokenLifeCycleManagementService->getTokenStatus");
                return errorMap;

            }


        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug("Exit TokenLifeCycleManagementService->getTokenStatus");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);

        }

    }



    public Map<String,Object>lifeCycleManagementVisa(LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest) {
        //TODO:Check vProvisonID is valid or not
        LOGGER.debug("Enter TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
        String vProvisionedTokenID = lifeCycleManagementVisaRequest.getVprovisionedTokenID();

        //Construct LifecycleMnagement operation request
        JSONObject requestJson = new JSONObject();
        JSONObject updateReason = new JSONObject();
        updateReason.put("reasonCode", lifeCycleManagementVisaRequest.getReasonCode());
        updateReason.put("reasonDesc", lifeCycleManagementVisaRequest.getReasonDesc());
        requestJson.put("updateReason", updateReason);

        HitVisaServices hitVisaServices = new HitVisaServices(env);
        Map response = new LinkedHashMap();
        ResponseEntity responseEntity = null;
        String strResponse = null;
        String url = "";
        String resourcePath = "";
        JSONObject jsonResponse = null;
        String cardStatus = null;
        List<CardDetails> cardDetailsList = null;
        CardDetails cardDetails = null;
        try {

            cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(vProvisionedTokenID);
            if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
                cardDetails = cardDetailsList.get(0);

                if(cardDetails.getStatus().equals(HCEConstants.INACTIVE)){
                    throw new HCEActionException(HCEMessageCodes.CARD_DETAILS_NOT_EXIST);
                }



            }else{
                throw new HCEActionException(HCEMessageCodes.CARD_DETAILS_NOT_EXIST);
            }


            switch (lifeCycleManagementVisaRequest.getOperation()) {
                case "DELETE":
                    resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/delete";
                    //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/delete?apiKey=key
                    url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/delete" + "?apiKey=" + env.getProperty("apiKey");
                    cardStatus = HCEConstants.INACTIVE;
                    break;
                case "SUSPEND":
                    resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/suspend";
                    //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/suspend?apiKey=key
                    url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/suspend" + "?apiKey=" + env.getProperty("apiKey");
                    cardStatus = HCEConstants.SUSUPEND;
                    break;
                case "RESUME":
                    resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/resume";
                    //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/resume?apiKey=key
                    url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/resume" + "?apiKey=" + env.getProperty("apiKey");
                    cardStatus = HCEConstants.ACTIVE;
                    break;
                default:
                    LOGGER.debug("Exit TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
                    return hceControllerSupport.formResponse(HCEMessageCodes.INVALID_OPERATION);

            }

            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestJson.toString(), resourcePath, "PUT");

            if (responseEntity.getStatusCode().value() == 200 || responseEntity.getStatusCode().value() == 201) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
                cardDetails.setStatus(cardStatus);
                cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                cardDetailRepository.save(cardDetails);
                return hceControllerSupport.formResponse(HCEMessageCodes.SUCCESS);
            }
            else {
                Map errorMap = new LinkedHashMap();
                errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                LOGGER.debug("Exit TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
                return errorMap;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            LOGGER.debug("Exception occurred in TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
    }
    @Transactional
   public  Map<String,Object> getTokenList(GetTokenListRequest getTokenListRequest) {

        Map<String, Object> responseMap = null;
        String userId = null;
        List<UserDetail> userDetailList = null;
        UserDetail userDetail = null;
        List<DeviceInfo> deviceInfoList = null;
        List<DeviceInfo> activeDeviceInfoList = null;
        DeviceInfo deviceInfo = null;
        List<CardDetails> cardDetailsList = null;
        CardDetails cardDetails = null;
        Map<String,Object> userDetailsMap = null;
        Map<String,Object> deviceDetailsMap = null;
        Map<String,Object> cardDetailsMap = null;
        List<Map<String,Object>> cardDetailsMapList = null;
        List<Map<String,Object>> deviceDetailsMapList = null;
        String index = null;
        String maxRecord = null;
        int page =0;
        int size = 0;

        try {

            userId = getTokenListRequest.getUserId();

            index = getTokenListRequest.getIndex();
            maxRecord = getTokenListRequest.getMaxRecord();

            if(index!=null && !index.isEmpty()){
                page = Integer.parseInt(index)-1;
            }
            if(maxRecord!=null && !maxRecord.isEmpty()){
                size = Integer.parseInt(maxRecord);
            }

            userDetailList = userDetailRepository.findByUserIdAndStatus(userId, HCEConstants.ACTIVE);
            if (userDetailList != null && !userDetailList.isEmpty()) {

                cardDetailsList = cardDetailRepository.getNCardList(userId,HCEConstants.ACTIVE,HCEConstants.ACTIVE,HCEConstants.SUSUPEND,page,size);
                if(cardDetailsList!=null &&  !cardDetailsList.isEmpty()){
                    responseMap = new HashMap<String ,Object>();
                    cardDetailsMapList = new ArrayList();
                    for(int i=0;i<cardDetailsList.size();i++){
                        cardDetailsMap = new HashMap<String ,Object>();
                        cardDetails = cardDetailsList.get(i);
                        cardDetailsMap.put(HCEConstants.CARD_ID,cardDetails.getCardId());
                        cardDetailsMap.put(HCEConstants.CARD_IDENTIFIER,cardDetails.getCardIdentifier());
                        cardDetailsMap.put(HCEConstants.CARD_SUFFIX,cardDetails.getCardSuffix());
                        cardDetailsMap.put(HCEConstants.CARD_TYPE,cardDetails.getCardType());
                        cardDetailsMap.put(HCEConstants.MODIFIED_ON,cardDetails.getModifiedOn());
                        cardDetailsMap.put(HCEConstants.PAN_UNIQUE_REFERENCE,cardDetails.getPanUniqueReference());
                        cardDetailsMap.put(HCEConstants.REPLENISH_ON,cardDetails.getReplenishOn());
                        cardDetailsMap.put(HCEConstants.STATUS,cardDetails.getStatus());
                        cardDetailsMap.put(HCEConstants.TOKEN_SUFFIX,cardDetails.getTokenSuffix());
                        cardDetailsMap.put(HCEConstants.CLIENT_DEVICE_ID,cardDetails.getDeviceInfo().getClientDeviceId());
                        cardDetailsMap.put(HCEConstants.CREATED_ON,cardDetails.getDeviceInfo().getCreatedOn());
                        cardDetailsMap.put(HCEConstants.DEVICE_MODEL,cardDetails.getDeviceInfo().getDeviceModel());
                        cardDetailsMap.put(HCEConstants.HOST_DEVICE_ID,cardDetails.getDeviceInfo().getHostDeviceId());
                        cardDetailsMap.put(HCEConstants.IMEI,cardDetails.getDeviceInfo().getImei());
                        cardDetailsMap.put(HCEConstants.IS_MASTER_CARD_ENABLED,cardDetails.getDeviceInfo().getIsMastercardEnabled());
                        cardDetailsMap.put(HCEConstants.IS_VISA_ENABLED,cardDetails.getDeviceInfo().getIsVisaEnabled());
                        cardDetailsMap.put(HCEConstants.MODIFIED_ON,cardDetails.getDeviceInfo().getModifiedOn());
                        cardDetailsMap.put(HCEConstants.NFC_CAPABLE,cardDetails.getDeviceInfo().getNfcCapable());
                        cardDetailsMap.put(HCEConstants.OS_NAME,cardDetails.getDeviceInfo().getOsName());
                        cardDetailsMap.put(HCEConstants.OS_VERSION,cardDetails.getDeviceInfo().getOsVersion());
                        cardDetailsMap.put(HCEConstants.USER_ID,cardDetails.getDeviceInfo().getUserDetail().getUserId());
                        cardDetailsMap.put(HCEConstants.CLIENT_WALLET_ACCOUNT_ID,cardDetails.getDeviceInfo().getUserDetail().getClientWalletAccountId());
                        cardDetailsMapList.add(cardDetailsMap);
                    }

                    responseMap.put(HCEConstants.CARD_DETAILS_LIST,cardDetailsMapList);


                }else{
                    throw new HCEActionException(HCEMessageCodes.CARD_DETAILS_NOT_EXIST);
                }


            }else{
                throw new HCEActionException(HCEMessageCodes.INVALID_USER);
            }
        } catch (HCEActionException getTokeListHceActionException) {
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", getTokeListHceActionException);
            throw getTokeListHceActionException;

        } catch (Exception getTokenListException) {
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", getTokenListException);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }

        return responseMap;

    }



}