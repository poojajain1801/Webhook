package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.CardDetail;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.comviva.mfs.hce.appserver.util.mdes.DeviceRegistrationMdes;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceVts;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService,UserDetailRepository userDetailRepository,HCEControllerSupport hceControllerSupport,DeviceRegistrationMdes deviceRegistrationMdes,EnrollDeviceVts enrollDeviceVts,CardDetailRepository cardDetailRepository ) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.deviceRegistrationMdes = deviceRegistrationMdes;
        this.enrollDeviceVts = enrollDeviceVts;
        this.cardDetailRepository = cardDetailRepository;
    }

    /**
     * @param enrollDeviceRequest Register Device Parameters
     * @return Response
     */
    @Override
    @Transactional
    public Map<String, Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest) {
        String vClientID = env.getProperty("vClientID");
        Map<String, Object> response = new HashMap();
        Map mdesRespMap = new HashMap();
        Map vtsRespMap = new HashMap();
        boolean isMdesDevElib = false;
        String respCodeMdes = "";
        DeviceRegistrationResponse devRegRespMdes = null;
        UserDetail userDetail;
        DeviceInfo deviceInfo;
        try {
            List<UserDetail> userDetails = userDetailRepository.findByUserIdAndStatus(enrollDeviceRequest.getUserId(), HCEConstants.ACTIVE);

            if(userDetails!=null && !userDetails.isEmpty()){
                userDetail = userDetails.get(0);
                List<DeviceInfo> deviceInfos = deviceDetailRepository.findByClientDeviceIdAndStatus(enrollDeviceRequest.getClientDeviceID(),HCEConstants.INITIATE);
                if(deviceInfos!=null && !deviceInfos.isEmpty()){
                    deviceInfo = deviceInfos.get(0);
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
            // MDES : Check device eligibility from MDES api.
            //JSONObject mdesResponse=new JSONObject();
           // deviceRegistrationMdes.setEnrollDeviceRequest(enrollDeviceRequest);
            isMdesDevElib = deviceRegistrationMdes.checkDeviceEligibility(enrollDeviceRequest);


            if (isMdesDevElib) {
                // MDES : Register with CMS-d
                devRegRespMdes = deviceRegistrationMdes.registerDevice(enrollDeviceRequest);
                respCodeMdes = devRegRespMdes.getResponse().get(HCEConstants.RESPONSE_CODE).toString();
                // If registration fails for MDES return error
                if (!HCEMessageCodes.getSUCCESS().equalsIgnoreCase(respCodeMdes)) {
                    mdesRespMap.put(HCEConstants.MDES_RESPONSE_CODE, devRegRespMdes.getResponse().get(HCEConstants.STATUS_CODE).toString());
                    mdesRespMap.put(HCEConstants.MDES_MESSAGE, devRegRespMdes.getResponse().get(HCEConstants.STATUS_MESSAGE).toString());
                    response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                    response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                    response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

                } else {
                    response.put(HCEConstants.MDES_RESPONSE_MAP, devRegRespMdes.getResponse());
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
                mdesRespMap.put(HCEConstants.MDES_MESSAGE, "Device is not eligible");
                mdesRespMap.put(HCEConstants.MDES_RESPONSE_CODE, "207");
                response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

            }
            // *******************VTS : Register with VTS Start**********************
            String vtsResp = enrollDeviceVts.register(vClientID,enrollDeviceRequest);
            JSONObject vtsJsonObject = new JSONObject(vtsResp);
            if (!vtsJsonObject.get(HCEConstants.STATUS_CODE).equals(HCEMessageCodes.getSUCCESS())) {
                vtsRespMap.put(HCEConstants.VTS_MESSAGE, vtsJsonObject.get(HCEConstants.STATUS_MESSAGE));
                vtsRespMap.put(HCEConstants.VTS_RESPONSE_CODE, vtsJsonObject.get(HCEConstants.STATUS_CODE));
                response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getDeviceRegistrationFailed());
                response.put(HCEConstants.VISA_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
            } else {
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsResp);
                deviceInfo.setIsVisaEnabled(HCEConstants.ACTIVE);
                deviceInfo.setVClientId(vClientID);
                deviceInfo.setStatus(HCEConstants.ACTIVE);
                deviceInfo.setDeviceName(enrollDeviceRequest.getVts().getDeviceInfo().getDeviceName());
                deviceInfo.setOsVersion(enrollDeviceRequest.getVts().getDeviceInfo().getOsVersion());
                deviceInfo.setModifiedOn(HCEUtil.convertDateToTimestamp(new Date()));
                deviceDetailRepository.save(deviceInfo);
                response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.getSUCCESS());
                response.put(HCEConstants.VISA_FINAL_MESSAGE, "OK");
            }

            if(HCEConstants.ACTIVE.equals(env.getProperty("is.hvt.supported"))){
                response.put("isHvtSupported", true);
            }else{
                response.put("isHvtSupported", false);
            }

            response.put("hvtThreshold", env.getProperty("hvt.limit"));

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
    @Override
    @Transactional
    public Map<String, Object> unRegisterDevice(UnRegisterReq unRegisterReq) {


        String userID = null;
        String imei = null;
        DeviceInfo deviceInfo;
        String paymentAppInstanceID = null;
        JSONObject requestJson = null;
        ResponseEntity responseEntitye = null;
        String url = null;

        try {
            userID = unRegisterReq.getUserId();
            imei = unRegisterReq.getImei();
           /* clintDeviceID = unRegisterReq.getClientDeviceID();
            paymentAppInstanceID = unRegisterReq.getPaymentAppInstanceId();*/

            //If user id and imei is null and paymentAppInstanceID or clint device is is null throw Insuficiant input data
            if(imei.isEmpty()||userID.isEmpty())
            {
                //Retrun Insufucaiant input data
                hceControllerSupport.formResponse(HCEMessageCodes.getInsufficientData());
            }

            //Hit master card for the device unregister
            //TODO: Retrive the payment app instance in from the device info table

            requestJson = new JSONObject();
            requestJson.put("responseHost","Wallet.mahindracomviva.com");
            requestJson.put("requestId","12344");
            requestJson.put("paymentAppInstanceId",paymentAppInstanceID);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport")+ env.getProperty("digitization")+"/unregister";
            responseEntitye = hitMasterCardService.restfulServiceConsumerMasterCard(url,requestJson.toString(),"POST");
            if (responseEntitye.getStatusCode().value()!=HCEConstants.REASON_CODE7)
            {
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
            /*
            * Get all the imei number for the user id
            * Update the status for all the IMEI number
            * Get the FCM registration id for all the IMEI
            * Send remote notification for to all the device*/

            //userid and imei and status
            deviceInfo = deviceDetailRepository.findDeviceDetailsWithIMEI(imei,userID,HCEConstants.ACTIVE);


            if(deviceInfo!=null)
            {
                cardDetailRepository.updateCardDetails(deviceInfo.getClientDeviceId(),HCEConstants.INACTIVE);
                deviceInfo.setStatus(HCEConstants.INACTIVE);
                deviceDetailRepository.save(deviceInfo);

            }else
            {
                throw new HCEActionException(HCEMessageCodes.getDeviceNotRegistered());

            }



           // cardDetailsList = cardDetailRepository.findCardDetailsByIdentifier()


        }catch (HCEActionException unRegisterException)
        {
            unRegisterException.printStackTrace();
            throw unRegisterException;
        }
        catch(Exception unRegisterException)
        {
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
    }

    public Map<String, Object> getDeviceInfo(GetDeviceInfoRequest getDeviceInfo) {
        String paymentAppInstanceId = getDeviceInfo.getPaymentAppInstanceId();
        String tokenUniqueReference = getDeviceInfo.getTokenUniqueReference();
        HitMasterCardService hitMasterCardService = new HitMasterCardService();
        JSONObject reqMdes = new JSONObject();
        ResponseEntity responseMdes ;
        JSONObject jsonResponse = null;
        String response = null;
        String url=null ;
        try {
            reqMdes.put("paymentAppInstanceId", paymentAppInstanceId);
            reqMdes.put("tokenUniqueReference", tokenUniqueReference);
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("digitizationpath") + "/getDeviceInfo" ;
            responseMdes = hitMasterCardService.restfulServiceConsumerMasterCard(url,reqMdes.toString(),"POST");

            if (responseMdes.hasBody()) {
                response = String.valueOf(responseMdes.getBody());
                jsonResponse = new JSONObject(response);
            }
            if(responseMdes.getStatusCode().value()==HCEConstants.REASON_CODE7) {
                hceControllerSupport.formResponse(HCEMessageCodes.getSUCCESS());
            }
            else{
                throw new HCEActionException(HCEMessageCodes.getFailedAtThiredParty());
            }
        } catch (HCEActionException getDeviceInfoHCEactionException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->activate", getDeviceInfoHCEactionException);
            throw getDeviceInfoHCEactionException;

        } catch (Exception getDeviceInfoException) {
            LOGGER.error("Exception occured in CardDetailServiceImpl->enrollPan", getDeviceInfoException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return JsonUtil.jsonToMap(jsonResponse);
    }

}
