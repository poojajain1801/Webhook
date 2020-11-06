/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.comviva.mfs.hce.appserver.util.common.messagedigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDigestUtil.class);

    private MessageDigestUtil() {}

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
        }catch (NoSuchAlgorithmException e) {
            LOGGER.error("Exception occured",e);
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
            LOGGER.error("Exception occured",e);
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
        final int thousand = 1000;
        for (int i=0; i<thousand; ++i) {
            currentHash = sha256Hasing(currentHash);
        }
        return sha256Hasing(sha256Hasing(email) + currentHash);
    }
}
