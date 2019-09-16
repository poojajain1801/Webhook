package com.comviva.mfs.hce.appserver.util.mdes;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.MdesDeviceRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.util.common.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DeviceRegistrationMdes
{
    @Autowired
    public Environment env;

    @Autowired
    private HitMasterCardService hitMasterCardService;
    @Autowired
    private DeviceDetailRepository deviceDetailRepository;
    private final HCEControllerSupport hceControllerSupport;

    public void setEnv(Environment env) {
        this.env = env;
    }


    public void setHitMasterCardService(HitMasterCardService hitMasterCardService) {
        this.hitMasterCardService = hitMasterCardService;
    }

    @Autowired
    public DeviceRegistrationMdes(HCEControllerSupport hceControllerSupport) {
        this.hceControllerSupport = hceControllerSupport;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationMdes.class);

   /* private String registerDeviceWithCMSD(EnrollDeviceRequest enrollDeviceRequest)
    {
        ResponseEntity responseEntity = null;
        String response = null;
        String url = null;
        JSONObject jsonRegDevice = null;
        MdesDeviceRequest mdesDeviceRequest = null;
        JSONObject rnsInfo = null;
        String id = "";
        try
        {
            jsonRegDevice = new JSONObject();
            mdesDeviceRequest = enrollDeviceRequest.getMdes();
            //TODO:Generate a random number for the request ID
            jsonRegDevice.put("requestId","12345678");
            jsonRegDevice.put("paymentAppId", mdesDeviceRequest.getPaymentAppId());
            jsonRegDevice.put("paymentAppInstanceId", mdesDeviceRequest.getPaymentAppInstanceId());
            jsonRegDevice.put("publicKeyFingerprint", mdesDeviceRequest.getPublicKeyFingerprint());
            jsonRegDevice.put("rgk", mdesDeviceRequest.getRgk());
            jsonRegDevice.put("deviceFingerprint", mdesDeviceRequest.getDeviceFingerprint());
            jsonRegDevice.put("newMobilePin", mdesDeviceRequest.getMobilePin());

            rnsInfo = new JSONObject();
            rnsInfo.put("rnsRegistrationId", enrollDeviceRequest.getGcmRegistrationId());
            jsonRegDevice.put("rnsInfo", rnsInfo);



            url = this.env.getProperty("mdesip")  +this.env.getProperty("mpamanagementPath");
            id = "register";

            responseEntity = this.hitMasterCardService.restfulServiceConsumerMasterCard(url, jsonRegDevice.toString(), "POST",id);
            if ((responseEntity.hasBody()) && (responseEntity.getStatusCode().value() == 200)) {}
            return String.valueOf(responseEntity.getBody());
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occured", e);
        }
        return null;
    }*/

    public JSONObject registerDevice(EnrollDeviceRequest enrollDeviceRequest) {
        ResponseEntity responseEntity = null;
        String response = null;
        String url = null;
        JSONObject jsonRegDevice = null;
        JSONObject responseJson = null;
        JSONObject mdes = null;
        MdesDeviceRequest mdesDeviceRequest = null;
        JSONObject rnsInfo = null;
        String id = "";
        String requestId = null;
        try {
            jsonRegDevice = new JSONObject();
            mdesDeviceRequest = enrollDeviceRequest.getMdes();
            //TODO:Generate a random number for the request ID
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            jsonRegDevice.put("requestId", requestId);
            jsonRegDevice.put("paymentAppId", mdesDeviceRequest.getPaymentAppId());
            jsonRegDevice.put("paymentAppInstanceId", mdesDeviceRequest.getPaymentAppInstanceId());
            jsonRegDevice.put("publicKeyFingerprint", mdesDeviceRequest.getPublicKeyFingerprint());
            jsonRegDevice.put("rgk", mdesDeviceRequest.getRgk());
            jsonRegDevice.put("deviceFingerprint", mdesDeviceRequest.getDeviceFingerprint());
            jsonRegDevice.put("newMobilePin", mdesDeviceRequest.getMobilePin());

            rnsInfo = new JSONObject();
            rnsInfo.put("gcmRegistrationId", enrollDeviceRequest.getGcmRegistrationId());
            jsonRegDevice.put("rnsInfo", rnsInfo);
            url = this.env.getProperty("mdesip") + this.env.getProperty("mpamanagementPath");
            id = "register";
            LOGGER.info("Master card register device before hit --> TIME " +HCEUtil.convertDateToTimestamp(new Date()));
            responseEntity = this.hitMasterCardService.restfulServiceConsumerMasterCard(url, jsonRegDevice.toString(), "POST", id);
            LOGGER.info("Master card register device After hit --> TIME " +HCEUtil.convertDateToTimestamp(new Date()));
            if ((responseEntity.hasBody()) && (responseEntity.getStatusCode().value() == 200)){
                response =  String.valueOf(responseEntity.getBody());
                mdes = new JSONObject(response);
                responseJson = new JSONObject();
                responseJson.put("mdes",mdes);
                if (mdes.has("errors"))
                {
                    responseJson.put("responseCode", HCEMessageCodes.getFailedAtThiredParty());
                    responseJson.put("message",mdes.getJSONArray("errors").getJSONObject(0).getString("errorDescription"));
                }
                else
                {
                    responseJson.put("responseCode",HCEMessageCodes.getSUCCESS());
                    responseJson.put("message","Success");
                }

            }

        }catch (Exception e)
        {
            LOGGER.error("Exception occord in DeviceRegistrationMdes->registerDevice",e);
        }
        return responseJson;
    }

    public boolean checkDeviceEligibility(EnrollDeviceRequest enrollDeviceRequest) {
        JSONObject jsonRequest = new JSONObject();
        JSONObject jsonResp = null;
        JSONObject deviceinfo = null;
        String requestId = null;
        String response = "";
        String url = "";
        String id = "";
        String clientDeviceId = "";
        Optional<DeviceInfo> deviceDetail;
        ResponseEntity responseEntity = null;
        LOGGER.debug("Enter in DeviceRegistrationMdes:->checkDeviceEligibility");
        try {
            deviceinfo = new JSONObject(enrollDeviceRequest.getMdes().getDeviceInfo());
            requestId = this.env.getProperty("reqestid")+ArrayUtil.getHexString(ArrayUtil.getRandom(22));
            jsonRequest.put("requestId",requestId);
            jsonRequest.put("paymentAppInstanceId", enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
            jsonRequest.put("tokenType", "CLOUD");
            jsonRequest.put("paymentAppId", enrollDeviceRequest.getMdes().getPaymentAppId());
            jsonRequest.put("deviceInfo", deviceinfo);
            jsonRequest.put("consumerLanguage","en");
            jsonRequest.put("cardletId",this.env.getProperty("cardletId"));
           // "https://mtf.services.mastercard.com/mtf/mdes/digitization/1/0/{id}"
            //url ="https://mtf.services.mastercard.com/mtf/mdes/digitization/1/0/checkEligibility";
            url = this.env.getProperty("mdesip") +this.env.getProperty("digitizationpath");
            id = "checkEligibility";
            LOGGER.debug("URL in checkDeviceEligibility"+url);
            LOGGER.info("URL in checkDeviceEligibility"+url);
            LOGGER.info("MC check device eligibility  before hit --> TIME " +HCEUtil.convertDateToTimestamp(new Date()));
            responseEntity = this.hitMasterCardService.restfulServiceConsumerMasterCard(url, jsonRequest.toString(), "POST",id);
            LOGGER.info("MC check device eligibility after hit --> TIME " +HCEUtil.convertDateToTimestamp(new Date()));
            LOGGER.info("Master Card check eligibility --> RESPONSE " +responseEntity);
            if ((responseEntity.hasBody()) && (responseEntity.getStatusCode().value() == 200)) {
                response = String.valueOf(responseEntity.getBody());
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occured" + e);
        }
        if (("".equals(response)) || (response == null)) {
            return false;
        }
        System.out.println("Response = " + response);
        JSONObject jsonResponse = new JSONObject(response);
        LOGGER.debug("Exit in DeviceRegistrationMdes:->checkDeviceEligibility");
        boolean eligibility = jsonResponse.has("errors");
        return !eligibility;
    }
}
