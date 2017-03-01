package com.comviva.mfs.promotion.modules.mobilepaymentapi.model.rns;

import com.comviva.mfs.promotion.constants.Constants;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.domain.SessionInfo;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.model.ProcessSessionResponse;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.model.SessionValidationResult;
import com.comviva.mfs.promotion.modules.common.sessionmanagement.repository.SessionInfoRepository;
import com.comviva.mfs.promotion.modules.mpamanagement.domain.ApplicationInstanceInfo;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.DateFormatISO8601;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

/**
 * Contains functions of validating session and encrypting/decrypting data.
 * Created by tarkeshwar.v on 2/20/2017.
 */
@Component
public class RemoteManagementUtil {
    private ApplicationInstanceInfoRepository appInstInfoRepository;
    private SessionInfoRepository sessionInfoRepository;

    private SessionInfo sessionInfo;
    private byte[] mobSessionKeyConf;
    private byte[] mobSessionKeyMac;
    private int m2c;
    private int c2m;

    private boolean isSessionInitialized;

    /**
     * Prepare SV (Starting Value/Initial Value) for encryption/decryption. <br/>
     *  '00' || M2C || Z, for messages received from the Mobile Payment Application <br/>
     *  '01' || C2M || Z, for messages sent to the Mobile Payment Application <br/>
     *      where Z is 12 bytes of hexadecimal zeros
     *
     * @param counter   Counter value m2c or c2m
     * @param m2c       <code>true </code>m2c Counter <br/>
     *                  <code>false </code>c2m Counter
     * @return 16 bytes SV value
     */
    private byte[] prepareSv(int counter, boolean m2c) {
        return ArrayUtil.getByteArray((m2c?"00":"01") + String.format("%06X", counter) + "000000000000000000000000");
    }

    public RemoteManagementUtil(SessionInfoRepository sessionInfoRepository,
                                ApplicationInstanceInfoRepository appInstInfoRepository) {
        this.sessionInfoRepository = sessionInfoRepository;
        this.appInstInfoRepository = appInstInfoRepository;
    }

    private void resetSession(boolean deleteSessionInfo) {
        if(deleteSessionInfo) {
            sessionInfoRepository.delete(sessionInfo);
        }

        if(null != mobSessionKeyConf) {
            Arrays.fill(mobSessionKeyConf, (byte) 0x00);
            Arrays.fill(mobSessionKeyMac, (byte) 0x00);
            mobSessionKeyConf = null;
            mobSessionKeyMac = null;
        }
        m2c = 0;
        c2m = 0;
        sessionInfo = null;
    }

    private void initSession(byte[] mobSessionKeyConf, byte[] mobSessionKeyMac, int m2c, int c2m) {
        this.mobSessionKeyConf = mobSessionKeyConf;
        this.mobSessionKeyMac = mobSessionKeyMac;
        this.m2c = m2c;
        this.c2m = c2m;
        isSessionInitialized = true;
    }

    public boolean isSessionInitialized() {
        return isSessionInitialized;
    }

    /**
     * Validates incoming session for a request from MPA.
     * @param authCode  Authentication COde to validate
     * @return Result of session Validation
     */
    public ProcessSessionResponse processRMRequest(String mobileKeysetId, String authCode, String encryptedData) {
        isSessionInitialized = false;
        ProcessSessionResponse response = new ProcessSessionResponse();
        Optional<SessionInfo> sessionInfoOptional = sessionInfoRepository.findByAuthenticationCode(authCode);

        // Reset if any session exist
        if(null != sessionInfo) {
            resetSession(false);
        }

        // Invalid session
        if(!sessionInfoOptional.isPresent()) {
            response.setSessionValidationResult(SessionValidationResult.SESSION_NOT_FOUND);
            return response;
        }
        sessionInfo = sessionInfoOptional.get();

        // Validate mobileKeysetId
        ApplicationInstanceInfo appInstInfo = appInstInfoRepository.findByPaymentAppInstId(sessionInfo.getPaymentAppInstanceId()).get();
        if (!mobileKeysetId.equalsIgnoreCase(appInstInfo.getMobileKeySetId())) {
            response.setSessionValidationResult(SessionValidationResult.INVALID_MOBILE_KEYSET_ID);
            return response;
        }

        initSession(ArrayUtil.getByteArray(sessionInfo.getMobileSessionKeyConf()),
                ArrayUtil.getByteArray(sessionInfo.getMobileSessionKeyMac()),
                sessionInfo.getM2cCounter(),
                sessionInfo.getC2mCounter());

        m2c++;
        byte[] svM2c = prepareSv(m2c, true);
        sessionInfo.setM2cCounter(m2c);
        sessionInfoRepository.save(sessionInfo);
        sessionInfo = sessionInfoRepository.findByAuthenticationCode(authCode).get();

        try {
            // Session Expired
            Calendar sessionExpDate = DateFormatISO8601.toCalendar(sessionInfo.getExpiryTimeStamp());
            if(sessionExpDate.getTime().before(Calendar.getInstance().getTime())) {
                response.setSessionValidationResult(SessionValidationResult.SESSION_EXPIRED);
                resetSession(true);
                return response;
            }
        } catch (ParseException e) {
            // Incorrect Date format
            response.setSessionValidationResult(SessionValidationResult.INCORRECT_DATE_FORMAT);
            return response;
        }

        // Check session validity in seconds
        String sessionFirstUse = sessionInfo.getSessionFirstUse();
        // Session is being used for first time
        if(null == sessionFirstUse) {
            sessionInfo.setSessionFirstUse(DateFormatISO8601.now());
            sessionInfoRepository.save(sessionInfo);
        } else {
            // Session is being used for subsequent time so validate that session is still valid
            try {
                Calendar sessionStartTime = DateFormatISO8601.toCalendar(sessionFirstUse);
                long sessionDuration = (Calendar.getInstance().getTimeInMillis() - sessionStartTime.getTimeInMillis())/1000;
                if(sessionDuration > sessionInfo.getValidForSeconds()) {
                    response.setSessionValidationResult(SessionValidationResult.SESSION_EXPIRED);
                    resetSession(true);
                    return response;
                }
            } catch (ParseException e) {
            }
        }

        // Verify MAC and Decrypt encrypted data
        String decData;
        try {
            byte[] bEncryptedData = ArrayUtil.getByteArray(encryptedData);
            byte[] actMac = Arrays.copyOfRange(bEncryptedData, bEncryptedData.length-Constants.LEN_MAC, bEncryptedData.length);
            byte[] encData = Arrays.copyOfRange(bEncryptedData, 0, bEncryptedData.length-Constants.LEN_MAC);

            // Verify MAC
            byte[] expMac = AESUtil.aesMac(encData, mobSessionKeyMac);
            if(!ArrayUtil.compare(actMac, 0, expMac, 0, Constants.LEN_MAC)) {
                response.setSessionValidationResult(SessionValidationResult.MAC_ERROR);
                return response;
            }

            decData = new String(AESUtil.cipherCcm(encData, mobSessionKeyConf, svM2c,false));
            JSONObject reqData = new JSONObject(decData);
            response.setJsonRequest(reqData);
        } catch (GeneralSecurityException e) {
            response.setSessionValidationResult(SessionValidationResult.CRYPTO_ERROR);
            return response;
        } catch (JSONException e) {
            response.setSessionValidationResult(SessionValidationResult.INCORRECT_REQUEST_DATA);
            return response;
        }
        response.setSessionValidationResult(SessionValidationResult.SESSION_OK);
        return response;
    }

    /**
     * Encrypts and calculate mac for response.
     * @param response  CMS response data to be encrypte.
     * @return Encrypted response
     */
    public String encryptResponse(JSONObject response) {
        byte[] bEncData;
        try {
            c2m++;
            byte[] svC2m = prepareSv(c2m, false);
            bEncData = AESUtil.cipherCcm(response.toString().getBytes(), mobSessionKeyConf, svC2m, true);
            byte[] mac = AESUtil.aesMac(bEncData, mobSessionKeyMac);

            byte[] bEncryptedData = Arrays.copyOf(bEncData, bEncData.length + mac.length);
            System.arraycopy(mac, 0, bEncryptedData, bEncData.length, mac.length);

            sessionInfo.setC2mCounter(c2m);
            sessionInfoRepository.save(sessionInfo);

            return ArrayUtil.getHexString(bEncryptedData);
        } catch (GeneralSecurityException e) {
            // Log exception
        }
        return null;
    }
}
