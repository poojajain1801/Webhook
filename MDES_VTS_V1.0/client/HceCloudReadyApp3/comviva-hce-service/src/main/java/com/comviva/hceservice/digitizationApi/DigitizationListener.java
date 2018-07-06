package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CommonListener;
import com.comviva.hceservice.digitizationApi.authentication.AuthenticationMethod;
import com.visa.cbp.external.common.StepUpRequest;

import java.util.ArrayList;

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
     * @param panEnrollmentId  Pan Enrollment id of card
     * @param provisionID    Provision Id of card
     * @param stepUpRequests When additional authentication is required, this is the list of supported authentication methods.
     */
    void onRequireAdditionalAuthentication(String panEnrollmentId, String provisionID, ArrayList<StepUpRequest> stepUpRequests);
}
