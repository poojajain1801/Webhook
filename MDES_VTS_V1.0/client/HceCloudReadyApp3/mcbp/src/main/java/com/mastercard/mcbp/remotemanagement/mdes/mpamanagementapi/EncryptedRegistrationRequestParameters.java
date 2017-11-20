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

package com.mastercard.mcbp.remotemanagement.mdes.mpamanagementapi;

import com.mastercard.mobile_api.bytes.ByteArray;



/**
 * Utility class to store the encrypted mobile pin and random generated key
 */
public class EncryptedRegistrationRequestParameters {
    /***
     * The Random Generated Key encrypted with the public key of the CMS-D
     */
    final ByteArray mEncryptedRandomGeneratedKey;
    /**
     * The encrypted mobile PIN block using the RGK as key
     * */
    final ByteArray mEncryptedMobilePinBlock;

    /**
     * Default constructor. Both encrypted random RGK and mobile pin block must be provided
     *
     * @param encryptedMobilePinBlock The encrypted mobile PIN block (the encryption is done using
     *                                the RGK)
     * @param encryptedRandomGeneratedKey the random generated key in encrypted format using the
     *                                    public key of the CMS-D
     *
     * */
    public EncryptedRegistrationRequestParameters(final ByteArray encryptedRandomGeneratedKey,
                                                  final ByteArray encryptedMobilePinBlock) {
        mEncryptedRandomGeneratedKey = encryptedRandomGeneratedKey;
        mEncryptedMobilePinBlock = encryptedMobilePinBlock;
    }

    /**
     * Get the Random Generated Key encrypted with the public key certificate
     * */
    public ByteArray getEncryptedRandomGeneratedKey() {
        return mEncryptedRandomGeneratedKey;
    }

    /**
     * Get the encrypted PIN block (the RGK is typically used for this encryption
     * */
    public ByteArray getEncryptedMobilePinBlock() {
        return mEncryptedMobilePinBlock;
    }
}
