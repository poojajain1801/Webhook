package com.comviva.mfs.hce.appserver.util.vts;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.vts.EnrollDevice;
import com.comviva.mfs.hce.appserver.service.UserDetailServiceImpl;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.visa.cbp.encryptionutils.common.DevicePersoData;
import com.visa.cbp.encryptionutils.common.EncDevicePersoData;
import com.visa.cbp.encryptionutils.map.VisaSDKMapUtil;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

/** Send enroll device request to VTS */
// 1. Prepare Device Info
// 2. Prepare VTS Certificates
//       a) i) vCertificateID
//         ii) certUsage - CONFIDENTIALITY
//       b) i) vCertificateID
//         ii) certUsage - INTEGRITY
// 3. Generate Device Encryption Key Pair
//          i) certUsage - CONFIDENTIALITY
//         ii) certFormat - X509
//        iii) certValue -
// 4. Generate Device Signature Key Pair
//          i) certUsage - INTEGRITY
//         ii) certFormat - X509
//        iii) certValue -
// 5. Prepare EnrollDevice request and send it to VTS

/** Send response to Mobile Application */
// 1. Fetch clientDeviceID from VTS response
// 2. Fetch vClientID from VTS response
// 3. Fetch vServerNonce from VTS response
// 4. Prepare VTS Encryption Public Key
// 5. Prepare VTS Signature Public Key
// 6. Prepare Device Encryption Private Key
// 7. Prepare Device Signature Private Key
// 8. Send response to Mobile Application

@Setter
public class EnrollDeviceVts {
    private Environment env;
    private EncDevicePersoData encDevicePersoData;
    private EnrollDeviceRequest enrollDeviceRequest;
    private DevicePersoData devicePersoData;
    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollDeviceVts.class);


    public EnrollDeviceVts () {
        devicePersoData=new DevicePersoData();
    }
    public EncDevicePersoData getEncDevicePersoData() {
        return encDevicePersoData;
    }
     public String register(final String vClientID) {
         LOGGER.debug("Inside EnrollDeviceVts->Register");
        EnrollDevice enrollDevice = new EnrollDevice(env);
        enrollDevice.setVClientID(vClientID);
        String response="";
        try {
            response = enrollDevice.enrollDevice(enrollDeviceRequest.getVts().getDeviceInfo(),enrollDeviceRequest.getClientDeviceID());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.debug("Exception occurred in EnrollDeviceVts->register");
        } catch (GeneralSecurityException e) {
            LOGGER.debug("Exception occurred in EnrollDeviceVts->register");
            e.printStackTrace();
        }
         LOGGER.debug("Exit EnrollDeviceVts->register");
         return encDevicePersoData(response);
    }


    //method to create encDevicePersoData
    public String encDevicePersoData(String inputString){
        LOGGER.debug("Inside EnrollDeviceVts->encDevicePersoData");
        JSONObject jsonObject=new JSONObject(inputString);
        if("200".equals(jsonObject.get("statusCode"))) {
            devicePersoData.setDeviceId((String) jsonObject.getJSONObject("responseBody").get("clientDeviceID"));
            String DeviceSalt = String.format("%014X", Calendar.getInstance().getTime().getTime());
            DeviceSalt = DeviceSalt + ArrayUtil.getHexString(ArrayUtil.getRandom(9));
            devicePersoData.setDeviceSalt(DeviceSalt);
            devicePersoData.setMapKey(env.getProperty("mapKey"));
            devicePersoData.setMapSalt(env.getProperty("mapSalt"));
            devicePersoData.setWalletAccountId(env.getProperty("walletAccountId"));
            devicePersoData.setServerEntropy((String) jsonObject.getJSONObject("responseBody").get("vServerNonce"));
            devicePersoData.setEncExpoHex((String) jsonObject.getJSONObject("responseBody").get("devEncKeyPair"));
            devicePersoData.setEncCert((String) jsonObject.getJSONObject("responseBody").get("devEncCertificate"));
            devicePersoData.setSignExpoHex((String) jsonObject.getJSONObject("responseBody").get("devSignKeyPair"));
            devicePersoData.setSignCert((String) jsonObject.getJSONObject("responseBody").get("devSignCertificate"));
            try {
                encDevicePersoData = VisaSDKMapUtil.getEncryptedDevicePersoData(devicePersoData);
            } catch (Exception e) {
                jsonObject = new JSONObject();
                jsonObject.put("statusCode", "444");
                jsonObject.put("statusMessage", "Error while Encrypting Device PersoData");
                jsonObject.put("Error Message", e.getMessage());
                LOGGER.debug("Exception occurred in EnrollDeviceVts->encDevicePersoData");
                return jsonObject.toString();
            }
            JSONObject devicePersoDataObject = new JSONObject();
            devicePersoDataObject.put("deviceId", encDevicePersoData.getDeviceId());
            devicePersoDataObject.put("walletAccountId", encDevicePersoData.getWalletAccountId());
            devicePersoDataObject.put("encryptedDPM", encDevicePersoData.getEncryptedDPM());
            devicePersoDataObject.put("signExpo", encDevicePersoData.getSignExpo());
            devicePersoDataObject.put("encExpo", encDevicePersoData.getEncExpo());
            devicePersoDataObject.put("signCert", encDevicePersoData.getSignCert());
            devicePersoDataObject.put("encCert", encDevicePersoData.getEncCert());
            jsonObject.put("encDevicePersoData", devicePersoDataObject);
        }
        LOGGER.debug("Exit EnrollDeviceVts->encDevicePersoData");
        return jsonObject.toString();
    }
    //end
    private void enrollDevice() {

    }


}
