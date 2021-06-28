/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.util.common;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Certificate Util
 * used to fetch/read private and public key
 * */
public class CertificateUtil {

    private CertificateUtil() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtil.class);

    /**
     * Read RSA Private Key from given file.
     * @param filename  File path.
     * @return  Private Key
     * @throws IOException IO Exception
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
     * @throws IOException IO exception
     * @throws GeneralSecurityException GeneralSecurityException
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
     * @throws CertificateException certificate exception
     * @throws IOException io exception
     */
    public static RSAPublicKey getRsaPublicKey(InputStream filename) throws CertificateException, IOException{
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(filename);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) cer.getPublicKey();

        String strKeyValue = ArrayUtil.getHexString(rsaPublicKey.getEncoded());
        LOGGER.info(strKeyValue + "");
        return rsaPublicKey;
    }

    public static X509Certificate getCertificate(InputStream filename) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        return  (X509Certificate) fact.generateCertificate(filename);
    }


}
