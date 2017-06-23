package com.comviva.hceservice.util.crypto;

import android.content.Context;

import com.comviva.hceservice.util.ArrayUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Utility to read public key from certificate files.
 *
 * Created by tarkeshwar.v on 5/23/2017.
 */
public class CertificateUtil {
    /**
     * Parses Key file and creates RSAPrivateKey.
     * @param filename  File containing Public Key Certificate
     * @return RSAPublicKey
     * @throws CertificateException
     * @throws IOException
     */
    public static RSAPublicKey getRsaPublicKey(String filename, Context context) throws CertificateException, IOException{
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        InputStream is = context.getAssets().open(filename);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) cer.getPublicKey();

        String strKeyValue = ArrayUtil.getHexString(rsaPublicKey.getEncoded());
        System.out.println(strKeyValue);
        return rsaPublicKey;
    }

    public static X509Certificate getCertificate(String filename) throws IOException, CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(filename);
        return  (X509Certificate) fact.generateCertificate(is);
    }

}
