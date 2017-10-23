package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.constants.ServerConfig;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.controller.UserRegistrationController;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.MDES.HitMasterCardService;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnRegisterReq;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.CardDetailRepository;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import com.comviva.mfs.hce.appserver.repository.VisaCardDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
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
    private final UserDetailService userDetailService;
    private final UserDetailRepository userDetailRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDetailServiceImpl.class);
    private final HCEControllerSupport hceControllerSupport;

    @Autowired
    private Environment env;

    @Autowired
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService,UserDetailRepository userDetailRepository,HCEControllerSupport hceControllerSupport) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
    }

    /**
     * @param enrollDeviceRequest Register Device Parameters
     * @return Response
     */
    @Override
    @Transactional
    public Map<String, Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest) {
        LOGGER.debug("Inside DeviceDetailServiceImpl->registerDevice");
        String vClientID = env.getProperty("vClientID");
        Map<String, Object> response = new HashMap();
        EnrollDeviceVts enrollDeviceVts = new EnrollDeviceVts();
        Map mdesRespMap = new HashMap();
        Map vtsRespMap = new HashMap();
        boolean isMdesDevElib = false;
        String respCodeMdes = "";
        DeviceRegistrationResponse devRegRespMdes = null;
        try {
            List<UserDetail> userDetails = userDetailRepository.find(enrollDeviceRequest.getUserId());
            List<DeviceInfo> deviceInfo = deviceDetailRepository.find(enrollDeviceRequest.getClientDeviceID());
            deviceInfo.get(0).setRnsRegistrationId(enrollDeviceRequest.getGcmRegistrationId());
            response = validate(enrollDeviceRequest, userDetails, deviceInfo);
            if (!response.get("responseCode").equals("200")) {
                return response;
            }
            // *********************MDES : Check device eligibility from MDES api.************************
            // MDES : Check device eligibility from MDES api.
            //JSONObject mdesResponse=new JSONObject();
            DeviceRegistrationMdes devRegMdes = new DeviceRegistrationMdes();
            devRegMdes.setEnrollDeviceRequest(enrollDeviceRequest);
            isMdesDevElib = devRegMdes.checkDeviceEligibility();
            if (!isMdesDevElib) {
                //throw error device not eligible.
                mdesRespMap.put("mdesMessage", "Device is not eligible");
                mdesRespMap.put("mdesResponseCode", "207");
                response.put("mdesFinalCode", "201");
                response.put("mdesFinalMessage", "NOTOK");
                response.put("mdes", mdesRespMap);
            }

            if (isMdesDevElib) {
                // MDES : Register with CMS-d
                devRegRespMdes = devRegMdes.registerDevice();
                respCodeMdes = devRegRespMdes.getResponse().get("responseCode").toString();
                // If registration fails for MDES return error
                if (!respCodeMdes.equalsIgnoreCase("200")) {
                    mdesRespMap.put("mdesResponseCode", devRegRespMdes.getResponse().get("responseCode").toString());
                    mdesRespMap.put("mdesMessage", devRegRespMdes.getResponse().get("message").toString());
                    response.put("mdesFinalCode", "201");
                    response.put("mdesFinalMessage", "NOTOK");
                    response.put("mdes", mdesRespMap);

                } else {
                    // Save PaymentAppInstanceId mapping with user
                    UserDetail userDetail = userDetails.get(0);
                    userDetail.setPaymentAppInstanceId(enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
                    userDetailRepository.save(userDetail);

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

           /* deviceInfo.get(0).setVtscerts_certusage_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-certUsage-confidentiality"));
            deviceInfo.get(0).setVtscerts_vcertificateid_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-vCertificateID-confidentiality"));

            deviceInfo.get(0).setVtscerts_certusage_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-certUsage-integrity"));
            deviceInfo.get(0).setVtscerts_vcertificateid_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("vtsCerts-vCertificateID-integrity"));

            deviceInfo.get(0).setDevicecerts_certformat_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certFormat-confidentiality"));
            deviceInfo.get(0).setDevicecerts_certusage_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certUsage-confidentiality"));
            deviceInfo.get(0).setDevicecerts_certvalue_confidentiality((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certValue-confidentiality"));

            deviceInfo.get(0).setDevicecerts_certformat_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certFormat-integrity"));
            deviceInfo.get(0).setDevicecerts_certusage_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certUsage-integrity"));
            deviceInfo.get(0).setDevicecerts_certvalue_integrity((String) vtsJsonObject.getJSONObject("responseBody").get("deviceCerts-certValue-integrity"));*/

                deviceInfo.get(0).setVClientId(vClientID);
                deviceDetailRepository.save(deviceInfo.get(0));
                response.put("visaFinalCode", "200");
                response.put("visaFinalMessage", "OK");
            }
        }
        catch(HCEActionException regDeviceactionException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", regDeviceactionException);
            return hceControllerSupport.formResponse(regDeviceactionException.getMessageCode());

        }catch(Exception regDeviceException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", regDeviceException);
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }

        LOGGER.debug("Exit DeviceDetailServiceImpl->registerDevice");
        //******************VTS :Register with END***********************************
        return response;
    }

    private Map<String, Object> validate(EnrollDeviceRequest enrollDeviceRequest, List<UserDetail> userDetails, List<DeviceInfo> deviceInfo) {
        Map<String, Object> result = new HashMap();
        List<UserDetail> userDevice;
        try {
            if ((null == userDetails || userDetails.isEmpty()) || (null == deviceInfo || deviceInfo.isEmpty())) {
                throw new HCEActionException(HCEMessageCodes.INVALID_USER);
            } else if ("userActivated".equals(userDetails.get(0).getUserStatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())) {
                userDevice = userDetailRepository.findByClientDeviceId(enrollDeviceRequest.getClientDeviceID());
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
                result.put("message", hceControllerSupport.prepareMessage(HCEMessageCodes.SUCCESS));
                result.put("responseCode", HCEMessageCodes.SUCCESS);
                return result;
            } else {
                throw new HCEActionException(HCEMessageCodes.INVALID_USER);
            }
        }
        catch(HCEActionException regDeviceactionException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->validate", regDeviceactionException);
            return hceControllerSupport.formResponse(regDeviceactionException.getMessageCode());

        }catch(Exception regDeviceException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->validate", regDeviceException);
            return hceControllerSupport.formResponse(HCEMessageCodes.SERVICE_FAILED);
        }
    }
    @Override
    @Transactional
    public Map<String, Object> unRegisterDevice(UnRegisterReq unRegisterReq) {

        JSONObject jsonRequest  = null;
        String url = null;
        HitMasterCardService hitMasterCardService = null;
        ResponseEntity responseEntity = null;
        String paymentAppInstanceID = null;
        Optional<DeviceInfo> deviceInfoOptional = null;
        String userID = null;

        String clintDeviceID = null;
        String imei = null;

        try {
            userID = unRegisterReq.getUserId();
            imei = unRegisterReq.getImei();
            clintDeviceID = unRegisterReq.getClientDeviceID();
            paymentAppInstanceID = unRegisterReq.getPaymentAppInstanceId();

            //If user id and imei is null and paymentAppInstanceID or clint device is is null throw Insuficiant input data
            if(((imei.isEmpty()||imei ==null)&&(userID.isEmpty()||userID==null))
                    ||((clintDeviceID.isEmpty()||clintDeviceID==null)||((paymentAppInstanceID.isEmpty()||paymentAppInstanceID==null))))
            {
                //Retrun Insufucaiant input data
            }

            jsonRequest = new JSONObject();
            deviceInfoOptional = deviceDetailRepository.findByPaymentAppInstanceId(paymentAppInstanceID);


            //Fatch all the card details and mark the satus as deleted

            //call master cad unregister API
            if(!(paymentAppInstanceID==null || paymentAppInstanceID.isEmpty())) {

                //Validate payment app instance ID
                if (!deviceInfoOptional.isPresent()) {
                    //Return In valid paymentApp Instance ID
                }

                jsonRequest.put("responseHost", ServerConfig.RESPONSE_HOST);
                jsonRequest.put("requestId", "12343443");
                jsonRequest.put("paymentAppInstanceId",paymentAppInstanceID);
                url = ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/mpamanagement/1/0/unregister";
                hitMasterCardService = new HitMasterCardService();
                responseEntity = hitMasterCardService.restfulServiceConsumerMasterCard(url, jsonRequest.toString(), "POST");
            }

            if (!(clintDeviceID.isEmpty()||clintDeviceID.equalsIgnoreCase(null)))
            {
                //Validate clint device ID
                if (deviceDetailRepository.findByClientDeviceId(unRegisterReq.getClientDeviceID()).isPresent())
                {
                    //Return Invalid ClintDeviceID
                }
            }

            //Validate all the vProvison ID and call delete card API of the VISA.


        }catch (HCEActionException unRegisterException)
        {
            unRegisterException.printStackTrace();
        }
        return  null;

    }

}