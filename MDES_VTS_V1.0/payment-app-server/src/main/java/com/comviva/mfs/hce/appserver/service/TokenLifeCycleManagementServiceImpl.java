package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenListRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokenStatusRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Madan amgoth on 5/10/2017.
 */
@Service
public class TokenLifeCycleManagementServiceImpl implements TokenLifeCycleManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenLifeCycleManagementServiceImpl.class);

    @Autowired
    private Environment env;
    private final HCEControllerSupport hceControllerSupport;
    private final CardDetailRepository cardDetailRepository;
    private final UserDetailService userDetailService;
    private final UserDetailRepository userDetailRepository;

    @Autowired
    public TokenLifeCycleManagementServiceImpl(HCEControllerSupport hceControllerSupport,UserDetailService userDetailService, UserDetailRepository userDetailRepository,CardDetailRepository cardDetailRepository) {
        this.hceControllerSupport = hceControllerSupport;
        this.cardDetailRepository = cardDetailRepository;
        this.userDetailService = userDetailService;
        this.userDetailRepository = userDetailRepository;
    }

    public Map<String, Object>getTokenStatus(GetTokenStatusRequest getTokenStatusRequest) {
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        Map<String, Object> response= new LinkedHashMap<>();
        JSONObject jsonResponse  = null;
        String request = "";
        ResponseEntity responseEntity =null;
        String strResponse=null;
        String url = "";
        String currentcardStatus = null;
        List<CardDetails> cardDetailsList = null;
        CardDetails cardDetails = null;
        String cardStatus = null;
        String resourcePath ="vts/provisionedTokens/"+getTokenStatusRequest.getVprovisionedTokenID();

        //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}?apiKey=key

        url =  env.getProperty("visaBaseUrlSandbox")+"/vts/provisionedTokens/"+getTokenStatusRequest.getVprovisionedTokenID()+"?apiKey="+env.getProperty("apiKey");
        try {

            cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(getTokenStatusRequest.getVprovisionedTokenID());
            if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
                cardDetails = cardDetailsList.get(0);
            }else{
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }

            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");
            if (responseEntity.hasBody()) {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
                response = JsonUtil.jsonStringToHashMap(strResponse);

            }
            if(responseEntity.getStatusCode().value()==HCEConstants.REASON_CODE7)
            {
                if (null !=jsonResponse) {
                    currentcardStatus = jsonResponse.getJSONObject("tokenInfo").getString("tokenStatus");
                }
                if (null != currentcardStatus) {
                    switch (currentcardStatus) {
                        case "ACTIVE":
                            cardStatus = HCEConstants.ACTIVE;
                            break;
                        case "DELETED":
                        case "INACTIVE":
                            cardStatus = HCEConstants.INACTIVE;
                            break;
                        case "SUSPENDED":
                            cardStatus = HCEConstants.SUSUPEND;
                            break;
                        default:
                            response.put("responseCode", HCEMessageCodes.getSUCCESS());
                            response.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                            return response;
                    }
                }
                cardDetails.setStatus(cardStatus);
                cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                cardDetailRepository.save(cardDetails);
                LOGGER.debug("Exit TokenLifeCycleManagementService->getTokenStatus");
                response.put("responseCode", HCEMessageCodes.getSUCCESS());
                response.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                return response;
            } else {
                Map<String, Object> errorMap = new LinkedHashMap<>();
                if (null !=jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit TokenLifeCycleManagementService->getTokenStatus");
                return errorMap;

            }


        } catch (Exception e) {
            LOGGER.debug("Exit TokenLifeCycleManagementService->getTokenStatus",e);
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());

        }

    }


    /**
     * lifeCycleManagementVisa - card life cycle mgmt
     * @param lifeCycleManagementVisaRequest delete or suspend
     * @return Map
     * */
    public Map<String,Object> lifeCycleManagementVisa(LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest) {
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
//        Map response = new LinkedHashMap();
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
                    throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
                }
            }else{
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }
            switch (lifeCycleManagementVisaRequest.getOperation()) {
                case "DELETE":
                    LOGGER.debug("Inside TokenLifeCycleManagementServiceImpl-> DELETE");
                    resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/delete";
                    //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/delete?apiKey=key
                    url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/delete" + "?apiKey=" + env.getProperty("apiKey");
                    cardStatus = HCEConstants.INACTIVE;
                    break;
                case "SUSPEND":
                    LOGGER.debug("Inside TokenLifeCycleManagementServiceImpl-> SUSPEND");
                    resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/suspend";
                    //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/suspend?apiKey=key
                    url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/suspend" + "?apiKey=" + env.getProperty("apiKey");
                    cardStatus = HCEConstants.SUSUPEND;
                    break;
                case "RESUME":
                case "UNSUSPEND":
                    LOGGER.debug("Inside TokenLifeCycleManagementServiceImpl-> RESUME");
                    resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/resume";
                    //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/resume?apiKey=key
                    url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/resume" + "?apiKey=" + env.getProperty("apiKey");
                    cardStatus = HCEConstants.ACTIVE;
                    break;
                default:
                    LOGGER.debug("Exit TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa ->Invalid Operation");
                    return hceControllerSupport.formResponse(HCEMessageCodes.getInvalidOperation());

            }

            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestJson.toString(), resourcePath, "PUT");
            if (responseEntity.hasBody()) {
                strResponse = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(strResponse);
            }

            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7 || responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE8) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
                cardDetails.setStatus(cardStatus);
                cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                cardDetailRepository.save(cardDetails);
                return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
            } else {
                Map<String, Object> errorMap = new LinkedHashMap<>();
                if (null !=jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa");
                return errorMap;
            }
        } catch (Exception e) {
            LOGGER.debug("Exception occurred in TokenLifeCycleManagementServiceImpl->lifeCycleManagementVisa",e);
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
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
        int totalCount = 0;
        try {
            userId = getTokenListRequest.getUserId();
            index = getTokenListRequest.getIndex();
            maxRecord = getTokenListRequest.getMaxRecord();
            if(index!=null && !index.isEmpty()){
                int i = Integer.parseInt(index);
                int ten = 10;
                page = i/ten;
            }
            if(maxRecord!=null && !maxRecord.isEmpty()){
                size = Integer.parseInt(maxRecord);
            }
            userDetailList = userDetailRepository.findByUserIdAndStatus(userId, HCEConstants.ACTIVE);
            if (userDetailList != null && !userDetailList.isEmpty()) {
                cardDetailsList = cardDetailRepository.getNCardList(userId,HCEConstants.ACTIVE,HCEConstants.ACTIVE,HCEConstants.SUSUPEND,page,size);
                if(cardDetailsList!=null &&  !cardDetailsList.isEmpty()){
                    responseMap = new HashMap<>();
                    cardDetailsMapList = new ArrayList<>();
                    for(int i=0;i<cardDetailsList.size();i++){
                        cardDetailsMap = new HashMap<>();
                        cardDetails = cardDetailsList.get(i);
                        cardDetailsMap.put(HCEConstants.CARD_ID,cardDetails.getCardId()!=null?cardDetails.getCardId():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.CARD_IDENTIFIER,cardDetails.getCardIdentifier()!=null ?cardDetails.getCardIdentifier():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.CARD_SUFFIX,cardDetails.getCardSuffix()!=null ?cardDetails.getCardSuffix():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.CARD_TYPE,cardDetails.getCardType()!=null ? cardDetails.getCardType():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.MODIFIED_ON,cardDetails.getModifiedOn()!=null ? cardDetails.getModifiedOn().toString():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.PAN_UNIQUE_REFERENCE,cardDetails.getPanUniqueReference()!=null ?cardDetails.getPanUniqueReference():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.VISA_PROVISION_TOKENID,cardDetails.getVisaProvisionTokenId()!=null ?cardDetails.getVisaProvisionTokenId():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.MASTER_TOKEN_UNIQUE_REFERENCE,cardDetails.getMasterTokenUniqueReference()!=null ?cardDetails.getMasterTokenUniqueReference():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.REPLENISH_ON,cardDetails.getReplenishOn()!=null ? cardDetails.getReplenishOn().toString():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.STATUS,cardDetails.getStatus()!=null ? cardDetails.getStatus():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.TOKEN_SUFFIX,cardDetails.getTokenSuffix()!=null ? cardDetails.getTokenSuffix():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.CLIENT_DEVICE_ID,cardDetails.getDeviceInfo()!=null ? cardDetails.getDeviceInfo().getClientDeviceId():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.CREATED_ON, cardDetails.getCreatedOn()!=null ? cardDetails.getCreatedOn().toString():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.DEVICE_MODEL,cardDetails.getDeviceInfo()!=null ? cardDetails.getDeviceInfo().getDeviceModel():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.HOST_DEVICE_ID,cardDetails.getDeviceInfo().getHostDeviceId()!=null ? cardDetails.getDeviceInfo().getHostDeviceId():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.IMEI,cardDetails.getDeviceInfo().getImei()!=null ? cardDetails.getDeviceInfo().getImei():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.IS_MASTER_CARD_ENABLED,cardDetails.getDeviceInfo().getIsMastercardEnabled()!=null ? cardDetails.getDeviceInfo().getIsMastercardEnabled():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.IS_VISA_ENABLED,cardDetails.getDeviceInfo().getIsVisaEnabled()!=null ? cardDetails.getDeviceInfo().getIsVisaEnabled():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.NFC_CAPABLE,cardDetails.getDeviceInfo().getNfcCapable()!=null ? cardDetails.getDeviceInfo().getNfcCapable():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.OS_NAME,cardDetails.getDeviceInfo().getOsName()!=null ? cardDetails.getDeviceInfo().getOsName():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.OS_VERSION,cardDetails.getDeviceInfo().getOsVersion()!=null ? cardDetails.getDeviceInfo().getOsVersion():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.USER_ID,cardDetails.getDeviceInfo().getUserDetail().getUserId()!=null ? cardDetails.getDeviceInfo().getUserDetail().getUserId():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.DEVICE_NAME,cardDetails.getDeviceInfo().getDeviceName()!=null ? cardDetails.getDeviceInfo().getDeviceName():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMap.put(HCEConstants.CLIENT_WALLET_ACCOUNT_ID,cardDetails.getDeviceInfo().getUserDetail().getClientWalletAccountId()!=null ? cardDetails.getDeviceInfo().getUserDetail().getClientWalletAccountId():HCEConstants.NOT_AVAILABLE);
                        cardDetailsMapList.add(cardDetailsMap);
                    }

                    responseMap.put(HCEConstants.CARD_DETAILS_LIST,cardDetailsMapList);

                    cardDetailsList = cardDetailRepository.getNCardList(userId,HCEConstants.ACTIVE,HCEConstants.ACTIVE,HCEConstants.SUSUPEND,0,0);
                    if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
                        totalCount = cardDetailsList.size();
                    }
                    responseMap.put(HCEConstants.TOTAL_COUNT,Integer.toString(totalCount));
                }else{
                    throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
                }
            }else{
                throw new HCEActionException(HCEMessageCodes.getInvalidUser());
            }
        }catch (HCEActionException getTokeListHceActionException) {
            LOGGER.error("Exception occurred in DeviceDetailServiceImpl->registerDevice", getTokeListHceActionException);
            throw getTokeListHceActionException;

        }catch (Exception getTokenListException) {
            LOGGER.error("Exception occurred in DeviceDetailServiceImpl->registerDevice", getTokenListException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return responseMap;

    }



}