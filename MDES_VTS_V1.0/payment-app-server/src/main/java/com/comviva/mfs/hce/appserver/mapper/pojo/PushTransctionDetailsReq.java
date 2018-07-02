package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PushTransctionDetailsReq {
private String requestId;
private List<Transactions> transactions;

    public PushTransctionDetailsReq(String requestId, List<Transactions> transactions) {
        this.requestId = requestId;
        this.transactions = transactions;
    }

    public PushTransctionDetailsReq() {

    }


}
