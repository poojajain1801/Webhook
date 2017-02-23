package com.comviva.mfs.promotion.modules.credentialmanagement.service.contract;

import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionRequestMdes;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionResponseMdes;

/**
 * Card Provisioning Service.
 * Created by tarkeshwar.v on 2/1/2017.
 */
public interface ProvisionService {
    /** Provisioning request from MDES */
   ProvisionResponseMdes provisionMdes(ProvisionRequestMdes provisionRequestMdes);

 }
