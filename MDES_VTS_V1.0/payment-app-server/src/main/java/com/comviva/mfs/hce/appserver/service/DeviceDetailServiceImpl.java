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
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
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
    private final DeviceRegistrationMdes deviceRegistrationMdes;
    private final EnrollDeviceVts enrollDeviceVts;
    @Autowired
    private Environment env;

    @Autowired
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService,UserDetailRepository userDetailRepository,HCEControllerSupport hceControllerSupport,DeviceRegistrationMdes deviceRegistrationMdes,EnrollDeviceVts enrollDeviceVts ) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
        this.userDetailRepository=userDetailRepository;
        this.hceControllerSupport = hceControllerSupport;
        this.deviceRegistrationMdes = deviceRegistrationMdes;
        this.enrollDeviceVts = enrollDeviceVts;
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

            LOGGER.debug("Enter DeviceDetailServiceImpl->registerDevice");
            List<UserDetail> userDetails = userDetailRepository.findByUserIdandStatus(enrollDeviceRequest.getUserId(), HCEConstants.ACTIVE);

            if(userDetails!=null && !userDetails.isEmpty()){
                userDetail = userDetails.get(0);
                List<DeviceInfo> deviceInfos = deviceDetailRepository.findByClientDeviceIdandStatus(enrollDeviceRequest.getClientDeviceID(),HCEConstants.INITIATE);
                if(deviceInfos!=null && !deviceInfos.isEmpty()){
                    deviceInfo = deviceInfos.get(0);
                    if(!userDetail.getClientWalletAccountId().equals(deviceInfo.getUserDetail().getClientWalletAccountId())){
                        throw new HCEActionException(HCEMessageCodes.INVALID_USER_AND_DEVICE);
                    }
                }else{
                    throw new HCEActionException(HCEMessageCodes.INVALID_CLIENT_DEVICE_ID);
                }

            }else{
                throw new HCEActionException(HCEMessageCodes.INVALID_USER);
            }
            deviceInfo.setRnsRegistrationId(enrollDeviceRequest.getGcmRegistrationId());


            // *********************MDES : Check device eligibility from MDES api.************************
            // MDES : Check device eligibility from MDES api.
            //JSONObject mdesResponse=new JSONObject();
            deviceRegistrationMdes.setEnrollDeviceRequest(enrollDeviceRequest);
            isMdesDevElib = deviceRegistrationMdes.checkDeviceEligibility();


            if (isMdesDevElib) {
                // MDES : Register with CMS-d
                devRegRespMdes = deviceRegistrationMdes.registerDevice();
                respCodeMdes = devRegRespMdes.getResponse().get(HCEConstants.RESPONSE_CODE).toString();
                // If registration fails for MDES return error
                if (!HCEMessageCodes.SUCCESS.equalsIgnoreCase(respCodeMdes)) {
                    mdesRespMap.put(HCEConstants.MDES_RESPONSE_CODE, devRegRespMdes.getResponse().get(HCEConstants.RESPONSE_CODE).toString());
                    mdesRespMap.put(HCEConstants.MDES_MESSAGE, devRegRespMdes.getResponse().get(HCEConstants.MESSAGE).toString());
                    response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.DEVICE_REGISTRATION_FAILED);
                    response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                    response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

                } else {
                    response.put(HCEConstants.MDES_RESPONSE_MAP, devRegRespMdes.getResponse());
                    response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.SUCCESS);
                    response.put(HCEConstants.MDES_FINAL_MESSAGE, "OK");
                    deviceInfo.setPaymentAppInstanceId(enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
                    deviceInfo.setPaymentAppId(enrollDeviceRequest.getMdes().getPaymentAppId());
                    deviceInfo.setIsMastercardEnabled(HCEConstants.ACTIVE);
                    deviceDetailRepository.save(deviceInfo);
                }

            }else{

                //throw error device not eligible.
                mdesRespMap.put(HCEConstants.MDES_MESSAGE, "Device is not eligible");
                mdesRespMap.put(HCEConstants.MDES_RESPONSE_CODE, "207");
                response.put(HCEConstants.MDES_FINAL_CODE, HCEMessageCodes.DEVICE_REGISTRATION_FAILED);
                response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

            }
            // *******************VTS : Register with VTS Start**********************

            enrollDeviceVts.setEnrollDeviceRequest(enrollDeviceRequest);
            String vtsResp = enrollDeviceVts.register(vClientID);
            JSONObject vtsJsonObject = new JSONObject(vtsResp);
            if (!vtsJsonObject.get(HCEConstants.RESPONSE_CODE).equals(HCEMessageCodes.SUCCESS)) {
                vtsRespMap.put(HCEConstants.VTS_MESSAGE, vtsJsonObject.get(HCEConstants.MESSAGE));
                vtsRespMap.put(HCEConstants.VTS_RESPONSE_CODE, vtsJsonObject.get(HCEConstants.RESPONSE_CODE));
                response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.DEVICE_REGISTRATION_FAILED);
                response.put(HCEConstants.VISA_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
            } else {
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsResp);
                deviceInfo.setIsVisaEnabled(HCEConstants.ACTIVE);
                deviceInfo.setVClientId(vClientID);
                deviceDetailRepository.save(deviceInfo);
                response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.SUCCESS);
                response.put(HCEConstants.VISA_FINAL_MESSAGE, "OK");
            }

            LOGGER.debug("Exit DeviceDetailServiceImpl->registerDevice");
            //******************VTS :Register with END***********************************
            return response;
        }
        catch(HCEActionException regDeviceactionException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", regDeviceactionException);
            throw regDeviceactionException;

        }catch(Exception regDeviceException){
            LOGGER.error("Exception occured in DeviceDetailServiceImpl->registerDevice", regDeviceException);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
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
