package com.comviva.mfs.hce.appserver.mapper.pojo;

import java.util.List;

public class PushTransctionDetailsReq {
private String requestId;
private List<Transactions> transactions;

    public PushTransctionDetailsReq(String requestId, List<Transactions> transactions) {
        this.requestId = requestId;
        this.transactions = transactions;
    } public PushTransctionDetailsReq() {

    }

    public String getRequestId() {
        return requestId;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }
}
