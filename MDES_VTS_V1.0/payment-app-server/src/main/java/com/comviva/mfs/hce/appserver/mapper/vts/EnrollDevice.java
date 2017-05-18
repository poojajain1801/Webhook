package com.comviva.mfs.hce.appserver.mapper.vts;

import com.comviva.mfs.hce.appserver.mapper.pojo.VtsDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.util.common.ArrayUtil;
import com.comviva.mfs.hce.appserver.util.common.CertificateUtil;
import com.comviva.mfs.hce.appserver.util.vts.EnrollDeviceResponse;
import com.comviva.mfs.hce.appserver.util.vts.SdkUsageType;
import com.visa.cbp.encryptionutils.common.CertMetaData;
import com.visa.cbp.encryptionutils.common.DeviceKeyPair;
import com.visa.cbp.encryptionutils.common.DevicePersoData;
import com.visa.cbp.encryptionutils.map.VisaSDKMapUtil;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;

@Setter
public class EnrollDevice extends VtsRequest {
    private String vClientID;
    private String clientDeviceID;
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
       // devInfoVts.put("osType", devInfo.getOsName());
        devInfoVts.put("osVersion", devInfo.getOsVersion());
        devInfoVts.put("deviceType", devInfo.getDeviceType());
        //devInfoVts.put("deviceName", devInfo.getDeviceNickName());

        String var = devInfo.getDeviceManufacturer();
        if (var != null) {
            devInfoVts.put("deviceManufacturer", var);
        }
        var = devInfo.getDeviceModel();
        if (var != null) {
            devInfoVts.put("deviceModel", var);
        }
        var = devInfo.getHostDeviceID();
        if (var != null) {
            devInfoVts.put("hostDeviceID", var);
        }
        var = devInfo.getOsBuildID();
        if (var != null) {
            devInfoVts.put("osBuildID", var);
        }
        var = devInfo.getDeviceIDType();
        if (var != null) {
            devInfoVts.put("deviceIDType", var);
        }
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

    private void generateClientDeviceId() {
        clientDeviceID = String.format("%014X", Calendar.getInstance().getTime().getTime());
        clientDeviceID = clientDeviceID + ArrayUtil.getHexString(ArrayUtil.getRandom(5));
    }

    public DevicePersoData getDevicePersoData() {
        return devicePersoData;
    }

    public ResponseEntity<EnrollDeviceResponse> enrollDevice(VtsDeviceInfoRequest deviceInfo) throws IOException, GeneralSecurityException {
        /* Device Information & Init Param */
        JSONObject jsDeviceInfo = prepareDeviceInfo(deviceInfo);
        JSONObject jsDevInitParams = createDeviceInitParam();

        generateClientDeviceId();

        /* VTS Certificates */
        JSONArray vtsCerts = new JSONArray();
        JSONObject var = new JSONObject();
        // Fetch Certificate Id and value
        String vtsCertificateIDConf = env.getProperty("vCertificateID_Conf");
        String vtsCertificateIDSign = env.getProperty("vCertificateID_Sign");
        // VTS Encryption Key Pair
        var.put("certUsage", CertUsage.CONFIDENTIALITY.name());
        var.put("vCertificateID", vtsCertificateIDConf);
        vtsCerts.put(0, var);
        // VTS Signature Key Pair
        var = new JSONObject();
        var.put("certUsage", CertUsage.INTEGRITY.name());
        var.put("vCertificateID", vtsCertificateIDSign);
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
        DeviceKeyPair devEncKeyPair = VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID, issuerName, certMetaData, masterPrivateKey);
        DeviceKeyPair devSignKeyPair = VisaSDKMapUtil.generateDeviceKeyPair(clientDeviceID, issuerName, certMetaData, masterPrivateKey);
        JSONArray deviceCerts = new JSONArray();
        // Device Encryption Certificate
        var = new JSONObject();
        var.put("certFormat", "X509");
        var.put("certUsage", CertUsage.CONFIDENTIALITY);
        var.put("certValue", devEncKeyPair.getCertificate());
        deviceCerts.put(0, var);
        // Device Encryption Certificate
        var = new JSONObject();
        var.put("certFormat", "X509");
        var.put("certUsage", CertUsage.INTEGRITY);
        var.put("certValue", devSignKeyPair.getCertificate());
        deviceCerts.put(1, var);

        // Prepare channelSecurityContext
        JSONObject channelSecurityContext = new JSONObject();
        channelSecurityContext.put("deviceCerts", deviceCerts);
        channelSecurityContext.put("vtsCerts", vtsCerts);

        /* Prepare Enroll Device Request */
        jsonRequest.put("deviceInfo", jsDeviceInfo);
        jsonRequest.put("deviceInitParams", jsDevInitParams);
        jsonRequest.put("channelSecurityContext", channelSecurityContext);
        String requestBody = jsonRequest.toString();

        prepareHeader(ArrayUtil.getHexString(ArrayUtil.getRandom(36)),
                RequestId.ENROLL_DEVICE, queryString.toString(), requestBody);

        RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        final String sandBoxUrl = vtsUrl + PATH_SEPARATOR
                + RequestId.ENROLL_DEVICE.getResourcePath() + PATH_SEPARATOR
                + vClientID + PATH_SEPARATOR + "devices" + PATH_SEPARATOR
                + clientDeviceID + "?apiKey=" + apiKey;

        ResponseEntity<EnrollDeviceResponse> responseEntity = restTemplate.postForEntity(sandBoxUrl, entity, EnrollDeviceResponse.class);

        // Prepare device personalization data
        EnrollDeviceResponse enrollDevResp = responseEntity.getBody();
        devicePersoData.setDeviceId(clientDeviceID);
        devicePersoData.setDeviceSalt(ArrayUtil.getHexString(ArrayUtil.getRandom(32)));
        devicePersoData.setMapKey(env.getProperty("mapKey"));
        devicePersoData.setMapSalt(env.getProperty("mapSalt"));
        devicePersoData.setWalletAccountId(env.getProperty("walletAccountId"));
        devicePersoData.setServerEntropy(enrollDevResp.getVServerNonce());
        devicePersoData.setEncExpoHex(devEncKeyPair.getPrivateKeyHex());
        devicePersoData.setEncCert(devEncKeyPair.getCertificate());
        devicePersoData.setSignExpoHex(devSignKeyPair.getPrivateKeyHex());
        devicePersoData.setSignCert(devSignKeyPair.getCertificate());

        return responseEntity;
    }



}