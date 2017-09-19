package com.comviva.hceservice.util.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {
    public enum Algorithm {
        SHA_1("SHA-1"),
        SHA_256("SHA-256"),
        SHA_384("SHA-384"),
        SHA_512("SHA-512");

        private String algName;

        Algorithm(String algName) {
            this.algName = algName;
        }

        public String getAlgorithmName() {
            return algName;
        }
    }

    public static byte[] getMessageDigest(byte[] data, Algorithm algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm.getAlgorithmName());
        return md.digest(data);
    }
}
