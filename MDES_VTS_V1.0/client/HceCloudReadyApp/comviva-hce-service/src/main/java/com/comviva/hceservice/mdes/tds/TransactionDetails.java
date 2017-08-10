package com.comviva.hceservice.mdes.tds;

import java.io.Serializable;

/**
 * Transaction Detail for a given token..
 */
public class TransactionDetails implements Serializable {
    private String tokenUniqueReference;
    private String recordId;
    private String transactionIdentifier;
    private String transactionType;
    private Number amount;
    private String currencyCode;
    private String authorizationStatus;
    private String transactionTimestamp;
    private String merchantName;
    private String merchantType;
    private String merchantPostalCode;

    /**
     * The Token for this transaction.
     * @return  Token Unique Reference
     */
    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    /**
     * Set Token Unique Reference.
     * @param tokenUniqueReference Token Unique Reference
     */
    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    /**
     * Returns Unique identifier for this transaction record. Opaque value.
     * @return Record ID
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * Set Record Id
     * @param recordId Record Id
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * Returns a unique identifier for the transaction that is used to match a transaction event on
     * the device (for example, a contactless tap, or a DSRP payment) to a transaction details record provided by the TDS.
     * @return Transaction Identifier
     */
    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    /**
     * Set Transaction Identifier.
     * @param transactionIdentifier Transaction Identifier
     */
    public void setTransactionIdentifier(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    /**
     * Returns transaction type. Must be one of:<br>
     *     <table>
     *         <tr>
     *             <th>Value</th>
     *             <th>Meaning</th>
     *         </tr>
     *         <tr>
     *             <td>PURCHASE</td>
     *             <td>Purchase transaction</td>
     *         </tr>
     *         <tr>
     *             <td>REFUND</td>
     *             <td>Refund transaction</td>
     *         </tr>
     *     </table>
     * @return Transaction Type
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Set transaction type.
     * @param transactionType Transaction Type
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * The transaction amount. Negative amounts indicate a refund. REFUND transaction types will always have a negative amount.
     * @return Transaction Amount
     */
    public Number getAmount() {
        return amount;
    }

    /**
     * Set Transaction Amount.
     * @param amount Transaction Amount
     */
    public void setAmount(Number amount) {
        this.amount = amount;
    }

    /**
     * Returns transaction currency.
     * @return Transaction Currency
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Set transaction currency
     * @param currencyCode transaction currency
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * The authorization status of the transaction. Must be one of:
     * <table>
     *     <tr>
     *         <td>Value</td>
     *         <td>Meaning</td>
     *     </tr>
     *     <tr>
     *         <td>AUTHORIZED</td>
     *         <td>Transaction has been authorized, pending to be cleared.</td>
     *     </tr>
     *     <tr>
     *         <td>DECLINED</td>
     *         <td>Transaction was declined.</td>
     *     </tr>
     *     <tr>
     *         <td>CLEARED</td>
     *         <td>Transaction has been cleared.</td>
     *     </tr>
     *     <tr>
     *         <td>REVERSED</td>
     *         <td>Transaction has been reversed.</td>
     *     </tr>
     * </table>
     * @return
     */
    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    /**
     * Set authorization status
     * @param authorizationStatus authorization status
     */
    public void setAuthorizationStatus(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    /**
     * Returns the date/time when the transaction occurred.
     * @return Transaction Time
     */
    public String getTransactionTimestamp() {
        return transactionTimestamp;
    }

    /**
     * Set Transaction Time.
     * @param transactionTimestamp Transaction Time
     */
    public void setTransactionTimestamp(String transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    /**
     * Returns Merchant Name.
     * @return Merchant Name
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Set Merchant Name.
     * @param merchantName Merchant Name
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    /**
     * Returns merchant’s type of business or service. It is Merchant Category Code (MCC).
     * @return Merchant Type
     */
    public String getMerchantType() {
        return merchantType;
    }

    /**
     * Set Merchant Type
     * @param merchantType Merchant Type
     */
    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

    /**
     * Returns postal code (for example, zipcode in the U.S.) of the merchant.
     * @return Postal Code
     */
    public String getMerchantPostalCode() {
        return merchantPostalCode;
    }

    /**
     * Set postal code of the merchant.
     * @param merchantPostalCode Postal Code
     */
    public void setMerchantPostalCode(String merchantPostalCode) {
        this.merchantPostalCode = merchantPostalCode;
    }

}
