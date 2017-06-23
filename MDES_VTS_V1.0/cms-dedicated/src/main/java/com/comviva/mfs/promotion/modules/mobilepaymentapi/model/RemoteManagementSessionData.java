package com.comviva.mfs.promotion.modules.mobilepaymentapi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Remote Management Session data.
 * Created by tarkeshwar.v on 2/13/2017.
 */
@Getter
@Setter
public class RemoteManagementSessionData {
    /** Version number of the Mobile Payment APIs. */
    private String version;

    /** The 29-byte remote management session code used by the Mobile Payment App to generate an
     * authentication code and to derive the Mobile Session Keys when communicating with MDES. */
    private String sessionCode;

    /** The date/time when the remote management session code will expire
     * In ISO 8601 extended format as one of the following:
     *  YYYY-MM-DDThh:mm:ss[.sss]Z
     *  YYYY-MM-DDThh:mm:ss[.sss]±hh:mm
     *  e.g. 2015-03-06T03:23:26Z */
    private String expiryTimestamp;

    /** The number of seconds after which the remote management session code will expire after first use. */
    private String validForSeconds;

    /** The pending action requested by MDES for a SessionInfo on the Mobile Payment App. Must be
     * PROVISION or RESET_MOBILE_PIN*/
    private String pendingAction;

    /** The SessionInfo Credential on which the action is requested. Must be a valid reference as assigned by MDES */
    private String tokenUniqueReference;

    public RemoteManagementSessionData(String version,
                                       String sessionCode,
                                       String expiryTimestamp,
                                       String validForSeconds,
                                       String pendingAction,
                                       String tokenUniqueReference) {
        this.version = version;
        this.sessionCode = sessionCode;
        this.expiryTimestamp = expiryTimestamp;
        this.validForSeconds = validForSeconds;
        this.pendingAction = pendingAction;
        this.tokenUniqueReference = tokenUniqueReference;
    }
}
