package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class CertificateUtil {
    /**
     * Read RSA Private Key from given file.
     * @param filename  File path.
     * @return  Private Key
     * @throws IOException
     */
    private static String readRsaPrivateKey(InputStream filename) throws IOException {
        // Read key from file
        StringBuilder buff = new StringBuilder();
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
                buff.append(line);
            }

        }
        String strKeyPEM = buff.toString();
        br.close();
        return strKeyPEM;
    }

    /**
     * Parses Key file and creates RSAPrivateKey.
     * @param filename  File containing Private Key
     * @return RSAPrivateKey
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static RSAPrivateKey getRsaPrivateKey(InputStream filename) throws IOException, GeneralSecurityException {
        String privateKeyPEM = readRsaPrivateKey(filename);
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return  (RSAPrivateKey) kf.generatePrivate(keySpec);
    }

    /**
     * Parses Key file and creates RSAPrivateKey.
     * @param filename  File containing Public Key Certificate
     * @return RSAPublicKey
     * @throws CertificateException
     * @throws IOException
     */
    public static RSAPublicKey getRsaPublicKey(InputStream filename) throws CertificateException, IOException{
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        //FileInputStream is = new FileInputStream(filename);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(filename);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) cer.getPublicKey();

        String strKeyValue = ArrayUtil.getHexString(rsaPublicKey.getEncoded());
        System.out.println(strKeyValue);
        return rsaPublicKey;
    }

    public static X509Certificate getCertificate(InputStream filename) throws IOException, CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
       //InputStream is = new FileInputStream(filename);
        return  (X509Certificate) fact.generateCertificate(filename);
    }


}
