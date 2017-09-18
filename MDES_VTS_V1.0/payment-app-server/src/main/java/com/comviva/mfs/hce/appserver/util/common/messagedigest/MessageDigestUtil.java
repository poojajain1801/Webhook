package com.comviva.mfs.hce.appserver.util.common.messagedigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Contains Message Digest algorithms (e.g. SHA-256)
 * Created by tarkeshwar.v on 2/9/2017.
 */
public class MessageDigestUtil {
    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * Generates message digest of given data with SHA-256.
     * @param data  Input data
     * @return  SHA-256
     * @throws NoSuchAlgorithmException
     */
    public static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }

    /**
     * Computes Authentication Code.
     * @param mobileKeysetId        Mobile Key SetId
     * @param sessionCode           Session Id
     * @param deviceFingerprint     Device Fingerprint
     * @return Authentication Code
     */
    public static byte[] generateAuthenticationCode(byte[] mobileKeysetId, byte[] sessionCode, byte[] deviceFingerprint) {
        byte[] data = Arrays.copyOf(mobileKeysetId, mobileKeysetId.length+sessionCode.length + deviceFingerprint.length);
        System.arraycopy(sessionCode, 0, data, mobileKeysetId.length, sessionCode.length);
        System.arraycopy(deviceFingerprint, 0, data, mobileKeysetId.length+sessionCode.length, deviceFingerprint.length);
        byte[] authenticationCode = null;
        try {
            authenticationCode = sha256(data);
        } catch (NoSuchAlgorithmException e) {
        }
        return authenticationCode;
    }

    /**
     * Calculates HMAC with SHA-256 of a given data.
     * @param data  Input data
     * @param key   Key
     * @return
     */
    public static byte[] hMacSha256(byte[] data, byte[] key) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String sha256Hasing(String inputData)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        inputData = ArrayUtil.asciiToHex(inputData);
        byte[] arrTest = ArrayUtil.getByteArray(inputData);
        md.update(arrTest);

        byte hashData[] = md.digest();
        return ArrayUtil.getHexString(hashData);
    }
    public static String getEmailHashAlgorithmValue(String email) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String currentHash = email.toLowerCase(); // Use a consistent case
        for (int i=0; i<1000; ++i) {
            currentHash = sha256Hasing(currentHash);
        }
        return sha256Hasing(sha256Hasing(email) + currentHash);
    }
}
