package com.comviva.hceservice.register;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.DeviceType;
import com.comviva.hceservice.common.SchemeType;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.HttpResponse;
import com.comviva.hceservice.util.HttpUtil;
import com.comviva.hceservice.util.Miscellaneous;
import com.comviva.hceservice.util.UrlUtil;
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
import com.visa.cbp.sdk.facade.exception.VisaPaymentSDKException;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Date;

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
    private String clientWalletAccountId;
    private String clientDeviceId;

    private static Registration instance;

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

    private JSONObject getRegisterDeviceRequest(RegisterParam registerParam) throws SdkException {
        try {
            paymentAppInstanceId = generatePaymentAppInstanceId();
            fcmRegistrationToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("fcmRegistrationToken = ",fcmRegistrationToken);

            if (fcmRegistrationToken == null) {
                Log.d(Constants.LOGGER_TAG_SDK_ERROR, "FCM registration Token is not found");
                throw new SdkException(SdkErrorStandardImpl.SDK_RNS_REG_EXCEPTION);
            }

            JSONObject regDevParam = new JSONObject();

            // Common Register Parameters
            regDevParam.put("userId", registerParam.getUserId());
            regDevParam.put("gcmRegistrationId", fcmRegistrationToken);

            JSONObject jsDeviceInfo = getDeviceInfoInJson();
            regDevParam.put("clientDeviceID", clientDeviceId);

            // VTS
            if (registerParam.getSchemeType().equals(SchemeType.ALL) || registerParam.getSchemeType().equals(SchemeType.VISA)) {
                // VTS Registration Parameters
                JSONObject vtsEnrollDeviceReqJson = new JSONObject();
                JSONObject vtsDeviceInfo = enrollDeviceVtsReqJSon(registerParam.getDeviceName());
                vtsEnrollDeviceReqJson.put("deviceInfo", vtsDeviceInfo);
                regDevParam.put("vts", vtsEnrollDeviceReqJson);
            }

            // MDES
            if (registerParam.getSchemeType().equals(SchemeType.ALL) || registerParam.getSchemeType().equals(SchemeType.MASTERCARD)) {
                // MDES Registration Parameters
                JSONObject mdesRegDevJson = new JSONObject();
                mdesRegDevJson.put("deviceInfo", jsDeviceInfo);
                mdesRegDevJson.put("paymentAppId", registerParam.getPaymentAppId());
                mdesRegDevJson.put("paymentAppInstanceId", paymentAppInstanceId);
                mdesRegDevJson.put("publicKeyFingerprint", registerParam.getPublicKeyFingerprint());

                byte[] encRgk = null;
                byte[] encMobPin = null;
                ByteArray publicKey = null;
                try {
                    // Encrypt RGK
                    publicKey = ByteArray.of(getPublicKey());
                    CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
                    encRgk = cryptoService.buildRgkForRegistrationRequest(publicKey).getBytes();
                    String strEncRgk = ArrayUtil.getHexString(encRgk);
                    Log.d("Encrypted RGK(PIN)", strEncRgk);

                    // Encrypting Mobile PIN
                    ByteArray mobPin = ByteArray.of(registerParam.getMobilePin().getBytes());
                    encMobPin = cryptoService.encryptPinBlockUsingRgk(mobPin, paymentAppInstanceId).getBytes();
                    String strEncMobPin = ArrayUtil.getHexString(encMobPin);
                    mdesRegDevJson.put("mobilePin", strEncMobPin);
                    mdesRegDevJson.put("rgk", strEncRgk);
                    mdesRegDevJson.put("deviceFingerprint", ArrayUtil.getHexString(deviceFingerprint));
                    Log.d("Encrypted RGK(PIN)", strEncMobPin);
                } catch (McbpCryptoException e) {
                    Log.d(Constants.LOGGER_TAG_SDK_ERROR, "Crypto Exception while encrypting Mobile PIN " + e.getMessage());
                    throw new SdkException(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
                } finally {
                    // RGK & Encrypted PIN
                    if (encRgk != null) {
                        Arrays.fill(encRgk, Constants.DEFAULT_FILL_VALUE);
                    }
                    if (encMobPin != null) {
                        Arrays.fill(encMobPin, Constants.DEFAULT_FILL_VALUE);
                    }
                    if (publicKey != null) {
                        publicKey.clear();
                    }
                }
                regDevParam.put("mdes", mdesRegDevJson);
            }
            return regDevParam;
        } catch (JSONException e) {
            Log.d(Constants.LOGGER_TAG_SDK_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        }
    }

    private byte[] getPublicKey() throws SdkException {
        String modulus = "00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5";
        String pubKeyExp = "010001";
        String prKeyExp = "00395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11";
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)), new BigInteger(ArrayUtil.getByteArray(pubKeyExp)));

            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
            return publicKey.getEncoded();
        } catch (Exception e) {
            throw new SdkException(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
        }
    }

    /**
     * Prepares device information in JSON format for MDES.
     *
     * @return Device information in JSON
     */
    private JSONObject getDeviceInfoInJson() throws SdkException {
        JSONObject jsDeviceInfo = new JSONObject();
        try {
            Context context = ComvivaSdk.getInstance(null).getApplicationContext();
            final String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (imei == null) {
                imei = mTelephonyMgr.getDeviceId();
            }

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
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
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
    private JSONObject enrollDeviceVtsReqJSon(String deviceName) throws SdkException {
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
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
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

    private byte[] getDeviceFingerprint(JSONObject deviceInfo) throws SdkException {
        byte[] baDeviceInfo = deviceInfo.toString().getBytes();
        byte[] deviceFingerprint;
        try {
            deviceFingerprint = MessageDigestUtil.getMessageDigest(baDeviceInfo, MessageDigestUtil.Algorithm.SHA_256);
        } catch (NoSuchAlgorithmException e) {
            Log.d(Constants.LOGGER_TAG_SDK_ERROR, "NoSuchAlgorithmException while calculating device fingerprint");
            throw new SdkException(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
        }
        return deviceFingerprint;
    }

    private void initializeComvivaSdk(ComvivaSdkInitData comvivaSdkInitData, String rnsRegistrationId) throws SdkException {
        // If anyone of MDES/VTS initialized successfully, keep comviva sdk initialization state true
        if (comvivaSdkInitData.isMdesInitialized() || comvivaSdkInitData.isVtsInitialized()) {
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

    private void initializeSdkForMdes(final JSONObject jsRegDevResp, final ComvivaSdkInitData comvivaSdkInitData) throws SdkException {
        try {
            if (jsRegDevResp.has("mdesFinalCode")) {
                String mdesRespCode = jsRegDevResp.getString("mdesFinalCode");
                if (mdesRespCode.equalsIgnoreCase("200")) {
                    JSONObject mdesResponse = jsRegDevResp.getJSONObject("mdes");
                    initializeMdes(mdesResponse);
                    comvivaSdkInitData.setMdesInitState(true);
                }
            }
        } catch (JSONException e) {
            Log.d(Constants.LOGGER_TAG_SERVER_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (InvalidInput e) {
            Log.d(Constants.LOGGER_TAG_SERVER_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.SERVER_INVALID_VALUE);
        } catch (McbpCryptoException e) {
            Log.d(Constants.LOGGER_TAG_SERVER_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.COMMON_CRYPTO_ERROR);
        } catch (GcmRegistrationFailed e) {
            Log.d(Constants.LOGGER_TAG_SDK_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.SDK_RNS_REG_EXCEPTION);
        }
    }

    private void initializeSdkForVts(final JSONObject jsRegDevResp,
                                     final ComvivaSdkInitData comvivaSdkInitData) throws SdkException {
        try {
            if (jsRegDevResp.has("visaFinalMessage")) {
                String vtsRespCode = jsRegDevResp.getString("visaFinalCode");
                if (vtsRespCode.equalsIgnoreCase("200")) {
                    JSONObject jsVts = new JSONObject(jsRegDevResp.getString("vts"));
                    JSONObject encDevicePersonalizationData = jsVts.getJSONObject("encDevicePersoData");
                    visaPaymentSDK.onBoardDevicePerso(createEncDevicePersonalizationData(encDevicePersonalizationData));
                    comvivaSdkInitData.setVtsInitState(true);
                }
            }
        } catch (JSONException e) {
            Log.d(Constants.LOGGER_TAG_SERVER_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
        } catch (VisaPaymentSDKException e) {
            Log.d(Constants.LOGGER_TAG_SDK_ERROR, e.getMessage());
            throw new SdkException(e.getCbpError().getErrorCode(), e.getCbpError().getErrorMessage());
        }
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
            clientDeviceId = Miscellaneous.getUniqueClientDeviceId(this.imei);
            registerUser.put("userId", this.userId);
            registerUser.put("clientDeviceID", clientDeviceId);
            registerUser.put("imei", this.imei);
            registerUser.put("os_name", "ANDROID");
            registerUser.put("device_model", Build.MODEL);
        } catch (JSONException e) {
            regUserListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
        } catch (SdkException e) {
            regUserListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
        }

        class RegisterUserTask extends AsyncTask<Void, Void, HttpResponse> {
            @Override
            protected void onPreExecute() {
                if (regUserListener != null) {
                    regUserListener.onStarted();
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
                    regUserListener.onError(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR);
                    return;
                }

                int respCode = httpResponse.getStatusCode();
                if (respCode == 200) {
                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        respCode = respObj.getInt("responseCode");

                        // If success then retrieve activation code
                        if (respCode == 200 || respCode == 501) {
                            clientWalletAccountId = respObj.getString(Tags.CLIENT_WALLET_ACCOUNT_ID.getTag());

                            regUserListener.onRegistrationCompeted();
                        } else {
                            if (regUserListener != null) {
                                regUserListener.onError(SdkErrorImpl.getInstance(respCode, respObj.getString("message")));
                            }
                        }
                    } catch (JSONException e) {
                        if (regUserListener != null) {
                            regUserListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                        }
                    }
                } else {
                    if (regUserListener != null) {
                        regUserListener.onError(SdkErrorImpl.getInstance(respCode, httpResponse.getReqStatus()));
                    }
                }
            }
        }
        RegisterUserTask registerUserTask = new RegisterUserTask();
        try {
            registerUserTask.execute();
        } catch (Exception e) {
            if (regUserListener != null) {
                regUserListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
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
            registrationListener.onError(SdkErrorStandardImpl.SDK_INVALID_USER);
            return;
        }

        try {
            activateUserReq.put("userId", userId);
            activateUserReq.put("activationCode", activationCode);
            activateUserReq.put("clientDeviceID", clientDeviceId);
        } catch (JSONException e) {
            registrationListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
            return;
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
                    registrationListener.onError(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR);
                    return;
                }

                if (httpResponse.getStatusCode() == 200) {
                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        int respCode = respObj.getInt("responseCode");
                        if (respCode != 200) {
                            registrationListener.onError(SdkErrorImpl.getInstance(respCode, respObj.getString("message")));
                            return;
                        }
                        registrationListener.onCompleted();
                    } catch (JSONException e) {
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    }
                } else {
                    registrationListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                }
            }
        }
        ActivateUserTask activateUserTask = new ActivateUserTask();
        try {
            activateUserTask.execute();
        } catch (Exception e) {
            if (registrationListener != null) {
                registrationListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
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
        final SchemeType schemeType = registerParam.getSchemeType();

        Log.d("Register Device Request", "UserId:" + registerParam.getUserId() + "\n"
                + "Scheme:" + registerParam.getSchemeType() + "\n"
                + "Device Name:" + registerParam.getDeviceName() + "\n"
                + "PaymentAppId:" + registerParam.getPaymentAppId() + "\n"
                + "Public Key Fingerprint:" + registerParam.getPublicKeyFingerprint() + "\n");

        // If scheme type is not provided then set all scheme type
        if (schemeType == null) {
            registerParam.setSchemeType(SchemeType.ALL);
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
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_NOT_RESPONDING);
                        return;
                    }

                    if (httpResponse.getStatusCode() != 200) {
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR);
                        return;
                    }
                    try {
                        if (httpResponse.getStatusCode() == 200) {
                            JSONObject respObj = new JSONObject(httpResponse.getResponse());
                            int respCode = respObj.getInt("responseCode");
                            if (respCode != 200) {
                                registrationListener.onError(SdkErrorImpl.getInstance(respCode, respObj.getString("message")));
                                return;
                            }
                        }
                    }catch (Exception e)
                    {
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR);
                        return;
                    }

                    ComvivaSdkInitData comvivaSdkInitData = new ComvivaSdkInitData();
                    comvivaSdkInitData.setMdesInitState(false);
                    comvivaSdkInitData.setVtsInitState(false);
                    comvivaSdkInitData.setInitState(false);
                    comvivaSdkInitData.setClientWalletAccountId(clientWalletAccountId);

                    try {
                        JSONObject respObj = new JSONObject(httpResponse.getResponse());
                        SdkError sdkError = SdkErrorStandardImpl.SDK_INTERNAL_ERROR;

                        boolean isHvtSupported = respObj.getBoolean(Tags.HVT_SUPPPORT.getTag());

                        // Fetch these values from server response, when implemented on paymentAppServer
                        comvivaSdkInitData.setHvtSupport(isHvtSupported);
                        if(comvivaSdkInitData.isHvtSupport()) {
                            double hvtLimit = respObj.getDouble(Tags.HVT_LIMIT.getTag());
                            comvivaSdkInitData.setHvtLimit(hvtLimit);
                        }
                        comvivaSdkInitData.setReplenishmentThresold(2);

                        // MDES Initialization
                        if (schemeType == null || schemeType == SchemeType.ALL || schemeType == SchemeType.MASTERCARD) {
                            try {
                                initializeSdkForMdes(respObj, comvivaSdkInitData);
                            } catch (SdkException e) {
                                sdkError = SdkErrorStandardImpl.getError(e.getErrorCode());
                            }
                        }

                        // VTS Initialization
                        if (schemeType == null || schemeType == SchemeType.ALL || schemeType == SchemeType.VISA) {
                            try {
                                initializeSdkForVts(respObj, comvivaSdkInitData);
                            } catch (SdkException e) {
                                sdkError = SdkErrorStandardImpl.getError(e.getErrorCode());
                            }
                        }

                        if (comvivaSdkInitData.isMdesInitialized() || comvivaSdkInitData.isVtsInitialized()) {
                            // Initialize Comviva SDK with common data for all schemes
                            initializeComvivaSdk(comvivaSdkInitData, fcmRegistrationToken);
                            registrationListener.onCompleted();
                        } else {
                            registrationListener.onError(sdkError);
                            return;
                        }
                    } catch (JSONException e) {
                        Log.d(Constants.LOGGER_TAG_SERVER_ERROR, e.getMessage());
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
                    } catch (SdkException e) {
                        registrationListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
                    }
                }
            }
            RegisterDeviceTask registerDeviceTask = new RegisterDeviceTask();
            registerDeviceTask.execute();
        } catch (SdkException e) {
            Log.d(Constants.LOGGER_TAG_SDK_ERROR, e.getMessage());
            registrationListener.onError(SdkErrorStandardImpl.getError(e.getErrorCode()));
        }
    }


    /**
     * Un-Register device of the user.
     *
     * @param imei   IMEI of the user device
     * @param userID  user ID of the user.
     * @param registrationListener UI listener for Activate User
     */
    public void unRegisterDevice(final String imei, final String userID, final  RegistrationListener registrationListener) {
        // Un-Register Device
        try {
            final JSONObject unRegisterDevice = new JSONObject();
            unRegisterDevice.put("imei", imei);
            unRegisterDevice.put("userId",userID);
            // Activate User
            class UnRegisterDeviceTask extends AsyncTask<Void, Void, HttpResponse> {
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
                    return httpUtil.postRequest(UrlUtil.getUnRegisterDeviceUrl(), unRegisterDevice.toString());
                }

                @Override
                protected void onPostExecute(HttpResponse httpResponse) {
                    super.onPostExecute(httpResponse);

                    if (null == httpResponse) {
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_NOT_RESPONDING);
                        return;
                    }

                    if (httpResponse.getStatusCode() != 200) {
                        registrationListener.onError(SdkErrorStandardImpl.SERVER_INTERNAL_ERROR);
                        return;
                    }
                    try {
                        if (httpResponse.getStatusCode() == 200) {
                            JSONObject jsEnrollPanResp = new JSONObject(httpResponse.getResponse());
                            try {
                                if (jsEnrollPanResp.getInt(Tags.RESPONSE_CODE.getTag()) != 200) {
                                    registrationListener.onError(SdkErrorImpl.getInstance(jsEnrollPanResp.getInt(Tags.RESPONSE_CODE.getTag()),
                                            jsEnrollPanResp.getString(Tags.MESSAGE.getTag())));
                                }else
                                {
                                    ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                                    comvivaSdk.resetDevice();
                                    registrationListener.onCompleted();
                                }
                            } catch (Exception e) {
                                registrationListener.onError(SdkErrorImpl.getInstance(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION.getErrorCode(), jsEnrollPanResp.getString(Tags.MESSAGE.getTag())));
                            }
                        } else {
                            if (registrationListener != null) {
                                registrationListener.onError(SdkErrorImpl.getInstance(httpResponse.getStatusCode(), httpResponse.getReqStatus()));
                            }
                        }
                    }catch (Exception e)
                    {
                        if (registrationListener != null) {
                            registrationListener.onError(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
                        }
                    }
                }
            }

            UnRegisterDeviceTask unRegisterDeviceTask = new UnRegisterDeviceTask();
            unRegisterDeviceTask.execute();
        }catch (JSONException e) {
            e.printStackTrace();
            registrationListener.onError(SdkErrorStandardImpl.SERVER_JSON_EXCEPTION);
        }
    }
}
