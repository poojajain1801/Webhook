package com.comviva.mfs.promotion.constants;

/**
 * Status of the card.
 * Created by tarkeshwar.v on 2/9/2017.
 */
public enum TokenState {
    // MDES sent new card profile
    NEW,
    // CMS-d send card profile to MPA and received notification
    DIGITIZED,
    // SessionInfo is activated
    ACTIVATED
}
