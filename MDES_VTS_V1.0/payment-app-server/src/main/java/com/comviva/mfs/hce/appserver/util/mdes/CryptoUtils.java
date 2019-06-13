package com.comviva.mfs.hce.appserver.util.mdes;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.service.CardDetailServiceImpl;
import com.comviva.mfs.hce.appserver.util.common.CertificateUtil;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.apache.commons.codec.binary.Base64;
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
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Logger;

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
            if (null !=cipher)
                cipher.init(Cipher.DECRYPT_MODE, key);
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

    public static String AESEncryption(String strInputData, String strKey,
                                       int mode,String iv) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {

        // strInputData = IsoPadding(strInputData);
        byte[] byteDataToEncrypt = hexStringToByteArray(strInputData);

        byte[] key = hexStringToByteArray(strKey);
        Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        byte[] IV = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

        final SecretKeySpec dKey = new SecretKeySpec(key, "AES");
        byte[] ivv = hexStringToByteArray(iv);
        aesCipherForEncryption.init(mode, dKey, new IvParameterSpec(ivv));
        byte[] byteCipherText = aesCipherForEncryption
                .doFinal(byteDataToEncrypt);
        return new String(byteCipherText);

    }
    private static byte[] hexStringToByteArray(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

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
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static RSAPrivateKey getRsaPrivateKey() throws IOException, GeneralSecurityException {
        InputStream inputStream = null;
        InputStream fout1=null;
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
