package com.comviva.mfs.hce.appserver.mapper.pojo;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyDeviceCert {
    private DeviceCerts deviceCerts;

    public PublicKey getPublicKey() throws CertificateException{
        String certString = deviceCerts.getCertValue();
        byte[] decodedCert = Base64.getDecoder().decode(certString);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        InputStream inputStream = new ByteArrayInputStream(decodedCert);
        X509Certificate x509Certificate = (X509Certificate)certificateFactory.generateCertificate(inputStream);
        return x509Certificate.getPublicKey();
    }

}
