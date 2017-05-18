package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * SsdData request
 * Created by amgoth.naik on 4/25/2017.
 */
@Getter
@Setter
public class SsdData {
    private String VMPAVersion;
    private String sdScript;
    private String aidArray;
    private String keyVersionNumber;
    private String sequenceCounter;
    private String CASDCert;
    private String vCertificateID;
    private String sdAID;
    public SsdData(String VMPAVersion,String sdScript,String aidArray,String keyVersionNumber,String sequenceCounter,String CASDCert,String vCertificateID,String sdAID) {

        this.VMPAVersion=VMPAVersion;
        this.sdScript=sdScript;
        this.aidArray=aidArray;
        this.keyVersionNumber=keyVersionNumber;
        this.sequenceCounter=sequenceCounter;
        this.CASDCert=CASDCert;
        this.vCertificateID=vCertificateID;
        this.sdAID=sdAID;
    }

    public SsdData() {
    }
}
