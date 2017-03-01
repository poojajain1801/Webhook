package com.comviva.mfs.promotion.modules.device_management.service;

import com.comviva.mfs.promotion.constants.ServerConfig;
import com.comviva.mfs.promotion.modules.device_management.domain.DeviceInfo;
import com.comviva.mfs.promotion.modules.device_management.model.DeviceRegistrationResponse;

import com.comviva.mfs.promotion.modules.device_management.model.RegDeviceParam;

import com.comviva.mfs.promotion.modules.device_management.repository.DeviceDetailRepository;
import com.comviva.mfs.promotion.modules.device_management.service.contract.DeviceDetailService;

import com.comviva.mfs.promotion.modules.device_management.service.contract.HttpClint;
import com.comviva.mfs.promotion.modules.device_management.service.contract.HttpRestHandeler;
import com.comviva.mfs.promotion.modules.user_management.service.contract.UserDetailService;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class DeviceDetailServiceImpl implements DeviceDetailService {

    private final DeviceDetailRepository deviceDetailRepository;

    private final UserDetailService userDetailService;

    HttpRestHandeler httpRestHandeler = new HttpRestHandelerImpl();

    @Autowired
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
    }


    /**
     * @param regDeviceParam
     * @return
     */
    @Override
    @Transactional
    public DeviceRegistrationResponse registerDevice(RegDeviceParam regDeviceParam) {
        // Check User is existing

        if ((!userDetailService.checkIfUserExistInDb(regDeviceParam.getUserId()))) {
            return new DeviceRegistrationResponse(ImmutableMap.of("message", "Invalid User", "responseCode", "205"));
        }
        boolean chekUserStatus = userDetailService.getUserstatus(regDeviceParam.getUserId()).equalsIgnoreCase("userActivated");
        if (!chekUserStatus)
        {
            return new DeviceRegistrationResponse(ImmutableMap.of("message", "User is not active", "responseCode", "207"));
        }

        // 2. Check activation code is valid
        if (!regDeviceParam.getActivationCode().equalsIgnoreCase(userDetailService.getActivationCode(regDeviceParam.getUserId()))) {
            return new DeviceRegistrationResponse(ImmutableMap.of("message", "Invalid Activation Code", "responseCode", "206"));
        }

        // 3. Check device eligibility from MDES api currently it isi
        if (!checkDeviceEligibility(regDeviceParam)) {
            //throw error device not eligible.
            return new DeviceRegistrationResponse(ImmutableMap.of("message", "Device is not eligible", "responseCode", "207"));
        }


        // 4. Call register API of CMS

        String response = registerDeviceWithCMSD(regDeviceParam);
        System.out.println("Respons from CMS : \n" + response);
        JSONObject jsonResponse = new JSONObject(response).getJSONObject("response");
        String responseCode = jsonResponse.getString("responseCode");
        Map responsemap = null;
        if (responseCode.equalsIgnoreCase("200")) {

            // Save Device Detail
            DeviceInfo deviceInfo = regDeviceParam.getDeviceInfo();
            deviceInfo.setUserName(regDeviceParam.getUserId());
            deviceInfo.setPaymentAppInstanceId(regDeviceParam.getPaymentAppInstanceId());
            deviceDetailRepository.save(regDeviceParam.getDeviceInfo());

            JSONObject jsonMobKeys = jsonResponse.getJSONObject("mobileKeys");
            Map mobKeys = ImmutableMap.of("transportKey", jsonMobKeys.getString("transportKey"),
                    "macKey", jsonMobKeys.getString("macKey"),
                    "dataEncryptionKey", jsonMobKeys.getString("dataEncryptionKey"));

            responsemap = new ImmutableMap.Builder <>()
                    .put("message", jsonResponse.getString("message"))
                    .put("responseCode", jsonResponse.getString("responseCode"))
                    .put("responseHost", jsonResponse.getString("responseHost"))
                    .put("mobileKeysetId", jsonResponse.getString("mobileKeysetId"))
                    .put("remoteManagementUrl", jsonResponse.getString("remoteManagementUrl")).put("mobKeys", mobKeys).build();
        } else {

            responsemap = ImmutableMap.of("message", jsonResponse.getString("message"), "responseCode", jsonResponse.getString("responseCode"));
        }
        // 5. Prepare response
        return new DeviceRegistrationResponse(responsemap);
    }

    /**
     * @param regDeviceParam
     * @return
     */
    public boolean checkDeviceEligibility(RegDeviceParam regDeviceParam) {

        MultiValueMap <String, String> map = new LinkedMultiValueMap <String, String>();
        map.add("responseHost", "paymentapp-server");
        map.add("requestId", "123456");
        map.add("paymentAppInstanceId", "123456789");
        map.add("tokenType", "CLOUD");
        map.add("paymentAppId", "Walletapp");
        map.add("deviceInfo", regDeviceParam.getDeviceInfo().toString());
        String response = httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes", map);
        System.out.println("Response = " + response);
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.has("eligibilityReceipt"))
            return true;
        else
            return false;


    }

   /* public String registerDeviceWithCMSD(RegDeviceParam regDeviceParam) {

        MultiValueMap <String, Object> requestParameterMap = new LinkedMultiValueMap <String, Object>();
        requestParameterMap.add("paymentAppId", regDeviceParam.getPaymentAppId());
        requestParameterMap.add("paymentAppInstanceId", regDeviceParam.getPaymentAppInstanceId());
        MultiValueMap <String, Object> rnsInfoMap = new LinkedMultiValueMap <String, Object>();
        rnsInfoMap.add("rnsRegistrationId", regDeviceParam.getGcmRegistrationId());
        requestParameterMap.add("rnsInfo", rnsInfoMap);
        requestParameterMap.add("publicKeyFingerprint", regDeviceParam.getPublicKeyFingerprint());
        requestParameterMap.add("rgk", regDeviceParam.getRgk());
        requestParameterMap.add("deviceFingerprint", "1bbefaa95b26b9e82e3fdd37b20050fc782b2f229a8f8bcbbcb6aa6abe4c851e");
        requestParameterMap.add("newMobilePin", regDeviceParam.getMobilePin());

        String response = httpRestHandeler.restfulServieceConsumer("http://172.19.3.79:9099/cms-dedicated/api/device/register", requestParameterMap);
        System.out.println("Response = " + response);
        return response;
    }*/

    public String registerDeviceWithCMSD(RegDeviceParam regDeviceParam) {
        HttpClint httpClint = new HttpClintImpl();
        JSONObject jsonRegDevice = new JSONObject();
        jsonRegDevice.put("paymentAppId", regDeviceParam.getPaymentAppId());
        jsonRegDevice.put("paymentAppInstanceId", regDeviceParam.getPaymentAppInstanceId());
        jsonRegDevice.put("publicKeyFingerprint", regDeviceParam.getPublicKeyFingerprint());
        jsonRegDevice.put("rgk", regDeviceParam.getRgk());
        jsonRegDevice.put("deviceFingerprint", "1bbefaa95b26b9e82e3fdd37b20050fc782b2f229a8f8bcbbcb6aa6abe4c851e");
        jsonRegDevice.put("newMobilePin", regDeviceParam.getMobilePin());

        JSONObject rnsInfo = new JSONObject();
        rnsInfo.put("rnsRegistrationId", regDeviceParam.getGcmRegistrationId());
        jsonRegDevice.put("rnsInfo", rnsInfo);
        //Tarak ip 172.19.3.79
        String response = httpClint.postHttpRequest(jsonRegDevice.toString().getBytes(),
                ServerConfig.CMSD_IP + ":" + ServerConfig.CMSD_PORT + "/mdes/mpamanagement/1/0/register");
        return response;
    }


}


