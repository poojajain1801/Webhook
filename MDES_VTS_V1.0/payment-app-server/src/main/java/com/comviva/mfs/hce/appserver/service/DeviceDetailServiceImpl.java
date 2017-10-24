package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.controller.UserRegistrationController;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                if (!respCodeMdes.equalsIgnoreCase("200")) {
                    mdesRespMap.put(HCEConstants.MDES_RESPONSE_CODE, devRegRespMdes.getResponse().get(HCEConstants.RESPONSE_CODE).toString());
                    mdesRespMap.put(HCEConstants.MDES_MESSAGE, devRegRespMdes.getResponse().get(HCEConstants.MESSAGE).toString());
                    response.put(HCEConstants.MDES_FINAL_CODE, "201");
                    response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                    response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

                } else {
                    response.put(HCEConstants.MDES_RESPONSE_MAP, devRegRespMdes.getResponse());
                    response.put(HCEConstants.MDES_FINAL_CODE, "200");
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
                response.put(HCEConstants.MDES_FINAL_CODE, "201");
                response.put(HCEConstants.MDES_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.MDES_RESPONSE_MAP, mdesRespMap);

            }
            // *******************VTS : Register with VTS Start**********************

            enrollDeviceVts.setEnrollDeviceRequest(enrollDeviceRequest);
            String vtsResp = enrollDeviceVts.register(vClientID);
            JSONObject vtsJsonObject = new JSONObject(vtsResp);
            if (!vtsJsonObject.get("statusCode").equals("200")) {
                vtsRespMap.put(HCEConstants.VTS_MESSAGE, vtsJsonObject.get(HCEConstants.MESSAGE));
                vtsRespMap.put(HCEConstants.VTS_RESPONSE_CODE, vtsJsonObject.get(HCEConstants.STATUS_CODE));
                response.put(HCEConstants.VISA_FINAL_CODE, HCEMessageCodes.VISA_FINAL_CODE);
                response.put(HCEConstants.VISA_FINAL_MESSAGE, "NOTOK");
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsRespMap);
            } else {
                response.put(HCEConstants.VTS_RESPONSE_MAP, vtsResp);
                deviceInfo.setIsVisaEnabled("Y");
                deviceInfo.setVClientId(vClientID);
                deviceDetailRepository.save(deviceInfo);
                response.put(HCEConstants.VISA_FINAL_CODE, "200");
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
}