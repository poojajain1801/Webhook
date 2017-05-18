package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * EnrollPan Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class EnrollPanRequest {

    private String userId;
    private String activationCode;
    private String clientAppId;
    private String clientWalletAccountId;
    private String clientDeviceId;
    private String locale;
    private String panSource;
    private String consumerEntryMode;
    private EncPaymentInstrument encPaymentInstrument;
    private String encryptionMetaData;
    private String platformType;
    private ChannelSecurityContext channelSecurityContext;


   public EnrollPanRequest(String userId,String activationCode,String clientAppId, String clientWalletAccountId, String clientDeviceId, String locale, String panSource, String consumerEntryMode,EncPaymentInstrument encPaymentInstrument,String encryptionMetaData,String platformType,ChannelSecurityContext channelSecurityContext) {
        this.userId=userId;
        this.activationCode=activationCode;
        this.clientAppId=clientAppId;
        this.clientWalletAccountId = clientWalletAccountId;
        this.clientDeviceId = clientDeviceId;
        this.locale = locale;
        this.panSource = panSource;
        this.consumerEntryMode = consumerEntryMode;
        this.encPaymentInstrument=encPaymentInstrument;
        this.encryptionMetaData=encryptionMetaData;
        this.platformType=platformType;
        this.channelSecurityContext=channelSecurityContext;
    }

    public EnrollPanRequest() {
    }
}