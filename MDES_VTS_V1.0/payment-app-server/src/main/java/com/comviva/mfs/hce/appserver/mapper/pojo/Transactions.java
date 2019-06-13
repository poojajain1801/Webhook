package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transactions {
    private String tokenUniqueReference;
    private String recordId;
    private String transactionIdentifier;
    private String transactionType;
    private String amount;
    private String currencyCode;
    private String authorizationStatus;
    private String transactionTimestamp;
    private String merchantName;
    private String merchantType;
    private String merchantPostalCode;

    public Transactions(String tokenUniqueReference, String recordId, String transactionIdentifier, String transactionType, String amount, String currencyCode, String authorizationStatus, String transactionTimestamp, String merchantName, String merchantType, String merchantPostalCode) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.recordId = recordId;
        this.transactionIdentifier = transactionIdentifier;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.authorizationStatus = authorizationStatus;
        this.transactionTimestamp = transactionTimestamp;
        this.merchantName = merchantName;
        this.merchantType = merchantType;
        this.merchantPostalCode = merchantPostalCode;
    }

    public Transactions() {

    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public String getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public String getMerchantPostalCode() {
        return merchantPostalCode;
    }
}
