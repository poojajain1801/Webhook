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

package com.mastercard.mcbp.hce;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;


@TargetApi(Build.VERSION_CODES.KITKAT)
public abstract class AndroidHceService extends HostApduService {
    public final static String ACTION_FIRST_TAP = "firstTap";
    public final static String PARAM_AMOUNT = "amount";
    public final static String PARAM_CURRENCY = "currency";
    public final static String PARAM_CURRENT = "current_card_used";

    /**
     * Logger module
     **/
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance ().getLogger (this);

    @Override
    public void onCreate () {
        super.onCreate ();
    }

    @Override
    public byte[] processCommandApdu (byte[] bytes, Bundle bundle) {
        return processApdu (bytes);
    }

    @Override
    public void onDeactivated (int reason) {
        mLogger.d ("onDeactivated received with reason: " + reason);
        if (reason == DEACTIVATION_LINK_LOSS) {
            this.processOnDeactivated ();
        }
        // We do not handle the de-activation in case of a new Select Command
        // (i.e. DEACTIVATION_DESELECTED)
    }

    /**
     * Initialize
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    protected abstract void init ();

    /**
     * Process a C-APDU and return a R-APDU
     *
     * @param bytes byte array data for processApdu.
     * @return byte array of processApdu response.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    protected abstract byte[] processApdu (byte[] bytes);

    /**
     * Utility function to process the NFC deactivation signal handled by the
     * HCE service
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    protected abstract void processOnDeactivated ();
}
