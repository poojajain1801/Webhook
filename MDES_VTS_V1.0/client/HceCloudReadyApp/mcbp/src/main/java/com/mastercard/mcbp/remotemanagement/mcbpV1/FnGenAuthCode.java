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

import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class FnGenAuthCode {
    /**
     * Cms Mpa Id byte array
     */
    private ByteArray cmsMpaId;
    /**
     * Session Id byte array
     */
    private ByteArray sessionId;
    /**
     * Device FingerPrint byte array
     */
    private ByteArray deviceFingerPrint;
    /**
     * Mcbp Crypto Service
     */
    private CryptoService crypto;

    public FnGenAuthCode withCmsMpaId(ByteArray cmsMpaId) {
        this.cmsMpaId = cmsMpaId;
        return this;
    }

    public FnGenAuthCode withSessionId(ByteArray sid) {
        this.sessionId = sid;
        return this;
    }

    public FnGenAuthCode withDeviceFingerPrint(ByteArray dfp) {
        this.deviceFingerPrint = dfp;
        return this;
    }

    public FnGenAuthCode withMcbpCryptoService(CryptoService cryptoService) {
        this.crypto = cryptoService;
        return this;
    }

    /**
     * @return tag
     * @throws McbpCryptoException
     */
    public ByteArray generateCode() throws McbpCryptoException {
        // Combine byte arrays
        ByteArray combine = ByteArray.of(cmsMpaId)
                .append(sessionId).append(deviceFingerPrint);
        // Hash the result
        return crypto.sha256(combine);
    }

}
