package com.comviva.mfs.hce.appserver.util.vts;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.vts.EnrollDevice;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.visa.cbp.encryptionutils.common.DevicePersoData;
import com.visa.cbp.encryptionutils.common.EncDevicePersoData;
import com.visa.cbp.encryptionutils.common.EncryptionEnvironment;
import com.visa.cbp.encryptionutils.map.VisaSDKMapUtil;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import java.util.Calendar;
import java.util.Date;

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
@Component
public class EnrollDeviceVts {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollDeviceVts.class);
    @Autowired
    public Environment env;
    @Autowired
    public  EnrollDevice enrollDevice;

    public String register(final String vClientID, EnrollDeviceRequest enrollDeviceRequest) {
        String response="";
        try {
            response = enrollDevice.enrollDevice(enrollDeviceRequest.getVts().getDeviceInfo(),enrollDeviceRequest.getClientDeviceID(), vClientID);
        } catch (HCEActionException regHceActionException) {
            LOGGER.error("Exception occured in EnrollDeviceVts->register", regHceActionException);
            throw regHceActionException;
        } catch (Exception regException) {
            LOGGER.error("Exception occured in EnrollDeviceVts->register", regException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return encDevicePersoData(response);
    }

    //method to create encDevicePersoData
    public String encDevicePersoData(String inputString){

        DevicePersoData devicePersoData =null;
        EncDevicePersoData encDevicePersoData = null;
        JSONObject jsonObject=null;

        try {
            devicePersoData = new DevicePersoData();
            encDevicePersoData = new EncDevicePersoData();
            jsonObject=new JSONObject(inputString);
            if("200".equals(jsonObject.get(HCEConstants.STATUS_CODE))) {
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

                LOGGER.info("Encrypt device perso before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
                if(!(this.env.getProperty("mdeshost")).equalsIgnoreCase("mtf")) {
                    if (this.env.getProperty("cemea").equalsIgnoreCase("Y")){
                        encDevicePersoData = VisaSDKMapUtil.getEncryptedDevicePersoData(EncryptionEnvironment.CEMA_PROD, devicePersoData);
                    }else{
                        encDevicePersoData = VisaSDKMapUtil.getEncryptedDevicePersoData(EncryptionEnvironment.PROD, devicePersoData);
                    }
                }else {
                    encDevicePersoData = VisaSDKMapUtil.getEncryptedDevicePersoData(EncryptionEnvironment.SBX, devicePersoData);
                }
                LOGGER.info("Encrypt device perso After hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
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
        } catch (HCEActionException regHceActionException) {
            LOGGER.error("Exception occured in EnrollDeviceVts->encDevicePersoData", regHceActionException);
            throw regHceActionException;
        } catch (Exception regException) {
            LOGGER.error("Exception occured in EnrollDeviceVts->encDevicePersoData", regException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return jsonObject.toString();
    }

}