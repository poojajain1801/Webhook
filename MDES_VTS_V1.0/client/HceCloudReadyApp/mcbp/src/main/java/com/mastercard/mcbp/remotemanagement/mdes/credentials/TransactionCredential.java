/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mcbp.remotemanagement.mdes.credentials;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import flexjson.JSON;

public class TransactionCredential {
    /**
     * Application Transaction Counter unique for this transaction using this set of keys.
     * Max Length: 5
     */
    @JSON(name = "atc")
    public int atc;
    /**
     * The ICC dynamic number for this transaction.
     * Encrypted by the Mobile Data Encryption Key.Max Length: 48
     */
    @JSON(name = "idn")
    public ByteArray idn;
    /**
     * Session key used for mobile device authentication (MD) for contactless transactions.
     * Encrypted by the Mobile Data Encryption Key. Max Length: 48
     */
    @JSON(name = "contactlessMdSessionKey")
    public ByteArray contactlessMdSessionKey;
    /**
     * Single use key used for user and mobile device authentication (UMD) for contactless transactions.
     * Encrypted by the Mobile Data Encryption Key. Max Length: 48
     */
    @JSON(name = "contactlessUmdSingleUseKey")
    public ByteArray contactlessUmdSingleUseKey;
    /**
     * Session key used for mobile device authentication (MD) for DSRP transactions.
     * Encrypted by the Mobile Data Encryption Key. Max Length: 48
     */
    @JSON(name = "dsrpMdSessionKey")
    public ByteArray dsrpMdSessionKey;
    /**
     * Single use key used for user and mobile device authentication (UMD) for DSRP transactions.
     * Encrypted by the Mobile Data Encryption Key. Max Length: 48
     */
    @JSON(name = "dsrpUmdSingleUseKey")
    public ByteArray dsrpUmdSingleUseKey;


    public static TransactionCredential valueOf(final byte[] content) {
        return new JsonUtils<TransactionCredential>(TransactionCredential.class).valueOf(content);
    }
}
