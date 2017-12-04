package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by Amgoth.madan on 5/10/2017.
 */
public interface TransactionManagementService {
    Map<String,Object>getTransactionHistory(GetTransactionHistoryRequest getTransactionHistoryRequest);
}
