package com.comviva.hceservice.responseobject.transactionhistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Transaction Detail for a given token..
 */
public class TransactionDetails implements Serializable {

    @SerializedName("vProvisionedTokenID")
    @Expose
    private String vProvisionedTokenID;
    @SerializedName("tokenUniqueReference")
    @Expose
    private String tokenUniqueReference;
    @SerializedName("recordId")
    @Expose
    private String recordId;
    @SerializedName("transactionType")
    @Expose
    private String transactionType;
    @SerializedName(value = "transactionDate", alternate = {"transactionTimestamp"})
    @Expose
    private String transactionTimestamp;
    @SerializedName("merchantName")
    @Expose
    private String merchantName;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("transactionStatus")
    @Expose
    private int transactionStatus;
    @SerializedName("merchantZipCode")
    @Expose
    private String merchantZipCode;
    @SerializedName("atc")
    @Expose
    private String atc;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("merchantPostalCode")
    @Expose
    private String merchantPostalCode;
    @SerializedName(value = "transactionIdentifier", alternate = {"transactionID"})
    @Expose
    private String transactionIdentifier;
    @SerializedName("merchantType")
    @Expose
    private String merchantType;
    @SerializedName("authorizationStatus")
    @Expose
    private String authorizationStatus;


    public String getTokenUniqueReference() {

        return tokenUniqueReference;
    }


    public String getRecordId() {

        return recordId;
    }


    public String getMerchantType() {

        return merchantType;
    }


    public String getAuthorizationStatus() {

        return authorizationStatus;
    }


    public String getvProvisionedTokenID() {

        return vProvisionedTokenID;
    }


    public void setvProvisionedTokenID(String vProvisionedTokenID) {

        this.vProvisionedTokenID = vProvisionedTokenID;
    }


    public String getTransactionIdentifier() {

        return transactionIdentifier;
    }


    public void setTransactionIdentifier(String transactionIdentifier) {

        this.transactionIdentifier = transactionIdentifier;
    }


    public String getTransactionType() {

        return transactionType;
    }


    public void setTransactionType(String transactionType) {

        this.transactionType = transactionType;
    }


    public String getTransactionTimestamp() {

        return transactionTimestamp;
    }


    public void setTransactionTimestamp(String transactionTimestamp) {

        this.transactionTimestamp = transactionTimestamp;
    }


    public String getMerchantName() {

        return merchantName;
    }


    public void setMerchantName(String merchantName) {

        this.merchantName = merchantName;
    }


    public String getCurrencyCode() {

        return currencyCode;
    }


    public void setCurrencyCode(String currencyCode) {

        this.currencyCode = currencyCode;
    }


    public int getTransactionStatus() {

        return transactionStatus;
    }


    public void setTransactionStatus(int transactionStatus) {

        this.transactionStatus = transactionStatus;
    }


    public String getMerchantZipCode() {

        return merchantZipCode;
    }


    public void setMerchantZipCode(String merchantZipCode) {

        this.merchantZipCode = merchantZipCode;
    }


    public String getAtc() {

        return atc;
    }


    public void setAtc(String atc) {

        this.atc = atc;
    }


    public String getAmount() {

        return amount;
    }


    public void setAmount(String amount) {

        this.amount = amount;
    }


    public String getMerchantPostalCode() {

        return merchantPostalCode;
    }


    public void setMerchantPostalCode(String merchantPostalCode) {

        this.merchantPostalCode = merchantPostalCode;
    }
}
