package com.comviva.hceservice.register;


import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.DeviceType;
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
 * This class contains all apis related to user registration and device enrollment.
 */
public class Registration {
    private VisaPaymentSDK visaPaymentSDK;
    private byte[] deviceFingerprint;
    private String paymentAppInstanceId;
    private String fcmRegistrationToken;
    private String imei;
    private String userId;

    private static Registration instance;

    private class RegistrationException extends Exception {
        static final int ERR_CODE_FCM = 101;
        static final int ERR_CODE_CRYPTO = 102;
        static final int ERR_CODE_JSON = 103;

        private int errorCode;

        RegistrationException(int errorCode) {
            super();
            this.errorCode = errorCode;
        }

        int getErrorCode() {
            return errorCode;
        }
    }

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

    private boolean isNfcEnabled(Context context) {
        PackageManager pm = context.getPackageManager();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        return pm.hasSystemFeature(PackageManager.FEATURE_NFC) || (null != nfcAdapter);
    }

    private JSONObject getRegisterDeviceRequest(RegisterParam registerParam) throws RegistrationException {
        try {
            paymentAppInstanceId = generatePaymentAppInstanceId();
            fcmRegistrationToken = FirebaseInstanceId.getInstance().getToken();
            if (fcmRegistrationToken == null) {
                throw new RegistrationException(RegistrationException.ERR_CODE_FCM);
            }

            JSONObject regDevParam = new JSONObject();
            // Common Register Parameters
            regDevParam.put("userId", registerParam.getUserId());
            regDevParam.put("gcmRegistrationId", fcmRegistrationToken);

            // VTS Registration Parameters
            JSONObject vtsEnrollDeviceReqJson = new JSONObject();
            JSONObject vtsDeviceInfo = enrollDeviceVtsReqJSon(registerParam.getDeviceName());
            vtsEnrollDeviceReqJson.put("deviceInfo", vtsDeviceInfo);

            // MDES Registration Parameters
            JSONObject mdesRegDevJson = new JSONObject();
            JSONObject jsDeviceInfo = getDeviceInfoInJson();
            mdesRegDevJson.put("deviceInfo", jsDeviceInfo);
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
                throw new RegistrationException(RegistrationException.ERR_CODE_CRYPTO);
            }

            regDevParam.put("mdes", mdesRegDevJson);
            regDevParam.put("vts", vtsEnrollDeviceReqJson);
            regDevParam.put("clientDeviceID", Miscellaneous.getUniqueClientDeviceId(jsDeviceInfo.getString("imei")));
            return regDevParam;
        } catch (JSONException e) {
            throw new RegistrationException(RegistrationException.ERR_CODE_JSON);
        }
    }

    private byte[] getPublicKey() throws RegistrationException {
        String modulus = "00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5";
        String pubKeyExp = "010001";
        String prKeyExp = "00395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11";
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)), new BigInteger(ArrayUtil.getByteArray(pubKeyExp)));

            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
            return publicKey.getEncoded();
        } catch (Exception e) {
            throw new RegistrationException(RegistrationException.ERR_CODE_CRYPTO);
        }
    }

    /**
     * Prepares device information in JSON format for MDES.
     *
     * @return Device information in JSON
     */
    private JSONObject getDeviceInfoInJson() throws RegistrationException {
        JSONObject jsDeviceInfo = new JSONObject();
        try {
            Context context = ComvivaSdk.getInstance(null).getApplicationContext();
            final String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            jsDeviceInfo.put("deviceName", Build.MODEL);
            jsDeviceInfo.put("formFactor", DeviceType.PHONE);
            jsDeviceInfo.put("id", deviceId);
            jsDeviceInfo.put("imei", imei);
            jsDeviceInfo.put("msisdn", mTelephonyMgr.getSubscriberId());
            jsDeviceInfo.put("nfcCapable", (isNfcEnabled(context) ? "true" : "false"));
            jsDeviceInfo.put("osName", "ANDROID");
            jsDeviceInfo.put("osVersion", Build.VERSION.RELEASE);
            jsDeviceInfo.put("serialNumber", Build.FINGERPRINT);
            jsDeviceInfo.put("storageTechnology", "DEVICE_MEMORY");
        } catch (JSONException e) {
            throw new RegistrationException(RegistrationException.ERR_CODE_JSON);
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
    private JSONObject enrollDeviceVtsReqJSon(String deviceName) throws RegistrationException {
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
            throw new RegistrationException(RegistrationException.ERR_CODE_JSON);
        }
    }

    private void initializeMdes(final JSONObject mdesResponse) throws JSONException,
            InvalidInput, McbpCryptoException, GcmRegistrationFailed {
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

    private byte[] getDeviceFingerprint(JSONObject deviceInfo) throws RegistrationException {
        byte[] baDeviceInfo = deviceInfo.toString().getBytes();
        byte[] deviceFingerprint;
        try {
            deviceFingerprint = MessageDigestUtil.getMessageDigest(baDeviceInfo, MessageDigestUtil.Algorithm.SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RegistrationException(RegistrationException.ERR_CODE_CRYPTO);
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
        ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
        comvivaSdk.initializeSdk(comvivaSdkInitData);
    }

    private EncDevicePersoData createEncDevicePersonalizationData(JSONObject jsEncDevicePersoData) {
        EncDevicePersoData encDevicePersoData = new EncDevicePersoData();
        try {
            encDevicePersoData.setDeviceId(jsEncDevicePersoData.getString("deviceId"));
            encDevicePersoData.setEncCert(jsEncDevicePersoData.getString("encCert"));
            encDevicePersoData.setEncExpo(jsEncDevicePersoData.getString("encExpo"));
            encDevicePersoData.setEncryptedDPM(jsEncDevicePersoData.getString("encryptedDPM"));
            encDevicePersoData.setSignCert(jsEncDevicePersoData.getString("signCert"));
            encDevicePersoData.setSignExpo(jsEncDevicePersoData.getString("signExpo"));
            encDevicePersoData.setWalletAccountId(jsEncDevicePersoData.getString("walletAccountId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return encDevicePersoData;
    }

    private Registration() {
        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
    }

    /**
     * Returns Singleton instance of Registration.
     *
     * @return Instance of this class.
     */
    public static Registration getInstance() {
        if (instance == null) {
            instance = new Registration();
        }
        return instance;
    }

    /**
     * Register a new user with payment App Server.
     *
     * @param userId          User Id
     * @param imei            IMEI of the device being used
     * @param regUserListener UI listener for this api
     */
    public void registerUser(final String userId, final String imei, final RegisterUserListener regUserListener) {
        final HttpUtil httpUtil = HttpUtil.getInstance();
        final JSONObject registerUser = new JSONObject();
        this.imei = imei;
        this.userId = userId;
        try {
            registerUser.put("userId", this.userId);
            registerUser.put("clientDeviceID", Miscellaneous.getUniqueClientDeviceId(this.imei));
            registerUser.put("imei", this.imei);
            registerUser.put("os_name", "ANDROID");
            registerUser.put("device_model", Build.MODEL);
        } catch (JSONException e) {
            regUserListener.onError("SDK Error : JSONException");
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
                    regUserListener.onError("Unable to connect to server");
                    return;
                }

                int respCode = httpResponse.getStatusCode();
                if (respCode == 200) {
                    try {
                        RegisterUserResponse regResp = new RegisterUserResponse();
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        respCode = respObj.getInt("responseCode");

                        // If success then retrieve activation code
                        if (respCode == 200) {
                            if (respObj.has("userDetails")) {
                                JSONObject userDetails = respObj.getJSONObject("userDetails");

                                if (userDetails.has("activationCode")) {
                                    regResp.setActivationCode(userDetails.getString("activationCode"));
                                }
                                regUserListener.onRegistrationCompeted(regResp);
                            }
                        } else {
                            if (regUserListener != null) {
                                regUserListener.onError(respObj.getString("message"));
                            }
                        }
                    } catch (JSONException e) {
                        if (regUserListener != null) {
                            regUserListener.onError("Wrong data from server");
                        }
                    }
                } else {
                    if (regUserListener != null) {
                        regUserListener.onError(httpResponse.getReqStatus());
                    }
                }
            }
        }
        RegisterUserTask registerUserTask = new RegisterUserTask();
        try {
            registerUserTask.execute();
        } catch (Exception e) {
            if (regUserListener != null) {
                regUserListener.onError("Some error occurred");
            }
        }
    }

    /**
     * Activates an already registered user.
     *
     * @param userId               User ID to be activated
     * @param activationCode       Activation Code received by user
     * @param imei                 IMEI of the device
     * @param registrationListener UI listener for Activate User
     */
    public void activateUser(final String userId, final String activationCode, final String imei, final RegistrationListener registrationListener) {
        final HttpUtil httpUtil = HttpUtil.getInstance();
        final JSONObject activateUserReq = new JSONObject();

        if (!this.userId.equalsIgnoreCase(userId)) {
            registrationListener.onError("Incorrect UserId");
            return;
        }

        try {
            activateUserReq.put("userId", userId);
            activateUserReq.put("activationCode", activationCode);
            activateUserReq.put("clientDeviceID", Miscellaneous.getUniqueClientDeviceId(imei));
        } catch (JSONException e) {
            registrationListener.onError("SDK Error : JSONException");
        }

        // Activate User
        class ActivateUserTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                if (registrationListener != null) {
                    registrationListener.onStarted();
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
                    registrationListener.onError("Unable to connect to server");
                    return;
                }

                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        int respCode = respObj.getInt("responseCode");
                        if (respCode != 200) {
                            registrationListener.onError(respObj.getString("message"));
                            return;
                        }
                        registrationListener.onCompeted();
                    } catch (JSONException e) {
                        registrationListener.onError("Wrong data from server");
                    }
                } else {
                    registrationListener.onError(httpResponse.getReqStatus());
                }
            }
        }
        ActivateUserTask activateUserTask = new ActivateUserTask();
        try {
            activateUserTask.execute();
        } catch (Exception e) {
            if (registrationListener != null) {
                registrationListener.onError("Some error occurred");
            }
        }
    }

    /**
     * Register device of the user.
     *
     * @param registerParam        Registration parameters
     * @param registrationListener UI listener for Activate User
     */
    public void registerDevice(final RegisterParam registerParam, final RegistrationListener registrationListener) {
        // Validate that UserId is same as provided in register user api.
        if (!this.userId.equalsIgnoreCase(registerParam.getUserId())) {
            registrationListener.onError("Incorrect UserId");
            return;
        }

        // Register Device
        try {
            // Calculate Device Fingerprint
            deviceFingerprint = getDeviceFingerprint(getDeviceInfoInJson());

            final JSONObject regDeviceJson = getRegisterDeviceRequest(registerParam);

            // Activate User
            class RegisterDeviceTask extends AsyncTask<Void, Void, HttpResponse> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (registrationListener != null) {
                        registrationListener.onStarted();
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

                    if (null == httpResponse) {
                        registrationListener.onError("Server Not Responding");
                        return;
                    }

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
                                    initializeMdes(mdesResponse);
                                    comvivaSdkInitData.setMdesInitState(true);
                                }
                            }

                            // VTS Initialization
                            if (respObj.has("visaFinalMessage")) {
                                String vtsRespCode = respObj.getString("visaFinalCode");
                                if (vtsRespCode.equalsIgnoreCase("200")) {
                                    JSONObject jsVts = new JSONObject(respObj.getString("vts"));
                                    JSONObject encDevicePersonalizationData = jsVts.getJSONObject("encDevicePersoData");
                                    visaPaymentSDK.onBoardDevicePerso(createEncDevicePersonalizationData(encDevicePersonalizationData));
                                    comvivaSdkInitData.setVtsInitState(true);
                                }
                            }

                            if (comvivaSdkInitData.isMdesInitState() || comvivaSdkInitData.isVtsInitState()) {
                                registrationListener.onCompeted();
                            } else {
                                registrationListener.onError("Registration Failed");
                            }
                        } catch (JSONException | InvalidInput | McbpCryptoException e) {
                            registrationListener.onError("Wrong data from server");
                        } catch (GcmRegistrationFailed e) {
                            // Do nothing as we are supporting FCM
                        } catch (Exception e) {
                            registrationListener.onError("Some error occurred");
                        } finally {
                            // Initialize Comviva SDK with common data for all schemes
                            initializeComvivaSdk(comvivaSdkInitData, fcmRegistrationToken);
                        }
                    } else {
                        registrationListener.onError("Error from server");
                    }
                }
            }
            RegisterDeviceTask registerDeviceTask = new RegisterDeviceTask();
            registerDeviceTask.execute();
        } catch (RegistrationException e) {
            switch (e.getErrorCode()) {
                case RegistrationException.ERR_CODE_FCM:
                    registrationListener.onError("FCM Registration Token Error");
                    break;

                case RegistrationException.ERR_CODE_CRYPTO:
                    registrationListener.onError("Crypto Error");
                    break;

                case RegistrationException.ERR_CODE_JSON:
                    registrationListener.onError("JSON Error");
                    break;
            }
        }
    }
}
