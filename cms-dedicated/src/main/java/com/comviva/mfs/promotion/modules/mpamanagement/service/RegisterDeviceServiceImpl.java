package com.comviva.mfs.promotion.modules.mpamanagement.service;

import com.comviva.mfs.promotion.modules.mpamanagement.domain.ApplicationInstanceInfo;
import com.comviva.mfs.promotion.modules.mpamanagement.model.DeviceRegParam;

import com.comviva.mfs.promotion.modules.mpamanagement.model.DeviceRegisterResp;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import com.comviva.mfs.promotion.modules.mpamanagement.service.contract.RegisterDeviceService;

import com.comviva.mfs.promotion.util.ArrayUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Calendar;
import java.util.Map;

@Service
public class RegisterDeviceServiceImpl implements RegisterDeviceService {
    private ApplicationInstanceInfoRepository appInstInfoRepository;

    private static final String REMOTE_MANAGEMENT_URL = "http://localhost:9099/cms-dedicated/api/device/register";

    @Autowired
    public RegisterDeviceServiceImpl(ApplicationInstanceInfoRepository appInstInfoRepository) {
        this.appInstInfoRepository = appInstInfoRepository;
    }

    @Override
    @Transactional
    public DeviceRegisterResp registerDevice(DeviceRegParam deviceRegParam) {
        byte[] rgk;
        String mobilePin = null;
        String mobileKeySetId;
        SecretKey objTransportKey;
        SecretKey objMacKey;
        SecretKey objDataEncryptionKey;
        SecretKeySpec rgkKey;
        Cipher rgkCipher;

        DeviceRegisterResp registerResp;
        // Device is already registered
        if (appInstInfoRepository.findByPaymentAppInstId(deviceRegParam.getPaymentAppInstanceId()).isPresent()) {
            return new DeviceRegisterResp(ImmutableMap.of("responseCode", "210", "message", "Device is already registered"));

        }

        // 1. Fetch public key indexed with publicKeyFingerprint received in request
        // @TODO Fetch it from HSM or database
        // Private Key
        // Exponent : 395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11
        // Modulus : 00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5
        // Public Key
        // Exponent : 010001
        // Modulus : 00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5
        final String modulus = "00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5";
        final String prKeyExp = "00395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11";

        try {
            // 2. Recover rgk by decrypting encrypted rgk
            // Create Private Key instance
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)),
                    new BigInteger(ArrayUtil.getByteArray(prKeyExp)));
            PrivateKey privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);

            // Create Cipher instance and initialize it with key
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Decrypt rgk
            rgk = cipher.doFinal(ArrayUtil.getByteArray(deviceRegParam.getRgk()));
        } catch (GeneralSecurityException e) {
            return new DeviceRegisterResp(ImmutableMap.of("responseCode", "211", "message", "Failed to recover rgk"));
        }

        try {
            // 3. GenerateMobile keys and provide a unique id as mobileKeysetId
            // MobileKeySetId is 32 byte long (27 bytes random + 5 bytes current time in ms)
            SecureRandom secureRandom = new SecureRandom();
            byte[] mobKeySetIdP1 = new byte[25];
            secureRandom.nextBytes(mobKeySetIdP1);
            mobileKeySetId = ArrayUtil.getHexString(mobKeySetIdP1) + String.format("%014X", Calendar.getInstance().getTimeInMillis());

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);

            objTransportKey = keyGen.generateKey();
            objMacKey = keyGen.generateKey();
            objDataEncryptionKey = keyGen.generateKey();

            // 4. Encrypt each key component with rgk
            rgkCipher = Cipher.getInstance("AES/ECB/NoPadding");
            rgkKey = new SecretKeySpec(rgk, "AES");

            // 5. Recover Mobile Pin if present
            // Check if mobile pin is available then decrypt it with rgk
            if (null != deviceRegParam.getNewMobilePin()) {
                rgkCipher.init(Cipher.DECRYPT_MODE, rgkKey);
                byte[] bPinBlock = rgkCipher.doFinal(ArrayUtil.getByteArray(deviceRegParam.getNewMobilePin()));
                mobilePin = ArrayUtil.getHexString(bPinBlock);

                // TODO verify that pin is in correct format (ISO PIN format 4)
            }
        } catch (GeneralSecurityException e) {
            return new DeviceRegisterResp(ImmutableMap.of("responseCode", "211", "message", "Failed to recover mobile PIN"));
        }

        try {
            rgkCipher.init(Cipher.ENCRYPT_MODE, rgkKey);
            String encTransportKey = ArrayUtil.getHexString(rgkCipher.doFinal(objTransportKey.getEncoded()));
            String encMacKey = ArrayUtil.getHexString(rgkCipher.doFinal(objMacKey.getEncoded()));
            String encDataEncryptionKey = ArrayUtil.getHexString(rgkCipher.doFinal(objDataEncryptionKey.getEncoded()));

            // 6. Store device initialization parameters
            String transportKey = ArrayUtil.getHexString(objTransportKey.getEncoded());
            String macKey = ArrayUtil.getHexString(objMacKey.getEncoded());
            String dataEncryptionKey = ArrayUtil.getHexString(objDataEncryptionKey.getEncoded());
            appInstInfoRepository.save(new ApplicationInstanceInfo(null,
                    deviceRegParam.getPaymentAppId(),
                    deviceRegParam.getPaymentAppInstanceId(),
                    deviceRegParam.getDeviceFingerprint(),
                    mobilePin,
                    mobileKeySetId,
                    transportKey,
                    macKey,
                    dataEncryptionKey,
                    deviceRegParam.getRnsInfo().getRnsRegistrationId()));

            // 7. Prepare response and send
            Map<String, Object> mobKeys = ImmutableMap.of("transportKey", encTransportKey,
                    "macKey", encMacKey,
                    "dataEncryptionKey", encDataEncryptionKey);
            Map<String, Object> response = new ImmutableMap.Builder<String, Object>()
                    .put("responseHost", "site1.cmsd.com")
                    .put("responseId", "123456")
                    .put("mobileKeysetId", mobileKeySetId)
                    .put("mobileKeys", mobKeys)
                    .put("responseCode", "200")
                    .put("message", "User has been successfully registered in the system")
                    .put("remoteManagementUrl", REMOTE_MANAGEMENT_URL).build();
            registerResp = new DeviceRegisterResp(response);
        } catch (Exception e) {
            return new DeviceRegisterResp(ImmutableMap.of("responseCode", "211", "message", "Server error, failed to register device"));
        }
        return registerResp;
    }
}


