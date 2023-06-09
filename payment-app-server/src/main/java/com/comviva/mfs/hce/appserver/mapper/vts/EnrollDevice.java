package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.VtsDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.CertificateUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEConstants;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceVts;
import com.comviva.mfs.hce.appserver.util.vts.SdkUsageType;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import com.visa.cbp.encryptionutils.common.CertMetaData;
import com.visa.cbp.encryptionutils.common.DeviceKeyPair;
import com.visa.cbp.encryptionutils.common.DevicePersoData;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;


@Setter
@Component
public class EnrollDevice{

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollDevice.class);

    @Autowired
    public  SendReqest sendReqest;
    @Autowired
    public Environment env;

    private static final long CERTIFICATE_EXPIRY_DURATION = 2l * 365 * 24 * 60 * 60 * 1000l;

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



    public String enrollDevice(VtsDeviceInfoRequest deviceInfo,String clientDeviceID,String vClientID) {


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
        DeviceKeyPair devSignKeyPair =null;
        DeviceKeyPair devEncKeyPair =null;
        JSONArray deviceCerts =null;
        JSONObject encryptionScheme=null;
        JSONObject channelSecurityContext =null;
        String requestBody = null;
        JSONObject object= null;
        JSONObject prepareHeaderRequest= null;
        String result="";
        JSONObject jsonObject = null;
        JSONObject jsonResponse=null;
        JSONObject jsonRequest = null;
        ResourceLoader resourceLoader = null;

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


            resourceLoader = new FileSystemResourceLoader() ;
            resource = resourceLoader.getResource("classpath:master_keyPkcs8.key");
            inputStream  = resource.getInputStream();
            PrivateKey masterPrivateKey = CertificateUtil.getRsaPrivateKey(inputStream);

            resource = resourceLoader.getResource("classpath:master_cert.pem");
            inputStream   = resource.getInputStream();
            X509Certificate masterCertificate = CertificateUtil.getCertificate(inputStream);

            issuerName = new JcaX509CertificateHolder(masterCertificate).getSubject();

            // Preparing Certificate Meta Data
            currentTimeInMilli = System.currentTimeMillis();
            certMetaData = new CertMetaData();
            certMetaData.setIssuer(issuerName);
            certMetaData.setSerial("2");
            certMetaData.setNotBefore(System.currentTimeMillis());
            certMetaData.setNotAfter(currentTimeInMilli + CERTIFICATE_EXPIRY_DURATION);
            certMetaData.setSubject(new X500Name("CN=Device Certificate"));

             devEncKeyPair =VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID, issuerName, certMetaData, masterPrivateKey);
             devSignKeyPair =VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID,issuerName, certMetaData, masterPrivateKey);


            deviceCerts = new JSONArray();
            var = new JSONObject();
            var.put("certFormat", "X509");
            var.put("certUsage", CertUsage.CONFIDENTIALITY);
            byte[] b64EncCert = Base64.encodeBase64URLSafe(devEncKeyPair.getCertificate().getBytes());
            var.put("certValue",new String(b64EncCert));
            deviceCerts.put(0,var);

            // Device Encryption Certificate
            var = new JSONObject();
            var.put("certFormat", "X509");
            var.put("certUsage", CertUsage.INTEGRITY);
            byte[] b64SignCert = Base64.encodeBase64URLSafe(devSignKeyPair.getCertificate().getBytes());
            var.put("certValue",new String(b64SignCert));
            deviceCerts.put(1,var);



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
            jsonResponse = sendReqest.postHttpRequest(requestBody.getBytes(),requestBody,sandBoxUrl,prepareHeaderRequest);
            if (HttpStatus.SC_OK == jsonResponse.getInt(HCEConstants.STATUS_CODE) ) {
                jsonObject = jsonResponse.getJSONObject("response");
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

}