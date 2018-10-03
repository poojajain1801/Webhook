package com.comviva.hceservice.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.comviva.hceservice.apiCalls.NetworkApi;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.SchemeType;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkErrorImpl;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.ServerResponseListener;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.common.app_properties.PropertyConst;
import com.comviva.hceservice.common.app_properties.PropertyReader;
import com.comviva.hceservice.common.database.ComvivaSdkInitData;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.pojo.RegisterUserResponse;
import com.comviva.hceservice.pojo.UnRegisterDeviceResponse;
import com.comviva.hceservice.pojo.registerdevice.EncryptedDevicePersoData;
import com.comviva.hceservice.pojo.registerdevice.Mdes;
import com.comviva.hceservice.pojo.registerdevice.RegisterDeviceResponse;
import com.comviva.hceservice.requestobjects.RegisterRequestParam;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.listeners.ResponseListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mastercard.mpsdk.componentinterface.crypto.WalletIdentificationDataProvider;
import com.mastercard.mpsdk.componentinterface.crypto.keys.RgkEncryptedData;
import com.mastercard.mpsdk.componentinterface.crypto.keys.RgkEncryptedMobileKeys;
import com.mastercard.mpsdk.componentinterface.crypto.keys.WalletDekEncryptedData;
import com.mastercard.mpsdk.utils.Utils;
import com.visa.cbp.external.common.EncDevicePersoData;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.exception.VisaPaymentSDKException;

import java.security.GeneralSecurityException;

/**
 * This class contains all apis related to user registration and device enrollment.
 */
public class Registration {

    private VisaPaymentSDK visaPaymentSDK;
    private String clientWalletAccountId;
    private SharedPreferences sharedPreferences;
    private static SDKData sdkData;
    private String clientDeviceId;
    private NetworkApi networkApi;
    private SchemeType schemeType;


    private Registration() {

        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        sharedPreferences = sdkData.getContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, Context.MODE_PRIVATE);
        networkApi = new NetworkApi();
    }


    /**
     * Returns Singleton instance of Registration.
     *
     * @return Instance of this class.
     */
    public static Registration getInstance() {
        sdkData = SDKData.getInstance();
        if (null == sdkData.getRegistration()) {
            Registration registration = new Registration();
            sdkData.setRegistration(registration);
        }
        return sdkData.getRegistration();
    }


    /**
     * Register a new user with payment App Server.
     *
     * @param userId           User Id
     * @param imei             IMEI of the device being used
     * @param responseListener UI listener for this api
     */
    public void registerUser(final String userId, final String imei, final ResponseListener responseListener) throws SdkException {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Tags.USER_ID.getTag(), userId);
        editor.putString(Tags.IMEI.getTag(), imei);
        editor.apply();
        sdkData.setImei(imei);
        responseListener.onStarted();
        networkApi.setServerAuthenticateListener(serverResponseListener);
        clientDeviceId = CommonUtil.getUniqueClientDeviceId();
        networkApi.registerUser(userId, clientDeviceId, responseListener);
    }


    /**
     * Register device of the user.
     *
     * @param registerRequestParam Registration parameters
     * @param responseListener     UI listener for Activate User
     */
    public void registerDevice(final RegisterRequestParam registerRequestParam, final ResponseListener responseListener) throws SdkException {

        responseListener.onStarted();
        schemeType = registerRequestParam.getSchemeType();
        networkApi.setServerAuthenticateListener(serverResponseListener);
        networkApi.registerDevice(clientDeviceId, registerRequestParam, responseListener);
    }


    /**
     * This api provides with the current user ID with the Comviva SDK.
     */
    public String getUserId() {

        return sharedPreferences.getString(Tags.USER_ID.getTag(), null);
    }


    /**
     * Un-Register device of the user.
     *
     * @param imei             IMEI of the user device
     * @param userID           user ID of the user.
     * @param responseListener UI listener for Activate User
     */
    public void unRegisterDevice(final String imei, final String userID, final ResponseListener responseListener) throws SdkException {
        // Un-Register Device
        responseListener.onStarted();
        networkApi.setServerAuthenticateListener(serverResponseListener);
        networkApi.UnregisterDevice(imei, userID, responseListener);
    }


    @NonNull
    private RgkEncryptedMobileKeys getRgkEncryptedMobileKeys(final Mdes mdes) {

        return new RgkEncryptedMobileKeys() {
            @Override
            public String getKeySetId() {

                return mdes.getMobileKeysetId();
            }


            @Override
            public RgkEncryptedData
            getEncryptedMacKey() {

                return new RgkEncryptedData(Utils.fromHexStringToByteArray(mdes.getMobileKeys().getMacKey()));
            }


            @Override
            public RgkEncryptedData
            getEncryptedTransportKey() {

                return new RgkEncryptedData(Utils.fromHexStringToByteArray(mdes.getMobileKeys().getTransportKey()));
            }


            @Override
            public RgkEncryptedData
            getEncryptedDek() {

                return new RgkEncryptedData(Utils.fromHexStringToByteArray(mdes.getMobileKeys().getDataEncryptionKey()));
            }
        };
    }


    private void initializeComvivaSdk(ComvivaSdkInitData comvivaSdkInitData, String rnsRegistrationId) throws SdkException {
        // If anyone of MDES/Vts initialized successfully, keep comviva sdk initialization state true
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


    private EncDevicePersoData createEncDevicePersonalizationData(EncryptedDevicePersoData encryptedDevicePersoData) {

        EncDevicePersoData encDevicePersoData = new EncDevicePersoData();
        encDevicePersoData.setDeviceId(encryptedDevicePersoData.getDeviceId());
        encDevicePersoData.setEncCert(encryptedDevicePersoData.getEncCert());
        encDevicePersoData.setEncExpo(encryptedDevicePersoData.getEncExpo());
        encDevicePersoData.setEncryptedDPM(encryptedDevicePersoData.getEncryptedDPM());
        encDevicePersoData.setSignCert(encryptedDevicePersoData.getSignCert());
        encDevicePersoData.setSignExpo(encryptedDevicePersoData.getSignExpo());
        encDevicePersoData.setWalletAccountId(encryptedDevicePersoData.getWalletAccountId());
        return encDevicePersoData;
    }


    private void initializeSdkForMdes(RegisterDeviceResponse registerDeviceResponse, final ComvivaSdkInitData comvivaSdkInitData) throws SdkException {

        try {
            if (null != registerDeviceResponse.getMdes()) {
                String mdesRespCode = registerDeviceResponse.getMdesFinalCode();
                if (Constants.HTTP_RESPONSE_CODE_200.equals(mdesRespCode)) {
                    sdkData.getMcbp().getMpaManagementHelper().setRegistrationResponseData(getRgkEncryptedMobileKeys(registerDeviceResponse.getMdes()), registerDeviceResponse.getMdes().getRemoteManagementUrl());
                    comvivaSdkInitData.setMdesInitState(true);
                }
            }
        } catch (GeneralSecurityException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        } catch (Exception e) {
            Log.d(Constants.LOGGER_TAG_SERVER_ERROR, e.getMessage());
            throw new SdkException(SdkErrorStandardImpl.SERVER_INVALID_VALUE);
        }
    }


    private void initializeSdkForVts(RegisterDeviceResponse registerDeviceResponse, ComvivaSdkInitData comvivaSdkInitData) throws SdkException {

        try {
            if (null != registerDeviceResponse.getVisaFinalCode()) {
                if (Constants.HTTP_RESPONSE_CODE_200.equals(registerDeviceResponse.getVisaFinalCode())) {
                    visaPaymentSDK.onBoardDevicePerso(createEncDevicePersonalizationData(registerDeviceResponse.getVts().getEncDevicePersoData()));
                    comvivaSdkInitData.setVtsInitState(true);
                }
            }
        } catch (VisaPaymentSDKException e) {
            Log.d(Constants.LOGGER_TAG_SDK_ERROR, e.getMessage());
            throw new SdkException(e.getCbpError().getErrorCode(), e.getCbpError().getErrorMessage());
        }
    }


    private ServerResponseListener serverResponseListener = new ServerResponseListener() {
        @Override
        public void onRequestCompleted(Object result, Object listener) {

            ResponseListener responseListener = (ResponseListener) listener;
            try {
                if (result instanceof RegisterUserResponse) {
                    RegisterUserResponse registerUserResponse = (RegisterUserResponse) result;
                    if (Constants.HTTP_RESPONSE_CODE_200.equals(registerUserResponse.getResponseCode()) || Constants.HTTP_RESPONSE_CODE_501.equals(registerUserResponse.getResponseCode())) {
                        clientWalletAccountId = registerUserResponse.getClientWalletAccountId();
                        responseListener.onSuccess();
                    } else {
                        responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(registerUserResponse.getResponseCode()), registerUserResponse.getResponseMessage()));
                    }
                } else if (result instanceof UnRegisterDeviceResponse) {
                    UnRegisterDeviceResponse unRegisterDeviceResponse = (UnRegisterDeviceResponse) result;
                    if (Constants.HTTP_RESPONSE_CODE_200.equals(unRegisterDeviceResponse.getResponseCode())) {
                        ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
                        if (comvivaSdk.resetDevice()) {
                            responseListener.onSuccess();
                        } else {
                            responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
                        }
                    } else {
                        responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(unRegisterDeviceResponse.getResponseCode()), unRegisterDeviceResponse.getResponseMessage()));
                    }
                } else if (result instanceof RegisterDeviceResponse) {
                    RegisterDeviceResponse registerDeviceResponse = (RegisterDeviceResponse) result;
                    SdkError sdkError = SdkErrorStandardImpl.SDK_INTERNAL_ERROR;
                    if (Constants.HTTP_RESPONSE_CODE_200.equals(registerDeviceResponse.getResponseCode())) {
                       /* AppProperties appProperties = new AppProperties();
                        appProperties.setMdesInitState(false);
                        appProperties.setVtsInitState(false);
                        appProperties.setInitState(false);
                        appProperties.setClientWalletAccId(clientWalletAccountId);
                        sdkData.getSdkDatabase().appPropertiesDao().update(appProperties);*/
                        ComvivaSdkInitData comvivaSdkInitData = new ComvivaSdkInitData();
                        comvivaSdkInitData.setMdesInitState(false);
                        comvivaSdkInitData.setVtsInitState(false);
                        comvivaSdkInitData.setInitState(false);
                        comvivaSdkInitData.setClientWalletAccountId(clientWalletAccountId);
                        boolean isHvtSupported = registerDeviceResponse.getIsHvtSupported();
                        comvivaSdkInitData.setHvtSupport(isHvtSupported);
                        if (comvivaSdkInitData.isHvtSupport()) {
                            double hvtLimit = registerDeviceResponse.getHvtThreshold();
                            comvivaSdkInitData.setHvtLimit(hvtLimit);
                        }
                        comvivaSdkInitData.setReplenishmentThresold(2);
                        // MDES Initialization
                        if (schemeType == null || schemeType == SchemeType.ALL || schemeType == SchemeType.MASTERCARD) {
                            try {
                                initializeSdkForMdes(registerDeviceResponse, comvivaSdkInitData);
                            } catch (SdkException e) {
                                sdkError = SdkErrorStandardImpl.getError(e.getErrorCode());
                            }
                        }
                        // Vts Initialization
                        if (schemeType == null || schemeType == SchemeType.ALL || schemeType == SchemeType.VISA) {
                            try {
                                initializeSdkForVts(registerDeviceResponse, comvivaSdkInitData);
                            } catch (SdkException e) {
                                sdkError = SdkErrorStandardImpl.getError(e.getErrorCode());
                            }
                        }
                        if (comvivaSdkInitData.isMdesInitialized() || comvivaSdkInitData.isVtsInitialized()) {
                            // Initialize Comviva SDK with common data for all schemes
                            String fcmRegistrationToken = FirebaseInstanceId.getInstance().getToken();
                            initializeComvivaSdk(comvivaSdkInitData, fcmRegistrationToken);
                            responseListener.onSuccess();
                        } else {
                            responseListener.onError(sdkError);
                        }
                    } else {
                        responseListener.onError(SdkErrorImpl.getInstance(Integer.parseInt(registerDeviceResponse.getResponseCode()), registerDeviceResponse.getMessage()));
                    }
                }
            } catch (Exception e) {
                Log.e(Tags.EXCEPTION_LOG.getTag(), String.valueOf(e));
                handleError(listener);
            }
        }


        @Override
        public void onRequestError(String message, Object listener) {

            handleError(listener);
        }
    };


    private void handleError(Object listener) {

        if (listener instanceof ResponseListener) {
            ResponseListener responseListener = (ResponseListener) listener;
            responseListener.onError(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }
}
