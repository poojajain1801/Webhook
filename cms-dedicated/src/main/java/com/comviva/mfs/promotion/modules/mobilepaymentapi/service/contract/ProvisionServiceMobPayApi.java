package com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract;

import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RmResponseMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RemoteManagementReqMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSession;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSessionResp;

/**
 * Card Provisioning Service.
 * Created by tarkeshwar.v on 2/1/2017.
 */
public interface ProvisionServiceMobPayApi {
    RequestSessionResp requestSession(RequestSession requestSession);

    /** Provisioning request from MP SDK. */
    RmResponseMpa provision(RemoteManagementReqMpa remoteManagementReqMpa);

    /**
     * Invoked by MP-SDK after successfully adding the card.
     * @param remoteManagementReqMpa   Contains request data
     * @return  Response
     */
    RmResponseMpa notifyProvisioningResult(RemoteManagementReqMpa remoteManagementReqMpa);

    RmResponseMpa deleteToken(RemoteManagementReqMpa remoteManagementReqMpa);
}
