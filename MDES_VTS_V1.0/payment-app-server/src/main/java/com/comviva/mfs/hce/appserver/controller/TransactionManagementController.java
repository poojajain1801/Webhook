package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Madan amgoth on 5/10/2017.
 */
@RestController
@RequestMapping("/api/transaction")
public class TransactionManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagementController.class);

    @Autowired
    private TransactionManagementService transactionManagementService;
    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    public TransactionManagementController(TransactionManagementService transactionManagementService) {
        this.transactionManagementService=transactionManagementService;
    }

    @ResponseBody
    @RequestMapping(value = "/getTransactionHistory",method = RequestMethod.POST)
    public Map<String,Object>getTransactionHistory(@RequestBody GetTransactionHistoryRequest getTransactionHistoryRequest){
        LOGGER.debug("Enter TransactionManagementController->getTransactionHistory");
      Map<String,Object> getTransctionHistoryResp =  transactionManagementService.getTransactionHistory(getTransactionHistoryRequest);
        LOGGER.debug("Extit TransactionManagementController->getTransactionHistory");
        return getTransctionHistoryResp;
    }

    @ResponseBody
    @RequestMapping(value = "/pushTransactionDetails",method = RequestMethod.POST)
    public Map<String,Object>pushTransctionDetails(String pushTxnDetailsReq ) {
        LOGGER.debug("Enter TransactionManagementController->pushTransctionDetails");
        Map<String, Object> pushTransctionDetailsResponse = null;
        PushTransctionDetailsReq pushTransctionDetailsReq = null;
        try {
            pushTransctionDetailsReq = (PushTransctionDetailsReq) hCEControllerSupport.requestFormation(pushTxnDetailsReq, PushTransctionDetailsReq.class);
            pushTransctionDetailsResponse = transactionManagementService.pushTransctionDetails(pushTransctionDetailsReq);
        } catch (HCEValidationException enrollPanRequestValidation) {
            LOGGER.error("Exception Occured in CardManagementController->enrollPan", enrollPanRequestValidation);
            throw enrollPanRequestValidation;
        } catch (HCEActionException enrollPanHceActionException) {
            LOGGER.error("Exception Occured in CardManagementController->enrollPan", enrollPanHceActionException);
            throw enrollPanHceActionException;
        } catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return pushTransctionDetailsResponse;
    }

}