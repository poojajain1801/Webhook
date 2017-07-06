package com.comviva.mfs.promotion.modules.mobilepaymentapi.service;

import com.comviva.mfs.promotion.constants.ConstantErrorCodes;
import com.comviva.mfs.promotion.constants.Constants;
import com.comviva.mfs.promotion.constants.ServerConfig;
import com.comviva.mfs.promotion.constants.TokenState;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.domain.SessionInfo;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.model.ProcessSessionResponse;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.repository.SessionInfoRepository;
import com.comviva.mfs.promotion.modules.common.tokens.domain.Token;
import com.comviva.mfs.promotion.modules.common.tokens.repository.TokenRepository;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RemoteManagementReqMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSession;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSessionResp;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RmResponseMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.RemoteManagementUtil;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.SetOrChangeMpinReq;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract.RemoteManagementServiceApi;
import com.comviva.mfs.promotion.modules.mpamanagement.domain.ApplicationInstanceInfo;
import com.comviva.mfs.promotion.modules.mpamanagement.model.MobilePinUtil;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.DateFormatISO8601;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import com.comviva.mfs.promotion.util.cpadapter.CardProfile;
import com.comviva.mfs.promotion.util.httpHandler.HttpRestHandeler;
import com.comviva.mfs.promotion.util.messagedigest.MessageDigestUtil;
import com.comviva.mfs.promotion.util.rns.fcm.RemoteNotificationService;
import com.comviva.mfs.promotion.util.rns.fcm.RnsFactory;
import com.comviva.mfs.promotion.util.rns.fcm.RnsResponse;
import com.mastercard.mcbp.remotemanagement.mdes.RnsMessage;
import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredential;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDProvisionResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDReplenishResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.RemoteManagementSessionData;
import com.mastercard.mcbp.remotemanagement.mdes.profile.DigitizedCardProfileMdes;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import flexjson.JSONSerializer;
import flexjson.transformer.NumberTransformer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.*;

/**
 * Implementation of RemoteManagementServiceApi.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Service
public class RemoteManagementServiceImplApi implements RemoteManagementServiceApi {
    private final TokenRepository tokenRepository;
    private final ApplicationInstanceInfoRepository appInstInfoRepository;
    private final SessionInfoRepository sessionInfoRepository;
    private final RemoteManagementUtil remoteManagementUtil;
    private SessionInfo sessionInfo;
    private HttpRestHandeler httpRestHandeler;

    @Autowired
    public RemoteManagementServiceImplApi(TokenRepository tokenRepository,
                                          ApplicationInstanceInfoRepository appInstInfoRepository,
                                          SessionInfoRepository sessionInfoRepository,
                                          RemoteManagementUtil remoteManagementUtil) {
        this.tokenRepository = tokenRepository;
        this.appInstInfoRepository = appInstInfoRepository;
        this.sessionInfoRepository = sessionInfoRepository;
        this.remoteManagementUtil = remoteManagementUtil;
    }

    /**
     * Prepare response in case of success.
     *
     * @param cmsDProvisionResponse CMS-D Provision Response
     * @return Response
     */
    private RmResponseMpa prepareProvisionResponseMpa(String cmsDProvisionResponse, String iccKek) {
        // Encrypt response prepared
        String encryptedData = remoteManagementUtil.encryptResponse(cmsDProvisionResponse);
        return new RmResponseMpa(encryptedData,
                Integer.toString(ConstantErrorCodes.SC_OK),
                "Card Profile Sent Successfully");
    }

    /**
     * Prepare Response if any known error occurs.
     *
     * @param reasonCode Error Code
     * @return Response
     */
    private RmResponseMpa prepareProvisionResponseMpaError(final int reasonCode) {
        // Prepare Response
        JSONObject response = new JSONObject();
        response.put("responseId", "3000000001");
        response.put("responseHost", Constants.RESPONSE_HOST);

        // Encrypt response prepared
        String encryptedData;
        if (remoteManagementUtil.isSessionInitialized()) {
            encryptedData = remoteManagementUtil.encryptResponse(response);
        } else {
            encryptedData = "";
        }
        return new RmResponseMpa(encryptedData,
                Integer.toString(reasonCode),
                ConstantErrorCodes.errorCodes.get(reasonCode));
    }

    /**
     * Prepare response according to given reasonCode and reasonDescription.
     *
     * @param reasonCode        Error Code
     * @param reasonDescription Error Description
     * @return Response
     */
    private RmResponseMpa prepareProvisionResponseMpaError(final int reasonCode, final String reasonDescription) {
        // Prepare Response
        JSONObject response = new JSONObject();
        response.put("responseId", "3000000001");
        response.put("responseHost", Constants.RESPONSE_HOST);

        // Encrypt response prepared
        String encryptedData;
        if (remoteManagementUtil.isSessionInitialized()) {
            encryptedData = remoteManagementUtil.encryptResponse(response);
        } else {
            encryptedData = "";
        }
        return new RmResponseMpa(encryptedData, Integer.toString(reasonCode), reasonDescription);
    }

    /**
     * Generate session code.
     *
     * @return Session Code
     */
    private byte[] generateSessionCode() {
        SecureRandom random = new SecureRandom();
        byte[] sessionCode = new byte[Constants.LEN_SESSION_CODE];
        random.nextBytes(sessionCode);
        return sessionCode;
    }

    /**
     * Prepares expiry date for Session Code.
     *
     * @return ISO-8601 formatted date.
     */
    private String prepareSessionExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        // Add expiry duration to current date
        calendar.add(Calendar.MONTH, RemoteNotificationService.RNS_SESSION_EXPIRY_DURATION_IN_MONTH);
        return DateFormatISO8601.fromCalendar(calendar);
    }

    private byte[] prepareNotificationData(final String responseHost,
                                           final String tokenUniqueReference,
                                           RemoteNotificationService.PENDING_ACTION pendingAction,
                                           ApplicationInstanceInfo appInstanceInfo) throws GeneralSecurityException {
        sessionInfo.setExpiryTimeStamp(prepareSessionExpiryDate());
        sessionInfo.setValidForSeconds(RemoteNotificationService.RNS_SESSION_VALIDITY_DURATION_IN_SECONDS);
        sessionInfo.setTokenUniqueReference(tokenUniqueReference);

        // Generate Session Code
        // Prepare RemoteManagementSessionData Object
        RemoteManagementSessionData rmSessionDataObj = new RemoteManagementSessionData();
        rmSessionDataObj.setVersion(RemoteNotificationService.VERSION);
        rmSessionDataObj.setSessionCode(ByteArray.of(sessionInfo.getSessionCode()));
        rmSessionDataObj.setExpiryTimestamp(sessionInfo.getExpiryTimeStamp());
        rmSessionDataObj.setValidForSeconds(sessionInfo.getValidForSeconds());
        if (pendingAction != null) {
            rmSessionDataObj.setPendingAction(pendingAction.name());
            rmSessionDataObj.setTokenUniqueReference(tokenUniqueReference);
        }
        JSONSerializer jsonSerializer = new JSONSerializer();
        String remoteManagementSessionData = jsonSerializer.include("ByteArray.class")
                .transform(new ByteArrayTransformer(), ByteArray.class).serialize(rmSessionDataObj);

        // ***** Encrypt RemoteManagementSessionData and generate MAC using mobile keys
        byte[] baRandomData = ArrayUtil.getRandom(16);
        byte[] rmSessionData = remoteManagementSessionData.getBytes();
        byte[] baRemoteManagementSessionData = Arrays.copyOf(baRandomData, baRandomData.length + rmSessionData.length);
        System.arraycopy(rmSessionData, 0, baRemoteManagementSessionData, baRandomData.length, rmSessionData.length);

        byte[] encData = AESUtil.cipherCBC(baRemoteManagementSessionData,
                ArrayUtil.getByteArray(appInstanceInfo.getTransportKey()),
                null,
                AESUtil.Padding.ISO7816_4,
                true);

        byte[] mac = AESUtil.aesMac(encData, ArrayUtil.getByteArray(appInstanceInfo.getMacKey()));
        byte[] encMacData = Arrays.copyOf(encData, encData.length + mac.length);
        System.arraycopy(mac, 0, encMacData, encData.length, mac.length);

        String encryptedData = new String(org.apache.commons.codec.binary.Base64.encodeBase64(encMacData));

        RnsMessage rnsMessage = new RnsMessage();
        rnsMessage.setResponseHost(responseHost);
        rnsMessage.setMobileKeysetId(appInstanceInfo.getMobileKeySetId());
        rnsMessage.setEncryptedData(encryptedData);

        String notificationData = jsonSerializer.exclude("encryptedDataInByteArray").serialize(rnsMessage);

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("paymentAppProviderId", "547102052016");
        dataMap.put("requestId", "123456");
        dataMap.put("paymentAppInstanceId", sessionInfo.getPaymentAppInstanceId());
        dataMap.put("notificationData", notificationData);
        JSONObject data = new JSONObject(dataMap);

        JSONObject payloadObject = new JSONObject();
        payloadObject.put("data", data);
        payloadObject.put("to", appInstanceInfo.getRnsRegistrationId());

        return payloadObject.toString().getBytes();
    }

    private RmResponseMpa prepareReplenishResponseMpa(CmsDReplenishResponse cmsDReplenishResponse, final int reasonCode, final String reasonDescription) {
        // Prepare Response
        JSONSerializer jsonSerializer = new JSONSerializer();
        String replenishResp = jsonSerializer.include("ByteArray.class")
                .transform(new ByteArrayTransformer(), ByteArray.class).deepSerialize(cmsDReplenishResponse);

        // Encrypt response prepared
        String encryptedData = remoteManagementUtil.encryptResponse(replenishResp);
        return new RmResponseMpa(encryptedData, Integer.toString(reasonCode), reasonDescription);
    }

    private String getMpin(String tokenUniqueReference) {
        String paymentAppInstanceId = tokenRepository.findByTokenUniqueReference(tokenUniqueReference).get().getPaymentAppInstId();

        String mPinBlock = appInstInfoRepository.findByPaymentAppInstId(paymentAppInstanceId).get().getMobilePin();
        String mPin = getMpinfromMobilePinBlock(mPinBlock);


        return mPin;
    }

    @Transactional
    private void updateNewPin(ApplicationInstanceInfo appInstanceInfo, byte[] newPin) {
        appInstanceInfo.setMobilePin(ArrayUtil.getHexString(newPin));
        appInstanceInfo.setPinTryCounter(Constants.MAX_PIN_TRY_COUNTER);
        appInstInfoRepository.save(appInstanceInfo);

    }

    @Transactional
    private void updatePinTryCounter(ApplicationInstanceInfo applicationInstanceInfo, int pinTryCounter) {
        applicationInstanceInfo.setPinTryCounter(pinTryCounter);
        appInstInfoRepository.save(applicationInstanceInfo);
    }

    private String getMpinfromMobilePinBlock(String pinBlock) {
        int mPinLength = Integer.parseInt(pinBlock.substring(1, 2));
        String mPin = pinBlock.substring(2, 2 + mPinLength);
        return mPin;
    }

    private void notifyMobilePinChangeResult(String paymentAppInstanceId) {
        MultiValueMap<String, Object> notifyMobilePinChangeResultReq = new LinkedMultiValueMap<String, Object>();
        notifyMobilePinChangeResultReq.add("responseHost", Constants.RESPONSE_HOST);
        notifyMobilePinChangeResultReq.add("requestId", "123456");
        notifyMobilePinChangeResultReq.add("paymentAppInstanceId", paymentAppInstanceId);
        notifyMobilePinChangeResultReq.add("result", "SUCCESS");
        httpRestHandeler = new HttpRestHandeler();
        httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/credentials/1/0/notifyPinChangeResult", notifyMobilePinChangeResultReq);
    }

    private RmResponseMpa prepareSetOrChangePinResp(String result, int mobilePinTriesRemaining,
                                                    final int reasonCode, final String reasonDescription) {
        // Prepare Response
        JSONObject response = new JSONObject();
        response.put("responseId", "3000000001");
        response.put("responseHost", Constants.RESPONSE_HOST);
        response.put("result", result);
        response.put("mobilePinTriesRemaining", mobilePinTriesRemaining);
        //Prepare Response

        // Encrypt response prepared
        String encryptedData;
        if (remoteManagementUtil.isSessionInitialized()) {
            encryptedData = remoteManagementUtil.encryptResponse(response);
        } else {
            encryptedData = "";
        }
        return new RmResponseMpa(encryptedData, Integer.toString(reasonCode), reasonDescription);
    }

    private boolean verifyPinFormat(byte[] pin) {
        // Verify PIN Format
        int pinFormat = (pin[0] & 0xF0) >> 4;
        if (pinFormat != 4) {
            return false;
        }

        // Verify PIN Length
        int pinLength = pin[0] & 0x0F;
        if (pinLength < Constants.MIN_PIN_LENGTH || pinLength > Constants.MAX_PIN_LENGTH) {
            return false;
        }
        return true;
    }

    private boolean comparePin(byte[] actualPinBlock, byte[] expectedPinBlock) {
        int actualPinLen = actualPinBlock[0] & 0x0F;
        int expPinLen = expectedPinBlock[0] & 0x0F;

        if (actualPinLen != expPinLen) {
            return false;
        }
        return ArrayUtil.compare(actualPinBlock, 1, expectedPinBlock, 1, expPinLen);
    }

    @Override
    public RequestSessionResp requestSession(RequestSession requestSession) {
        // Validate paymentAppInstanceId
        Optional<ApplicationInstanceInfo> applicationInstanceInfo = appInstInfoRepository.findByPaymentAppInstId(requestSession.getPaymentAppInstanceId());
        if (!applicationInstanceInfo.isPresent()) {
            return new RequestSessionResp(Integer.toString(ConstantErrorCodes.INVALID_PAYMENT_APP_INSTANCE_ID),
                    ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.INVALID_PAYMENT_APP_INSTANCE_ID));
        }

        // Validate mobileKeysetId
        ApplicationInstanceInfo appInstanceInfo = applicationInstanceInfo.get();
        if (!appInstanceInfo.getMobileKeySetId().equalsIgnoreCase(requestSession.getMobileKeysetId())) {
            return new RequestSessionResp(Integer.toString(ConstantErrorCodes.INVALID_FIELD_VALUE), "Invalid Key");
        }

        // Validate paymentAppProviderId
        if (!appInstanceInfo.getPaymentAppId().equalsIgnoreCase(requestSession.getPaymentAppProviderId())) {
            return new RequestSessionResp(Integer.toString(ConstantErrorCodes.INVALID_PAYMENT_APP_PROVIDER_ID),
                    ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.INVALID_PAYMENT_APP_PROVIDER_ID));
        }

        // If there is already a session existing then delete previous and create new one
        Optional<SessionInfo> sessionInfoOpt = sessionInfoRepository.findByPaymentAppInstanceId(appInstanceInfo.getPaymentAppInstId());
        if (sessionInfoOpt.isPresent()) {
            sessionInfoRepository.delete(sessionInfoOpt.get());
        }

        // Create Remote Notification Data
        RemoteNotificationService rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM);
        String rnsRegId = appInstanceInfo.getRnsRegistrationId();

        byte[] sessionCode = generateSessionCode();
        sessionInfo = new SessionInfo();
        sessionInfo.setPaymentAppInstanceId(requestSession.getPaymentAppInstanceId());
        sessionInfo.setSessionCode(ArrayUtil.getHexString(sessionCode));
        try {
            byte[] rnsPostData = prepareNotificationData(Constants.RESPONSE_HOST,
                    "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
                    /*RemoteNotificationService.PENDING_ACTION.PROVISION*/ /*null*/RemoteNotificationService.PENDING_ACTION.RESET_MOBILE_PIN,
                    appInstanceInfo);
            RnsResponse response = rns.sendRns(rnsRegId, rnsPostData);
            if (Integer.valueOf(response.getErrorCode()) != 200) {
                return new RequestSessionResp(Integer.toString(ConstantErrorCodes.RNS_UNAVAILABLE),
                        ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.RNS_UNAVAILABLE));
            }
        } catch (GeneralSecurityException e) {
            return new RequestSessionResp(Integer.toString(ConstantErrorCodes.CRYPTOGRAPHY_ERROR),
                    ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.CRYPTOGRAPHY_ERROR));
        }

        // Calculation Authentication Code
        byte[] authenticationCode = MessageDigestUtil.generateAuthenticationCode(appInstanceInfo.getMobileKeySetId().getBytes(),
                sessionCode, ArrayUtil.getByteArray(appInstanceInfo.getDeviceFingerprint()));
        if (null == authenticationCode) {
            return new RequestSessionResp(Integer.toString(ConstantErrorCodes.CRYPTOGRAPHY_ERROR),
                    ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.CRYPTOGRAPHY_ERROR));
        }

        // Generate Mobile Session Keys
        byte[] bMobSessionConfKey = MessageDigestUtil.hMacSha256(sessionCode, ArrayUtil.getByteArray(appInstanceInfo.getTransportKey()));
        byte[] bMobSessionMacKey = MessageDigestUtil.hMacSha256(sessionCode, ArrayUtil.getByteArray(appInstanceInfo.getMacKey()));
        String mobSessionConfKey = ArrayUtil.getHexString(bMobSessionConfKey).substring(0, 32);
        String mobSessionMacKey = ArrayUtil.getHexString(bMobSessionMacKey).substring(0, 32);

        // Create Session and store
        sessionInfo.setId(null);
        sessionInfo.setAuthenticationCode(ArrayUtil.getHexString(authenticationCode));
        sessionInfo.setMobileSessionKeyConf(mobSessionConfKey);
        sessionInfo.setMobileSessionKeyMac(mobSessionMacKey);
        sessionInfo.setM2cCounter(0);
        sessionInfo.setC2mCounter(0);
        sessionInfoRepository.save(sessionInfo);

        return new RequestSessionResp(Integer.toString(ConstantErrorCodes.SC_OK), "Session Created Successfully");
    }

    @Override
    @Transactional
    public RmResponseMpa provision(RemoteManagementReqMpa remoteManagementReqMpa) {
        ProcessSessionResponse processSessionResponse = remoteManagementUtil.processRMRequest(remoteManagementReqMpa.getMobileKeysetId(),
                remoteManagementReqMpa.getAuthenticationCode(),
                remoteManagementReqMpa.getEncryptedData());

        switch (processSessionResponse.getSessionValidationResult()) {
            case SESSION_EXPIRED:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.SESSION_EXPIRED);

            case INCORRECT_DATE_FORMAT:
            case INCORRECT_REQUEST_DATA:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT);

            case SESSION_NOT_FOUND:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_SESSION);

            case MAC_ERROR:
            case CRYPTO_ERROR:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);

            case INVALID_MOBILE_KEYSET_ID:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "Invalid MobileKeysetId");
        }

        JSONObject provisionReqData = processSessionResponse.getJsonRequest();
        String tokenUniqueReference = provisionReqData.getString("tokenUniqueReference");

        // Validate tokenUniqueReference received
        Optional<Token> tokenInfo = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if (!tokenInfo.isPresent()) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        Token token = tokenInfo.get();
        /*byte[] encIccKek = ArrayUtil.getByteArray(token.getIccKek());
        byte[] kek = Constants.AES_KEY;
        byte[] iccKek;
        try {
            iccKek = AESUtil.cipherECB(encIccKek, kek, AESUtil.Padding.ISO7816_4, false);
        } catch (GeneralSecurityException e) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }*/

        // Get Data Encryption Key
        ApplicationInstanceInfo appInstInfo = appInstInfoRepository.findByPaymentAppInstId(token.getPaymentAppInstId()).get();
        byte[] dataEncKey = ArrayUtil.getByteArray(appInstInfo.getDataEncryptionKey());

        // Prepare Card Profile
        final byte[] iccKek = Constants.AES_KEY;
        String encIccKek = null;
        try {
            encIccKek = ArrayUtil.getHexString(AESUtil.cipherECB(iccKek, dataEncKey, AESUtil.Padding.ISO7816_4, true));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        CmsDProvisionResponse cmsDProvisionResponse = new CmsDProvisionResponse();
        CardProfile cardProfile = new CardProfile();
        DigitizedCardProfileMdes digitizedCardProfileMdes = cardProfile.getDigitizedCardProfileMdes(iccKek,
                "5480981500100002",
                "5480981500100002FFFF01150305163347");
        cmsDProvisionResponse.setCardProfile(digitizedCardProfileMdes);
        cmsDProvisionResponse.setIccKek(encIccKek);
        cmsDProvisionResponse.setResponseHost(Constants.RESPONSE_HOST);
        cmsDProvisionResponse.setResponseId("123456");

        // For Error case only
        // cmsDProvisionResponse.setErrorCode("200");
        // cmsDProvisionResponse.setErrorDescription("Card Added Successfully");

        JSONSerializer jsonSerializer = new JSONSerializer();
        String strCmsDProvisionResponse = jsonSerializer.include("ByteArray.class").transform(new ByteArrayTransformer(), ByteArray.class)
                .include("Integer.class").transform(new NumberTransformer(), Integer.class)
                .deepSerialize(cmsDProvisionResponse);

        // Prepare Response
        return prepareProvisionResponseMpa(strCmsDProvisionResponse, ArrayUtil.getHexString(iccKek));
    }

    @Override
    public RmResponseMpa notifyProvisioningResult(RemoteManagementReqMpa remoteManagementReqMpa) {
        ProcessSessionResponse processSessionResponse = remoteManagementUtil.processRMRequest(remoteManagementReqMpa.getMobileKeysetId(),
                remoteManagementReqMpa.getAuthenticationCode(),
                remoteManagementReqMpa.getEncryptedData());

        switch (processSessionResponse.getSessionValidationResult()) {
            case SESSION_EXPIRED:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.SESSION_EXPIRED);

            case INCORRECT_DATE_FORMAT:
            case INCORRECT_REQUEST_DATA:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT);

            case SESSION_NOT_FOUND:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_SESSION);

            case MAC_ERROR:
            case CRYPTO_ERROR:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);

            case INVALID_MOBILE_KEYSET_ID:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "Invalid MobileKeysetId");
        }

        JSONObject provisionReqData = processSessionResponse.getJsonRequest();
        String tokenUniqueReference = provisionReqData.getString("tokenUniqueReference");

        // Validate tokenUniqueReference
        Optional<Token> tokenInfo = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if (!tokenInfo.isPresent()) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        Token token = tokenInfo.get();
        if (!(token.getState().equalsIgnoreCase(TokenState.NEW.name()) ||
                token.getState().equalsIgnoreCase(TokenState.DELETED.name()))) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        // If result = SUCCESS then change the state of the corresponding SessionInfo
        if (provisionReqData.getString("result").equalsIgnoreCase("SUCCESS")) {
            token.setState(TokenState.DIGITIZED.name());
            tokenRepository.save(token);
        }

        // Return response
        return prepareProvisionResponseMpaError(ConstantErrorCodes.SC_OK, "Card Digitized successfully");
    }

    @Override
    public RmResponseMpa deleteToken(RemoteManagementReqMpa remoteManagementReqMpa) {
        ProcessSessionResponse processSessionResponse = remoteManagementUtil.processRMRequest(remoteManagementReqMpa.getMobileKeysetId(),
                remoteManagementReqMpa.getAuthenticationCode(),
                remoteManagementReqMpa.getEncryptedData());

        switch (processSessionResponse.getSessionValidationResult()) {
            case SESSION_EXPIRED:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.SESSION_EXPIRED);

            case INCORRECT_DATE_FORMAT:
            case INCORRECT_REQUEST_DATA:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT);

            case SESSION_NOT_FOUND:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_SESSION);

            case MAC_ERROR:
            case CRYPTO_ERROR:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);

            case INVALID_MOBILE_KEYSET_ID:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "Invalid MobileKeysetId");
        }

        JSONObject deleteReqData = processSessionResponse.getJsonRequest();
        String tokenUniqueReference = deleteReqData.getString("tokenUniqueReference");

        // Validate tokenUniqueReference
        if (!deleteReqData.has("tokenUniqueReference") || !deleteReqData.has("transactionCredentialsStatus")) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.MISSING_REQUIRED_FIELD);
        }

        // Validate tokenUniqueReference
        Optional<Token> tokenInfo = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if (!tokenInfo.isPresent()) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        Token token = tokenInfo.get();
        TokenState tokenState = TokenState.valueOf(token.getState());
        switch (tokenState) {
            case NEW:
                // If token is just added and not activated then transactionCredentialsStatus must be empty
                JSONArray transactionCredentialsStatus = deleteReqData.getJSONArray("transactionCredentialsStatus");
                if (transactionCredentialsStatus.length() != 0) {
                    return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE);
                }
                break;

            // Check if token is in DELETED state
            case DELETED:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_STATUS);
        }

        // Set token status to DELETED
        token.setState(TokenState.DELETED.name());
        tokenRepository.save(token);
        return prepareProvisionResponseMpaError(ConstantErrorCodes.SC_OK, "Token Deleted Successfully");
    }

    @Override
    public RmResponseMpa replenish(RemoteManagementReqMpa remoteManagementReqMpa) {

        ProcessSessionResponse processSessionResponse = remoteManagementUtil.processRMRequest(remoteManagementReqMpa.getMobileKeysetId(),
                remoteManagementReqMpa.getAuthenticationCode(),
                remoteManagementReqMpa.getEncryptedData());

        switch (processSessionResponse.getSessionValidationResult()) {
            case SESSION_EXPIRED:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.SESSION_EXPIRED);

            case INCORRECT_DATE_FORMAT:
            case INCORRECT_REQUEST_DATA:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT);

            case SESSION_NOT_FOUND:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_SESSION);

            case MAC_ERROR:
            case CRYPTO_ERROR:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);

            case INVALID_MOBILE_KEYSET_ID:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "Invalid MobileKeysetId");
        }

        JSONObject reqData = processSessionResponse.getJsonRequest();

        // Validate tokenUniqueReference
        if (!reqData.has("tokenUniqueReference")) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.MISSING_REQUIRED_FIELD);
        }

        // TODO Validate if it is not the first time replenishment then transactionCredentialsStatus must be present

        String tokenUniqueReference = reqData.getString("tokenUniqueReference");

        // Validate tokenUniqueReference
        Optional<Token> tokenInfo = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if (!tokenInfo.isPresent()) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        //Call masterCard replenish API
        //Prepare request parameter for replenish
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<String, Object>();
        requestMap.add("responseHost", ServerConfig.responseHost);
        requestMap.add("requestId", reqData.getString("requestId"));
        requestMap.add("tokenUniqueReference", tokenUniqueReference);

        int iValue;
        String strValue = "";
        if (reqData.has("transactionCredentialsStatus")) {
            //String strTtransactionCredentialsStatus = reqData.get("transactionCredentialsStatus").toString();
            JSONArray transactionCredentialsStatusList = reqData.getJSONArray("transactionCredentialsStatus");
            MultiValueMap<String, Object> transactionCredentialsStatusMap = new LinkedMultiValueMap<String, Object>();

            for (int i = 0; i < transactionCredentialsStatusList.length(); i++) {
                JSONObject j = transactionCredentialsStatusList.getJSONObject(i);
                Iterator<?> it = j.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    if (key.equalsIgnoreCase("atc")) {
                        iValue = j.getInt(key);
                        transactionCredentialsStatusMap.add(key, iValue);
                    } else {
                        strValue = j.getString(key);
                        transactionCredentialsStatusMap.add(key, strValue);
                    }
                }
            }
            requestMap.add("transactionCredentialsStatus", transactionCredentialsStatusMap);
        }
        httpRestHandeler = new HttpRestHandeler();
        //Call MasterCard replinish API to get the raw transctional.
        String response = httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/credentials/1/0/replenish", requestMap);

        //Decrypt the response data from mastercard
        JSONObject jsonResponse = new JSONObject(response);
        // Fetch tokenCredential
        JSONObject tokenCredential = jsonResponse.getJSONObject("rawTransactionCredentials");
        // TODO For the time being we have taken CCM keys as fixed value
        // TODO Check that ccmKeyId is valid

        byte[] bEncData = ArrayUtil.getByteArray(tokenCredential.getString("encryptedData"));
        byte[] bCcmNonce = ArrayUtil.getByteArray(tokenCredential.getString("ccmNonce"));
        byte[] ccmKey = Constants.AES_KEY;
        String rawTransactionCredentialsData = null;
        try {
            rawTransactionCredentialsData = new String(AESUtil.cipherCcm(bEncData, ccmKey, bCcmNonce, false));
            System.out.println("rawTransactionCredentialsData = " + rawTransactionCredentialsData);
        } catch (GeneralSecurityException e) {
            //return prepareProvisionMdesResp(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }

        //Get the decrypted session keys from the response
        JSONObject jsonRawTransactionCredentialsData = new JSONObject(rawTransactionCredentialsData);
        JSONArray arrRawTransactionCredentials = jsonRawTransactionCredentialsData.getJSONObject("rawTransactionCredentialsData").getJSONArray("rawTransactionCredential");

        int noOfTxnCredentials = arrRawTransactionCredentials.length();
        TransactionCredential[] transactionCredentials = new TransactionCredential[noOfTxnCredentials];

        //Get the Pin block from DB and get pin from pin Block.
        String mPin = getMpin(tokenUniqueReference);
        JSONObject jsTxnCredential;
        for (int i = 0; i < noOfTxnCredentials; i++) {
            jsTxnCredential = arrRawTransactionCredentials.getJSONObject(i);

            transactionCredentials[i] = new TransactionCredential();
            transactionCredentials[i].atc = jsTxnCredential.getInt("atc");
            transactionCredentials[i].idn = ByteArray.of(jsTxnCredential.getString("idn"));
            transactionCredentials[i].contactlessMdSessionKey = ByteArray.of(jsTxnCredential.getString("contactlessMdSessionKey"));

            // Create SUK and then set
            String strSUK = remoteManagementUtil.generateSUK(jsTxnCredential.getString("contactlessUmdSessionKey"), mPin);
            transactionCredentials[i].contactlessUmdSingleUseKey = ByteArray.of(strSUK);

            if (jsTxnCredential.has("dsrpMdSessionKey")) {
                transactionCredentials[i].dsrpMdSessionKey = ByteArray.of(jsTxnCredential.getString("dsrpMdSessionKey"));
            }
            if (jsTxnCredential.has("dsrpUmdSessionKey")) {
                transactionCredentials[i].dsrpUmdSingleUseKey = ByteArray.of(jsTxnCredential.getString("dsrpUmdSessionKey"));
            }
        }

        CmsDReplenishResponse replenishResp = new CmsDReplenishResponse();
        replenishResp.setResponseHost("3000000001");
        replenishResp.setResponseHost(Constants.RESPONSE_HOST);
        replenishResp.setTransactionCredentials(transactionCredentials);

        //Prepare response and Send it to the
        return prepareReplenishResponseMpa(replenishResp, 200, "Success");
    }

    @Override
    public RmResponseMpa setOrChangeMpin(SetOrChangeMpinReq setOrChangeMpinReq) {
        String mobileKeySetId = setOrChangeMpinReq.getMobileKeysetId();
        ProcessSessionResponse processSessionResponse = remoteManagementUtil.processRMRequest(mobileKeySetId,
                setOrChangeMpinReq.getAuthenticationCode(),
                setOrChangeMpinReq.getEncryptedData());

        switch (processSessionResponse.getSessionValidationResult()) {
            case SESSION_EXPIRED:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.SESSION_EXPIRED);

            case INCORRECT_DATE_FORMAT:
            case INCORRECT_REQUEST_DATA:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT);

            case SESSION_NOT_FOUND:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_SESSION);

            case MAC_ERROR:
            case CRYPTO_ERROR:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);

            case INVALID_MOBILE_KEYSET_ID:
                return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "Invalid MobileKeysetId");
        }
        JSONObject reqData = processSessionResponse.getJsonRequest();

        Optional<ApplicationInstanceInfo> applicationInstanceInfo = appInstInfoRepository.findByMobileKeySetId(mobileKeySetId);
        ApplicationInstanceInfo appInstanceInfo = applicationInstanceInfo.get();

        // Check if PIN is blocked
        int pinTryCounter = appInstanceInfo.getPinTryCounter();
        if (pinTryCounter == 0) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.MAX_PIN_TRY_LIMIT_REACHED, "Pin Blocked");
        }

        // Check if newMobilePin is available in the request or not
        if (!reqData.has("newMobilePin")) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "New Pin value is required");
        }

        // If Mobile PIN is already set, Current Mobile PIN must be provided
        String currentPin = applicationInstanceInfo.get().getMobilePin();
        if ((currentPin != null || !currentPin.isEmpty()) && !reqData.has("currentMobilePin")) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_VALUE, "Current Pin value is required");
        }

        // Verify PIN lengths
        String encCurrentMobPin = reqData.getString("currentMobilePin");
        String encNewMobPin = reqData.getString("newMobilePin");
        if (encCurrentMobPin.length() != 32 || encNewMobPin.length() != 32) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_LENGTH, "Incorrect Pin length");
        }

        // *** Decrypt received PIN values
        // Get Mobile data encryption key
        String mobileDataEncKey = appInstanceInfo.getDataEncryptionKey();
        // Current Mobile PIN
        byte[] bCurrentMobPinReceived;
        try {
            bCurrentMobPinReceived = MobilePinUtil.decryptPinBlock(ByteArray.of(encCurrentMobPin),
                    appInstanceInfo.getPaymentAppInstId(), ByteArray.of(mobileDataEncKey)).getBytes();

        } catch (GeneralSecurityException | McbpCryptoException e) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_LENGTH, "Pin decryption error");
        }

        if (!verifyPinFormat(bCurrentMobPinReceived)) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_LENGTH, "Wrong Pin Format of current PIN");
        }

        // Verify that current PIN is correct
        if (!comparePin(bCurrentMobPinReceived, ArrayUtil.getByteArray(currentPin))) {
            pinTryCounter--;
            updatePinTryCounter(appInstanceInfo, pinTryCounter);
            return prepareSetOrChangePinResp("INCORRECT_PIN", pinTryCounter, 200, "Wrong Old Pin");
        }

        // New Mobile PIN
        byte[] bNewMobPinReceived;
        try {
            bNewMobPinReceived = MobilePinUtil.decryptPinBlock(ByteArray.of(encNewMobPin),
                    appInstanceInfo.getPaymentAppInstId(), ByteArray.of(mobileDataEncKey)).getBytes();
        } catch (GeneralSecurityException | McbpCryptoException e) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_LENGTH, "Pin decryption error");
        }

        if (!verifyPinFormat(bNewMobPinReceived)) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_LENGTH, "Wrong Pin Format of new PIN");
        }

        // Update New Mobile PIN and set retry counter to max
        updateNewPin(appInstanceInfo, bNewMobPinReceived);

        // Notify PIN Change
        notifyMobilePinChangeResult(appInstanceInfo.getPaymentAppInstId());

        //Prepare Response And send.
        return prepareSetOrChangePinResp("SUCCESS", pinTryCounter, 200, "Set pin successful");
    }

}
