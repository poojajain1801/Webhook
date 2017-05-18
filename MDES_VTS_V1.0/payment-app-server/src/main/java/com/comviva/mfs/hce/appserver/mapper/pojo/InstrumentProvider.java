package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * InstrumentProvider Request
 * Created by amgoth madan on 4/19/2017.
 */
@Getter
@Setter
public class InstrumentProvider {

    private String intent;
    private String clientWalletProvider;
    private String clientWalletAccountID;
    private String clientDeviceID;
    private String clientAppID;
    private String isIDnV;

    public InstrumentProvider(String intent,String clientWalletProvider,String clientWalletAccountID,String clientDeviceID ,String clientAppID,String isIDnV) {

        this.intent=intent;
        this.clientWalletProvider=clientWalletProvider;
        this.clientWalletAccountID=clientWalletAccountID;
        this.clientDeviceID=clientDeviceID;
        this.clientAppID=clientAppID;
        this.isIDnV=isIDnV;
    }

    public InstrumentProvider() {
    }
}