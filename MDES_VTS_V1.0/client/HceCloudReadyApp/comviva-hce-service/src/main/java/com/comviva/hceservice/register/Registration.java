package com.comviva.hceservice.register;


import android.os.AsyncTask;

import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.common.RestResponse;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.Miscellaneous;
import com.comviva.hceservice.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import com.comviva.hceservice.util.crypto.MessageDigestUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mastercard.mcbp.api.McbpApi;
import com.mastercard.mcbp.api.MpaManagementApi;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.gcm.GcmRegistrationFailed;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.visa.cbp.external.common.EncDevicePersoData;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;

/**
 *  This class contains all apis related to user registration and device enrollment.
 */
public class Registration {
    private VisaPaymentSDK visaPaymentSDK;
    private byte[] deviceFingerprint;

    /**
     * Identifier for the specific Mobile Payment App instance, unique across a given Wallet Identifier.
     * Maximum length is 48 characters(24 bytes). Random Number (14 bytes) + Current time in millisecond(10 byte)
     *
     * @return PaymentAppInstanceId
     */
    private String generatePaymentAppInstanceId() {
        byte[] random = ArrayUtil.getRandomNumber(14);
        long currentTimeInMs = new Date().getTime();
        return ArrayUtil.getHexString(random) + String.format("%020d", currentTimeInMs);
    }

    private JSONObject getRegisterDeviceRequest(RegisterParam registerParam) {
        try {
            ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
            String paymentAppInstanceId = generatePaymentAppInstanceId();
            registerParam.setPaymentAppInstanceId(paymentAppInstanceId);
            if (registerParam.getFcmRegistrationId() == null) {
                registerParam.setFcmRegistrationId(comvivaHce.getRnsInfo().getRegistrationId());
            }

            JSONObject regDevParam = new JSONObject();
            // Common Register Parameters
            regDevParam.put("userId", registerParam.getUserId());
            regDevParam.put("gcmRegistrationId", registerParam.getFcmRegistrationId());

            // VTS Registration Parameters
            JSONObject vtsEnrollDeviceReqJson = new JSONObject();
            JSONObject vtsDeviceInfo = enrollDeviceVtsReqJSon(registerParam.getDeviceName());
            vtsEnrollDeviceReqJson.put("deviceInfo", vtsDeviceInfo);

            // MDES Registration Parameters
            JSONObject mdesRegDevJson = new JSONObject();
            mdesRegDevJson.put("deviceInfo", getDeviceInfoInJson(registerParam.getDeviceInfo()));
            mdesRegDevJson.put("paymentAppId", registerParam.getPaymentAppId());
            mdesRegDevJson.put("paymentAppInstanceId", paymentAppInstanceId);
            mdesRegDevJson.put("publicKeyFingerprint", registerParam.getPublicKeyFingerprint());

            try {
                // Encrypt RGK
                ByteArray publicKey = ByteArray.of(getPublicKey());
                CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
                byte[] encRgk = cryptoService.buildRgkForRegistrationRequest(publicKey).getBytes();

                // Encrypting Mobile PIN
                ByteArray mobPin = ByteArray.of(registerParam.getMobilePin().getBytes());
                byte[] encMobPin = cryptoService.encryptPinBlockUsingRgk(mobPin, paymentAppInstanceId).getBytes();
                mdesRegDevJson.put("mobilePin", ArrayUtil.getHexString(encMobPin));
                mdesRegDevJson.put("rgk", ArrayUtil.getHexString(encRgk));
                mdesRegDevJson.put("deviceFingerprint", ArrayUtil.getHexString(deviceFingerprint));
            } catch (McbpCryptoException e) {
                return null;
            }

            regDevParam.put("mdes", mdesRegDevJson);
            regDevParam.put("vts", vtsEnrollDeviceReqJson);
            regDevParam.put("clientDeviceID", Miscellaneous.getUniqueClientDeviceId(registerParam.getDeviceInfo().getImei()));
            return regDevParam;
        } catch (JSONException e) {
        }
        return null;
    }

    private byte[] getPublicKey() {
        String modulus = "00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5";
        String pubKeyExp = "010001";
        String prKeyExp = "00395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11";
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)), new BigInteger(ArrayUtil.getByteArray(pubKeyExp)));

            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
            return publicKey.getEncoded();
        } catch (Exception e) {
        }
        return null;

    }

    /**
     * Prepares device information in JSON format for MDES.
     *
     * @param deviceInfo DeviceInfo object
     * @return Device information in JSON
     */
    private JSONObject getDeviceInfoInJson(DeviceInfo deviceInfo) {
        JSONObject jsDeviceInfo = new JSONObject();
        try {
            jsDeviceInfo.put("deviceName", deviceInfo.getDeviceName());
            jsDeviceInfo.put("formFactor", "PHONE");
            jsDeviceInfo.put("id", deviceInfo.getDeviceId());
            jsDeviceInfo.put("imei", deviceInfo.getImei());
            jsDeviceInfo.put("msisdn", deviceInfo.getMsisdn());
            jsDeviceInfo.put("nfcCapable", deviceInfo.getNfcCapable());
            jsDeviceInfo.put("osName", deviceInfo.getOsName());
            jsDeviceInfo.put("osVersion", deviceInfo.getOsVersion());
            jsDeviceInfo.put("serialNumber", deviceInfo.getSerialNumber());
            jsDeviceInfo.put("storageTechnology", "DEVICE_MEMORY");
        } catch (JSONException e) {
            return null;
        }
        return jsDeviceInfo;
    }

    /**
     * Prepares Enroll Device request for VTS
     *
     * @param deviceName A readable name for the device. Ideally, this name would be entered by the consumer.
     *                   It can be used to identify the device in Customer Support calls.
     * @return Enroll Device request in JSON for VTS
     */
    private JSONObject enrollDeviceVtsReqJSon(String deviceName) {
        com.visa.cbp.external.common.DeviceInfo deviceInfoVts = visaPaymentSDK.getDeviceInfo("");
        deviceInfoVts.setDeviceName(deviceName);

        JSONObject deviceInfoVtsJson = new JSONObject();
        try {
            deviceInfoVtsJson.put("osType", deviceInfoVts.getOsType()); // Required
            deviceInfoVtsJson.put("deviceType", deviceInfoVts.getDeviceType()); // required
            deviceInfoVtsJson.put("deviceName", deviceInfoVts.getDeviceName()); // Required
            deviceInfoVtsJson.put("osVersion", deviceInfoVts.getOsVersion());
            deviceInfoVtsJson.put("osBuildID", deviceInfoVts.getOsBuildID());
            deviceInfoVtsJson.put("deviceIDType", deviceInfoVts.getDeviceIDType());
            deviceInfoVtsJson.put("deviceManufacturer", deviceInfoVts.getDeviceManufacturer());
            deviceInfoVtsJson.put("deviceBrand", deviceInfoVts.getDeviceBrand());
            deviceInfoVtsJson.put("deviceModel", deviceInfoVts.getDeviceModel());

            //deviceInfoVtsJson.put("hostDeviceID", deviceInfoVts.getHostDeviceID());
            //deviceInfoVtsJson.put("phoneNumber", deviceInfoVts.getPhoneNumber());
            return deviceInfoVtsJson;
        } catch (JSONException e) {
        }
        return null;
    }

    private void initializeMdes(final JSONObject mdesResponse, RegisterParam registerParam) throws JSONException,
            InvalidInput, McbpCryptoException, GcmRegistrationFailed {
        final String paymentAppInstanceId = registerParam.getPaymentAppInstanceId();
        String mobileKeySetId = mdesResponse.getString("mobileKeysetId");
        String remoteManagementUrl = mdesResponse.getString("remoteManagementUrl");
        JSONObject mobileKeys = mdesResponse.getJSONObject("mobKeys");
        String transportKey = mobileKeys.getString("transportKey");
        String macKey = mobileKeys.getString("macKey");
        String dataEncryptionKey = mobileKeys.getString("dataEncryptionKey");

        // Fetch Payment App Provider Id
        PropertyReader propertyReader = PropertyReader.getInstance(null);
        String paymentAppProviderId = propertyReader.getProperty(PropertyConst.KEY_PAYMENT_APP_PROVIDER_ID);

        McbpApi.initialize(paymentAppInstanceId, paymentAppProviderId, ByteArray.of(deviceFingerprint)); // DeviceFingerprint
        MpaManagementApi.register(mobileKeySetId, transportKey, macKey, dataEncryptionKey, remoteManagementUrl);
    }

    private byte[] getDeviceFingerprint(JSONObject deviceInfo) {
        byte[] baDeviceInfo = deviceInfo.toString().getBytes();
        byte[] deviceFingerprint = null;
        try {
            deviceFingerprint = MessageDigestUtil.getMessageDigest(baDeviceInfo, MessageDigestUtil.Algorithm.SHA_256);
        } catch (NoSuchAlgorithmException e) {
        }
        return deviceFingerprint;
    }

    private void initializeComvivaSdk(ComvivaSdkInitData comvivaSdkInitData, String rnsRegistrationId) {
        // If anyone of MDES/VTS initialized successfully, keep comviva sdk initialization state true
        if (comvivaSdkInitData.isMdesInitState() || comvivaSdkInitData.isVtsInitState()) {
            comvivaSdkInitData.setInitState(true);
        } else {
            return;
        }
        RnsInfo rnsInfo = new RnsInfo();
        if (rnsRegistrationId != null && rnsRegistrationId.equalsIgnoreCase("")) {
            rnsInfo.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
        } else {
            rnsInfo.setRegistrationId(rnsRegistrationId);
        }
        rnsInfo.setRnsType(RnsInfo.RNS_TYPE.FCM);
        comvivaSdkInitData.setRnsInfo(rnsInfo);
        ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
        comvivaHce.initializeSdk(comvivaSdkInitData);
    }

    private EncDevicePersoData createEncDevicePersonalizationData(JSONObject jsonObject) {
        EncDevicePersoData encDevicePersoData = new EncDevicePersoData();
        encDevicePersoData.setDeviceId("");
        encDevicePersoData.setEncCert("");
        encDevicePersoData.setEncExpo("");
        encDevicePersoData.setEncryptedDPM("");
        encDevicePersoData.setSignCert("");
        encDevicePersoData.setSignExpo("");
        encDevicePersoData.setWalletAccountId("");
        return encDevicePersoData;
    }

    public Registration() {
        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
    }

    /**
     * Register a new user with payment App Server.
     *
     * @param userId          User Id
     * @param imei            IMEI of the device being used
     * @param osName          OS Name of device e.g. Android
     * @param deviceModel     Device model name
     * @param regUserListener UI listener for this api
     */
    public void registerUser(final String userId, final String imei,
                             final String osName, final String deviceModel,
                             final RegisterUserListener regUserListener) {
        final HttpUtil httpUtil = HttpUtil.getInstance();
        final RegisterUserResponse regResp = new RegisterUserResponse();
        regUserListener.setRegisterUserResponse(regResp);
        regResp.setResponseCode(-1);
        final JSONObject registerUser = new JSONObject();
        try {
            registerUser.put("userId", userId);
            registerUser.put("clientDeviceID", Miscellaneous.getUniqueClientDeviceId(imei));
            registerUser.put("imei", imei);
            registerUser.put("os_name", osName);
            registerUser.put("device_model", deviceModel);
        } catch (JSONException e) {
        }

        class RegisterUserTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                if (regUserListener != null) {
                    regUserListener.onRegistrationStarted();
                }
                super.onPreExecute();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                return httpUtil.postRequest(UrlUtil.getRegisterUserUrl(), registerUser.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                onProgressUpdate();

                if (null == httpResponse) {
                    regResp.setResponseMessage("Unable to connect to server");
                    regUserListener.onError();
                    return;
                }

                String message = httpResponse.getReqStatus();
                int respCode = httpResponse.getStatusCode();
                regResp.setResponseCode(respCode);
                regResp.setResponseMessage(message);
                if (respCode == 200) {
                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        respCode = respObj.getInt("responseCode");
                        regResp.setResponseCode(respCode);
                        regResp.setResponseMessage(respObj.getString("message"));

                        // If success then retrieve activation code
                        if (respCode == 200) {
                            if (respObj.has("userDetails")) {
                                JSONObject userDetails = respObj.getJSONObject("userDetails");

                                if (userDetails.has("activationCode")) {
                                    regResp.setActivationCode(userDetails.getString("activationCode"));
                                }
                                regUserListener.onRegistrationCompeted();
                            }
                        } else {
                            if (regUserListener != null) {
                                regUserListener.onError();
                            }
                        }
                    } catch (JSONException e) {
                        regResp.setResponseMessage("Wrong data from server");
                        if (regUserListener != null) {
                            regUserListener.onError();
                        }
                    }
                } else {
                    if (regUserListener != null) {
                        regUserListener.onError();
                    }
                }
            }
        }
        RegisterUserTask registerUserTask = new RegisterUserTask();
        try {
            registerUserTask.execute();
        } catch (Exception e) {
            regResp.setResponseMessage("Some error occurred");
            if (regUserListener != null) {
                regUserListener.onError();
            }
        }
    }

    /**
     * Activates an already registered user.
     *
     * @param userId               User ID to be activated
     * @param activationCode       Activation Code received by user
     * @param imei                 IMEI of the device
     * @param activateUserListener UI listener for Activate User
     */
    public void activateUser(final String userId, final String activationCode, final String imei, final ActivateUserListener activateUserListener) {
        final HttpUtil httpUtil = HttpUtil.getInstance();
        final RestResponse restResp = new RestResponse();
        restResp.setResponseCode(-1);

        final JSONObject activateUserReq = new JSONObject();
        try {
            activateUserReq.put("userId", userId);
            activateUserReq.put("activationCode", activationCode);
            activateUserReq.put("clientDeviceID", Miscellaneous.getUniqueClientDeviceId(imei));
        } catch (JSONException e) {
        }

        // Activate User
        class ActivateUserTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                if (activateUserListener != null) {
                    activateUserListener.onActivationStarted();
                }
                super.onPreExecute();
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                return httpUtil.postRequest(UrlUtil.getActivateUserUrl(), activateUserReq.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                onProgressUpdate();

                if (null == httpResponse) {
                    activateUserListener.onError("Unable to connect to server");
                    return;
                }

                String message = httpResponse.getReqStatus();
                int respCode = httpResponse.getStatusCode();
                restResp.setResponseCode(respCode);
                restResp.setResponseMessage(message);

                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        restResp.setResponseMessage(respObj.getString("message"));
                        respCode = respObj.getInt("responseCode");
                        if (respCode != 200) {
                            activateUserListener.onError("Error from Server");
                            return;
                        }
                        activateUserListener.onActivationCompeted();
                    } catch (JSONException e) {
                        activateUserListener.onError("Wrong data from server");
                    }
                } else {
                    activateUserListener.onError("Server Error");
                }
            }
        }
        ActivateUserTask activateUserTask = new ActivateUserTask();
        try {
            activateUserTask.execute();
        } catch (Exception e) {
            if (activateUserListener != null) {
                activateUserListener.onError("Some error occurred");
            }
        }
    }

    /**
     * Register device of the user.
     *
     * @param registerParam        Registration parameters
     * @param activateUserListener UI listener for Activate User
     */
    public void registerDevice(final RegisterParam registerParam, final ActivateUserListener activateUserListener) {
        // Calculate Device Fingerprint
        deviceFingerprint = getDeviceFingerprint(getDeviceInfoInJson(registerParam.getDeviceInfo()));

        // Register Device
        final JSONObject regDeviceJson = getRegisterDeviceRequest(registerParam);

        // Activate User
        class RegisterDeviceTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (activateUserListener != null) {
                    activateUserListener.onActivationStarted();
                }
            }

            @Override
            protected HttpResponse doInBackground(Void... params) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                return httpUtil.postRequest(UrlUtil.getRegisterDeviceUrl(), regDeviceJson.toString());
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);
                if (activateUserListener != null) {
                    activateUserListener.onActivationCompeted();
                }
                if (null == httpResponse) {
                    activateUserListener.onError("Server Not Responding");
                }

                // TODO for testing only, Remove after server api development completion
                /*try {
                    httpResponse.setStatusCode(200);
                    JSONObject mockResp = new JSONObject(strMockRespp);
                    httpResponse.setResponse(mockResp.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                if (httpResponse.getStatusCode() == 200) {
                    ComvivaSdkInitData comvivaSdkInitData = new ComvivaSdkInitData();
                    comvivaSdkInitData.setMdesInitState(false);
                    comvivaSdkInitData.setVtsInitState(false);
                    comvivaSdkInitData.setInitState(false);

                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());

                        // MDES Initialization
                        if (respObj.has("mdesFinalCode")) {
                            String mdesRespCode = respObj.getString("mdesFinalCode");
                            if (mdesRespCode.equalsIgnoreCase("200")) {
                                JSONObject mdesResponse = respObj.getJSONObject("mdes");
                                initializeMdes(mdesResponse, registerParam);
                                comvivaSdkInitData.setMdesInitState(true);
                            }
                        }

                        // VTS Initialization
                        if (respObj.has("visaFinalMessage")) {
                            String vtsRespCode = respObj.getString("visaFinalMessage");
                            if (vtsRespCode.equalsIgnoreCase("200")) {
                                JSONObject encDevicePersonalizationData = new JSONObject();
                                visaPaymentSDK.onBoardDevicePerso(createEncDevicePersonalizationData(encDevicePersonalizationData));
                            }
                        }
                    } catch (JSONException | InvalidInput | McbpCryptoException e) {
                        activateUserListener.onError("Wrong data from server");
                    } catch (GcmRegistrationFailed e) {
                        // Do nothing as we are supporting FCM
                    } catch (Exception e) {
                        activateUserListener.onError("Some error occurred");
                    } finally {
                        // Initialize Comviva SDK with common data for all schemes
                        initializeComvivaSdk(comvivaSdkInitData, registerParam.getFcmRegistrationId());
                    }
                } else {
                    activateUserListener.onError("Error from server");
                }
            }
        }
        RegisterDeviceTask registerDeviceTask = new RegisterDeviceTask();
        registerDeviceTask.execute();
    }

    // TODO Mocked request and responses. Remove after development completion
   /* public static final String strMockRespp = "{\"mdesResponseCode\":\"200\"," +
            "\"mdesMessage\":\"Device Registered Successfully\",\n" +
            "\"vtsResponseCode\":\"200\",\n" +
            "\"vtsMessage\":\"Device Registered Successfully\",\n" +
            "\"mdes\":{\n" +
            "\"mobileKeysetId\": \"CB61605BEB9807E40491AB314FC720013F42F797A83F7BFE8400015A183C02DC\",\n" +
            "\"remoteManagementUrl\": \"http://localhost:9099/cms-dedicated/api/device/register\",\n" +
            "\"responseHost\": \"site1.cmsd.com\",\n" +
            "\"mobKeys\": {\n" +
            "\"transportKey\": \"18B873E90B4105ABF0F0969A99986CD3\",\n" +
            "\"macKey\": \"7FB841365727183B1E4663F090B0AFAA\",\n" +
            "\"dataEncryptionKey\": \"6B0D7E6B848D94B6B491006D7F26167A\"\n" +
            "}\n" +
            "},\n" +
            "\"vts\":{\n" +
            "\"deviceInitParams\":\"\",\n" +
            "\"clientDeviceID\":\"Abcdef26823823892893baced\",\n" +
            "\"vClientID\":\"adfysifsajdfhis78adsfhsi7\",\n" +
            "\"encDevicePersoData\":{\n" +
            "\"deviceId\":\"kjadhsdksakhfkhsiu88998dfksjakdfhk\",\n" +
            "\"encCert\":\"asjdfksakfkskfj77979\",\n" +
            "\"encExpo\":\"ashfhskf8s9989shkdjfksy9f8sdjjsd\",\n" +
            "\"encryptedDPM\":\"sdfskjdf879899sdjfksjdfkj\",\n" +
            "\"signCert\":\"shkskd89890s9djfksd9s9sdo\",\n" +
            "\"signExpo\":\"skkdhfkhskdfhksd87978987dfhs\",\n" +
            "\"walletAccountId\":\"abhyirweo78898jldsjlfjdosui\"\n" +
            "}\n" +
            "}\n" +
            "}";*/

}
