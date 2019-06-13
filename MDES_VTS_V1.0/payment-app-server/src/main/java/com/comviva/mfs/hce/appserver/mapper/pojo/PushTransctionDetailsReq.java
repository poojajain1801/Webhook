package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class PushTransctionDetailsReq {
private String requestId;
private String responseHost;
private List<Transactions> transactions;


    public PushTransctionDetailsReq(String requestId, String responseHost, List<Transactions> transactions) {
        this.requestId = requestId;
        this.responseHost = responseHost;
        this.transactions = transactions;
    }

    public PushTransctionDetailsReq() {

    }

    public String getRequestId() {
        return requestId;
    }

    public List<Transactions> getTransactions() {
        return (transactions);
    }
}
