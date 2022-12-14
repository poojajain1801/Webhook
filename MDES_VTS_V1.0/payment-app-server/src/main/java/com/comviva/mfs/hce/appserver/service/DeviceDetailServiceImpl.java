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
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.PerformUserLifecycle;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceDasRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementVisaRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.VtsCerts;
import com.comviva.mfs.hce.appserver.mapper.vts.HitVisaServices;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.HvtManagement;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.HvtManagementRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TokenLifeCycleManagementService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RemoteNotification;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsFactory;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsResponse;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.UniqueIdType;
import com.comviva.mfs.hce.appserver.util.mdes.DeviceRegistrationMdes;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceVts;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class DeviceDetailServiceImpl implements DeviceDetailService {
    private final DeviceDetailRepository deviceDetailRepository;
    private final CardDetailRepository cardDetailRepository;
    private final UserDetailService userDetailService;
    private final UserDetailRepository userDetailRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDetailServiceImpl.class);
    private final HCEControllerSupport hceControllerSupport;
    private final DeviceRegistrationMdes deviceRegistrationMdes;
    private final EnrollDeviceVts enrollDeviceVts;

    @Autowired
    private Environment env;

    @Autowired
    private HitMasterCardService hitMasterCardService;

    @Autowired
    private TokenLifeCycleManagementService tokenLifeCycleManagementService;

    @Autowired
    private PerformUserLifecycle performLCMobj;

    @Autowired
    private HvtManagementRepository hvtManagementRepository;

    @Value("${liquibase.parameters.paymentAppId}")
    private String paymentAppId;

    @Autowired
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService,
                                   UserDetailRepository userDetailRepository,HCEControllerSupport hceControllerSupport,
                                   DeviceRegistrationMdes deviceRegistrationMdes, EnrollDeviceVts enrollDeviceVts,
                                   CardDetailRepository cardDetailRepository,
                                   HvtManagementRepository hvtManagementRepository) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.deviceRegistrationMdes = deviceRegistrationMdes;
        this.enrollDeviceVts = enrollDeviceVts;
        this.cardDetailRepository = cardDetailRepository;
        this.hvtManagementRepository = hvtManagementRepository;
    }

    /**
     * @param enrollDeviceRequest Register Device Parameters
     * @return Response
     */
    @Override
    @Transactional
    public Map<String, Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest) {
        String vClientID = env.getProperty("vClientID");
        Map<String, Object> response = new HashMap<>();
        String isMastercardRegistrationDone = null;
        String isVisaDeviceRegistrationDone = null;
        Map<String, Object> mdesRespMap = new HashMap();
        Map<String, Object> vtsRespMap = new HashMap<>();
        boolean isMdesDevElib = false;
        String respCodeMdes = "";
        String schemeType = "";
        //DeviceRegistrationResponse devRegRespMdes = null;
        UserDetail userDetail;
        DeviceInfo deviceInfo;
        Map<String, Object> dasResponse = null;
        try {
            List<UserDetail> userDetails = userDetailRepository.findByUserIdAndStatus(enrollDeviceRequest.getUserId(), HCEConstants.ACTIVE);
            if(userDetails!=null && !userDetails.isEmpty()){
                userDetail = userDetails.get(0);
                Optional<DeviceInfo> deviceInfos = deviceDetailRepository.findByClientDeviceId(enrollDeviceRequest.getClientDeviceID());
                if (deviceInfos.isPresent()){
                    isMastercardRegistrationDone = deviceInfos.get().getIsMastercardEnabled();
                    isVisaDeviceRegistrationDone = deviceInfos.get().getIsVisaEnabled();
                }

                if((deviceInfos.isPresent()) ||(isMastercardRegistrationDone.equalsIgnoreCase("Y")
                        &&isVisaDeviceRegistrationDone.equalsIgnoreCase("Y"))){
					deviceInfo = deviceInfos.get();
                    if(!userDetail.getClientWalletAccountId().equals(deviceInfo.getUserDetail().getClientWalletAccountId())){
                        throw new HCEActionException(HCEMessageCodes.getInvalidUserAndDevice());
                    }
                }else{
                    throw new HCEActionException(HCEMessageCodes.getInvalidClientDeviceId());
                }

            }else{
                throw new HCEActionException(HCEMessageCodes.getInvalidUser());
            }
            deviceInfo.setRnsRegistrationId(enrollDeviceRequest.getGcmRegistrationId());


            // *********************MDES : Check device eligibility from MDES api.************************
            schemeType = enrollDeviceRequest.getSchemeType();
            if(null == schemeType || "".equalsIgnoreCase(schemeType)) {
                schemeType = "ALL";
            }

            if (("ALL".equalsIgnoreCase(schemeType))||("MASTERCARD".equalsIgnoreCase(schemeType))) {
                isMdesDevElib = deviceRegistrationMdes.checkDeviceEligibility(enrollDeviceRequest);
            }
            if (isMdesDevElib) {
                // MDES : Register with CMS-d
                JSONObject responseJsonMdes = deviceRegistrationMdes.registerDevice(enrollDeviceRequest);
                respCodeMdes = responseJsonMdes.getString("responseCode");
                mdesRespMap = JsonUtil.jsonStringToHashMap(responseJsonMdes.getJSONObject("mdes").toString());
                // If registration fails for MDES return error
                if (!HCEMessageCodes.getSUCCESS().equalsIgnoreCase(respCodeMdes)) {
                    mdesRespMap.put(HCEConstants.MDES_RESPONSE_CODE,respCodeMdes);
                    mdesRespMap.put(HCEConstants.MDES_MESSAGE, responseJsonMdes.getString("message"));
                    response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                    response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                    response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);
                } else {
                    response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);
                    response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.getSUCCESS());
                    response.put(HCEConstants.MDES_FINAL_MESSAGE, "OK");
                    deviceInfo.setPaymentAppInstanceId(enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
                    deviceInfo.setPaymentAppId(enrollDeviceRequest.getMdes().getPaymentAppId());
                    deviceInfo.setOsVersion(enrollDeviceRequest.getMdes().getDeviceInfo().getOsVersion());
                    deviceInfo.setIsMastercardEnabled(HCEConstants.ACTIVE);
                    deviceInfo.setDeviceName(enrollDeviceRequest.getMdes().getDeviceInfo().getDeviceName());
                    deviceInfo.setStatus(HCEConstants.ACTIVE);
                    deviceInfo.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    deviceDetailRepository.save(deviceInfo);
                }

            }else{

                //throw error device not eligible.
                mdesRespMap = hceControllerSupport.formResponse("207");
                response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

            }
            // *******************VTS : Register with VTS Start**********************
            if (("ALL".equalsIgnoreCase(schemeType))||("VISA".equalsIgnoreCase(schemeType))) {
                String vtsResp = enrollDeviceVts.register(vClientID, enrollDeviceRequest);
                JSONObject vtsRespJson = null;
                JSONObject vtsJsonObject = new JSONObject(vtsResp);
                if (!vtsJsonObject.get(HCEConstants.STATUS_CODE).equals(HCEMessageCodes.getSUCCESS())) {
                    vtsRespMap.put(HCEConstants.VTS_MESSAGE, vtsJsonObject.get(HCEConstants.STATUS_MESSAGE));
                    vtsRespMap.put(HCEConstants.VTS_RESPONSE_CODE, vtsJsonObject.get(HCEConstants.STATUS_CODE));
                    response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                    response.put(HCEConstants.VISA_FINAL_MESSAGE, "NOTOK");
                    response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
                } else {
                    vtsRespMap = JsonUtil.jsonStringToHashMap(vtsResp);
                    deviceInfo.setIsVisaEnabled(HCEConstants.ACTIVE);
                    deviceInfo.setVClientId(vClientID);
                    deviceInfo.setStatus(HCEConstants.ACTIVE);
                    deviceInfo.setDeviceName(enrollDeviceRequest.getVts().getDeviceInfo().getDeviceName());
                    deviceInfo.setOsVersion(enrollDeviceRequest.getVts().getDeviceInfo().getOsVersion());
                    deviceInfo.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                    if(enrollDeviceRequest.getVts().getChannelSecurityContext() != null) {
                        dasResponse = enrollDeviceDas(enrollDeviceRequest.getVts().getDasRequest(),
                                enrollDeviceRequest.getClientDeviceID());
                        if (null != dasResponse && null == dasResponse.get("errorResponse")) {
                            deviceDetailRepository.save(deviceInfo);
                            response.put(HCEConstants.VISA_FINAL_MESSAGE, "OK");
                            response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
                            response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getSUCCESS());
                            vtsRespMap.put("dasResponse", dasResponse);
                        } else {
                            response.put(HCEConstants.VTS_MESSAGE, "Enroll Device for DAS failed");
                            vtsRespMap.put("dasResponse", dasResponse);
                            vtsRespMap.put(HCEConstants.VTS_RESPONSE_CODE,HCEMessageCodes.getInvalidOperation());
                            response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                            response.put(HCEConstants.VISA_FINAL_MESSAGE, "NOTOK");
                            response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
                        }
                    } else {
                        deviceDetailRepository.save(deviceInfo);
                        response.put(HCEConstants.VISA_FINAL_MESSAGE, "OK");
                        response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
                        response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getSUCCESS());
                    }
                }
            }else{
                vtsRespMap.put(HCEConstants.VTS_MESSAGE,"INVALID OPERATION");
                vtsRespMap.put(HCEConstants.VTS_RESPONSE_CODE,HCEMessageCodes.getInvalidOperation());
                response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                response.put(HCEConstants.VISA_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
            }
             //commented this line in SBi as SBi migrating from 5.2.1->6.2.1
             //and SBI database is old in that new column don't exist .
            //HvtManagement hvtManagement = hvtManagementRepository.findByPaymentAppId(paymentAppId);
            if (HCEConstants.ACTIVE.equals(env.getProperty("is.hvt.supported"))) {
                response.put("isHvtSupported", true);
            } else {
                response.put("isHvtSupported", false);
            }

            response.put("hvtThreshold", env.getProperty("hvt.limit"));

            if (response.get(HCEConstants.VISA_FINAL_CODE).equals(HCEMessageCodes.getSUCCESS()) || response.get(HCEConstants.MDES_FINAL_CODE).equals(HCEMessageCodes.getSUCCESS()) ){
                response.put("responseCode",HCEMessageCodes.getSUCCESS());
            }else {
                response.put("responseCode",HCEMessageCodes.getDeviceRegistrationFailed());
            }

            //******************VTS :Register with END***********************************
            return response;
        }
        catch(HCEActionException regDeviceactionException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", regDeviceactionException);
            throw regDeviceactionException;

        }catch(Exception regDeviceException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", regDeviceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
    }


    /**
     * unregisterDevice
     * @param unRegisterReq unRegisterReq
     * @return map
     */
    @Override
    @Transactional
    public Map<String, Object> unRegisterDevice(UnRegisterReq unRegisterReq) {
//        LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest = new LifeCycleManagementVisaRequest();
        DeviceInfo deviceInfo = null;
        Map<String, Object> responseMap = null;
        String userID = null;
        String imei = null;
        HttpStatus statusCode = null;
        RnsGenericRequest rnsGenericRequest;
        HashMap<String, String> rnsNotificationData = new HashMap<>();
        try {
            imei = unRegisterReq.getImei();
            userID = unRegisterReq.getUserId();
            if((imei.isEmpty()||(userID.isEmpty()))) {
                LOGGER.error("Either imei or userid is missing");
                hceControllerSupport.formResponse(HCEMessageCodes.getInsufficientData());
            }
            LOGGER.debug("MasterCard unregister is called for userID :"+userID);
            deviceInfo = deviceDetailRepository.findDeviceDetailsWithIMEI(imei,userID,HCEConstants.ACTIVE);
            if (deviceInfo == null){
                LOGGER.error(" No Device is registered with UserID :"+userID);
                responseMap = new HashMap<>();
                responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                return responseMap;
            }
            //Send Delete card request to VISA
            List<CardDetails> cardDetails = deviceInfo.getCardDetails();
            deleteVISACards(cardDetails);
            cardDetailRepository.updateCardDetails(deviceInfo.getClientDeviceId(),HCEConstants.INACTIVE);

            String rnsID = getRnsRegId(imei, userID);
            deviceInfo.setStatus(HCEConstants.INACTIVE);
            deviceDetailRepository.save(deviceInfo);

            //Sending notification to device
            rnsGenericRequest = new RnsGenericRequest();
            rnsNotificationData.put("imei",imei);
            rnsNotificationData.put("userID",userID);
            rnsNotificationData.put(HCEConstants.OPERATION, "DELETEDEVICE");
            rnsGenericRequest.setIdType(UniqueIdType.ALL);
            rnsGenericRequest.setRegistrationId(rnsID);
            rnsGenericRequest.setRnsData(rnsNotificationData);
            Map<String, String> rnsData = rnsGenericRequest.getRnsData();
            rnsData.put("TYPE", rnsGenericRequest.getIdType().name());
            JSONObject payloadObject = new JSONObject();
            payloadObject.put("data", new JSONObject(rnsData));
            payloadObject.put("to", rnsGenericRequest.getRegistrationId());
            LOGGER.debug("RemoteNotificationServiceImpl -> unRegisterDevice -> RNS ID "+rnsGenericRequest.getRegistrationId());
            payloadObject.put("priority","high");
            final int ttl = 2160000;
            payloadObject.put("time_to_live", ttl);
            RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM, env);
            RnsResponse response = rns.sendRns(payloadObject.toString().getBytes());
            Gson gson = new Gson();
            String json = gson.toJson(response);
            LOGGER.debug("RemoteNotificationServiceImpl -> unRegisterDevice ->Raw response from FCM server "+json);
            responseMap = new HashMap<>();
            responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
            responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
            if (Integer.valueOf(response.getErrorCode()) != HCEConstants.REASON_CODE7) {
                responseMap.put("Notification to device Status code", response.getErrorCode());
            } else {
                responseMap.put("Notification to device Status code", HCEMessageCodes.getSUCCESS());
            }
        }catch(HCEActionException unRegisterDeviceHCEactionException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->unRegisterDevice",unRegisterDeviceHCEactionException);
            throw unRegisterDeviceHCEactionException;
        }catch(Exception unRegisterDeviceException){
            LOGGER.error("Exception occured in CardDetailServiceImpl->unRegisterDevice", unRegisterDeviceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }


    @Override
    public Map<String, Object> enrollDeviceDas(EnrollDeviceDasRequest enrollDeviceDasReqPojo, String clientDeviceId) {
        HitVisaServices hitVisaServices = new HitVisaServices(env);
        String vClientID = env.getProperty("vClientID");
        Map<String, Object> responseMap = new HashMap<>();
        String url;
        String resourcePath, strResponse;
        ResponseEntity responseEntity = null;
        JSONObject jsonResponse = new JSONObject();
        String domainName="";

        try {
//            List<DeviceInfo> deviceInfos = deviceDetailRepository.findByClientDeviceIdAndStatus
//                    (clientDeviceId, HCEConstants.ACTIVE);
//
//            if (deviceInfos.isEmpty()) {
//                LOGGER.error("Device is not in active state");
//                // send error message back not success
//                responseMap.put("responseCode", HCEMessageCodes.getDeviceNotRegistered());
//                responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getDeviceNotRegistered()));
//                return responseMap;
//            } else {
                //if device is present with active state
                JSONObject requestJson = new JSONObject();
                JSONObject deviceInfo = new JSONObject();
                JSONObject deviceProfile = new JSONObject();
                List<JSONObject> visaCertList = new ArrayList<>();

                deviceInfo.put("deviceName", enrollDeviceDasReqPojo.getDeviceInfo().getDeviceName());
                deviceInfo.put("deviceType", enrollDeviceDasReqPojo.getDeviceInfo().getDeviceType());
                deviceInfo.put("osType", enrollDeviceDasReqPojo.getDeviceInfo().getOsType());

                deviceProfile.put("hardwareBackKeystoreSupport", enrollDeviceDasReqPojo.getDeviceProfile().hardwareBackKeystoreSupport);
                deviceProfile.put("keyAttestationSupport", enrollDeviceDasReqPojo.getDeviceProfile().keyAttestationSupport);

                List<VtsCerts> visaCerts = enrollDeviceDasReqPojo.getVisaCertReferenceList();

                if(!visaCerts.isEmpty()) {
                    for(VtsCerts vtsCerts: visaCerts) {
                        JSONObject map = new JSONObject();
                        map.put("certUsage", vtsCerts.getCertUsage());
                        map.put("vCertificateID",vtsCerts.getVCertificateID());
                        visaCertList.add(map);
                    }
                }

                requestJson.put("deviceCertList", enrollDeviceDasReqPojo.getDeviceCertList());
                requestJson.put("deviceInfo", deviceInfo);
                requestJson.put("deviceProfile", deviceProfile);
                requestJson.put("visaCertReferenceList", visaCertList);
//                requestJson.put("profileAppID", enrollDeviceDasReqPojo.getProfileAppID());

                LOGGER.info("before hitting enroll device for das");
                resourcePath = "vts/das/clients/"+vClientID+"/devices/"+clientDeviceId;

                domainName = env.getProperty("visaApiUrl").trim();

                url = domainName + "/vts/das/clients/" + vClientID + "/devices/"+ clientDeviceId + "?apiKey=" + env.getProperty("apiKey");

                responseEntity = hitVisaServices.restfulServiceConsumerVisa(url, requestJson.toString(), resourcePath, "POST");

                if (responseEntity.hasBody()) {
                    strResponse = String.valueOf(responseEntity.getBody());
                    jsonResponse = new JSONObject(strResponse);
                }
                if ((responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE8 || responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE7)
                        ||responseEntity.getStatusCode().value() == HCEConstants.REASON_CODE9) {
                    responseMap = JsonUtil.jsonStringToHashMap(jsonResponse.toString());
                    responseMap.put("responseCode", HCEMessageCodes.getSUCCESS());
                    responseMap.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.getSUCCESS()));
                } else {
                    if (null != jsonResponse) {
                        responseMap.put(HCEConstants.RESPONSE_CODE, Integer.toString((Integer) jsonResponse.getJSONObject("errorResponse").get("status")));
                        responseMap.put(HCEConstants.MESSAGE, jsonResponse.getJSONObject("errorResponse").get("message"));
                    }
                }
//            }
        } catch(HCEActionException enrollDeviceForDasException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->enrollDeviceDas",enrollDeviceForDasException);
            throw enrollDeviceForDasException;
        }catch(Exception enrollDeviceForDasException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->enrollDeviceDas", enrollDeviceForDasException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return responseMap;
    }

    public void deleteVISACards(List<CardDetails> visaCardList) {
        LifeCycleManagementVisaRequest lifeCycleManagementVisaRequest = null;
        Map<String, Object> responseMap1 = null;
        lifeCycleManagementVisaRequest = new LifeCycleManagementVisaRequest();
        lifeCycleManagementVisaRequest.setOperation("DELETE");
        lifeCycleManagementVisaRequest.setReasonCode("CUSTOMER_CONFIRMED");
        for (int i=0 ; i<visaCardList.size() ; i++) {
            if ((visaCardList.get(i).getVisaProvisionTokenId() != null)
                    && ("Y").equals(visaCardList.get(i).getStatus()) ){
                lifeCycleManagementVisaRequest.setVprovisionedTokenID(visaCardList.get(i).getVisaProvisionTokenId());
                responseMap1 = tokenLifeCycleManagementService.lifeCycleManagementVisa(lifeCycleManagementVisaRequest);
                LOGGER.debug("Visa response after unregister ****************   " + responseMap1);
            }
        }
    }

    private String getRnsRegId(String imei, String userID) {
        String rnsRegID = null;
        DeviceInfo deviceInfo = deviceDetailRepository.findDeviceDetailsWithIMEI(imei, userID, HCEConstants.ACTIVE);
        if(deviceInfo != null ){
            rnsRegID = deviceInfo.getRnsRegistrationId();
        }
        return rnsRegID;
    }
}

