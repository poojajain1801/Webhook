package com.comviva.hceservice.mdes;

public class CertificateData {
    private final byte[] publicKey;
    private final byte[] fingerPrint;

    public CertificateData(final byte[] key, final byte[] fingerPrint) {
        this.publicKey = key;
        this.fingerPrint = fingerPrint;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getFingerPrint() {
        return fingerPrint;
    }
}