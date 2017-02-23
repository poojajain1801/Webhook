package com.comviva.mfs.promotion.modules.credentialmanagement.controller;

import com.comviva.mfs.promotion.modules.credentialmanagement.model.CardLifeCycleReq;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.CardLifeCycleResp;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionRequestMdes;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionResponseMdes;
import com.comviva.mfs.promotion.modules.credentialmanagement.service.contract.CardLifeCycleService;
import com.comviva.mfs.promotion.modules.credentialmanagement.service.contract.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for all Credential Management APIs.
 * Created by tarkeshwar.v on 2/3/2017.
 */
@RestController
@RequestMapping("mdes/credentials/1/0")
public class CredentialManagementController {
    @Autowired
    private ProvisionService provisionService;

    @Autowired
    private CardLifeCycleService cardLifeCycleService;

    public CredentialManagementController(ProvisionService provisionService, CardLifeCycleService cardLifeCycleService) {
        this.provisionService = provisionService;
        this.cardLifeCycleService = cardLifeCycleService;
    }

    @ResponseBody
    @RequestMapping(value = "/provision", method = RequestMethod.POST)
    public ProvisionResponseMdes provisionMdes(@RequestBody ProvisionRequestMdes provisionRequestMdes) {
        return provisionService.provisionMdes(provisionRequestMdes);
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CardLifeCycleResp deleteCard(@RequestBody CardLifeCycleReq cardLifeCycleReq) {
        return cardLifeCycleService.deleteCard(cardLifeCycleReq);
    }
}
