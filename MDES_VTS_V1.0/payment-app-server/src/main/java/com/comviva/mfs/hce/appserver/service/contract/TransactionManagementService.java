package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.GetRegistrationCodeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTransactionHistoryRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTransactionsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.PushTransctionDetailsReq;

import java.util.Map;

/**
 * Created by Amgoth.madan on 5/10/2017.
 */
public interface TransactionManagementService {
    Map<String,Object>getTransactionHistoryVisa(GetTransactionHistoryRequest getTransactionHistoryRequest);
    Map<String,Object>pushTransctionDetails(PushTransctionDetailsReq pushTransctionDetailsReq);
    Map<String,Object> getTransactionsMasterCard(GetTransactionsRequest getTransactionsPojo);
    Map<String, Object> getRegistrationCode(GetRegistrationCodeReq getRegistrationCodeReq);
}
