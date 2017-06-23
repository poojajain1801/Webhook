package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceInfoRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceRegistrationResponse;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegDeviceParam;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.service.contract.DeviceDetailService;
import com.comviva.mfs.hce.appserver.service.contract.UserDetailService;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandeler;
import com.comviva.mfs.hce.appserver.util.common.HttpRestHandelerImpl;
import com.comviva.mfs.hce.appserver.util.mdes.DeviceRegistrationMdes;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceResponse;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceVts;
import com.google.common.collect.ImmutableMap;
import com.sun.xml.internal.bind.v2.TODO;
import com.visa.cbp.encryptionutils.common.EncDevicePersoData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Service
public class DeviceDetailServiceImpl implements DeviceDetailService {
    private final DeviceDetailRepository deviceDetailRepository;
    private final UserDetailService userDetailService;

    @Autowired
    private Environment env;

    @Autowired
    public DeviceDetailServiceImpl(DeviceDetailRepository deviceDetailRepository, UserDetailService userDetailService) {
        this.deviceDetailRepository = deviceDetailRepository;
        this.userDetailService = userDetailService;
    }

    /**
     * @param enrollDeviceRequest Register Device Parameters
     * @return Response
     */
    @Override
    @Transactional
    public Map<String,Object> registerDevice(EnrollDeviceRequest enrollDeviceRequest) {
        boolean mdes=false;
        boolean vts=false;
        String vClientID = env.getProperty("vClientID");
        Map<String,Object> response=new HashMap();
        response = validate(enrollDeviceRequest);
        if(!response.get("responseCode").equals("200")) {

            return response;
        }

        // *********************MDES : Check device eligibility from MDES api.************************
        // MDES : Check device eligibility from MDES api.
        Map mdesRespMap=new HashMap();
        Map vtsRespMap=new HashMap();
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

            }else{
                    response.put("mdesFinalCode", "200");
                    response.put("mdesFinalMessage", "OK");
            }

        }

        // *******************VTS : Register with VTS Start**********************
        EnrollDeviceVts enrollDeviceVts = new EnrollDeviceVts();
        enrollDeviceVts.setEnv(env);
        enrollDeviceVts.setEnrollDeviceRequest(enrollDeviceRequest);
        String vtsResp = enrollDeviceVts.register(vClientID);
        JSONObject vtsJsonObject=new JSONObject(vtsResp);
        if(!vtsJsonObject.get("statusCode").equals("200")) {
            vtsRespMap.put("vtsMessage",vtsJsonObject.get("statusMessage") );
            vtsRespMap.put("vtsResponseCode",vtsJsonObject.get("statusCode"));
            response.put("visaFinalCode", "201");
            response.put("visaFinalMessage", "NOTOK");
            response.put("vts",vtsRespMap);
        }else{
            response.put("vts",vtsResp);
            response.put("visaFinalCode", "200");
            response.put("visaFinalMessage", "OK");
        }
        //******************VTS :Register with END***********************************
        response.put("mdes", mdesRespMap);
        //********************push device status in db START**************************

        //********************push device status in db END**************************
        return response;
    }
    private Map<String,Object> validate(EnrollDeviceRequest enrollDeviceRequest) {
        // Check User is existing
        Map<String,Object> result=new HashMap();
        if ((!userDetailService.checkIfUserExistInDb(enrollDeviceRequest.getUserId()))) {
            result.put("message", "Invalid User");
            result.put("responseCode", "205");
            return result;
        }
        boolean checkUserStatus = userDetailService.getUserstatus(enrollDeviceRequest.getUserId()).equalsIgnoreCase("userActivated");
        if (!checkUserStatus) {
            result.put("message", "User is not active");
            result.put("responseCode", "207");
            return result;
        }
        result.put("message", "Valid and active user");
        result.put("responseCode", "200");
        return result;
    }

}