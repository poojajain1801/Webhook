package com.comviva.hceservice.listeners;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.StepUpRequest;

import java.util.List;

/**
 * UI Listener for digitization API.
 */
public interface DigitizationListener extends CommonListener{
    /**
     * Card digitization approved.
     */
    void onApproved(String instrumentID, Object object);

    /**
     * Card digitization declined. Card not eligible for digitization.
     */
    void onDeclined();

    /**
     * Card Digitization require additional authentication.
     * @param panEnrollmentId  Pan Enrollment id of card
     * @param provisionID    Provision Id of card
     * @param stepUpRequests When additional authentication is required, this is the list of supported authentication methods.
     */
    void onRequireAdditionalAuthentication(String panEnrollmentId, String provisionID, List<StepUpRequest> stepUpRequests, Object object);
}
