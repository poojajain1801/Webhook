package com.comviva.hceservice.listeners;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;

/**
 * Listener for Check Card Eligibility API.
 */
public interface CheckCardEligibilityListener extends CommonListener {

    /**
     * Terms & Condition is required.
     * @param contentGuid Asset for Terms & Conditions
     */
    void onTermsAndConditionsRequired(ContentGuid contentGuid);



}
