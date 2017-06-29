package com.comviva.hceservice.util.crypto;

import com.comviva.hceservice.util.ArrayUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;

/**
 * Utility class for RSA Cipher algorithm
 * Created by tarkeshwar.v on 1/17/2017.
 */
public class RsaCipher {

    public static void encryptWithOAEPWithSHA256(byte[] data, byte[] key) {

    }

    public static byte[] encryptWithOAEPWithSHA256(byte[] data) {
        String modulus = "00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5";
        String pubKeyExp = "010001";
        String prKeyExp = "00395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11";

        byte[] encData;
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");

            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)),
                    new BigInteger(ArrayUtil.getByteArray(pubKeyExp)));

            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);

            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)),
                    new BigInteger(ArrayUtil.getByteArray(prKeyExp)));

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encData = cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            return null;
        }
        return encData;
    }

    public static void main(String[] args) {
        try {
            long time = Calendar.getInstance().getTimeInMillis();
            String currTime = String.format("%014X", time);

            SecureRandom secureRandom = new SecureRandom();
            byte[] mobKeySetIdP1 = new byte[25];
            secureRandom.nextBytes(mobKeySetIdP1);
            String strMobKeySetIdP1 = ArrayUtil.getHexString(mobKeySetIdP1);
            String mobKeySetId = strMobKeySetIdP1 + currTime;

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // for example
            SecretKey transportKey = keyGen.generateKey();
            SecretKey macKey = keyGen.generateKey();
            SecretKey dataEncryptionKey = keyGen.generateKey();
            String strTransportKey = ArrayUtil.getHexString(transportKey.getEncoded());
            String strMacKey = ArrayUtil.getHexString(macKey.getEncoded());
            String strDataEncryptionKey = ArrayUtil.getHexString(dataEncryptionKey.getEncoded());

            int i = 10;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Private Key
        // Exponent : 395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11
        // Modulus : 00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5
        // Public Key
        // Exponent : 010001
        // Modulus : 00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5

        String modulus = "00835EF18FFBC76BBFEFCCE45F8F10E783E1B37BD89D22BE278B2EDA1D7B3CDA5AA5BCF9E790989EC90B39D5B8DC0CADB8AB65B50076351EE712423A686C251C0BC03FAB87A72791428CDCC3EBE18A0AD0988011FB207BF8D45AEAB6A839C541B4E8CBC19A4D91D5CD978EE8ADE34D204A08D23CC2CEFF14B68512C1187C2885E5";
        String pubKeyExp = "010001";
        String prKeyExp = "00395EA1074BE0CEC1472BA71FC40E91CC1A289391092E3EF46DE7CC00CBECCB3E82DA80180C215A5659BCAC04CAB40EB972C03BC733D806E2CA2A79EF582AEC8FE4E5087162DC40658F09BAFCE661172EFC17846236A0C0A76CCDCAD29FCDA3DDF194C73844F580955756C422E6BBE6047F5B2A2DFBD67CC48BA0014C79250F11";

        //String data = "0102030405060708";
        String data = "4245BB09534AAE4DA6C5D52E5A074100";
        System.out.println("Plain Data : " + data);


        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        String rgk = ArrayUtil.getHexString(bytes);
        System.out.println("rgk : " + rgk);


        try {
            /*KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            KeyPair pair = keyGen.generateKeyPair();
            RSAPrivateKey privateKey1 = (RSAPrivateKey)pair.getPrivate();
            RSAPublicKey publicKey1 = (RSAPublicKey)pair.getPublic();

            System.out.println("Private Key");
            System.out.println("Exponent : " + ArrayUtil.getHexString(privateKey1.getPrivateExponent().toByteArray()));
            System.out.println("Modulus : " + ArrayUtil.getHexString(privateKey1.getModulus().toByteArray()));

            System.out.println("Public Key");
            System.out.println("Exponent : " + ArrayUtil.getHexString(publicKey1.getPublicExponent().toByteArray()));
            System.out.println("Modulus : " + ArrayUtil.getHexString(publicKey1.getModulus().toByteArray()));*/

            KeyFactory fact = KeyFactory.getInstance("RSA");

            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)),
                    new BigInteger(ArrayUtil.getByteArray(pubKeyExp)));

            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);

            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(new BigInteger(ArrayUtil.getByteArray(modulus)),
                    new BigInteger(ArrayUtil.getByteArray(prKeyExp)));

            PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encData = cipher.doFinal(ArrayUtil.getByteArray(data));

            String strEncData = ArrayUtil.getHexString(encData);
            System.out.println("Enc Data : " + strEncData);

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decData = cipher.doFinal(encData);
            String strDecData = ArrayUtil.getHexString(decData);
            System.out.println("Dec Data : " + strDecData);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


    }


}