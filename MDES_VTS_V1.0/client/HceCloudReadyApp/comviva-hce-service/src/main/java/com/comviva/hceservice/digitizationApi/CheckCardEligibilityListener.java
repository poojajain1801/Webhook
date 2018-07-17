package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.common.CommonListener;

/**
 * Listener for Check Card Eligibility API.
 */
public interface CheckCardEligibilityListener extends CommonListener {
    /**
     * Check Eligibility performed successfully.
     */
    void onCheckEligibilityCompleted();

    /**
     * Terms & Condition is required.
     * @param contentGuid Asset for Terms & Conditions
     */
    void onTermsAndConditionsRequired(ContentGuid contentGuid);

    /**
     * Card Meta Data Details
     * @param cardMetaData  Card Meta Data
     */

    void getCardMetadataDetails(CardMetaData cardMetaData);


}
