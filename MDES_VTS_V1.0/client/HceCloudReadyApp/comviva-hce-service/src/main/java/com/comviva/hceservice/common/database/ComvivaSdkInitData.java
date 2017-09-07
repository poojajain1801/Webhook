package com.comviva.hceservice.common.database;

import com.comviva.hceservice.fcm.RnsInfo;

/**
 * Contain initialization data for comviva sdk which is common to all
 * Created by tarkeshwar.v on 5/18/2017.
 */
public class ComvivaSdkInitData {
    private boolean initState;
    private RnsInfo rnsInfo;
    private boolean vtsInitState;
    private boolean mdesInitState;

    /**
     * Returns initialization state of the SDK.
     * @return <code>true </code>If SDK is initialized
     *         <code>false </code>SDK is not initialized
     */
    public boolean isInitState() {
        return initState;
    }

    /**
     *
     * @return
     */
    public RnsInfo getRnsInfo() {
        return rnsInfo;
    }

    /**
     * Checks if SDK is initialized successfully with VTS.
     * @return <code>true </code>If SDK is initialized with VTS <br>
     *          <code>false </code>If SDk is not initialized with VTS
     */
    public boolean isVtsInitialized() {
        return vtsInitState;
    }

    /**
     * Checks if SDK is initialized successfully with MDES.
     * @return <code>true </code>If SDK is initialized with MDES <br>
     *          <code>false </code>If SDk is not initialized with MDES
     */
    public boolean isMdesInitialized() {
        return mdesInitState;
    }

    /**
     * Set Initialization state of the SDK.
     * @param initState <code>true </code>SDK is initialized <br>
     *                  <code>false </code>SDK is not initialized <br>
     */
    public void setInitState(boolean initState) {
        this.initState = initState;
    }

    /**
     * Set RNS information.
     * @param rnsInfo RNS info
     */
    public void setRnsInfo(RnsInfo rnsInfo) {
        this.rnsInfo = rnsInfo;
    }

    /**
     * Set VTS Initialization state of the SDK.
     * @param vtsInitState <code>true </code>If SDK is initialized with VTS <br>
     *          <code>false </code>If SDk is not initialized with VTS
     */
    public void setVtsInitState(boolean vtsInitState) {
        this.vtsInitState = vtsInitState;
    }

    /**
     * Set MDES Initialization state of the SDK.
     * @param mdesInitState <code>true </code>If SDK is initialized with MDES <br>
     *          <code>false </code>If SDk is not initialized with MDES
     */
    public void setMdesInitState(boolean mdesInitState) {
        this.mdesInitState = mdesInitState;
    }
}
