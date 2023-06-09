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
package com.comviva.mfs.hce.appserver.util.mdes;

import com.comviva.mfs.hce.appserver.util.common.CertificateUtil;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;


/**
 * Created by tanmay.patel on 9/24/2018.
 */
public class CryptoUtils {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CryptoUtils.class);

    public static String privateKeydecryption(PrivateKey key, String data) {
        Cipher cipher = null;
        byte[] cipherData = null;
        try {
            //OAEPWithSHA-256AndMGF1Padding
            //PKCS1Padding
            //RSA/ECB/NoPadding
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding","BC");
        } catch (NoSuchAlgorithmException e2) {
            // TODO Auto-generated catch block
            LOGGER.error("NoSuchAlgorithmException  -> CryptoUtils"+e2);
        } catch (NoSuchPaddingException e2) {
            // TODO Auto-generated catch block
            LOGGER.error("NoSuchPaddingException -> CryptoUtils :" +e2);
        } catch (NoSuchProviderException e) {
            LOGGER.error("NoSuchProviderException -> CryptoUtils :" +e);
        }
        try {
            if (null !=cipher) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
        } catch (InvalidKeyException e1) {
            // TODO Auto-generated catch block
            LOGGER.error("InvalidKeyException -> CryptoUtils :"+e1);
        }
        byte[] dataToEncrypt = hexStringToByteArray(data);
        try {
            if (cipher!=null){
                cipherData = cipher.doFinal(dataToEncrypt);
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            LOGGER.error("IllegalBlockSizeException -> CryptoUtils :" +e);
        }
        if (cipherData == null){
            return "";
        }
        return ByteArrayToHexString(cipherData);
    }

    /**
     * AESEncryption
     * @param iv iv
     * @param mode mode
     * @param strInputData strInputData
     * @param strKey strKey
     * @throws InvalidKeyException invalidKey
     * @throws NoSuchAlgorithmException NoSuchAlgorithm
     * @throws InvalidKeySpecException InvalidKeySpecException
     * @throws NoSuchPaddingException NoSuchPadding
     * @throws IllegalBlockSizeException IllegalBlockSizeException
     * @throws BadPaddingException BadPaddingException
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException
     * @return string
     * */
    public static String AESEncryption(String strInputData, String strKey,
                                       int mode,String iv) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        // strInputData = IsoPadding(strInputData);
        byte[] byteDataToEncrypt = hexStringToByteArray(strInputData);

        byte[] key = hexStringToByteArray(strKey);
        Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//        byte[] IV = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

        final SecretKeySpec dKey = new SecretKeySpec(key, "AES");
        byte[] ivv = hexStringToByteArray(iv);
        aesCipherForEncryption.init(mode, dKey, new IvParameterSpec(ivv));
        byte[] byteCipherText = aesCipherForEncryption
                .doFinal(byteDataToEncrypt);
        return new String(byteCipherText);

    }

    /**
     * hexStringToByteArray
     * @param s string
     * @return byte[]
     * */
    private static byte[] hexStringToByteArray(String s) {
        final int two = 2;
        final int four = 4;
        final int sixteen = 16;
        final int one = 1;
        int len = s.length();
        byte[] data = new byte[len / two];
        for (int i = 0; i < len; i += two) {
            data[i / two] = (byte) ((Character.digit(s.charAt(i), sixteen) << four) + Character
                    .digit(s.charAt(i + one), sixteen));
        }
        return data;
    }


    /**
     * ByteArrayToHExString
     * @param respApdu byte array
     * @return string
     * */
    private static String ByteArrayToHexString(byte[] respApdu) {
        StringBuilder hexString = new StringBuilder();
        for (int iTemp = 0; iTemp < respApdu.length; iTemp++) {
            String hex = Integer.toHexString(0xFF & respApdu[iTemp]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

   /* private static String readRsaPrivateKey(InputStream filename) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        Reader reader = new InputStreamReader(filename);
        BufferedReader br = new BufferedReader(reader);
        String line;
        boolean isPrivateKeyStart = false;
        while ((line = br.readLine()) != null) {
            if (line.contains("BEGIN") && line.contains("PRIVATE KEY")) {
                isPrivateKeyStart = true;
                continue;
            }
            if (line.contains("END") && line.contains("PRIVATE KEY")) {
                break;
            }
            if (isPrivateKeyStart) {
                strKeyPEM += line + "\n";
            }
        }
        br.close();
        return strKeyPEM;
    }*/

    /**
     * Parses Key file and creates RSAPrivateKey.
     * @return RSAPrivateKey
     * @throws IOException IOException
     * @throws GeneralSecurityException GeneralSecurityException
     */
    public static RSAPrivateKey getRsaPrivateKey() throws IOException, GeneralSecurityException {
        InputStream inputStream = null;
        ResourceLoader resourceLoader = null;
        Resource resource = null;
       // fout1=new FileInputStream(new File("D:\\Workspace\\KeyManagement\\libs\\nbkmdeswalletPkcs8.key"));


        resourceLoader = new FileSystemResourceLoader() ;
        resource = resourceLoader.getResource("classpath:nbkmdeswalletPkcs8.key");
        inputStream  = resource.getInputStream();
        PrivateKey masterPrivateKey = CertificateUtil.getRsaPrivateKey(inputStream);


       /*// caInput = new BufferedInputStream(fout1);
        String privateKeyPEM = readRsaPrivateKey(caInput);
        byte[] encoded = Base64.decodeBase64(privateKeyPEM.getBytes());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);*/
        return (RSAPrivateKey) masterPrivateKey;
    }

}
