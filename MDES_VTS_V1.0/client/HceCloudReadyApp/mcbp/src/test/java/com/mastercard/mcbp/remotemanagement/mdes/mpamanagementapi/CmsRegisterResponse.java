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

/***
 * Utility data structure to mock up the CMS-D response to the register
 */
class CmsRegisterResponse {
    final private String mRemoteManagementUrl;
    final private ByteArray mEncryptedTransportKey;
    final private ByteArray mEncryptedMacKey;
    final private ByteArray mEncryptedDataEncryptionKey;

    final private String mMobileKeySetId;


    public String getRemoteManagementUrl() {
        return mRemoteManagementUrl;
    }

    public CmsRegisterResponse(final String remoteManagementUrl,
                               final ByteArray encryptedTransportKey,
                               final ByteArray encryptedMacKey,
                               final ByteArray encryptedDataEncryptionKey,
                               final String mobileKeySetId) {
        mRemoteManagementUrl = remoteManagementUrl;
        mEncryptedTransportKey = encryptedTransportKey;
        mEncryptedMacKey = encryptedMacKey;
        mEncryptedDataEncryptionKey = encryptedDataEncryptionKey;
        mMobileKeySetId = mobileKeySetId;
    }

    public ByteArray getEncryptedTransportKey() {

        return mEncryptedTransportKey;
    }

    public ByteArray getEncryptedMacKey() {
        return mEncryptedMacKey;
    }

    public ByteArray getEncryptedDataEncryptionKey() {
        return mEncryptedDataEncryptionKey;
    }

    public String getMobileKeySetId() {
        return mMobileKeySetId;
    }
}