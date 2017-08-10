package com.comviva.hceservice.common;

import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;

/**
 * UI listener for mobile application. This listener will be invoked when any operation(e.g. provision, delete token etc) is performed
 * in background when Remote Notification is received.
 */
public interface ComvivaWalletListener extends MdesCmsDedicatedWalletEventListener {
    /**
     * On Registration Code 2 is received after Invoking Get Registration Code API.
     * @param tokenUniqueReference Token Unique Unique Reference for which TDS is registered.
     */
    void onTdsRegistrationCode2Received(String tokenUniqueReference);

    /***
     * Invoked when TDS registration is Successful.
     * @param tokenUniqueReference Token Unique Unique Reference for which TDS is registered.
     */
    void onTdsRegistrationSuccess(String tokenUniqueReference);

    /**
     * Invoked when TDS registration is failed.
     * @param tokenUniqueReference Token Unique Unique Reference for which TDS had to be registered.
     * @param errorMessage Error Message
     */
    void onTdsRegistrationError(String tokenUniqueReference, final String errorMessage);

    /**
     * Invoked when TDS notification is received.
     * @param tokenUniqueReference Token Unique Reference for which transaction details is notified.
     */
    void onTdsNotificationReceived(String tokenUniqueReference);
}
