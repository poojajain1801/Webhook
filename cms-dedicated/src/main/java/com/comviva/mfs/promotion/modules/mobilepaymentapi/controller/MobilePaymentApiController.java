package com.comviva.mfs.promotion.modules.mobilepaymentapi.controller;

import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RmResponseMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RemoteManagementReqMpa;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSession;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.model.RequestSessionResp;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.service.contract.ProvisionServiceMobPayApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for all Mobile Payment APIs.
 * Created by tarkeshwar.v on 2/3/2017.
 */
@RestController
@RequestMapping("/mdes/paymentapp/1/0")
public class MobilePaymentApiController {
    @Autowired
    private ProvisionServiceMobPayApi provisionService;

    public MobilePaymentApiController(ProvisionServiceMobPayApi provisionService) {
        this.provisionService = provisionService;
    }

    @ResponseBody
    @RequestMapping(value = "/requestSession", method = RequestMethod.POST)
    public RequestSessionResp requestSession(@RequestBody RequestSession requestSession) {
        return provisionService.requestSession(requestSession);
    }

    @ResponseBody
    @RequestMapping(value = "/provision", method = RequestMethod.POST)
    public RmResponseMpa provision(@RequestBody RemoteManagementReqMpa remoteManagementReqMpa) {
        return provisionService.provision(remoteManagementReqMpa);
    }

    @ResponseBody
    @RequestMapping(value = "/notifyProvisioningResult", method = RequestMethod.POST)
    public RmResponseMpa notifyProvisionResult(@RequestBody RemoteManagementReqMpa remoteManagementReqMpa) {
        return provisionService.notifyProvisioningResult(remoteManagementReqMpa);
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public RmResponseMpa deleteToken(@RequestBody RemoteManagementReqMpa remoteManagementReqMpa) {
        return provisionService.deleteToken(remoteManagementReqMpa);
    }
}
