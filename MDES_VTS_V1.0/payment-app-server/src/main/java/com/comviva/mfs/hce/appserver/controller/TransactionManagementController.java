package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetRegistrationCodeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTransactionHistoryRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTransactionsRequest;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/getTransactions", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> getTransactionsMasterCard(@RequestBody String getTransactionsRequest) {
        Map <String, Object> getTransactionsResp=null ;
        GetTransactionsRequest getTransactionsPojo=null ;
        try{
            getTransactionsPojo = (GetTransactionsRequest)hCEControllerSupport.requestFormation(getTransactionsRequest,GetTransactionsRequest.class);
            getTransactionsResp = transactionManagementService.getTransactionsMasterCard(getTransactionsPojo);
        }catch (HCEActionException getTransactionsHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getTransactions",getTransactionsHceActionException);
            throw getTransactionsHceActionException;
        }catch (Exception getTransactionsExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getTransactions", getTransactionsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getTransactionsResp;
    }

    @ResponseBody
    @ServiceFlowStep("paymentApp")
    @RequestMapping(value = "/getTransactionHistory",method = RequestMethod.POST)
    public Map<String,Object>getTransactionHistoryVisa(@RequestBody String getTransactionHistoryRequest){
        Map<String, Object> getTransctionHistoryResp = null;
        GetTransactionHistoryRequest getTransactionHistoryRequestPojo = null;
        try {
             getTransactionHistoryRequestPojo = (GetTransactionHistoryRequest) hCEControllerSupport.requestFormation(getTransactionHistoryRequest, GetTransactionHistoryRequest.class);
            LOGGER.debug("Enter TransactionManagementController->getTransactionHistory");
            getTransctionHistoryResp = transactionManagementService.getTransactionHistoryVisa(getTransactionHistoryRequestPojo);
            LOGGER.debug("Extit TransactionManagementController->getTransactionHistory");
        }catch (HCEActionException addCardHceActionException){
            LOGGER.error("Exception Occured in TransactionManagementController->getTransactionHistory",addCardHceActionException);
            throw addCardHceActionException;
        }catch (Exception addCardExcetption) {
            LOGGER.error(" Exception Occured in TransactionManagementController->getTransactionHistory", addCardExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getTransctionHistoryResp;
    }


    @ResponseBody
    @RequestMapping(value = "/registerWithTDS", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> registerWithTDS(@RequestBody String getRegistrationCodeReq) {
        Map <String, Object> getTransactionsResp=null ;
        GetRegistrationCodeReq getRegistrationCodeReqPojo=null ;
        try{
            getRegistrationCodeReqPojo = (GetRegistrationCodeReq) hCEControllerSupport.requestFormation(getRegistrationCodeReq,GetRegistrationCodeReq.class);
            getTransactionsResp = transactionManagementService.getRegistrationCode(getRegistrationCodeReqPojo);
        }catch (HCEActionException getTransactionsHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getTransactions",getTransactionsHceActionException);
            throw getTransactionsHceActionException;
        }catch (Exception getTransactionsExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getTransactions", getTransactionsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getTransactionsResp;
    }

}