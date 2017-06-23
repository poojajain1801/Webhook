package com.comviva.hceservice.util.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {
    public enum Algorithm {
        SHA_1,
        SHA_256,
        SHA_384,
        SHA_512
    }

    public static byte[] getMessageDigest(byte[] data, Algorithm algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }
}
