package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTokenUpdatedReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.TDSRegistrationReq;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 06-08-2018.
 */

@RestController
@RequestMapping("/digitization/1/0")
public class NotifyTokenController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardManagementController.class);

    @Autowired
    private CardDetailService cardDetailService;
    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    public NotifyTokenController(CardDetailService cardDetailService ) {
        this.cardDetailService = cardDetailService;
    }

    @ResponseBody
    @RequestMapping(value = "/notifyTokenUpdated", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map notifyTokenUpdated(@RequestBody String notifyTokenUpdatedReq) {
        Map <String, Object> notifyTokenUpdatedResp = null ;
        NotifyTokenUpdatedReq notifyTokenUpdatedReqPojo = null ;
        try{
            notifyTokenUpdatedReqPojo =(NotifyTokenUpdatedReq) hCEControllerSupport.requestFormation(notifyTokenUpdatedReq ,NotifyTokenUpdatedReq.class);
            notifyTokenUpdatedResp = cardDetailService.notifyTokenUpdated(notifyTokenUpdatedReqPojo);
        }catch (HCEActionException notifyTokenUpdatedHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->notifyTokenUpdated",notifyTokenUpdatedHceActionException);
            throw notifyTokenUpdatedHceActionException;

        }catch (Exception notifyTokenUpdatedExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->registerWithTDS", notifyTokenUpdatedExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return notifyTokenUpdatedResp;
    }
}

