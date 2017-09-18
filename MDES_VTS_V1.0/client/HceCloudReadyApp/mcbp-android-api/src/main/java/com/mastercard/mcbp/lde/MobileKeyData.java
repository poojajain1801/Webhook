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

package com.mastercard.mcbp.lde;

/**
 * Utility class for caching Mobile keys attributes
 */
class MobileKeyData {
    /**
     * Key data
     */
    final private byte[] mKeyValue;
    /**
     * Digitized card id
     */
    final private String mDigitizedCardId;
    /**
     * Mobile keyset id - The identifier for the Mobile Keys used to manage the CLOUD credentials,
     * as assigned by MDES upon successful registration.
     */
    final private String mMobileKeySetId;
    /**
     * Key type - transport key, mac key, data encryption key
     */
    final private String mKeyType;

    protected MobileKeyData(final String cardId, final String mobileKeySetId,
                            final String keyType, final byte[] keyValue) {
        mDigitizedCardId = cardId;
        mMobileKeySetId = mobileKeySetId;
        mKeyType = keyType;
        mKeyValue = keyValue;
    }

    public byte[] getKeyValue() {
        return mKeyValue;
    }

    public String getDigitizedCardId() {
        return mDigitizedCardId;
    }

    public String getMobileKeySetId() {
        return mMobileKeySetId;
    }

    public String getKeyType() {
        return mKeyType;
    }

}
