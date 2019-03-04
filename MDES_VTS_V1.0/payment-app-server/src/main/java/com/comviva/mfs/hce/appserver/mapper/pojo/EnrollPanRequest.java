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
    private String clientAppId;
    private String clientWalletAccountId;
    private String clientDeviceID;
    private String locale;
    private String panSource;
    private String consumerEntryMode;
    private EncPaymentInstrument encPaymentInstrument;
    private String encryptionMetaData;
    private String platformType;
    private ChannelSecurityContext channelSecurityContext;


   public EnrollPanRequest(String clientAppId, String clientWalletAccountId, String clientDeviceID, String locale, String panSource, String consumerEntryMode,EncPaymentInstrument encPaymentInstrument,String encryptionMetaData,String platformType,ChannelSecurityContext channelSecurityContext) {

        this.clientAppId=clientAppId;
        this.clientWalletAccountId = clientWalletAccountId;
        this.clientDeviceID = clientDeviceID;
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