package com.comviva.mfs.hce.appserver.mapper.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import lombok.Setter;

/**
 * GetTransactionHistoryRequest.
 * Created by Amgoth.madan on 5/10/2017.
 */

public class GetTransactionHistoryRequest {
    private String vProvisionedTokenID;
    private String Count;

    public GetTransactionHistoryRequest(String vProvisionedTokenID, String count) {
        this.vProvisionedTokenID = vProvisionedTokenID;
        Count = count;
    }

    public GetTransactionHistoryRequest() {
    }

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public String getCount() {
        return Count;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public void setCount(String count) {
        Count = count;
    }
}