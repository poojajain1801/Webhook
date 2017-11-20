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

package com.mastercard.mcbp.card.cvm;

import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

public class PinValidator implements ChValidator {
    /**
     * Pin Card Listener
     */
    private PinCardListener mPinCardListener;

    /**
     * Flag to keep track of the user authentication
     */
    private boolean mIsAuthenticated = false;

    /**
     * McbpLogger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);
    
    @Override
    public final void authenticate(final ByteArray key, final ChValidatorListener listener) {
        // We have requested a new PIN authentication, let's set the flag is Authentication to false
        // until the PIN is entered
        mIsAuthenticated = false;
        mPinCardListener.onPinRequired(new PinListener() {
            @Override
            public void pinEntered(final ByteArray pin) {
                try {
                    final CryptoService crypto = CryptoServiceFactory.getDefaultCryptoService();
                    final ByteArray sessionKey = crypto.deriveSessionKey(key, pin);
                    mIsAuthenticated = true;
                    listener.onSessionKeyReady(sessionKey);
                } catch (McbpCryptoException e) {
                    // Something went wrong and we could not unlock the keys, the listener will not
                    // be called and we can safely ignore it...
                    mLogger.d(e.getMessage());
                }finally {
                    // Clearing sensitive data
                    Utils.clearByteArray(pin);
                    Utils.clearByteArray(key);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDescription() {
        return "PIN Validator";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isAuthenticated() {
        return mIsAuthenticated;
    }

    @Override
    public void notifyTransactionCompleted() {
        mIsAuthenticated = false;
    }

    /**
     * Registers a pin listener
     */
    public final void setPinListener(PinCardListener pinListener) {
        this.mPinCardListener = pinListener;
    }
}
