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
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RmResponseMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RemoteManagementReqMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSession;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSessionResp;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.RemoteManagementUtil;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns.SetOrChangeMpinReq;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract.RemoteManagementServiceApi;
import com.comviva.mfs.promotion.modules.mpamanagement.domain.ApplicationInstanceInfo;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.DateFormatISO8601;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import com.comviva.mfs.promotion.util.httpHandler.HttpRestHandeler;
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

    /**
     * @param requestSession
     * @return
     */
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
        if(!reqData.has("tokenUniqueReference") || !reqData.has("transactionCredentialsStatus")) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.MISSING_REQUIRED_FIELD);
        }

        String tokenUniqueReference = reqData.getString("tokenUniqueReference");

        // Validate tokenUniqueReference
        Optional<Token> tokenInfo = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if (!tokenInfo.isPresent()) {
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        //Call masterCard replenish API
        //Prepare request parameter for replenish
        MultiValueMap<String,Object> requestMap = new LinkedMultiValueMap <String,Object>();
        requestMap.add("responseHost",ServerConfig.responseHost);
        requestMap.add("requestId",reqData.getString("requestId"));
        requestMap.add("tokenUniqueReference",tokenUniqueReference);

        //String strTtransactionCredentialsStatus = reqData.get("transactionCredentialsStatus").toString();
        JSONArray transactionCredentialsStatusList = reqData.getJSONArray("transactionCredentialsStatus");
        MultiValueMap<String,Object> transactionCredentialsStatusMap = new LinkedMultiValueMap<String,Object>();
        int iValue = 0;
        String strValue = "";
        for(int i=0;i<transactionCredentialsStatusList.length();i++)
        {
            JSONObject j = transactionCredentialsStatusList.getJSONObject(i);
            Iterator<?> it = j.keys();
            while (it.hasNext()) {
                String key =  it.next().toString();
                if(key.equalsIgnoreCase("atc")) {
                    iValue = j.getInt(key);
                    transactionCredentialsStatusMap.add(key,iValue);
                }
                else {
                    strValue = j.getString(key);
                    transactionCredentialsStatusMap.add(key,strValue);
                }


            }
        }
        requestMap.add("transactionCredentialsStatus",transactionCredentialsStatusMap);
        httpRestHandeler = new HttpRestHandeler();
        //Call MasterCard replinish API to get the raw transctional.
        String response = httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/credentials/1/0/replenish",requestMap);

       //Decrept the response data from mastercard
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
            System.out.println("rawTransactionCredentialsData = "+rawTransactionCredentialsData);
        } catch (GeneralSecurityException e) {
            //return prepareProvisionMdesResp(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }

        //Get the decrypted session keys from the response
        JSONObject jsonRawTransactionCredentialsData = new JSONObject(rawTransactionCredentialsData);
        JSONArray arrRawTransactionCredentials = jsonRawTransactionCredentialsData.getJSONObject("rawTransactionCredentialsData").getJSONArray("rawTransactionCredential");
        //List listOfMap = new ArrayList();
       JSONArray arrayDecRewTransctionalData = new JSONArray();
        byte[]bValue;

        for(int i=0;i<arrRawTransactionCredentials.length();i++)
        {
            JSONObject j = arrRawTransactionCredentials.getJSONObject(i);
            Iterator<?> it = j.keys();
            //Map mapRawTransactionCredentials =  new HashMap();
            while (it.hasNext()) {
                String key =  it.next().toString();
                if(key.equalsIgnoreCase("atc"))
                {
                    iValue = j.getInt(key);
                    j.put(key,iValue);

                }
                else
                {
                     strValue = j.getString(key);
                    bValue = ArrayUtil.getByteArray(strValue);
                    try {
                        String value = ArrayUtil.getHexString(AESUtil.cipherECB(bValue, Constants.KEK_KEY, AESUtil.Padding.NoPadding, false));
                        j.put(key,value);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }


            }
            arrayDecRewTransctionalData.put(j);
        }

        //Get the Pin block from DB and get pin from pin Block.
        String mPin = getMpin(tokenUniqueReference);
        //Call generateSUK
        JSONArray arrTransctionCredential = generateSUK(arrayDecRewTransctionalData,mPin);

        //Prepare response and Send it to the
        return prepareReplenishResponseMpa(arrTransctionCredential,200,"Success");
    }
    private JSONArray generateSUK(JSONArray arrRawTransactionCredentials,String mPin)
    {
        byte[]bValue;
        String strValue = "";
        String strSUK = "";
        JSONArray arrTransactionCredentials = new JSONArray();
        for(int i=0;i<arrRawTransactionCredentials.length();i++)
        {
            JSONObject j = arrRawTransactionCredentials.getJSONObject(i);
            Iterator<?> it = j.keys();
            //Map mapRawTransactionCredentials =  new HashMap();
            while (it.hasNext()) {
                String key =  it.next().toString();
                if(key.equalsIgnoreCase("contactlessUmdSessionKey") || key.equalsIgnoreCase("dsrpUmdSessionKey"))
                {
                    strValue = j.getString(key);
                    strSUK = remoteManagementUtil.generateSUK(strValue,mPin);
                    j.put(key,strSUK);

                }

            }
            arrTransactionCredentials.put(j);

        }
        //Generate and return SUK.
        return arrTransactionCredentials;
    }
    private RmResponseMpa prepareReplenishResponseMpa(JSONArray arrTransactionCredentials, final int reasonCode, final String reasonDescription) {
        // Prepare Response
        JSONObject response = new JSONObject();
        response.put("responseId", "3000000001");
        response.put("responseHost", Constants.RESPONSE_HOST);
        response.put("transactionCredentials",arrTransactionCredentials);
        //Prepare Response

        // Encrypt response prepared
        String encryptedData;
        if(remoteManagementUtil.isSessionInitialized()) {
            encryptedData = remoteManagementUtil.encryptResponse(response);
        } else {
            encryptedData = "";
        }
        return new RmResponseMpa(encryptedData, Integer.toString(reasonCode), reasonDescription);
    }
    private String getMpin(String tokenUniqueReference)
    {
        String paymentAppInstanceId = tokenRepository.findByTokenUniqueReference(tokenUniqueReference).get().getPaymentAppInstId();

        String mPinBlock = appInstInfoRepository.findByPaymentAppInstId(paymentAppInstanceId).get().getMobilePin();
        String mPin = getMpinfromMobilePinBlock(mPinBlock);

        return mPin;
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
        //Var delearation
        int pinTryCounter = 0;
        //Validate request ID
        if(!reqData.getString("taskId").equalsIgnoreCase(Constants.SET_OR_CHANGE_MPIN_TASK_ID))
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_TASK_ID,"Invalid taskID");



        //Check if newMobilePin is available in the request or not
        if (!reqData.has("newMobilePin"))
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT,"new Pin value is required");

        Optional<ApplicationInstanceInfo> applicationInstanceInfo = appInstInfoRepository.findByMobileKeySetId(mobileKeySetId);

        //Get MPin from data base
        String mobilePinBlockDb = applicationInstanceInfo.get().getMobilePin();
        //String mobilePinBlockDb = appInstInfoRepository.findByMobileKeySetId(mobileKeySetId).get().getMobilePin();

        //Check is current mobile pin is required or not
        if(!(reqData.has("currentMobilePin"))&& (mobilePinBlockDb!=null)) // mobilePinBlockDb shuld be validate first
            return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_FIELD_FORMAT,"Current mobile Pin is required");

        //Get Mobile data encryption key

        byte [] bArrMobileDataEncKey = ArrayUtil.getByteArray(applicationInstanceInfo.get().getDataEncryptionKey());
        String strCurrentMobilePinBlock = "";

        //get the new mobile pin
        String newMpinBlock = reqData.getString("newMobilePin");
        byte[] bArrnewMpinBlock = ArrayUtil.getByteArray(newMpinBlock);
        try {
            newMpinBlock = ArrayUtil.getHexString(AESUtil.cipherECB(bArrnewMpinBlock,bArrMobileDataEncKey, AESUtil.Padding.NoPadding,false));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        //Check if old Mobile pin is present in the request
        if (reqData.has("currentMobilePin")&& (mobilePinBlockDb != null))//Change Mobile Pin
        {
            //Get the Pin try counter from DB and verify if it is exceeding maximum Pin try limit.And notify MDES
             pinTryCounter = applicationInstanceInfo.get().getPinTryCounter();
            if(pinTryCounter>Constants.PIN_TRY_LIMIT) {
                notifyMobilePinChangeResult(applicationInstanceInfo.get().getPaymentAppInstId(),"MOBILE_PIN_TRIES_EXCEEDED");
                return prepareProvisionResponseMpaError(ConstantErrorCodes.MAX_PIN_TRY_LIMIT_REACHED, "Max Pin trys ecceeded");
            }


            byte [] bArrCurrentMpinBlock = ArrayUtil.getByteArray(reqData.getString("currentMobilePin"));
            //Decrypt Mobile pin block
            try {
                strCurrentMobilePinBlock = ArrayUtil.getHexString( AESUtil.cipherECB(bArrCurrentMpinBlock,bArrMobileDataEncKey, AESUtil.Padding.NoPadding,false));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            String mPin = getMpinfromMobilePinBlock(strCurrentMobilePinBlock);
            String mPinDb = getMpinfromMobilePinBlock(mobilePinBlockDb);
            //Verify user provided mPin
           if(!verifyMpin(mPin,mPinDb))
            {
                //Increase Pin try counter
                pinTryCounter++;
                updatePinTryCounter(applicationInstanceInfo.get(),pinTryCounter);
                //return prepareProvisionResponseMpaError(ConstantErrorCodes.INVALID_MOBILE_PIN, "Invalid Mobile Pin");
               return prepareSetOrChangePinResp("INCORRECT_PIN",pinTryCounter,ConstantErrorCodes.INVALID_MOBILE_PIN,"Wrong Pin");
            }
            else
            {
                //TODO:Encrypt the mobile Pin before storing the mobile Pin and decrypt the mobile pin where we are getting the mobile pin from DB
                //Save the newMobile Pin block in the DB
                updateApplicationInstanceInfo(applicationInstanceInfo.get(),newMpinBlock);

                ////Call MDES Notify Mobile PIN Change Result API to notify about the pin change result.
                notifyMobilePinChangeResult(applicationInstanceInfo.get().getPaymentAppInstId(),"SUCCESS");

                //Prepare Respponse and Send
                return prepareSetOrChangePinResp("SUCCESS",pinTryCounter,200,"Change pin successful");

            }
        }
        else //Set Mobile pin
        {
            //set Pin
            updateApplicationInstanceInfo(applicationInstanceInfo.get(),newMpinBlock);

            //Prepare Response And send.
            return prepareSetOrChangePinResp("SUCCESS",pinTryCounter,200,"Set pin successful");
        }


    }
    @Transactional
    private void updateApplicationInstanceInfo(ApplicationInstanceInfo applicationInstanceInfo, String dataToBeUpdated)
    {
       // ApplicationInstanceInfo applicationInstanceInfo1 = applicationInstanceInfo.get();
        applicationInstanceInfo.setMobilePin(dataToBeUpdated);
        appInstInfoRepository.save(applicationInstanceInfo);

    }
    @Transactional
    private void updatePinTryCounter(ApplicationInstanceInfo applicationInstanceInfo,int pinTryCounter)
    {
        applicationInstanceInfo.setPinTryCounter(pinTryCounter);
        appInstInfoRepository.save(applicationInstanceInfo);
    }


    private String getMpinfromMobilePinBlock (String pinBlock)
    {
        int mPinLength = Integer.parseInt(pinBlock.substring(1,2));
        String mPin = pinBlock.substring(2,2+mPinLength);
        return mPin;
    }
    private boolean verifyMpin(String userGiveCurrentMpin, String currentMpin)
    {
        boolean pinVerificationResult = false;
        if (currentMpin.equalsIgnoreCase(userGiveCurrentMpin))
            pinVerificationResult = true;
        return pinVerificationResult;
    }
    private void notifyMobilePinChangeResult(String paymentAppInstanceId ,String status)
    {

        MultiValueMap<String,Object> notifyMobilePinChangeResultReq = new  LinkedMultiValueMap<String,Object>();
        notifyMobilePinChangeResultReq.add("responseHost",Constants.RESPONSE_HOST);
        notifyMobilePinChangeResultReq.add("requestId","123456");
        notifyMobilePinChangeResultReq.add("paymentAppInstanceId",paymentAppInstanceId);
        notifyMobilePinChangeResultReq.add("result","SUCCESS");
        httpRestHandeler = new HttpRestHandeler();
        httpRestHandeler.restfulServieceConsumer(ServerConfig.MDES_IP + ":" + ServerConfig.MDES_PORT + "/mdes/credentials/1/0/notifyPinChangeResult",notifyMobilePinChangeResultReq);
    }
    private RmResponseMpa prepareSetOrChangePinResp(String result,int mobilePinTriesRemaining, final int reasonCode, final String reasonDescription) {
        // Prepare Response
        JSONObject response = new JSONObject();
        response.put("responseId", "3000000001");
        response.put("responseHost", Constants.RESPONSE_HOST);
        response.put("result",result);
        response.put("mobilePinTriesRemaining",mobilePinTriesRemaining);
        //Prepare Response

        // Encrypt response prepared
        String encryptedData;
        if(remoteManagementUtil.isSessionInitialized()) {
            encryptedData = remoteManagementUtil.encryptResponse(response);
        } else {
            encryptedData = "";
        }
        return new RmResponseMpa(encryptedData, Integer.toString(reasonCode), reasonDescription);
    }





}
