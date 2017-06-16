package com.comviva.mfs.promotion.modules.credentialmanagement.controller;

import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionRequestMdes;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionResponseMdes;
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

    public CredentialManagementController(ProvisionService provisionService) {
        this.provisionService = provisionService;
    }

    @ResponseBody
    @RequestMapping(value = "/provision", method = RequestMethod.POST)
    public ProvisionResponseMdes provisionMdes(@RequestBody ProvisionRequestMdes provisionRequestMdes) {
        return provisionService.provisionMdes(provisionRequestMdes);
    }
}
