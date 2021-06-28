/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.comviva.mfs.hce.appserver.util.vts;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.mapper.vts.EnrollDevice;
import com.comviva.mfs.hce.appserver.service.UserDetailServiceImpl;
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
            ChannelSecurityContext channelSecurityContext = enrollDeviceRequest.getVts().getChannelSecurityContext();
            if(null != channelSecurityContext) {
                response = enrollDevice.enrollDevice(enrollDeviceRequest.getVts().getDeviceInfo(), enrollDeviceRequest.getVts().getChannelSecurityContext(), enrollDeviceRequest.getClientDeviceID(), vClientID);
            } else {
                response = enrollDevice.enrollDevice(enrollDeviceRequest.getVts().getDeviceInfo(), null, enrollDeviceRequest.getClientDeviceID(), vClientID);
                return encDevicePersoData(response);
            }
        } catch (HCEActionException regHceActionException) {
            LOGGER.error("Exception occured in EnrollDeviceVts->register", regHceActionException);
            throw regHceActionException;
        } catch (Exception regException) {
            LOGGER.error("Exception occured in EnrollDeviceVts->register", regException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return response;
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