package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.mdes.DeviceRegistrationMdes;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceVts;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.sun.xml.internal.bind.v2.TODO;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class DeviceDetailServiceImpl implements DeviceDetailService {
    private final DeviceDetailRepository deviceDetailRepository;
    private final UserDetailService userDetailService;
    private final UserDetailRepository userDetailRepository;

    @Autowired
    private Environment env;

    @Autowired
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService, UserDetailRepository userDetailRepository) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
        this.userDetailRepository = userDetailRepository;
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

        List<UserDetail> userDetails = userDetailRepository.find(enrollDeviceRequest.getUserId());
        List<DeviceInfo> deviceInfo = deviceDetailRepository.find(enrollDeviceRequest.getClientDeviceID());
        deviceInfo.get(0).setRnsId(enrollDeviceRequest.getGcmRegistrationId());
        response = validate(enrollDeviceRequest, userDetails, deviceInfo);
        if (!response.get("responseCode").equals("200")) {
            return response;
        }
        // *********************MDES : Check device eligibility from MDES api.************************
        // MDES : Check device eligibility from MDES api.
        Map mdesRespMap = new HashMap();
        Map vtsRespMap = new HashMap();
        //JSONObject mdesResponse=new JSONObject();
        DeviceRegistrationMdes devRegMdes = new DeviceRegistrationMdes();
        devRegMdes.setEnrollDeviceRequest(enrollDeviceRequest);
        boolean isMdesDevElib = devRegMdes.checkDeviceEligibility();
        if (!isMdesDevElib) {
            //throw error device not eligible.
            mdesRespMap.put("mdesMessage", "Device is not eligible");
            mdesRespMap.put("mdesResponseCode", "207");
            response.put("mdesFinalCode", "201");
            response.put("mdesFinalMessage", "NOTOK");
            response.put("mdes", mdesRespMap);
        }
        DeviceRegistrationResponse devRegRespMdes = null;
        if (isMdesDevElib) {
            // MDES : Register with CMS-d
            devRegRespMdes = devRegMdes.registerDevice();
            String respCodeMdes = devRegRespMdes.getResponse().get("responseCode").toString();
            // If registration fails for MDES return error
            if (!respCodeMdes.equalsIgnoreCase("200")) {
                mdesRespMap.put("mdesResponseCode", devRegRespMdes.getResponse().get("responseCode").toString());
                mdesRespMap.put("mdesMessage", devRegRespMdes.getResponse().get("message").toString());
                response.put("mdesFinalCode", "201");
                response.put("mdesFinalMessage", "NOTOK");
                response.put("mdes", mdesRespMap);

            } else {
                response.put("mdes", devRegRespMdes.getResponse());
                response.put("mdesFinalCode", "200");
                response.put("mdesFinalMessage", "OK");
                deviceInfo.get(0).setPaymentAppInstanceId(enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
                deviceInfo.get(0).setPaymentAppId(enrollDeviceRequest.getMdes().getPaymentAppId());
                deviceInfo.get(0).setMastercardEnabled("Y");
                deviceInfo.get(0).setMastercardMessage("OK");
                deviceDetailRepository.save(deviceInfo.get(0));
            }
        }

        // *******************VTS : Register with VTS Start**********************


        EnrollDeviceVts enrollDeviceVts = new EnrollDeviceVts();
        enrollDeviceVts.setEnv(env);
        enrollDeviceVts.setEnrollDeviceRequest(enrollDeviceRequest);
        String vtsResp = enrollDeviceVts.register(vClientID);
        JSONObject vtsJsonObject = new JSONObject(vtsResp);
        if (!vtsJsonObject.get("statusCode").equals("200")) {
            vtsRespMap.put("vtsMessage", vtsJsonObject.get("statusMessage"));
            vtsRespMap.put("vtsResponseCode", vtsJsonObject.get("statusCode"));
            response.put("visaFinalCode", "201");
            response.put("visaFinalMessage", "NOTOK");
            response.put("vts", vtsRespMap);
        } else {
            response.put("vts", vtsResp);
            //DeviceInfo deviceInfo=new DeviceInfo();

            deviceInfo.get(0).setVisaEnabled("Y");
            deviceInfo.get(0).setVisaMessage("OK");

            deviceInfo.get(0).setVtscerts_certusage_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-certUsage-confidentiality"));
            deviceInfo.get(0).setVtscerts_vcertificateid_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-vCertificateID-confidentiality"));

            deviceInfo.get(0).setVtscerts_certusage_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-certUsage-integrity"));
            deviceInfo.get(0).setVtscerts_vcertificateid_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-vCertificateID-integrity"));

            deviceInfo.get(0).setDevicecerts_certformat_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certFormat-confidentiality"));
            deviceInfo.get(0).setDevicecerts_certusage_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certUsage-confidentiality"));
            deviceInfo.get(0).setDevicecerts_certvalue_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certValue-confidentiality"));

            deviceInfo.get(0).setDevicecerts_certformat_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certFormat-integrity"));
            deviceInfo.get(0).setDevicecerts_certusage_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certUsage-integrity"));
            deviceInfo.get(0).setDevicecerts_certvalue_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certValue-integrity"));

            deviceInfo.get(0).setVClientId(vClientID);
            deviceDetailRepository.save(deviceInfo.get(0));
            response.put("visaFinalCode", "200");
            response.put("visaFinalMessage", "OK");
        }

        //******************VTS :Register with END***********************************
        return response;
    }

    private Map<String, Object> validate(EnrollDeviceRequest enrollDeviceRequest, List<UserDetail> userDetails, List<DeviceInfo> deviceInfo) {
        Map<String, Object> result = new HashMap();
        if ((null == userDetails || userDetails.isEmpty()) || (null == deviceInfo || deviceInfo.isEmpty())) {
            result.put("message", "Invalid User please register");
            result.put("responseCode", "205");
            return result;
        } else if ("userActivated".equals(userDetails.get(0).getUserstatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())) {
            List<UserDetail> userDevice = userDetailRepository.findByClientDeviceId(enrollDeviceRequest.getClientDeviceID());
            if (null != userDevice && !userDevice.isEmpty()) {
                for (int i = 0; i < userDetails.size(); i++) {
                    if (!userDevice.get(i).getUserName().equals(userDetails.get(0).getUserName())) {
                        userDevice.get(i).setClientDeviceId("CD");
                        userDetailRepository.save(userDevice.get(i));
                    }
                }
            }
            userDetails.get(0).setClientDeviceId(enrollDeviceRequest.getClientDeviceID());
            userDetailRepository.save(userDetails.get(0));
            result.put("message", "Active User");
            result.put("responseCode", "200");
            return result;
        } else {
            result.put("message", "User not active");
            result.put("responseCode", "205");
            return result;
        }
    }

}