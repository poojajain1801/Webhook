package com.comviva.mfs.hce.appserver.util.mdes;


import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceInfoRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.util.common.*;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegDeviceParam;
import com.google.common.collect.ImmutableMap;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;

@Setter
public class DeviceRegistrationMdes {
    private EnrollDeviceRequest enrollDeviceRequest;

    /**
     * Registers device with CMS-d.
     * @return Response
     */
    private String registerDeviceWithCMSD() {
        HttpClint httpClint = new HttpClintImpl();
        JSONObject jsonRegDevice = new JSONObject();
        jsonRegDevice.put("paymentAppId", enrollDeviceRequest.getMdes().getPaymentAppId());
        jsonRegDevice.put("paymentAppInstanceId", enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
        jsonRegDevice.put("publicKeyFingerprint", enrollDeviceRequest.getMdes().getPublicKeyFingerprint());
        jsonRegDevice.put("rgk", enrollDeviceRequest.getMdes().getRgk());
        DeviceInfoRequest jsDevInfo = enrollDeviceRequest.getMdes().getDeviceInfo();
        String deviceFingerPrint;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(jsDevInfo.toString().getBytes());
            deviceFingerPrint = ArrayUtil.getHexString(hash);
        } catch (GeneralSecurityException e) {
            deviceFingerPrint = null;
        }

        jsonRegDevice.put("deviceFingerprint", deviceFingerPrint);
        jsonRegDevice.put("newMobilePin", enrollDeviceRequest.getMdes().getMobilePin());

        JSONObject rnsInfo = new JSONObject();
        rnsInfo.put("rnsRegistrationId", enrollDeviceRequest.getGcmRegistrationId());
        jsonRegDevice.put("rnsInfo", rnsInfo);
        try{
        return httpClint.postHttpRequest(jsonRegDevice.toString().getBytes(),
                ServerConfig.CMSD_IP + ":" + ServerConfig.CMSD_PORT + "/mdes/mpamanagement/1/0/register");
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Register device with CMS-d.
     * @return Response
     */
    public DeviceRegistrationResponse registerDevice() {
        // Register device with CMS-d
        String response = registerDeviceWithCMSD();
        JSONObject jsonResponse = new JSONObject(response);//.getJSONObject("response");
        String responseCode = jsonResponse.getString("responseCode");
        Map responseMap;
        if (responseCode.equalsIgnoreCase("200")) {
            JSONObject jsonMobKeys = jsonResponse.getJSONObject("mobileKeys");
            Map mobKeys = ImmutableMap.of("transportKey", jsonMobKeys.getString("transportKey"),
                    "macKey", jsonMobKeys.getString("macKey"),
                    "dataEncryptionKey", jsonMobKeys.getString("dataEncryptionKey"));

            responseMap = new ImmutableMap.Builder <>()
                    .put("message", jsonResponse.getString("message"))
                    .put("responseCode", jsonResponse.getString("responseCode"))
                    .put("responseHost", jsonResponse.getString("responseHost"))
                    .put("mobileKeysetId", jsonResponse.getString("mobileKeysetId"))
                    .put("remoteManagementUrl", jsonResponse.getString("remoteManagementUrl"))
                    .put("mobKeys", mobKeys).build();
        } else {
            responseMap = ImmutableMap.of("message", jsonResponse.getString("message"), "responseCode", jsonResponse.getString("responseCode"));
        }
        // Prepare response
        return new DeviceRegistrationResponse(responseMap);
    }

    /**
     * Checks device's eligibility with MDES.
     * @return <code>true </code>Device is eligible <br/>
     *         <code>false </code>Not eligible
     */
    public boolean checkDeviceEligibility() {
        HttpRestHandeler httpRestHandeler = new HttpRestHandelerImpl();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("responseHost", "paymentapp-server");
        map.add("requestId", "123456");
        map.add("paymentAppInstanceId", enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
        map.add("tokenType", "CLOUD");
        map.add("paymentAppId", enrollDeviceRequest.getMdes().getPaymentAppId());
        map.add("deviceInfo", enrollDeviceRequest.getMdes().getDeviceInfo().toString());
        String response="";
        try {
            response = httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes", map);
        }catch (Exception e){
                e.printStackTrace();
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