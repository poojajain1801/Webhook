package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CommonListener;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;

/**
 * UI Listener for digitization API.
 */
public interface DigitizationListener extends CommonListener{
    /**
     * Card digitization approved.
     */
    void onApproved(String instrumentID);

    /**
     * Card digitization declined. Card not eligible for digitization.
     */
    void onDeclined();

    /**
     * Card Digitization require additional authentication.
     * @param tokenUniqueReference  The unique reference allocated to the new Token. Serves as a
     *                              unique identifier for all subsequent queries or management functions relating to this Token.
     * @param authenticationMethods When additional authentication is required, this is the list of supported authentication methods.
     */
    void onRequireAdditionalAuthentication(String tokenUniqueReference, AuthenticationMethod[] authenticationMethods);
}
