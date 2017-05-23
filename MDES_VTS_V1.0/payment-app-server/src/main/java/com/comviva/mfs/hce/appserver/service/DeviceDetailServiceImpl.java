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
        String vClientID = env.getProperty("vClientID");
        Map<String,Object> response=new HashMap();
        response = validate(enrollDeviceRequest);

        if(!response.get("responseCode").equals("200")) {
            return response;
        }
        // MDES : Check device eligibility from MDES api.
        DeviceRegistrationMdes devRegMdes = new DeviceRegistrationMdes();
        devRegMdes.setEnrollDeviceRequest(enrollDeviceRequest);
        boolean isMdesDevElib = devRegMdes.checkDeviceEligibility();
        if (!isMdesDevElib) {
            //throw error device not eligible.
            response.put("mdesMessage", "Device is not eligible");
            response.put("mdesResponseCode", "207");
            //return response;
        }
           if(isMdesDevElib) {
               // MDES : Register with CMS-d
               DeviceRegistrationResponse devRegRespMdes = devRegMdes.registerDevice();
               String respCodeMdes = devRegRespMdes.getResponse().get("responseCode").toString();
               // If registration fails for MDES return error
               if (!respCodeMdes.equalsIgnoreCase("200")) {
                   response.put("mdesResponseCode", devRegRespMdes.getResponse().get("responseCode").toString());
                   response.put("mdesMessage", devRegRespMdes.getResponse().get("message").toString());
                   //return response;

               }

           }
        // VTS : Register with VTS
        // Prepare deviceInfo
       /* EnrollDeviceVts enrollDeviceVts = new EnrollDeviceVts();
        enrollDeviceVts.setEnv(env);
        enrollDeviceVts.setEnrollDeviceRequest(enrollDeviceRequest);
        ResponseEntity<EnrollDeviceResponse> vtsResp = enrollDeviceVts.register(vClientID);
        if(!vtsResp.getStatusCode().is2xxSuccessful()) {
            response.put("vtsMessage", vtsResp.getStatusCode().getReasonPhrase());
            response.put("vtsResponseCode", vtsResp.getStatusCode().value());
            return response;
        }
        EnrollDeviceResponse enrollDeviceResp = vtsResp.getBody();*/
        // TODO Save Device Detail



        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setUserName(enrollDeviceRequest.getUserId());
        deviceInfo.setPaymentAppInstanceId(enrollDeviceRequest.getMdes().getPaymentAppInstanceId());
        deviceInfo.setImei(enrollDeviceRequest.getMdes().getDeviceInfo().getImei());
        deviceInfo.setOsName(enrollDeviceRequest.getMdes().getDeviceInfo().getOsName());
        deviceInfo.setOsVersion(enrollDeviceRequest.getMdes().getDeviceInfo().getOsVersion());
        deviceInfo.setNfcCapable(enrollDeviceRequest.getMdes().getDeviceInfo().getNfcCapable());
        //Save the Visa details in the DB;
        //TODO:Upadate ClintDeviceID field with actual value
        deviceInfo.setClientDeviceId("walltet123");
        deviceInfo.setVClientId(vClientID);
        deviceInfo.setDeviceModel(enrollDeviceRequest.getVts().getDeviceInfo().getDeviceModel());
        deviceInfo.setHostDeviceID(enrollDeviceRequest.getVts().getDeviceInfo().getHostDeviceID());
        deviceDetailRepository.save(deviceInfo);


        // Append VTS response
       // Map respMap = devRegRespMdes.getResponse();
        // MDES response

        Map mdesMobKeys = new ImmutableMap.Builder<String,Object>()
                .put("transportKey", "qq")
                .put("macKey", "aa")
                .put("dataEncryptionKey", "aa").build();

        Map <String,Object> mdesRespMap = new ImmutableMap.Builder<String ,Object>()
                .put("mobileKeysetId", "1224")
                .put("responseHost", "site1.cmsd.com")
                .put("remoteManagementUrl", "rwerwe")
                .put("mobKeys", mdesMobKeys).build();

        /*
        * response = new ImmutableMap.Builder<String, Object>()
                    .put("responseHost", "site1.cmsd.com")
                    .put("responseId", "123456")
                    .put("mobileKeysetId", mobileKeySetId)
                    .put("mobileKeys", mobKeys)
                    .put("responseCode", "200")
                    .put("message", "User has been successfully registered in the system")
                    .put("remoteManagementUrl", REMOTE_MANAGEMENT_URL).build();*/
        // VTS Response
        /*Map vtsRespMap = ImmutableMap.builder().build();
        vtsRespMap.put("clientDeviceID",enrollDeviceResp.getClientDeviceID());
        vtsRespMap.put("vClientID", enrollDeviceResp.getVClientID());
        vtsRespMap.put("deviceInitParams", enrollDeviceResp.getDeviceInitParams());
        Map vtsEncDevPersoDataMap = ImmutableMap.builder().build();
        EncDevicePersoData encDevicePersoData = enrollDeviceVts.getEncDevicePersoData();
        vtsEncDevPersoDataMap.put("deviceId", encDevicePersoData.getDeviceId());
        vtsEncDevPersoDataMap.put("encCert", encDevicePersoData.getEncCert());  		//VTS EncPubKey
        vtsEncDevPersoDataMap.put("encExpo", encDevicePersoData.getEncExpo());			//Dev DecPrKey
        vtsEncDevPersoDataMap.put("encryptedDPM", encDevicePersoData.getEncryptedDPM());
        vtsEncDevPersoDataMap.put("signCert", encDevicePersoData.getSignCert());		//VTS VerifyPubKey
        vtsEncDevPersoDataMap.put("signExpo", encDevicePersoData.getSignExpo());		//Dev SignPrKey
        vtsEncDevPersoDataMap.put("walletAccountId", encDevicePersoData.getWalletAccountId());
        vtsRespMap.put("encDevicePersoData", vtsEncDevPersoDataMap);*/

        response.put("mdes", mdesRespMap);
       // response.put("vts", vtsRespMap);
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