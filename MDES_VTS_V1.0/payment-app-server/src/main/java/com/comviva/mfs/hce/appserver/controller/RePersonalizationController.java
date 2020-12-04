package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.decryptFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoTokenRequest;
import com.comviva.mfs.hce.appserver.service.contract.RePersoService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/device/")
public class RePersonalizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RePersonalizationController.class);

    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    @Autowired
    private RePersoService rePersoService;

    @ResponseBody
    @RequestMapping(value="/rePerso", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> rePersoToken(@RequestBody String rePersoReq) {
        LOGGER.info("re-personaliztion request lands --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        Map<String, Object> rePersoResponse = null;
        RePersoTokenRequest  rePersoTokenRequestPojo = null;
        try {
            rePersoTokenRequestPojo = (RePersoTokenRequest) hCEControllerSupport.requestFormation(rePersoReq,
                    RePersoTokenRequest.class);
            rePersoResponse = rePersoService.rePersoTokenDataRequest(rePersoTokenRequestPojo);
        } catch (HCEActionException rePersoTokenException){
            LOGGER.error("Exception Occured in Enter DeviceRegistrationController->rePersoToken",rePersoTokenException);
            throw rePersoTokenException;
        }catch (Exception rePersoToken) {
            LOGGER.error(" Exception Occured in Enter DeviceRegistrationController->rePersoToken", rePersoToken);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.info("Enroll device for DAS request Ends at --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        return rePersoResponse;
    }
}
