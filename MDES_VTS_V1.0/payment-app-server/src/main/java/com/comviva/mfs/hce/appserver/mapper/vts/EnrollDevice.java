package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.mapper.pojo.VtsDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.CertificateUtil;
import com.comviva.mfs.hce.appserver.util.vts.SdkUsageType;
import com.visa.cbp.encryptionutils.common.CertMetaData;
import com.visa.cbp.encryptionutils.common.DeviceKeyPair;
import com.visa.cbp.encryptionutils.common.DevicePersoData;
import com.visa.cbp.encryptionutils.map.VisaSDKMapUtil;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

@Setter
public class EnrollDevice extends VtsRequest {
    private String vClientID;
   // private String apiKey;
    //private String clientDeviceID;
    private DevicePersoData devicePersoData;

    private static final long CERTIFICATE_EXPIRY_DURATION = 2l * 365 * 24 * 60 * 60 * 1000l;

    public EnrollDevice(Environment env) {
        super(env);
        devicePersoData = new DevicePersoData();
    }

    /**
     * Prepares device information for VTS.
     *
     * @param devInfo Device Information
     * @return Device Information in JSON format.
     */
    private JSONObject prepareDeviceInfo(VtsDeviceInfoRequest devInfo) {
        JSONObject devInfoVts = new JSONObject();
        devInfoVts.put("osType", devInfo.getOsType());
        devInfoVts.put("deviceType", devInfo.getDeviceType());
        devInfoVts.put("deviceName", devInfo.getDeviceName());
        devInfoVts.put("osVersion",devInfo.getOsVersion());
        devInfoVts.put("osBuildID",devInfo.getOsBuildID());
        devInfoVts.put("deviceIDType",devInfo.getDeviceIDType());
        devInfoVts.put("deviceManufacturer",devInfo.getDeviceManufacturer());
        devInfoVts.put("deviceBrand",devInfo.getDeviceBrand());
        devInfoVts.put("deviceModel",devInfo.getDeviceModel());
        return devInfoVts;
    }

    /**
     * Creates DeviceInitParam for enroll device.
     *
     * @return deviceInitParam in JSON format
     */
    private JSONObject createDeviceInitParam() {
        JSONObject deviceInitParams = new JSONObject();
        deviceInitParams.put("sdkUsageType", SdkUsageType.Android_With_WBC.name());
        JSONObject regDevParams = new JSONObject();
        regDevParams.put("deviceInitParams", deviceInitParams);
        return regDevParams;
    }

    private String generateXrequestId() {
        String xRequestId = String.format("%014X", Calendar.getInstance().getTime().getTime());
        xRequestId = xRequestId + ArrayUtil.getHexString(ArrayUtil.getRandom(10));
        return xRequestId;
    }

    public DevicePersoData getDevicePersoData() {
        return devicePersoData;
    }
    public String enrollDevice(VtsDeviceInfoRequest deviceInfo,String clientDeviceID) throws IOException, GeneralSecurityException {
        /* Device Information & Init Param */
        JSONObject jsDeviceInfo = prepareDeviceInfo(deviceInfo);
        JSONObject jsDevInitParams = createDeviceInitParam();
        /* VTS Certificates */
        JSONArray vtsCerts = new JSONArray();
        JSONObject var = new JSONObject();
        // Fetch Certificate Id and value
        String vtsCertificateIDConf = env.getProperty("vCertificateID_Conf");
        String vtsCertificateIDSign = env.getProperty("vCertificateID_Sign");
        // VTS Encryption Key Pair
        var.put("certUsage", CertUsage.CONFIDENTIALITY.name());
        var.put("vCertificateID", vtsCertificateIDConf);
        //var.put("vCertificateID","f1606e98");
        vtsCerts.put(0, var);

        // VTS Signature Key Pair
        var = new JSONObject();
        var.put("certUsage", CertUsage.INTEGRITY.name());
        var.put("vCertificateID", vtsCertificateIDSign);
        //var.put("vCertificateID","bf617210");
        vtsCerts.put(1, var);

        /* Device Certificates */
        // Fetch master private key from PEM file
        ClassLoader classLoader = getClass().getClassLoader();
        File masterKeyFile = new File(classLoader.getResource("master_keyPkcs8.key").getFile());
        PrivateKey masterPrivateKey = CertificateUtil.getRsaPrivateKey(masterKeyFile.getAbsolutePath());

        // Create the metadata required for cert creation.
        File masterCertificateFile = new File(classLoader.getResource("master_cert.pem").getFile());
        X509Certificate masterCertificate = CertificateUtil.getCertificate(masterCertificateFile.getAbsolutePath());
        CertMetaData certMetaData = new CertMetaData();
        X500Name issuerName = null;
        try {
            issuerName = new JcaX509CertificateHolder(masterCertificate).getSubject();
        } catch (CertificateEncodingException e) {
        }
        certMetaData.setIssuer(issuerName);
        certMetaData.setSerial("2");
        long currentTimeInMilli = System.currentTimeMillis();
        certMetaData.setNotBefore(currentTimeInMilli);
        certMetaData.setNotAfter(currentTimeInMilli + CERTIFICATE_EXPIRY_DURATION);
        certMetaData.setSubject(new X500Name("CN=Device Certificate"));
        DeviceKeyPair devEncKeyPair =VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID, issuerName, certMetaData, masterPrivateKey);
        DeviceKeyPair devSignKeyPair =VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID,issuerName, certMetaData, masterPrivateKey);

        JSONArray deviceCerts = new JSONArray();
        var = new JSONObject();
        var.put("certFormat", "X509");
        var.put("certUsage", CertUsage.CONFIDENTIALITY);
        byte[] b64data = Base64.encodeBase64URLSafe(devEncKeyPair.getCertificate().getBytes());
        var.put("certValue",new String(b64data));
        deviceCerts.put(0,var);

        // Device Encryption Certificate
        var = new JSONObject();
        var.put("certFormat", "X509");
        var.put("certUsage", CertUsage.INTEGRITY);
        byte[] twoencodedBytes = Base64.encodeBase64URLSafe(devSignKeyPair.getCertificate().getBytes());
        var.put("certValue",new String(twoencodedBytes));
        deviceCerts.put(1,var);

        JSONObject encryptionScheme=new JSONObject();
        encryptionScheme.put("encryptionScheme","RSA_PKI");
       // Prepare channelSecurityContext
        JSONObject channelSecurityContext = new JSONObject();
        channelSecurityContext.put("deviceCerts", deviceCerts);
        channelSecurityContext.put("vtsCerts", vtsCerts);


        channelSecurityContext.put("channelInfo",encryptionScheme);

        /* Prepare Enroll Device Request */
        jsonRequest.put("deviceInfo", jsDeviceInfo);
        jsonRequest.put("channelSecurityContext", channelSecurityContext);
        String requestBody = jsonRequest.toString();

        JSONObject object=new JSONObject(requestBody);
        requestBody=object.toString();
        JSONObject prepareHeaderRequest=new JSONObject();
        prepareHeaderRequest.put("xRequestId",generateXrequestId());
        prepareHeaderRequest.put("queryString","apiKey=R7Q53W6KREF7DHCDXUAQ13RQPTXkdUwfMvteVPXPJhOz5xWBc");
        prepareHeaderRequest.put("resourcePath","vts/clients/"+vClientID+"/devices/"+clientDeviceID);
        prepareHeaderRequest.put("requestBody",requestBody);
        prepareHeader(prepareHeaderRequest);
        final HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("172.19.7.180",8080));
        requestFactory.setProxy(proxy);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        final String sandBoxUrl = vtsUrl + PATH_SEPARATOR + prepareHeaderRequest.get("resourcePath")+ "?apiKey=" + apiKey;
        String result="";
        JSONObject jsonObject = null;
        JSONObject jsonResponse=null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(sandBoxUrl, HttpMethod.PUT, entity, String.class);
            jsonResponse=new JSONObject();
            jsonResponse.put("statusCode",String.valueOf(response.getStatusCode().value()));
            jsonResponse.put("statusMessage",String.valueOf(response.getStatusCode().getReasonPhrase()));
            result=response.getBody();
            jsonObject=new JSONObject(result);
            jsonObject.put("devEncKeyPair",devEncKeyPair);
            jsonObject.put("devEncCertificate",new String(b64data));
            jsonObject.put("devSignKeyPair",devSignKeyPair);
            jsonObject.put("devSignCertificate",new String(twoencodedBytes));

            jsonObject.put("vtsCerts-certUsage-confidentiality",CertUsage.CONFIDENTIALITY.name());
            jsonObject.put("vtsCerts-vCertificateID-confidentiality",vtsCertificateIDConf);

            jsonObject.put("vtsCerts-certUsage-integrity",CertUsage.INTEGRITY.name());
            jsonObject.put("vtsCerts-vCertificateID-integrity",vtsCertificateIDSign);

            jsonObject.put("deviceCerts-certFormat-confidentiality","X509");
            jsonObject.put("deviceCerts-certUsage-confidentiality",CertUsage.CONFIDENTIALITY);
            jsonObject.put("deviceCerts-certValue-confidentiality",new String(b64data));

            jsonObject.put("deviceCerts-certFormat-integrity","X509");
            jsonObject.put("deviceCerts-certUsage-integrity",CertUsage.INTEGRITY);
            jsonObject.put("deviceCerts-certValue-integrity",new String(twoencodedBytes));


            jsonResponse.put("responseBody",jsonObject);
        }catch (Exception e){
           // ((HttpClientErrorException)e).getResponseBodyAsString();
            jsonResponse=new JSONObject();
            jsonResponse.put("statusCode",e.getMessage());
            jsonResponse.put("statusMessage","unauthorised");
            return jsonResponse.toString();
        }
        return jsonResponse.toString();
    }
}