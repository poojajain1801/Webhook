package com.comviva.hceservice.common;

import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;

/**
 * Created by tarkeshwar.v on 7/21/2017.
 */
public interface ComvivaWalletListener extends MdesCmsDedicatedWalletEventListener {
    void onTdsRegistrationCode2Received(String tokenUniqueReference);
    void onTdsRegistrationSuccess(String tokenUniqueReference);
    void onTdsRegistrationError(String tokenUniqueReference, final String errorMessage);
    void onTdsNotificationReceived(String tokenUniqueReference);
}
