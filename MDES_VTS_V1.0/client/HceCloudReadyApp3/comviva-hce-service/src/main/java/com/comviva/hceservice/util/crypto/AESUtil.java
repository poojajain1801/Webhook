package com.comviva.hceservice.util.crypto;


import com.comviva.hceservice.util.ArrayUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AESUtil {
    private static  byte[] mobSessionKeyConf = ArrayUtil.getByteArray("F767033AB5F7CEBDD38FCED2D74BCD0C");
    private static byte[] mobSessionKeyMac = ArrayUtil.getByteArray("978DFDB168E119601FB259945BD0FF7F");
    private static String tokenUniqueReference;

    public static final int BLOCK_SIZE = 16;

    public enum Padding {
        NoPadding,      // JCA standard
        PKCS5Padding,   // JCA standard
        ISO7816_4
    }

    public static void setMobSessionKeys(String mobSessionKeyConf, String mobSessionKeyMac) {
        AESUtil.mobSessionKeyConf = ArrayUtil.getByteArray(mobSessionKeyConf);
        AESUtil.mobSessionKeyMac = ArrayUtil.getByteArray(mobSessionKeyMac);
    }

    public static void setTokenUniqueReference(String tokenUniqueReference) {
        AESUtil.tokenUniqueReference = tokenUniqueReference;
    }

    public static void resetSession() {
        mobSessionKeyConf = null;
        mobSessionKeyMac = null;
        tokenUniqueReference = null;
    }

    public static byte[] prepareSv(int counter, boolean m2c) {
        return ArrayUtil.getByteArray((m2c ? "00" : "01") + String.format("%06X", counter) + "000000000000000000000000");
    }

    /**
     * ISO/IEC 7816-4 padding.
     *
     * @param data Data to be padded
     * @return padded data
     */
    private static byte[] padISO7816(byte[] data) {
        int paddedDataLen = data.length + (BLOCK_SIZE - (data.length % BLOCK_SIZE));
        byte[] paddedData = Arrays.copyOf(data, paddedDataLen);
        paddedData[data.length] = (byte) 0x80;
        return paddedData;
    }

    /**
     * Removes ISO/IES 7816-4 padding
     *
     * @param data Padded data
     * @return Data after removing padding
     */
    private static byte[] removeISO7816Padding(byte[] data) {
        int i = data.length - 1;
        while (i >= 0) {
            if (data[i] == (byte) 0x80) {
                break;
            }
            i--;
        }
        if (i < 0) {
            return null;
        }
        return Arrays.copyOf(data, i);
    }

    /**
     * Encrypts/decrypts the given data with given key using AES algorithm in CBC mode.
     *
     * @param data      Input data to encrypt/decrypt
     * @param key       AES Key
     * @param iv        Initial Vector. If iv is null then default value 00...00 (16 bytes) will be used.
     * @param padding   Padding method.
     * @param isEncrypt <code>true </code>Encrypt the data <br>
     *                  <code>false </code>Decrypt the data
     * @return Encrypted/Decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] cipherCBC(byte[] data, byte[] key, byte[] iv, Padding padding, boolean isEncrypt) throws GeneralSecurityException {
        byte[] outBuff = null;

        // Create Key object from the given key value
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");

        // Create Cipher instance and initialize it with key and encryption mode
        Cipher cipherObj;
        switch (padding) {
            case ISO7816_4:
                cipherObj = Cipher.getInstance("AES/CBC/" + Padding.NoPadding.name());
                if (isEncrypt) {
                    data = padISO7816(data);
                }
                break;

            // JCA Standard padding
            default:
                cipherObj = Cipher.getInstance("AES/CBC/" + padding.name());
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv != null ? iv : ArrayUtil.getByteArray("00000000000000000000000000000000"));
        cipherObj.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keyObj, ivParameterSpec);
        outBuff = cipherObj.doFinal(data);

        // Remove Padding for non-JCA standard Padding Algorithm
        if (!isEncrypt && Padding.ISO7816_4 == padding) {
            outBuff = removeISO7816Padding(outBuff);
        }
        return outBuff;
    }

    /**
     * Encrypts/decrypts the given data with given key using AES algorithm in ECB.
     *
     * @param data      Input data to encrypt/decrypt
     * @param key       AES Key
     * @param padding   Padding method.
     * @param isEncrypt <code>true </code>Encrypt the data <br>
     *                  <code>false </code>Decrypt the data
     * @return Encrypted/Decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] cipherECB(byte[] data, byte[] key, Padding padding, boolean isEncrypt) throws GeneralSecurityException {
        byte[] outBuff = null;

        // Create Key object from the given key value
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");

        // Create Cipher instance and initialize it with key and encryption mode
        Cipher cipherObj;
        switch (padding) {
            case ISO7816_4:
                cipherObj = Cipher.getInstance("AES/ECB/" + Padding.NoPadding.name());
                if (isEncrypt) {
                    data = padISO7816(data);
                }
                break;

            // JCA Standard padding
            default:
                cipherObj = Cipher.getInstance("AES/ECB/" + padding.name());
        }
        cipherObj.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keyObj);
        outBuff = cipherObj.doFinal(data);

        // Remove Padding for non-JCA standard Padding Algorithm
        if (!isEncrypt && Padding.ISO7816_4 == padding) {
            outBuff = removeISO7816Padding(outBuff);
        }
        return outBuff;
    }

    /**
     * CCM as defined in ISO/IEC 19772 as mechanism 3.
     *
     * @param inBuff  Input data
     * @param key     AES Key
     * @param iv      Starting Value
     * @param encrypt <code>true </code>If encryption is required <br></><code>false </code>Decryption is required
     * @return Encrypted/Decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] cipherCcm(byte[] inBuff, byte[] key, byte[] iv, boolean encrypt) throws GeneralSecurityException {
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keyObj, ivSpec);

        byte[] bEncData = cipher.doFinal(inBuff);
        return bEncData;
    }

    /**
     * Calculates AES MAC.
     *
     * @param data Input Data
     * @param key  AES Key
     * @return MAC of input data
     * @throws GeneralSecurityException
     */
    public static byte[] aesMac(byte[] data, byte[] key) throws GeneralSecurityException {
        // Pad data with according to ISO/IEC 7816-4
        byte[] paddedData = padISO7816(data);
        final int noOfBlocks = paddedData.length / BLOCK_SIZE;

        byte[] out = new byte[BLOCK_SIZE];

        // Create Key object from the given key value
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");

        Cipher cipherObj = Cipher.getInstance("AES/CBC/NoPadding");
        cipherObj.init(Cipher.ENCRYPT_MODE, keyObj, new IvParameterSpec(new byte[BLOCK_SIZE]));
        for (int i = 0; i < noOfBlocks; i++) {
            out = ArrayUtil.xor(paddedData, i * BLOCK_SIZE, out, 0, BLOCK_SIZE);
            out = cipherObj.doFinal(out);
        }
        return Arrays.copyOfRange(out, 0, 8);
    }


}




