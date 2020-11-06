/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
