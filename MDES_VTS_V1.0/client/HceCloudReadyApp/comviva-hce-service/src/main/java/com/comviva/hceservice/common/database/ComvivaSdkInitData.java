package com.comviva.hceservice.common.database;

import com.comviva.hceservice.fcm.RnsInfo;

/**
 * Contain initialization data for comviva sdk which is common to all
 * Created by tarkeshwar.v on 5/18/2017.
 */
public class ComvivaSdkInitData {
    private boolean initState;
    private String rnsRegistrationId;
    private RnsInfo rnsInfo;
    private boolean vtsInitState;
    private boolean mdesInitState;

    public boolean isInitState() {
        return initState;
    }

    public String getRnsRegistrationId() {
        return rnsRegistrationId;
    }

    public RnsInfo getRnsInfo() {
        return rnsInfo;
    }

    public boolean isVtsInitState() {
        return vtsInitState;
    }

    public boolean isMdesInitState() {
        return mdesInitState;
    }

    public void setInitState(boolean initState) {
        this.initState = initState;
    }

    public void setRnsRegistrationId(String rnsRegistrationId) {
        this.rnsRegistrationId = rnsRegistrationId;
    }

    public void setRnsInfo(RnsInfo rnsInfo) {
        this.rnsInfo = rnsInfo;
    }

    public void setVtsInitState(boolean vtsInitState) {
        this.vtsInitState = vtsInitState;
    }

    public void setMdesInitState(boolean mdesInitState) {
        this.mdesInitState = mdesInitState;
    }
}
