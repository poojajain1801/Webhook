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

package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ChannelSecurityContext;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceCerts;
import com.comviva.mfs.hce.appserver.mapper.pojo.PublicKeyDeviceCert;
import com.comviva.mfs.hce.appserver.mapper.pojo.VtsDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.CertificateUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import com.comviva.mfs.hce.appserver.util.vts.SdkUsageType;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import com.visa.cbp.encryptionutils.common.CertMetaData;
import com.visa.cbp.encryptionutils.common.DeviceKeyPair;
import com.visa.cbp.encryptionutils.map.VisaSDKMapUtil;

import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Setter
@Component
public class EnrollDevice{

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollDevice.class);

    @Autowired
    public  SendReqest sendReqest;
    @Autowired
    public Environment env;

    private static final long CERTIFICATE_EXPIRY_DURATION = 365 * 24 * 60 * 60 * 1000L;

    /**
     * Prepares device information for VTS.
     *
     * @param devInfo Device Information
     * @return Device Information in JSON format.
     */
    private JSONObject prepareDeviceInfo(VtsDeviceInfoRequest devInfo) {
        LOGGER.debug("Inside EnrollDevice->prepareDeviceInfo");

        JSONObject devInfoVts = new JSONObject();
        String deviceName;
        byte[] b64data;
        try {

            devInfoVts.put("osType", devInfo.getOsType());
            devInfoVts.put("deviceType", devInfo.getDeviceType());
            deviceName = devInfo.getDeviceName();
            b64data = Base64.encodeBase64URLSafe(deviceName.getBytes());
            devInfoVts.put("deviceName", new String(b64data, "UTF-8"));

            devInfoVts.put("osVersion", devInfo.getOsVersion());
            devInfoVts.put("osBuildID", devInfo.getOsBuildID());
            devInfoVts.put("deviceIDType", devInfo.getDeviceIDType());
            devInfoVts.put("deviceManufacturer", devInfo.getDeviceManufacturer());
            devInfoVts.put("deviceBrand", devInfo.getDeviceBrand());
            devInfoVts.put("deviceModel", devInfo.getDeviceModel());

        }catch (Exception e){
            LOGGER.error("Exit EnrollDevice",e);
            LOGGER.debug("Exit EnrollDevice->prepareDeviceInfo");
        }
        return devInfoVts;
    }

    /**
     * Creates DeviceInitParam for enroll device.
     *
     * @return deviceInitParam in JSON format
     */
    private JSONObject createDeviceInitParam() {
        LOGGER.debug("Inside EnrollDevice->createDeviceInitParam");
        JSONObject deviceInitParams = new JSONObject();
        deviceInitParams.put("sdkUsageType", SdkUsageType.Android_With_WBC.name());
        JSONObject regDevParams = new JSONObject();
        regDevParams.put("deviceInitParams", deviceInitParams);
        LOGGER.debug("Exit EnrollDevice->createDeviceInitParam");
        return regDevParams;
    }

    private String generateXrequestId() {
        String xRequestId = String.format("%014X", Calendar.getInstance().getTime().getTime());
        xRequestId = xRequestId + ArrayUtil.getHexString(ArrayUtil.getRandom(10));
        return xRequestId;
    }



    public String enrollDevice(VtsDeviceInfoRequest deviceInfo, ChannelSecurityContext channelSecurityContextReq, String clientDeviceID, String vClientID)
            throws NullPointerException {


        JSONObject jsDeviceInfo = null;
        JSONObject jsDevInitParams= null;
        JSONArray vtsCerts = null;
        JSONObject var =null;
        String vtsCertificateIDConf = null;
        String vtsCertificateIDSign =null;
        Resource resource = null;
        InputStream inputStream = null;
        CertMetaData certMetaData =null;
        X500Name issuerName = null;
        long currentTimeInMilli =0;
        JSONArray deviceCerts =null;
        JSONObject encryptionScheme=null;
        JSONObject channelSecurityContext =null;
        String requestBody = null;
        JSONObject prepareHeaderRequest= null;
        JSONObject jsonObject = null;
        DeviceKeyPair devSignKeyPair =null;
        DeviceKeyPair devEncKeyPair =null;
        JSONObject jsonResponse=null;
        JSONObject jsonRequest = null;
        ResourceLoader resourceLoader = null;
        String signedDeviceCert = "";
        byte[] b64SignCert = null;
        byte[] b64EncCert = null;


        try {
            jsDeviceInfo = prepareDeviceInfo(deviceInfo);
            jsDevInitParams = createDeviceInitParam();
            vtsCerts = new JSONArray();
            var = new JSONObject();
            vtsCertificateIDConf = env.getProperty("vCertificateID_Conf");
            vtsCertificateIDSign = env.getProperty("vCertificateID_Sign");
            // VTS Encryption Key Pair
            var.put("certUsage", CertUsage.CONFIDENTIALITY.name());
            var.put("vCertificateID", vtsCertificateIDConf);
            vtsCerts.put(0, var);

            // VTS Signature Key Pair
            var = new JSONObject();
            var.put("certUsage", CertUsage.INTEGRITY.name());
            var.put("vCertificateID", vtsCertificateIDSign);
            //var.put("vCertificateID","bf617210");
            vtsCerts.put(1, var);

            LOGGER.info("Load MasterKey.key file before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
            resourceLoader = new FileSystemResourceLoader();
            resource = resourceLoader.getResource("classpath:master_keyPkcs8.key");
            inputStream  = resource.getInputStream();
            PrivateKey masterPrivateKey = CertificateUtil.getRsaPrivateKey(inputStream);
            LOGGER.info("Load MasterKey.key file After hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));

            LOGGER.info("Load MasterKey.pem file before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
            resource = resourceLoader.getResource("classpath:master_cert.pem");
            inputStream = resource.getInputStream();
            X509Certificate masterCertificate = CertificateUtil.getCertificate(inputStream);


            issuerName = new JcaX509CertificateHolder(masterCertificate).getSubject();
            LOGGER.info("Load MasterKey.pem file after hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
            // Preparing Certificate Meta Data
            currentTimeInMilli = System.currentTimeMillis();
            certMetaData = new CertMetaData();
            certMetaData.setIssuer(issuerName);
            certMetaData.setSerial("2");
            certMetaData.setNotBefore(System.currentTimeMillis());
            certMetaData.setNotAfter(currentTimeInMilli + CERTIFICATE_EXPIRY_DURATION);
            certMetaData.setSubject(new X500Name("CN=Device Certificate"));

            //if device certs is in the request
            List<DeviceCerts> deviceCertsList = null;
            if(null != channelSecurityContextReq) {
                deviceCertsList = channelSecurityContextReq.getDeviceCerts();
                deviceCerts = prepareDeviceCerts(deviceCertsList, issuerName, certMetaData, masterPrivateKey);
            } else {
                LOGGER.info("generate devEncKeyPair before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
                devEncKeyPair =VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID, issuerName, certMetaData, masterPrivateKey);
                LOGGER.info("generate devEncKeyPair after hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));

                LOGGER.info("generate devSignKeyPair before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
                devSignKeyPair =VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID,issuerName, certMetaData, masterPrivateKey);
                LOGGER.info("generate devSignKeyPair after hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));

                deviceCerts = new JSONArray();
                var = new JSONObject();
                var.put("certFormat", "X509");
                var.put("certUsage", CertUsage.CONFIDENTIALITY);
                b64EncCert = Base64.encodeBase64URLSafe(devEncKeyPair.getCertificate().getBytes());
                var.put("certValue",new String(b64EncCert));
                deviceCerts.put(0,var);

                // Device Encryption Certificate
                var = new JSONObject();
                var.put("certFormat", "X509");
                var.put("certUsage", CertUsage.INTEGRITY);
                b64SignCert = Base64.encodeBase64URLSafe(devSignKeyPair.getCertificate().getBytes());
                var.put("certValue",new String(b64SignCert));
                deviceCerts.put(1,var);
            }



            encryptionScheme=new JSONObject();
            encryptionScheme.put("encryptionScheme","RSA_PKI");
            // Prepare channelSecurityContext
            channelSecurityContext = new JSONObject();
            channelSecurityContext.put("deviceCerts", deviceCerts);
            channelSecurityContext.put("vtsCerts", vtsCerts);
            channelSecurityContext.put("channelInfo",encryptionScheme);


            /* Prepare Enroll Device Request */
            jsonRequest = new JSONObject();
            jsonRequest.put("deviceInfo", jsDeviceInfo);
            jsonRequest.put("channelSecurityContext", channelSecurityContext);
            requestBody = jsonRequest.toString();

            prepareHeaderRequest=new JSONObject();
            prepareHeaderRequest.put("xRequestId",generateXrequestId());
            String queryString = "apiKey="+env.getProperty("apiKey");
            prepareHeaderRequest.put("queryString",queryString);
            prepareHeaderRequest.put("resourcePath","vts/clients/"+vClientID+"/devices/"+clientDeviceID);
            prepareHeaderRequest.put("requestBody",requestBody);

            final String sandBoxUrl =  env.getProperty("visaBaseUrlSandbox") + "/" + prepareHeaderRequest.get("resourcePath")+ "?apiKey=" +env.getProperty("apiKey");

            //final String sandBoxUrl =  env.getProperty("visaBaseUrlSandbox") + "/" + "vts/clients/1/0/register";

            LOGGER.info("VISA register device before hit --> TIME " +HCEUtil.convertDateToTimestamp(new Date()));
            jsonResponse = sendReqest.postHttpRequest(requestBody.getBytes(),requestBody,sandBoxUrl,prepareHeaderRequest);
            LOGGER.info("VISA register device After hit --> TIME " +HCEUtil.convertDateToTimestamp(new Date()));
            LOGGER.info("VISA register device --> RESPONSE " +jsonResponse);

            if (HttpStatus.SC_OK == jsonResponse.getInt(HCEConstants.STATUS_CODE) ) {
                jsonObject = jsonResponse.getJSONObject("response");
                if(null == channelSecurityContextReq) {
                    jsonObject.put("devEncKeyPair", devEncKeyPair.getPrivateKeyHex());
                    jsonObject.put("devEncCertificate", new String(b64EncCert));
                    jsonObject.put("devSignKeyPair", devSignKeyPair.getPrivateKeyHex());
                    jsonObject.put("devSignCertificate", new String(b64SignCert));

                    jsonObject.put("vtsCerts-certUsage-confidentiality", CertUsage.CONFIDENTIALITY.name());
                    jsonObject.put("vtsCerts-vCertificateID-confidentiality", vtsCertificateIDConf);

                    jsonObject.put("vtsCerts-certUsage-integrity", CertUsage.INTEGRITY.name());
                    jsonObject.put("vtsCerts-vCertificateID-integrity", vtsCertificateIDSign);

                    jsonObject.put("deviceCerts-certFormat-confidentiality", "X509");
                    jsonObject.put("deviceCerts-certUsage-confidentiality", CertUsage.CONFIDENTIALITY);
                    jsonObject.put("deviceCerts-certValue-confidentiality", new String(b64EncCert));

                    jsonObject.put("deviceCerts-certFormat-integrity", "X509");
                    jsonObject.put("deviceCerts-certUsage-integrity", CertUsage.INTEGRITY);
                    jsonObject.put("deviceCerts-certValue-integrity", new String(b64SignCert));
                    jsonResponse.put("responseBody", jsonObject);
                }
                jsonResponse.put("responseBody", jsonObject);
            }
            else {
                jsonResponse.put(HCEConstants.RESPONSE_CODE,String.valueOf(jsonResponse.getInt(HCEConstants.STATUS_CODE)));
                jsonResponse.put(HCEConstants.MESSAGE,jsonResponse.getString(HCEConstants.STATUS_MESSAGE));
            }
        } catch (HCEActionException enrollDeviceActionException) {
            LOGGER.error("Exception occured in  EnrollDevice->enrollDevice", enrollDeviceActionException);
            throw enrollDeviceActionException;
        } catch (Exception enrollDeviceException) {
            LOGGER.error("Exception occured in  EnrollDevice->enrollDevice", enrollDeviceException);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return jsonResponse.toString();
    }

    private JSONArray prepareDeviceCerts(List<DeviceCerts> deviceCertsList, X500Name issuerName, CertMetaData certMetaData,
                                         PrivateKey masterPrivateKey) throws CertificateException {
        JSONObject var = null;
        String signedDeviceCert = "";
        JSONArray deviceCerts = new JSONArray();
        for(DeviceCerts deviceCert : deviceCertsList) {
            try {
                PublicKeyDeviceCert publicKeyDeviceCert = new PublicKeyDeviceCert(deviceCert);
                PublicKey devicePublicKey = publicKeyDeviceCert.getPublicKey();

                LOGGER.info("generate device certificate before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
                signedDeviceCert = VisaSDKMapUtil.generateDeviceCertificate(issuerName, certMetaData, masterPrivateKey,
                        devicePublicKey.getEncoded());
                LOGGER.info("generate device certificate before hit  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));

                byte[] signedDeviceCertBytes = signedDeviceCert.getBytes();
                byte[] signedDeviceCertBase64 = Base64.encodeBase64URLSafe(signedDeviceCertBytes);
                String finalSignedDeviceCert = new String(signedDeviceCertBase64);
                //Set the new device certificate in the request
                deviceCert.setCertValue(finalSignedDeviceCert);

                var = new JSONObject();
                var.put("certFormat", "X509");
                if(CertUsage.CONFIDENTIALITY.name().equalsIgnoreCase(deviceCert.getCertUsage())) {
                    var.put("certUsage", CertUsage.CONFIDENTIALITY);
                    var.put("certValue", deviceCert.getCertValue());
                    deviceCerts.put(0,var);
                } else if(CertUsage.INTEGRITY.name().equalsIgnoreCase(deviceCert.getCertUsage())) {
                    var.put("certUsage", CertUsage.INTEGRITY);
                    var.put("certValue", deviceCert.getCertValue());
                    deviceCerts.put(1,var);
                } else if(CertUsage.DEVICE_ROOT.name().equalsIgnoreCase(deviceCert.getCertUsage())) {
                    var.put("certUsage", CertUsage.DEVICE_ROOT);
                    var.put("certValue", deviceCert.getCertValue());
                    deviceCerts.put(2,var);
                }

            } catch(CertificateException certificateException) {
                LOGGER.error("Exception in enroll device -> certificateException"+certificateException);
                throw certificateException;
            }
        }
        return deviceCerts;
    }
}
