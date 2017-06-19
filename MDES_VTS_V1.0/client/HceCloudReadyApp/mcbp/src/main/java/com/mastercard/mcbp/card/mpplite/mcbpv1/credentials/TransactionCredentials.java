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

package com.mastercard.mcbp.card.mpplite.mcbpv1.credentials;

import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

/**
 * Data Structure used by the MPP Lite to receive and use Transaction Credentials
 * */
public final class TransactionCredentials {
    /**
     * User and Mobile Device Session Key
     */
    private final ByteArray mUmdSessionKey;
    /**
     * Mobile Device Session Key
     */
    private final ByteArray mMdSessionKey;
    /**
     * Application Transaction Counter
     */
    private final ByteArray mAtc;
    /**
     *ICC Dynamic Number
     */
    private final ByteArray mIdn;

    /***
     * Constructor
     * @param umdSessionKey UMD Session Key (must have been unlocked to be valid)
     * @param mdSessionKey MD Session Key
     * @param atc The Application Transaction Counter
     * @param idn The ICC Dynamic Number
     */
    public TransactionCredentials(ByteArray umdSessionKey,
                                  ByteArray mdSessionKey,
                                  ByteArray atc,
                                  ByteArray idn) {
        final CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        if (umdSessionKey == null) {
            this.mUmdSessionKey = cryptoService.getRandomByteArray(16);
        } else {
            this.mUmdSessionKey = umdSessionKey;
        }
        if (mdSessionKey == null) {
            this.mMdSessionKey = cryptoService.getRandomByteArray(16);
        } else {
            this.mMdSessionKey = mdSessionKey;
        }
        if (idn == null) {
            this.mIdn = cryptoService.getRandomByteArray(8);
        } else {
            this.mIdn = idn;
        }
        if (atc == null) {
            this.mAtc = ByteArray.of((char)0x0001);
        } else {
            this.mAtc = atc;
        }

    }

    /**
     * Constructor
     * */
    public TransactionCredentials() {
        CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        this.mUmdSessionKey = cryptoService.getRandomByteArray(16);
        this.mMdSessionKey = cryptoService.getRandomByteArray(16);
        this.mIdn = cryptoService.getRandomByteArray(8);
        this.mAtc = ByteArray.of((char)0x0001);
    }

    /***
     * Get the MD Session Key
     * @return Return the MD Session Key
     */
    public final ByteArray getMdSessionKey() { return mMdSessionKey; }

    /***
     * Get the IDN
     * @return Return the IDN
     */
    public final ByteArray getIdn() { return mIdn; }

    /***
     * Get the ATC
     * @return Return the ATC
     */
    public final ByteArray getAtc() { return mAtc; }

    /***
     * Get the UMD Session Key
     * @return Return the UMD Session Key
     */
    public final ByteArray getUmdSessionKey() { return mUmdSessionKey; }

    /**
     * wipe all sensitive data.
     */
    public final void wipe() {
        Utils.clearByteArray(mUmdSessionKey);
        Utils.clearByteArray(mMdSessionKey);
        Utils.clearByteArray(mAtc);
        Utils.clearByteArray(mIdn);
    }
}
