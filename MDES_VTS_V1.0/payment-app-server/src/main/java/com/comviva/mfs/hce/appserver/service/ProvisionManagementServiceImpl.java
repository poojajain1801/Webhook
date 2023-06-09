/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActiveAccountManagementReplenishRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmProvisioningRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmReplenishmenRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetStepUpOptionsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ProvisionTokenGivenPanEnrollmentIdRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ReplenishODADataRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SubmitIDandVStepupMethodRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ValidateOTPRequest;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.ProvisionManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.messagedigest.MessageDigestUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final CardDetailRepository cardDetailRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionManagementServiceImpl.class);


    @Autowired
    public ProvisionManagementServiceImpl(UserDetailService userDetailService,UserDetailRepository userDetailRepository,
                                          DeviceDetailRepository deviceDetailRepository,HCEControllerSupport hceControllerSupport,
                                          CardDetailRepository cardDetailRepository) {
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.deviceDetailRepository=deviceDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.cardDetailRepository = cardDetailRepository;
    }

    public Map<String, Object> ProvisionTokenGivenPanEnrollmentId (ProvisionTokenGivenPanEnrollmentIdRequest provisionTokenGivenPanEnrollmentIdRequest) {
        //Calculate Email Hash
        String emailAdress = null;
        String emailHash = "";
        JSONObject reqest = new JSONObject();
        JSONArray presentationType = new JSONArray();
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
       // VisaCardDetails visaCardDetails= null;

        CardDetails cardDetails = null;
        Map<String,Object> responseMap = null;
        List<CardDetails> cardDetailsList = null;

        String vPanEnrollmentID = null;
        String clientDeviceID = null;
        try {
            LOGGER.debug("Enter ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
            emailAdress = provisionTokenGivenPanEnrollmentIdRequest.getEmailAddress();
            emailHash = MessageDigestUtil.getEmailHashAlgorithmValue(emailAdress);
            byte[] b64data = org.apache.commons.codec.binary.Base64.encodeBase64(emailHash.getBytes());
            emailAdress = new String(b64data);
            emailAdress = emailAdress.substring(0, 43);

            /**********Construct ProvisionTokenGivenPanEnrollmentId Request************************/

            reqest.put("clientAppID", env.getProperty("clientAppID"));
            reqest.put("clientWalletAccountID", provisionTokenGivenPanEnrollmentIdRequest.getClientWalletAccountId());
            reqest.put("clientWalletAccountEmailAddressHash", emailAdress);
            reqest.put("clientDeviceID", provisionTokenGivenPanEnrollmentIdRequest.getClientDeviceID());
            reqest.put("protectionType", provisionTokenGivenPanEnrollmentIdRequest.getProtectionType());
            presentationType.put(provisionTokenGivenPanEnrollmentIdRequest.getPresentationType());

            /*presentationType.put("NFC-HCE");
            presentationType.put("QR_CONSUMER_CLOUD");*/

            reqest.put("presentationType", presentationType);
            JSONObject termandCondition = new JSONObject();
            termandCondition.put("id", provisionTokenGivenPanEnrollmentIdRequest.getTermsAndConditionsID());

            long unixTimestamp = Instant.now().getEpochSecond();
            termandCondition.put("date", unixTimestamp);
            reqest.put("termsAndConditions", termandCondition);
            hitVisaServices = new HitVisaServices(env);

            vPanEnrollmentID = provisionTokenGivenPanEnrollmentIdRequest.getPanEnrollmentID();
            clientDeviceID =  provisionTokenGivenPanEnrollmentIdRequest.getClientDeviceID();
            cardDetailsList = cardDetailRepository.findByPanUniqueReferenceAndClientDeviceId(vPanEnrollmentID,clientDeviceID,HCEConstants.INITIATE);
            if(cardDetailsList!=null && !cardDetailsList.isEmpty()) {
                cardDetails = cardDetailsList.get(0);
            } else {
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/panEnrollments/" + vPanEnrollmentID + "/provisionedTokens" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/panEnrollments/" + vPanEnrollmentID + "/provisionedTokens";
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, reqest.toString(), resourcePath, "POST");
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                LOGGER.debug("Provison Response from VTS = "+response);
                jsonResponse = new JSONObject(response);
            }
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7
                    || responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE8) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
                if (null != jsonResponse) {
                    cardDetails.setVisaProvisionTokenId(jsonResponse.getString("vProvisionedTokenID"));

                    JSONObject tokenInfo = jsonResponse.getJSONObject("tokenInfo");
                    if (tokenInfo != null) {
                        cardDetails.setTokenSuffix(tokenInfo.getString("last4"));
                    }
                }
                cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                //cardDetails.setStatus(HCEConstants.ACTIVE);
                cardDetailRepository.save(cardDetails);
                if (null !=jsonResponse) {
                    responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                }
                if(null != responseMap) {
                    responseMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getSUCCESS());
                    responseMap.put(HCEConstants.MESSAGE, hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                }

                return responseMap;
            }
            else {
                Map<String, Object> errorMap = new LinkedHashMap<>();
                /* errorMap.put("responseCode", Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));*/
                errorMap.put(HCEConstants.RESPONSE_CODE, HCEMessageCodes.getFailedAtThiredParty());
                errorMap.put(HCEConstants.MESSAGE, HCEConstants.GENERIC_ERROR);
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
                return errorMap;
            }

        }catch(HCEActionException provisionHCEActionException){
            LOGGER.error("Exception occured in ProvisionManagementServiceImpl->provisionTokenGivenPanEnrollmentId", provisionHCEActionException);
            throw provisionHCEActionException;

        }catch(Exception provisionException){
            LOGGER.error("Exception occured in ProvisionManagementServiceImpl->provisionTokenGivenPanEnrollmentId", provisionException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
    }

    @Transactional
    public Map<String, Object> ConfirmProvisioning (ConfirmProvisioningRequest confirmProvisioningRequest) {
        LOGGER.debug("Enter ProvisionManagementServiceImpl->ConfirmProvisioning");
        String provisonStatus = confirmProvisioningRequest.getProvisioningStatus();
        String failureReason = confirmProvisioningRequest.getFailureReason();
        String vProvisionedTokenID = confirmProvisioningRequest.getVprovisionedTokenID();
        JSONObject requestMap = new JSONObject();
        HitVisaServices hitVisaServices =null;
        JSONObject jsonResponse= null;
        ResponseEntity responseEntity =null;
        String response = null;
        List<CardDetails> cardDetailsList = null;
        CardDetails cardDetails = null;
        try {
            requestMap.put("api", confirmProvisioningRequest.getApi());
            requestMap.put("provisioningStatus", provisonStatus);

            if ( provisonStatus.equalsIgnoreCase(HCEConstants.FAILURE) && (!(failureReason.equalsIgnoreCase(HCEConstants.NULL)) || (failureReason.isEmpty())))
                requestMap.put("failureReason", confirmProvisioningRequest.getFailureReason());

            hitVisaServices = new HitVisaServices(env);
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/confirmProvisioning" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/confirmProvisioning";
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestMap.toString(), resourcePath, "PUT");

            //Update the card status to active

            LOGGER.debug("Enter ProvisionManagementServiceImpl->ConfirmProvisioning->Update card status to Active");
            cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(confirmProvisioningRequest.getVprovisionedTokenID());
            if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
                cardDetails = cardDetailsList.get(0);
            }else{
                throw new HCEActionException(HCEMessageCodes.getCardDetailsNotExist());
            }

            /*cardDetails.setStatus(HCEConstants.ACTIVE);
            cardDetailRepository.save(cardDetails);
*/
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
            }

            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                //TODO:Store the vProvisonTokenID in the DB
                cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                cardDetails.setStatus(HCEConstants.ACTIVE);
                cardDetailRepository.save(cardDetails);
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ConfirmProvisioning");
                return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());

            }
            else
            {
                Map errorMap = new LinkedHashMap();
                if (null != jsonResponse) {
                    errorMap.put(HCEConstants.RESPONSE_CODE, jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ConfirmProvisioning");
                return errorMap;

            }


        }catch (Exception e) {
            LOGGER.error("Exception occured",e);
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ConfirmProvisioning");
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
    }

    public Map<String, Object> ActiveAccountManagementReplenish (ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest) {
        //TODO:Check vProvisonID is valid or not
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
        CardDetails cardDetails = null;
        Map<String, Object> responseMap = new LinkedHashMap<>();
        List tvlData = activeAccountManagementReplenishRequest.getTvl();
        try{
            signature.put("mac",activeAccountManagementReplenishRequest.getMac());
            requestMap.put("signature" ,signature);
            dynParams.put("api",activeAccountManagementReplenishRequest.getApi());
            dynParams.put("sc",activeAccountManagementReplenishRequest.getSc());

            for(int i=0;i<tvlData.size();i++)
            {
                tvl.put(tvlData.get(i));
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
            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());

            }


            if (responseEntity.getStatusCode().value() == 200) {
                //TODO:Store the vProvisonTokenID in the DB
                List<CardDetails> cardDetailsList = cardDetailRepository.findByVisaProvisionTokenId(vProvisionedTokenID);
                if(cardDetailsList!=null && !cardDetailsList.isEmpty()){
                    cardDetails = cardDetailsList.get(0);
                    cardDetails.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    cardDetails.setReplenishOn(HCEUtil.convertDateToTimestamp(new Date()));
                    cardDetails.setStatus(HCEConstants.ACTIVE);
                    cardDetailRepository.save(cardDetails);
                }
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                return responseMap;

            }
            else
            {
                Map<String, Object> errorMap = new LinkedHashMap<>();
                if (null !=jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
                return errorMap;

            }


        }catch (Exception e) {
            LOGGER.error("Exception occured",e);
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ActiveAccountManagementReplenish");
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
    }

    public Map<String, Object> ActiveAccountManagementConfirmReplenishment(ConfirmReplenishmenRequest activeAccountManagementConfirmReplenishmentRequest) {
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
//        JSONArray tvl = new JSONArray();
//        Map responseMap = new LinkedHashMap();
        try{
            dynParams.put("api",activeAccountManagementConfirmReplenishmentRequest.getApi());
            dynParams.put("sc",activeAccountManagementConfirmReplenishmentRequest.getSc());
            hceData.put("dynParams",dynParams);
            tokenInfo.put("hceData",hceData);
            requestMap.put("tokenInfo",tokenInfo);

            vProvisionedTokenID = activeAccountManagementConfirmReplenishmentRequest.getvProvisionedTokenID();
            //https://sandbox.digital.visa.com/vts/provisionedTokens/{vProvisionedTokenID}/replenish?apiKey=key
            String url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/confirmReplenishment" + "?apiKey=" + env.getProperty("apiKey");
            String resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/confirmReplenishment";
            hitVisaServices = new HitVisaServices(env);
            responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestMap.toString(), resourcePath, "PUT");

            if (responseEntity.hasBody()) {
                response = String.valueOf(responseEntity.getBody());
                jsonResponse = new JSONObject(response);

            }
            if (responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                //TODO:Store the vProvisonTokenID in the DB
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
                return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());

            }
            else
            {
                Map<String, Object> errorMap = new LinkedHashMap<>();
                if (null !=jsonResponse) {
                    errorMap.put("responseCode", jsonResponse.getJSONObject("errorResponse").get("status"));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
                return errorMap;
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception occured",e);
            LOGGER.debug("Exception Occurred in ProvisionManagementServiceImpl->ActiveAccountManagementConfirmReplenishment");
            return hceControllerSupport.formResponse(HCEMessageCodes.getServiceFailed());
        }
    }

    public Map<String, Object> submitIDandVStepupMethod(SubmitIDandVStepupMethodRequest submitIDandVStepupMethodRequest) {
        String stepUpRequestID = submitIDandVStepupMethodRequest.getStepUpRequestID();
        String vProvisionedTokenID = submitIDandVStepupMethodRequest.getvProvisionedTokenID();
        JSONObject reqJson = new JSONObject();
        ResponseEntity responseVts = null;
        JSONObject jsonResponse = null;
        String response;
        String resourcePath = null;
        String url;
        Map<String, Object> responseMap;
        HitVisaServices hitVisaServices =null;
        Date date;
        try {
            //check if the provision id is correct or not
            long unixTimestamp = Instant.now().getEpochSecond();
            reqJson.put("stepUpRequestID",stepUpRequestID);
            reqJson.put("date",unixTimestamp);
            resourcePath = "vts/provisionedTokens/"+vProvisionedTokenID+"/stepUpOptions/method";
            url = env.getProperty("visaBaseUrlSandbox")+"/"+resourcePath+ "?apiKey=" + env.getProperty("apiKey") ;
            hitVisaServices = new HitVisaServices(env);
            responseVts = hitVisaServices.restfulServiceConsumerVisa(url, reqJson.toString(),resourcePath ,"PUT");
            if (responseVts.hasBody()) {
                response = String.valueOf(responseVts.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseVts.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                if(null != responseMap ) {
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                }
            }
            else{
                Map<String, Object> errorMap = new LinkedHashMap<>();
                if (null != jsonResponse) {
                    errorMap.put("responseCode", Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                    errorMap.put("message", jsonResponse.getJSONObject("errorResponse").get("message"));
                }
                LOGGER.debug("Exit ProvisionManagementServiceImpl->ProvisionTokenGivenPanEnrollmentId");
                return errorMap;
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

    public Map<String, Object> validateOTP(ValidateOTPRequest validateOTPRequest) {
        String vProvisionedTokenID = validateOTPRequest.getvProvisionedTokenID();
        String otpValue = validateOTPRequest.getOtpValue();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        JSONObject reqJson = new JSONObject();
        ResponseEntity responseVts = null;
        JSONObject jsonResponse = null;
        String statusCode = "";
        Map<String, Object> responseMap= new LinkedHashMap<>();
        String response;
        String resourcePath = null;
        String url;
        long unixTimestamp;
        try {
            unixTimestamp = Instant.now().getEpochSecond();
            reqJson.put("otpValue", otpValue);
            reqJson.put("date", unixTimestamp);
            url = env.getProperty("visaBaseUrlSandbox") + "/vts/provisionedTokens/" + vProvisionedTokenID + "/stepUpOptions/validateOTP" + "?apiKey=" + env.getProperty("apiKey");
            resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/stepUpOptions/validateOTP";
            responseVts = hitVisaServices.restfulServiceConsumerVisa(url, reqJson.toString(), resourcePath, "POST");
            if (responseVts.hasBody()) {
                response = String.valueOf(responseVts.getBody());
                jsonResponse = new JSONObject(response);
            }
            if (responseVts.getStatusCode().value() == HCEConstants.REASON_CODE7){
                responseMap = JsonUtil.jsonToMap(jsonResponse);
                if(null != responseMap) {
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                }
            } else {
                if (jsonResponse !=null)
                    statusCode= Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status"));
                switch (statusCode){
                    case "400" :
                    case "401" :
                        responseMap.put("responseCode", HCEMessageCodes.getIncorrectOtp());
                        break;

                    default:
                        responseMap.put("responseCode",HCEMessageCodes.getFailedAtThiredParty());
                }
                /*if (null != jsonResponse) {
                    responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                    responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
                }*/
            }

        } catch (HCEActionException searchTokensHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensHCEactionException);
            throw searchTokensHCEactionException;
        } catch (Exception searchTokensException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;

    }

    public Map<String, Object> getStepUpOptions(GetStepUpOptionsRequest getStepUpOptionsRequest) {
        String vProvisionedTokenID = getStepUpOptionsRequest.getvProvisionedTokenID();
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        ResponseEntity responseVts = null;
        JSONObject jsonResponse = new JSONObject();
        String response;
        String resourcePath = null;
        String request = "";
        String url;
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/stepUpOptions";
            url = env.getProperty("visaBaseUrlSandbox") + "/" + resourcePath + "?apiKey=" + env.getProperty("apiKey");
            responseVts = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "GET");
            if (responseVts.hasBody()) {
                response = String.valueOf(responseVts.getBody());
                jsonResponse = new JSONObject(response);
            }
            if (responseVts.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            } else {
                if (null != jsonResponse) {
                    responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                    responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
                }
            }

        } catch (HCEActionException searchTokensHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensHCEactionException);
            throw searchTokensHCEactionException;
        } catch (Exception searchTokensException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->searchTokens", searchTokensException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    @Override
    public Map<String, Object> replenishODAData(ReplenishODADataRequest replenishODADataRequest) {
        String vProvisionedTokenID = replenishODADataRequest.getVprovisionedTokenID();
        String resourcePath = null;
        String url = null;
        String request = "";
        String response = null;
        JSONObject jsonResponse = new JSONObject();
        Map<String, Object> responseMap = new LinkedHashMap<>();
        ResponseEntity responseVts = null;
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        try{
            resourcePath = "vts/provisionedTokens/" + vProvisionedTokenID + "/replenishODA";
            url = env.getProperty("visaBaseUrlSandbox") + "/" + resourcePath + "?apiKey=" + env.getProperty("apiKey");
            responseVts = hitVisaServices.restfulServiceConsumerVisa(url, request, resourcePath, "POST");
            if (responseVts.hasBody()) {
                response = String.valueOf(responseVts.getBody());
                jsonResponse = new JSONObject(response);
            }
            if (responseVts.getStatusCode().value() == HCEConstants.REASON_CODE7) {
                responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            } else {
                if (null != jsonResponse) {
                    responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                    responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
                }
            }

        }catch (HCEActionException replenishODAData) {
            LOGGER.error("Exception occurred in ProvisionManagementServiceImpl->replenishODAData", replenishODAData);
            throw replenishODAData;
        } catch (Exception replenishODAData) {
            LOGGER.error("Exception occurred in ProvisionManagementServiceImpl->replenishODAData", replenishODAData);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;

    }
}
