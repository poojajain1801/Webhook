package com.comviva.mfs.hce.appserver.util.mdes;


import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceInfoRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.MdesDeviceRequest;
import com.comviva.mfs.hce.appserver.util.common.*;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Setter
@Component
public class DeviceRegistrationMdes {

    @Autowired
    public Environment env;

    @Autowired
    HttpClint httpClint;
    @Autowired
    HitMasterCardService hitMasterCardService;
    private final HCEControllerSupport hceControllerSupport;
    @Autowired
    public DeviceRegistrationMdes(HCEControllerSupport hceControllerSupport) {
        this.hceControllerSupport = hceControllerSupport;
    }

    /**
     * Registers device with CMS-d.
     * @return Response
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationMdes.class);

    private String registerDeviceWithCMSD(EnrollDeviceRequest enrollDeviceRequest) {
        ResponseEntity responseEntity=null;
        String response = null;
        String url = null;
        JSONObject jsonRegDevice = null;
        MdesDeviceRequest mdesDeviceRequest = null;
        JSONObject rnsInfo = null;
        try {
            jsonRegDevice = new JSONObject();
            mdesDeviceRequest = enrollDeviceRequest.getMdes();
            jsonRegDevice.put("paymentAppId", mdesDeviceRequest.getPaymentAppId());
            jsonRegDevice.put("paymentAppInstanceId", mdesDeviceRequest.getPaymentAppInstanceId());
            jsonRegDevice.put("publicKeyFingerprint", mdesDeviceRequest.getPublicKeyFingerprint());
            jsonRegDevice.put("rgk", mdesDeviceRequest.getRgk());
            jsonRegDevice.put("deviceFingerprint", mdesDeviceRequest.getDeviceFingerprint());
            jsonRegDevice.put("newMobilePin", mdesDeviceRequest.getMobilePin());

            rnsInfo = new JSONObject();
            rnsInfo.put("rnsRegistrationId", enrollDeviceRequest.getGcmRegistrationId());
            jsonRegDevice.put("rnsInfo", rnsInfo);

        /*String response = httpClint.postHttpRequest(jsonRegDevice.toString().getBytes(),
                ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/mpamanagement/1/0/register");*/
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + "/mdes/credentials/1/0/deviceRegistration";
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url,jsonRegDevice.toString(),"POST");
            if (responseEntity.hasBody() && (responseEntity.getStatusCode().value() == 200)) {
                response = String.valueOf(responseEntity.getBody());
            }
            return response;
        } catch (Exception e) {
            LOGGER.error("Exception occured", e);
            return null;
        }
    }

    /**
     * Register device with CMS-d.
     * @return Response
     */
    public DeviceRegistrationResponse registerDevice(EnrollDeviceRequest enrollDeviceRequest) {
        // Register device with CMS-d
        String response = null;
        JSONObject jsonResponse = null;
        Map responseMap = null;
        String message = null;
        String responseCode = null;
        try {
            response = registerDeviceWithCMSD(enrollDeviceRequest);
            if (response != null || !response.isEmpty()) {
                jsonResponse = new JSONObject(response);
                if (jsonResponse.has("message"))
                {
                    jsonResponse.getString("message");
                }else{
                    message = "SUCCESS";
                }
                if (jsonResponse.has("responseCode"))
                {
                    responseCode = jsonResponse.getString("responseCode");
                }else{
                    responseCode = "200";
                }
                JSONObject jsonMobKeys = jsonResponse.getJSONObject("mobileKeys");
                Map mobKeys = ImmutableMap.of("transportKey", jsonMobKeys.getString("transportKey"),
                        "macKey", jsonMobKeys.getString("macKey"),
                        "dataEncryptionKey", jsonMobKeys.getString("dataEncryptionKey"));

                responseMap = new ImmutableMap.Builder<>()
                        .put("message",message)
                        .put("responseCode", responseCode)
                        .put("responseHost", jsonResponse.getString("responseHost"))
                        .put("mobileKeysetId", jsonResponse.getString("mobileKeysetId"))
                        .put("remoteManagementUrl", jsonResponse.getString("remoteManagementUrl"))
                        .put("mobKeys", mobKeys).build();
            } else {
                responseMap = hceControllerSupport.formResponse(HCEConstants.SERVICE_FAILED);
            }
        } catch (Exception e) {
            LOGGER.error("Exceprion Occored in DeviceRegistration",e);
        }
        // Prepare response
        return new DeviceRegistrationResponse(responseMap);
    }

    /**
     * Checks device's eligibility with MDES.
     * @return <code>true </code>Device is eligible <br/>
     *         <code>false </code>Not eligible
     */
    public boolean checkDeviceEligibility(EnrollDeviceRequest enrollDeviceRequest) {
        HttpRestHandeler httpRestHandeler = new HttpRestHandelerImpl();
        JSONObject jsonRequest = new JSONObject();
        JSONObject deviceinfo = new JSONObject(enrollDeviceRequest.getMdes().getDeviceInfo());
        jsonRequest.put("responseHost", "paymentapp-server");
        jsonRequest.put("requestId", "123456");
        jsonRequest.put("paymentAppInstanceId", enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
        jsonRequest.put("tokenType", "CLOUD");
        jsonRequest.put("paymentAppId", enrollDeviceRequest.getMdes().getPaymentAppId());
        jsonRequest.put("deviceInfo", deviceinfo);
        String response="";
        String url = "";
        ResponseEntity responseEntity=null;
        try {
            url = env.getProperty("mdesip") + ":" + env.getProperty("mdesport") + env.getProperty("credentialspath")+"/checkEligibility";
            //response = httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes", map);
            responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url,jsonRequest.toString(),"POST");
            if (responseEntity.hasBody() && (responseEntity.getStatusCode().value() == 200)) {
                response = String.valueOf(responseEntity.getBody());
            }

        }catch (Exception e){
            LOGGER.error("Exception occured" +e);
        }
        if("".equals(response)||response==null){
            return false;
        }else{
            System.out.println("Response = " + response);
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.has("eligibilityReceipt");
        }
    }
}