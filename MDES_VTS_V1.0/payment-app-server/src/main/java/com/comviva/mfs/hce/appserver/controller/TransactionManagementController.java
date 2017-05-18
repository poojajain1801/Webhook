package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Madan amgoth on 5/10/2017.
 */
@RestController
@RequestMapping("/api/transaction")
public class TransactionManagementController {

    @Autowired
    private TransactionManagementService transactionManagementService;

    public TransactionManagementController(TransactionManagementService transactionManagementService) {
        this.transactionManagementService=transactionManagementService;
    }

    @ResponseBody
    @RequestMapping(value = "/getTransactionHistory",method = RequestMethod.POST)
    public Map<String,Object>getTransactionHistory(@RequestBody GetTransactionHistoryRequest getTransactionHistoryRequest){
      return transactionManagementService.getTransactionHistory(getTransactionHistoryRequest);
    }

}