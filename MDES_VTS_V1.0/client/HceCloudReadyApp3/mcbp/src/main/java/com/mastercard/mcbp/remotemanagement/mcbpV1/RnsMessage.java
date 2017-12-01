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

package com.mastercard.mcbp.remotemanagement.mcbpV1;

import com.mastercard.mobile_api.bytes.ByteArray;
import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
class RnsMessage {

    /**
     * Message length is Fixed to 54 bytes as defined in MCBP1.0
     */
    private static final int LENGTH_MESSAGE   = 54;
    private static final int LENGTH_RNS_MESSAGE_ID = 14;
    private static final int LENGTH_SESSION = 32;
    private static final int LENGTH_MAC       = 8;

    public static final int OFFSET_VERSION_CONTROL = (byte) 0;

    @JSON (name = "versionControl")
    private byte versionControl;

    @JSON (name = "RNS_Message_ID")
    private ByteArray rnsMessageId;

    @JSON (name = "E_SESSION")
    private ByteArray encryptedSession;

    @JSON (name = "MAC")
    private ByteArray mac;

    public RnsMessage(ByteArray rnsData) {

        if (rnsData == null || rnsData.getLength() != LENGTH_MESSAGE) {
            throw new IllegalArgumentException();
        }

        rnsMessageId = rnsData.copyOfRange(0, LENGTH_RNS_MESSAGE_ID);
        encryptedSession = rnsData.copyOfRange(LENGTH_RNS_MESSAGE_ID, LENGTH_RNS_MESSAGE_ID + LENGTH_SESSION);
        mac = rnsData.copyOfRange(LENGTH_RNS_MESSAGE_ID + LENGTH_SESSION, LENGTH_RNS_MESSAGE_ID
                + LENGTH_SESSION + LENGTH_MAC);
    }

    public ByteArray getRnsMessageId() {
        return rnsMessageId;
    }

    public ByteArray getEncryptedSession() {
        return encryptedSession;
    }

    public ByteArray getMac() {
        return mac;
    }

}
