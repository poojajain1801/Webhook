package com.comviva.hceservice.pojo.registerdevice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseBody {

    @SerializedName("deviceCerts-certFormat-confidentiality")
    @Expose
    private String deviceCertsCertConfidentiality;
    @SerializedName("devEncKeyPair")
    @Expose
    private String devEncKeyPair;
    @SerializedName("devSignKeyPair")
    @Expose
    private String devSignKeyPair;
    @SerializedName("vtsCerts-vCertificateID-integrity")
    @Expose
    private String vtsCertsVCertificateIDIntegrity;
    @SerializedName("devEncCertificate")
    @Expose
    private String devEncCertificate;
    @SerializedName("devSignCertificate")
    @Expose
    private String devSignCertificate;
    @SerializedName("vtsCerts-certUsage-confidentiality")
    @Expose
    private String vtsCertsCertUsageConfidentiality;
    @SerializedName("deviceID")
    @Expose
    private String deviceID;
    @SerializedName("deviceCerts-certValue-integrity")
    @Expose
    private String deviceCertsCertValueIntegrity;
}
