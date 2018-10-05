package com.comviva.hceservice.apiCalls;

import android.os.Build;
import android.util.Log;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.common.SchemeType;
import com.comviva.hceservice.common.SdkErrorStandardImpl;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.Tags;
import com.comviva.hceservice.pojo.checkcardeligibility.CheckCardEligibilityResponse;
import com.comviva.hceservice.pojo.enrollpanVts.EnrollPanResponse;
import com.comviva.hceservice.requestobjects.CardEligibilityRequestParam;
import com.comviva.hceservice.requestobjects.CardLcmRequestParam;
import com.comviva.hceservice.requestobjects.DigitizationRequestParam;
import com.comviva.hceservice.requestobjects.RegisterRequestParam;
import com.comviva.hceservice.util.ArrayUtil;
import com.comviva.hceservice.util.Constants;
import com.comviva.hceservice.util.crypto.AESUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mastercard.mcbp_android.R;
import com.mastercard.mpsdk.componentinterface.remotemanagement.RegistrationRequestParameters;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RequestParamsManager {

    private static SDKData sdkData = SDKData.getInstance();
    private static VisaPaymentSDK visaPaymentSDK;


    public static JSONObject getRegisterUserParams(String userID, String clientDeviceID) throws SdkException {

        try {
            JSONObject registerUserObject = new JSONObject();
            registerUserObject.put(Tags.USER_ID.getTag(), userID);
            registerUserObject.put(Tags.CLIENT_DEVICE_ID.getTag(), clientDeviceID);
            registerUserObject.put(Tags.IMEI.getTag(), sdkData.getImei());
            registerUserObject.put(Tags.OS_NAME.getTag(), Constants.OS_NAME);
            registerUserObject.put(Tags.DEVICE_UNDERSCORE_MODEL.getTag(), Build.MODEL);
            return registerUserObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getRegisterDeviceParams(String clientDeviceId, RegisterRequestParam registerRequestParam) throws SdkException {
        // String deviceFingerprint;
        String fcmRegistrationToken;
        String paymentAppInstanceId;
        JSONObject registerDeviceParams = new JSONObject();
        SchemeType schemeType = registerRequestParam.getSchemeType();
        if (schemeType == null) {
            registerRequestParam.setSchemeType(SchemeType.ALL);
        }
        try {
            paymentAppInstanceId = CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag());
            fcmRegistrationToken = FirebaseInstanceId.getInstance().getToken();
            if (fcmRegistrationToken == null) {
                Log.e(Tags.ERROR_LOG.getTag(), sdkData.getContext().getResources().getString(R.string.fcm_registration_error));
                throw new SdkException(SdkErrorStandardImpl.SDK_RNS_REG_EXCEPTION);
            }
            // Common Register Parameters
            registerDeviceParams.put(Tags.USER_ID.getTag(), registerRequestParam.getUserId());
            registerDeviceParams.put(Tags.GCM_REG_ID.getTag(), fcmRegistrationToken);
            registerDeviceParams.put(Tags.CLIENT_DEVICE_ID.getTag(), clientDeviceId);
            // registerDeviceParams.put(Tags.SCHEME_TYPE.getTag(), registerRequestParam.getSchemeType());
            JSONObject jsDeviceInfo = CommonUtil.getDeviceInfoInJson();
            // Vts Register Parameters
            if (registerRequestParam.getSchemeType().equals(SchemeType.ALL) || registerRequestParam.getSchemeType().equals(SchemeType.VISA)) {
                JSONObject vtsEnrollDeviceReqJson = new JSONObject();
                JSONObject vtsDeviceInfo = enrollDeviceVtsReqJSon(registerRequestParam.getDeviceName());
                vtsEnrollDeviceReqJson.put(Tags.DEVICE_INFO.getTag(), vtsDeviceInfo);
                registerDeviceParams.put(Tags.VTS.getTag(), vtsEnrollDeviceReqJson);
            }
            // MDES Register Parameters
            if (registerRequestParam.getSchemeType().equals(SchemeType.ALL) || registerRequestParam.getSchemeType().equals(SchemeType.MASTERCARD)) {
                JSONObject mdesRegDevJson = new JSONObject();
                mdesRegDevJson.put(Tags.DEVICE_INFO.getTag(), jsDeviceInfo);
                mdesRegDevJson.put(Tags.PAYMENT_APP_ID.getTag(), Constants.PAYMENT_APP_INSTANCE_ID);
                mdesRegDevJson.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), paymentAppInstanceId);
                PublicKey publicKey = CommonUtil.getPublicKeyFromCert(Constants.CMS_D_CERTIFICATE_NAME);
                byte[] bytesFromInputStream = CommonUtil.getBytesFromInputStream(Constants.CMS_D_CERTIFICATE_NAME);
                byte[] publicKeyFingerPrint = CommonUtil.sha256(bytesFromInputStream);
                RegistrationRequestParameters registrationRequestParameters = sdkData.getMcbp().getMpaManagementHelper().getRegistrationRequestData(publicKey.getEncoded(), null);
                mdesRegDevJson.put(Tags.RGK.getTag(), new String(Hex.encodeHex(registrationRequestParameters.getRandomGeneratedKey().getEncryptedData())));
                mdesRegDevJson.put(Tags.DEVICE_FINGER_PRINT.getTag(), CommonUtil.getSharedPreference(Tags.DEVICE_FINGER_PRINT.getTag(), Tags.USER_DETAILS.getTag()));
                mdesRegDevJson.put(Tags.PUBLIC_KEY_FINGER_PRINT.getTag(), new String(Hex.encodeHex(publicKeyFingerPrint)));
                registerDeviceParams.put(Tags.MDES.getTag(), mdesRegDevJson);
            }
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (GeneralSecurityException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        } catch (SdkException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
        return registerDeviceParams;
    }


    public static JSONObject getUnRegisterDeviceParams(String userID, String imei) throws SdkException {

        try {
            JSONObject registerUserObject = new JSONObject();
            registerUserObject.put(Tags.IMEI.getTag(), userID);
            registerUserObject.put(Tags.USER_ID.getTag(), imei);
            return registerUserObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getCheckCardEligibilityParams(CardEligibilityRequestParam cardEligibilityRequestParam) throws SdkException {

        try {
            JSONObject checkEligibilityObject = new JSONObject();
            JSONObject cardInfoData = prepareCardInfo(cardEligibilityRequestParam);
            JSONObject jsDeviceInfo = CommonUtil.getDeviceInfoInJson();
            checkEligibilityObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
            checkEligibilityObject.put(Tags.PAYMENT_APP_ID.getTag(), Constants.PAYMENT_APP_INSTANCE_ID);
            checkEligibilityObject.put(Tags.TOKEN_TYPE.getTag(), Constants.TOKEN_TYPE);
            checkEligibilityObject.put(Tags.DEVICE_INFO.getTag(), jsDeviceInfo);
            checkEligibilityObject.put(Tags.CARD_INFO.getTag(), cardInfoData);
            checkEligibilityObject.put(Tags.CARDLET_ID.getTag(), Constants.CARDLET_ID);
            checkEligibilityObject.put(Tags.CONSUMER_LANGUAGE.getTag(), Constants.CONSUMER_LAN);
            return checkEligibilityObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getAssetsMdes(String assetID) throws SdkException {

        try {
            JSONObject getAssetMdesObject = new JSONObject();
            getAssetMdesObject.put(Tags.ASSET_ID.getTag(), assetID);
            return getAssetMdesObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getDigitizationMdes(CheckCardEligibilityResponse checkCardEligibilityResponse) throws SdkException {

        try {
            JSONObject getDigitizationMdesObject = new JSONObject();
            JSONObject getEligibilityRecieptObject = new JSONObject();
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            getEligibilityRecieptObject.put(Tags.VALUE.getTag(), checkCardEligibilityResponse.getEligibilityReceipt().getValue());
            getEligibilityRecieptObject.put(Tags.VALUE_FOR_MINUTES.getTag(), checkCardEligibilityResponse.getEligibilityReceipt().getValidForMinutes());
            getDigitizationMdesObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
            getDigitizationMdesObject.put(Tags.SERVICE_ID.getTag(), checkCardEligibilityResponse.getServiceId());
            getDigitizationMdesObject.put(Tags.T_N_C_ID.getTag(), checkCardEligibilityResponse.getTermsAndConditionsAssetId());
            return getDigitizationMdesObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getEnrollPanVtsParams(CardEligibilityRequestParam cardEligibilityRequestParam) throws SdkException {

        try {
            JSONObject enrollPanObject = new JSONObject();
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            final String clientWalletAccId = comvivaSdk.getInitializationData().getClientWalletAccountId();
            JSONObject expirationDate = new JSONObject();
            expirationDate.put(Tags.MONTH.getTag(), cardEligibilityRequestParam.getExpiryMonth());
            expirationDate.put(Tags.YEAR.getTag(), cardEligibilityRequestParam.getExpiryYear());
            JSONObject encPaymentInstrument = new JSONObject();
            encPaymentInstrument.put(Tags.ACCOUNT_NUMBER.getTag(), cardEligibilityRequestParam.getAccountNumber());
            String cvv2 = cardEligibilityRequestParam.getSecurityCode();
            if (cvv2 != null && !cvv2.isEmpty()) {
                encPaymentInstrument.put(Tags.CVV_2.getTag(), cardEligibilityRequestParam.getSecurityCode());
            }
            encPaymentInstrument.put(Tags.EXPIRATION_DATE.getTag(), expirationDate);
            encPaymentInstrument.put(Tags.NAME.getTag(), cardEligibilityRequestParam.getCardholderName());
            visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            enrollPanObject.put(Tags.CLIENT_APP_ID.getTag(), Constants.CLIENT_APP_ID);
            enrollPanObject.put(Tags.CLIENT_WALLET_ACCOUNT_ID.getTag(), clientWalletAccId);
            enrollPanObject.put(Tags.CLIENT_DEVICE_ID.getTag(), visaPaymentSDK.getDeviceId());
            enrollPanObject.put(Tags.CONSUER_ENTRY_MODE.getTag(), cardEligibilityRequestParam.getConsumerEntryMode().name());
            enrollPanObject.put(Tags.ENC_PAYMENT_INSTRUMENT.getTag(), encPaymentInstrument);
            enrollPanObject.put(Tags.LOCALE.getTag(), cardEligibilityRequestParam.getLocale());
            enrollPanObject.put(Tags.PAN_SOURCE.getTag(), cardEligibilityRequestParam.getPanSource().name());
            return enrollPanObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getTermsAndConditionVtsParams(String guid) throws SdkException {

        try {
            JSONObject termsAndConditionVtsObject = new JSONObject();
            termsAndConditionVtsObject.put(Tags.GUID.getTag(), guid);
            return termsAndConditionVtsObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getProvisionVtsParams(DigitizationRequestParam digitizationRequestParam, EnrollPanResponse enrollPanResponse) throws SdkException {

        try {
            JSONObject provisionVtsObject = new JSONObject();
            ComvivaSdk comvivaSdk = ComvivaSdk.getInstance(null);
            final String clientWalletAccId = comvivaSdk.getInitializationData().getClientWalletAccountId();
            provisionVtsObject.put(Tags.CLIENT_APP_ID.getTag(), Constants.CLIENT_APP_ID);
            provisionVtsObject.put(Tags.CLIENT_WALLET_ACCOUNT_ID.getTag(), clientWalletAccId);
            visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
            provisionVtsObject.put(Tags.CLIENT_DEVICE_ID.getTag(), visaPaymentSDK.getDeviceId());
            provisionVtsObject.put(Tags.PAN_ENROLLMENT_ID.getTag(), enrollPanResponse.getvPanEnrollmentID());
            provisionVtsObject.put(Tags.TERMS_AND_CONDITION_ID.getTag(), enrollPanResponse.getCardMetaData().getTermsAndConditionsID());
            provisionVtsObject.put(Tags.EMAIL_ADDRESS.getTag(), digitizationRequestParam.getEmailAddress());
            provisionVtsObject.put(Tags.PROTECTION_TYPE.getTag(), Constants.PROTECTION_TYPE);
            provisionVtsObject.put(Tags.PRESENTATION_TYPE.getTag(), Constants.PRESENTATION_TYPE);
            return provisionVtsObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getCofirmProvisionVtsParams(ProvisionAckRequest provisionAckRequest, String vProvisionedTokenID) throws SdkException {

        try {
            JSONObject confirmProvisionObject = new JSONObject();
            confirmProvisionObject.put(Tags.V_PROVISIONED_TOKEN_ID_SMALLP.getTag(), vProvisionedTokenID);
            confirmProvisionObject.put(Tags.API.getTag(), provisionAckRequest.getApi());
            confirmProvisionObject.put(Tags.PROVISIONING_STATUS.getTag(), provisionAckRequest.getProvisioningStatus());
            String failureReason = provisionAckRequest.getFailureReason();
            if (failureReason != null && failureReason.equalsIgnoreCase(Tags.FAILURE.getTag())) {
                confirmProvisionObject.put(Tags.FAILURE_REASON.getTag(), Tags.PROCESSING_FAILURE.getTag());
            }
            return confirmProvisionObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getPerformCardLCMMdesVtsParams(CardLcmRequestParam cardLcmRequestParam) throws SdkException {

        try {
            JSONObject performCardLcmVtsObject = new JSONObject();
            if (CardType.VTS == cardLcmRequestParam.getPaymentCard().getCardType()) {
                performCardLcmVtsObject.put(Tags.V_PROVISIONED_TOKEN_ID_SMALLP.getTag(), ((TokenData) cardLcmRequestParam.getPaymentCard().getCurrentCard()).getVProvisionedTokenID());
                performCardLcmVtsObject.put(Tags.REASON_CODE.getTag(), cardLcmRequestParam.getReasonCode().name());
                performCardLcmVtsObject.put(Tags.OPERATION.getTag(), cardLcmRequestParam.getCardLcmOperation().name());
            } else {
                ArrayList<String> listOfTokenReferences = new ArrayList<>();
                listOfTokenReferences.add(cardLcmRequestParam.getPaymentCard().getCardUniqueId());
                performCardLcmVtsObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
                performCardLcmVtsObject.put(Tags.CAUSED_BY.getTag(), Constants.CARD_HOLDER);
                performCardLcmVtsObject.put(Tags.REASON.getTag(), cardLcmRequestParam.getReasonCode().name());
                performCardLcmVtsObject.put(Tags.TOKEN_UNIQUE_REFERENCES.getTag(), new JSONArray(listOfTokenReferences));
                performCardLcmVtsObject.put(Tags.REASON_CODE.getTag(), cardLcmRequestParam.getReasonCode().name());
                String operation = cardLcmRequestParam.getCardLcmOperation().name();
                if (cardLcmRequestParam.getCardLcmOperation().name().equals("RESUME")) {
                    operation = "UNSUSPEND";
                }
                performCardLcmVtsObject.put(Tags.OPERATION.getTag(), operation);
            }
            return performCardLcmVtsObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getGenerateOtpParams(CardType cardType, String provisionID, String stepUpRequestId) throws SdkException {

        try {
            JSONObject generateOtpObject = new JSONObject();
            if (cardType.equals(CardType.VTS)) {
                generateOtpObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), provisionID);
                generateOtpObject.put(Tags.STEPUP_REQUEST_ID.getTag(), stepUpRequestId);
            } else {
                generateOtpObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
                generateOtpObject.put(Tags.TOKEN_UNIQUE_REFERENCE.getTag(), provisionID);
                generateOtpObject.put(Tags.AUTHENTICATION_CODE_ID.getTag(), stepUpRequestId);
            }
            return generateOtpObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getTokenStatusVts(PaymentCard paymentCard) throws SdkException {

        try {
            TokenData tokenData = (TokenData) paymentCard.getCurrentCard();
            JSONObject tokenStatusVtsObject = new JSONObject();
            tokenStatusVtsObject.put(Tags.V_PROVISIONED_TOKEN_ID_SMALLP.getTag(), tokenData.getVProvisionedTokenID());
            return tokenStatusVtsObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getCardMetaDataParams(String vpanEnrollmentID) throws SdkException {

        try {
            JSONObject cardMetaDataObject = new JSONObject();
            cardMetaDataObject.put(Tags.vpan_ENROLLMENT_ID.getTag(), vpanEnrollmentID);
            return cardMetaDataObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getVerifyOtpParams(CardType cardType, String id, String otpValue) throws SdkException {

        try {
            JSONObject verifyOTPObject = new JSONObject();
            if (cardType.equals(CardType.VTS)) {
                verifyOTPObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), id);
                verifyOTPObject.put(Tags.OTP_VALUE.getTag(), otpValue);
            } else {
                verifyOTPObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
                verifyOTPObject.put(Tags.TOKEN_UNIQUE_REFERENCE.getTag(), id);
                verifyOTPObject.put(Tags.AUTHENTICATION_CODE.getTag(), otpValue);
            }
            return verifyOTPObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getStepUpParams(String stepUpID) throws SdkException {

        try {
            JSONObject stepUpObject = new JSONObject();
            stepUpObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), stepUpID);
            return stepUpObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    private static JSONObject enrollDeviceVtsReqJSon(String deviceName) throws SdkException {

        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
        com.visa.cbp.external.common.DeviceInfo deviceInfoVts = visaPaymentSDK.getDeviceInfo("");
        deviceInfoVts.setDeviceName(deviceName);
        JSONObject deviceInfoVtsJson = new JSONObject();
        try {
            deviceInfoVtsJson.put(Tags.OS_TYPE.getTag(), deviceInfoVts.getOsType()); // Required
            deviceInfoVtsJson.put(Tags.DEVICE_TYPE.getTag(), deviceInfoVts.getDeviceType()); // required
            deviceInfoVtsJson.put(Tags.DEVICE_NAME.getTag(), deviceInfoVts.getDeviceName()); // Required
            deviceInfoVtsJson.put(Tags.OS_VERSION.getTag(), deviceInfoVts.getOsVersion());
            deviceInfoVtsJson.put(Tags.OS_BUILD.getTag(), deviceInfoVts.getOsBuildID());
            deviceInfoVtsJson.put(Tags.DEVICE_ID_TYPE.getTag(), deviceInfoVts.getDeviceIDType());
            deviceInfoVtsJson.put(Tags.DEVICE_MANUFACTURER.getTag(), deviceInfoVts.getDeviceManufacturer());
            deviceInfoVtsJson.put(Tags.DEVICE_BRAND.getTag(), deviceInfoVts.getDeviceBrand());
            deviceInfoVtsJson.put(Tags.DEVICE_MODEL.getTag(), deviceInfoVts.getDeviceModel());
            return deviceInfoVtsJson;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        }
    }


    private static JSONObject prepareCardInfo(CardEligibilityRequestParam cardEligibilityRequestParam) throws SdkException {

        Map<String, Object> keyMap = null;
        try {
            JSONObject cardInfo = new JSONObject();
            JSONObject cardInfoData = new JSONObject();
            // Preparing Card Info Data
            cardInfoData.put(Tags.ACCOUNT_NUMBER.getTag(), cardEligibilityRequestParam.getAccountNumber());
            cardInfoData.put(Tags.EXPIRY_MONTH.getTag(), cardEligibilityRequestParam.getExpiryMonth());
            String expiryYear = cardEligibilityRequestParam.getExpiryYear().length() > 2 ? cardEligibilityRequestParam.getExpiryYear().substring(cardEligibilityRequestParam.getExpiryYear().length() - 2) : cardEligibilityRequestParam.getExpiryYear();
            cardInfoData.put(Tags.EXPIRY_YEAR.getTag(), expiryYear);
            // cardInfoData.put(Tags.SOURCE.getTag(), cardEligibilityRequestParam.getPanSource().name());
            cardInfoData.put(Tags.SOURCE.getTag(), "CARD_ADDED_MANUALLY");
            cardInfoData.put(Tags.CARD_HOLDER_NAME.getTag(), cardEligibilityRequestParam.getCardholderName());
            cardInfoData.put(Tags.SECURITY_CODE.getTag(), cardEligibilityRequestParam.getSecurityCode());
            keyMap = getMasterKeyEncryptedDataIVAndPublicFingerprint(cardInfoData);
            cardInfo.put(Tags.ENCRYPTED_DATA.getTag(), ArrayUtil.getHexString((byte[]) keyMap.get(Tags.ENCRYPTED_DATA.getTag())));
            cardInfo.put(Tags.ENCRYPTED_KEY.getTag(), ArrayUtil.getHexString((byte[]) keyMap.get(Tags.ENCRYPTED_KEY.getTag())));
            cardInfo.put(Tags.IV.getTag(), ArrayUtil.getHexString((byte[]) keyMap.get(Tags.IV.getTag())));
            cardInfo.put(Tags.PUBLIC_KEY_FINGER_PRINT.getTag(), Constants.PUBLIC_KEY_FINGERPRINT);
            return cardInfo;
        } catch (GeneralSecurityException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } finally {
            if (null != keyMap && keyMap.get(Tags.AES_KEY.getTag()) != null) {
                Arrays.fill((byte[]) keyMap.get(Tags.AES_KEY.getTag()), Constants.DEFAULT_FILL_VALUE);
            }
            if (null != keyMap && keyMap.get(Tags.IV.getTag()) != null) {
                Arrays.fill((byte[]) keyMap.get(Tags.IV.getTag()), Constants.DEFAULT_FILL_VALUE);
            }
            if (null != keyMap && keyMap.get(Tags.ENCRYPTED_KEY.getTag()) != null) {
                Arrays.fill((byte[]) keyMap.get(Tags.ENCRYPTED_KEY.getTag()), Constants.DEFAULT_FILL_VALUE);
            }
            if (null != keyMap && keyMap.get(Tags.ENCRYPTED_DATA.getTag()) != null) {
                Arrays.fill((byte[]) keyMap.get(Tags.ENCRYPTED_DATA.getTag()), Constants.DEFAULT_FILL_VALUE);
            }
        }
    }


    private static Map<String, Object> getMasterKeyEncryptedDataIVAndPublicFingerprint(JSONObject cardInfoData) throws GeneralSecurityException {

        byte[] oneTimeAesKey;
        byte[] oneTimeIv;
        byte[] encryptedKey;
        byte[] baEncryptedData;
        PublicKey masterPubKey = null;
        Map<String, Object> map = new HashMap<>();
        // Generating one time AES key & IV and encrypting card info data with AES key
        oneTimeAesKey = ArrayUtil.getRandomNumber(16);
        oneTimeIv = ArrayUtil.getRandomNumber(16);
        baEncryptedData = AESUtil.cipherCBC(cardInfoData.toString().getBytes(), oneTimeAesKey, oneTimeIv, AESUtil.Padding.PKCS5PADDING, true);
        masterPubKey = CommonUtil.getPublicKeyFromCert(Constants.CERT_NAME);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, masterPubKey);
        encryptedKey = cipher.doFinal(oneTimeAesKey);
        byte[] bytesFromInputStream = CommonUtil.getBytesFromInputStream(Constants.CERT_NAME);
        byte[] publicKeyFingerPrint = CommonUtil.sha256(bytesFromInputStream);
        map.put(Tags.PUBLIC_KEY_FINGER_PRINT.getTag(), publicKeyFingerPrint);
        map.put(Tags.ENCRYPTED_KEY.getTag(), encryptedKey);
        map.put(Tags.ENCRYPTED_DATA.getTag(), baEncryptedData);
        map.put(Tags.AES_KEY.getTag(), oneTimeAesKey);
        map.put(Tags.IV.getTag(), oneTimeIv);
        return map;
    }


    public static JSONObject getTransactionHistoryParams(PaymentCard paymentCard, int count) throws SdkException {

        try {
            JSONObject transactionHistoryObject = new JSONObject();
            if (CardType.MDES.equals(paymentCard.getCardType())) {
                transactionHistoryObject.put(Tags.TOKEN_UNIQUE_REFERENCE.getTag(), paymentCard.getInstrumentId());
                transactionHistoryObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
            } else if (CardType.VTS.equals(paymentCard.getCardType())) {
                transactionHistoryObject.put(Tags.V_PROVISIONED_TOKEN_ID.getTag(), paymentCard.getCardUniqueId());
                transactionHistoryObject.put(Tags.COUNT.getTag(), count);
            } else {
            }
            return transactionHistoryObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }


    public static JSONObject getRegisterTransactionHistoryForMdesParams(String tokenReference) throws SdkException {

        try {
            JSONObject registerTransactionHistoryForMdesObject = new JSONObject();
            registerTransactionHistoryForMdesObject.put(Tags.TOKEN_UNIQUE_REFERENCE.getTag(), tokenReference);
            registerTransactionHistoryForMdesObject.put(Tags.PAYMENT_APP_INSTANCE_ID.getTag(), CommonUtil.getSharedPreference(Tags.MDES_PAY_INSTANCE_ID.getTag(), Tags.USER_DETAILS.getTag()));
            return registerTransactionHistoryForMdesObject;
        } catch (JSONException e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_JSON_EXCEPTION);
        } catch (Exception e) {
            Log.e(Tags.ERROR_LOG.getTag(), String.valueOf(e));
            throw new SdkException(SdkErrorStandardImpl.SDK_INTERNAL_ERROR);
        }
    }
}
