package com.comviva.mfs.promotion.modules.mobilepaymentapi.service;

import com.comviva.mfs.promotion.constants.ConstantErrorCodes;
import com.comviva.mfs.promotion.constants.Constants;
import com.comviva.mfs.promotion.constants.TokenState;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.domain.SessionInfo;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.model.ProcessSessionResponse;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.repository.SessionInfoRepository;
import com.comviva.mfs.promotion.modules.common.tokens.domain.Token;
import com.comviva.mfs.promotion.modules.common.tokens.repository.TokenRepository;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RmResponseMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RemoteManagementReqMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSession;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSessionResp;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.RemoteManagementUtil;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract.RemoteManagementServiceApi;
import com.comviva.mfs.promotion.modules.mpamanagement.domain.ApplicationInstanceInfo;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.DateFormatISO8601;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import com.comviva.mfs.promotion.util.messagedigest.MessageDigestUtil;
import com.comviva.mfs.promotion.util.rns.fcm.RemoteNotificationService;
import com.comviva.mfs.promotion.util.rns.fcm.RnsFactory;
import com.comviva.mfs.promotion.util.rns.fcm.RnsResponse;
import flexjson.JSONSerializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.*;

/**
 * Implementation of RemoteManagementServiceApi.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Service
public class RemoteManagementServiceImplApi implements RemoteManagementServiceApi {
    private TokenRepository tokenRepository;
    private ApplicationInstanceInfoRepository appInstInfoRepository;
    private SessionInfoRepository sessionInfoRepository;
    private RemoteManagementUtil remoteManagementUtil;
    private SessionInfo sessionInfo;

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
     * @param cardProfile Card Profile Data
     * @param iccKek      ICC Key
     * @return Response
     */
    private RmResponseMpa prepareProvisionResponseMpa(JSONObject cardProfile, String iccKek) {
        // Prepare Response
        JSONObject response = new JSONObject();
        response.put("responseId", "3000000001");
        response.put("responseHost", Constants.RESPONSE_HOST);
        response.put("cardProfile", cardProfile);
        response.put("iccKek", iccKek);

        // Encrypt response prepared
        String encryptedData = remoteManagementUtil.encryptResponse(response);
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
        if(remoteManagementUtil.isSessionInitialized()) {
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
        if(remoteManagementUtil.isSessionInitialized()) {
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
        HashMap<String, String> remoteManagementSessionDataMap = new HashMap<>();
        remoteManagementSessionDataMap.put("version", RemoteNotificationService.VERSION);
        remoteManagementSessionDataMap.put("sessionCode", sessionInfo.getSessionCode());
        remoteManagementSessionDataMap.put("expiryTimeStamp", sessionInfo.getExpiryTimeStamp());
        remoteManagementSessionDataMap.put("validForSeconds", Integer.toString(sessionInfo.getValidForSeconds()));
        if (pendingAction != null) {
            remoteManagementSessionDataMap.put("pendingAction", pendingAction.name());
            remoteManagementSessionDataMap.put("tokenUniqueReference", tokenUniqueReference);
        }
        JSONObject remoteManagementSessionData = new JSONObject(remoteManagementSessionDataMap);

        // ***** Encrypt RemoteManagementSessionData and generate MAC using mobile keys
        byte[] baRemoteManagementSessionData = remoteManagementSessionData.toString().getBytes();

        byte[] encData = AESUtil.cipherCBC(baRemoteManagementSessionData,
                ArrayUtil.getByteArray(appInstanceInfo.getTransportKey()),
                null,
                AESUtil.Padding.ISO7816_4,
                true);

        byte[] mac = AESUtil.aesMac(encData, ArrayUtil.getByteArray(appInstanceInfo.getMacKey()));
        byte[] encMacData = Arrays.copyOf(encData, encData.length + mac.length);
        System.arraycopy(mac, 0, encMacData, encData.length, mac.length);

        String encryptedData = new String(Base64.getEncoder().encode(encMacData));

        // Prepare Notification data
        JSONObject jsonNotificationData = new JSONObject();
        jsonNotificationData.put("responseHost", responseHost);
        jsonNotificationData.put("mobileKeysetId", appInstanceInfo.getMobileKeySetId());
        jsonNotificationData.put("encryptedData", encryptedData);
        String notificationData = new JSONSerializer().serialize(jsonNotificationData);

        JSONObject payloadObject = new JSONObject();
        payloadObject.put("payload", notificationData);
        payloadObject.put("to", appInstanceInfo.getRnsRegistrationId());

        return payloadObject.toString().getBytes();
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
        if(sessionInfoOpt.isPresent()) {
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
            byte[] rnsPostData = prepareNotificationData(Constants.RESPONSE_HOST, null, null, appInstanceInfo);
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
        byte[] authenticationCode = MessageDigestUtil.generateAuthenticationCode(ArrayUtil.getByteArray(appInstanceInfo.getMobileKeySetId()),
                sessionCode, ArrayUtil.getByteArray(appInstanceInfo.getDeviceFingerprint()));
        if(null == authenticationCode) {
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
        byte[] encIccKek = ArrayUtil.getByteArray(token.getIccKek());
        byte[] kek = Constants.AES_KEY;
        byte[] iccKek;
        try {
            iccKek = AESUtil.cipherECB(encIccKek, kek, AESUtil.Padding.ISO7816_4, false);
        } catch (GeneralSecurityException e) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }

        // Prepare Response
        return prepareProvisionResponseMpa(new JSONObject(token.getCardProfile()), ArrayUtil.getHexString(iccKek));
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
        if(!deleteReqData.has("tokenUniqueReference") || !deleteReqData.has("transactionCredentialsStatus")) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.MISSING_REQUIRED_FIELD);
        }

        // Validate tokenUniqueReference
        Optional<Token> tokenInfo = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if (!tokenInfo.isPresent()) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        Token token = tokenInfo.get();
        TokenState tokenState= TokenState.valueOf(token.getState());
        switch (tokenState) {
            case NEW:
                // If token is just added and not activated then transactionCredentialsStatus must be empty
                JSONArray transactionCredentialsStatus = deleteReqData.getJSONArray("transactionCredentialsStatus");
                if(transactionCredentialsStatus.length() != 0) {
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
}
