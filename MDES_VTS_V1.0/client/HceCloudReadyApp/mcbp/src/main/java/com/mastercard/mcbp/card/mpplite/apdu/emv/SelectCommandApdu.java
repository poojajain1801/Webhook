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

package com.mastercard.mcbp.card.mpplite.apdu.emv;

import com.mastercard.mcbp.card.mpplite.apdu.CommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidCommandApdu;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidLc;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidP1P2;

/**
 * The Class Select Command APDU
 * */
public final class SelectCommandApdu extends CommandApdu {
    /**
     * Expected P1 value
     * */
    public static final byte EXPECTED_P1 = 0x04;

    /**
     * Expected P1 value
     * */
    public static final byte EXPECTED_P2 = 0x00;

    /**
     * Expected P1 value
     * */
    public static final byte EXPECTED_LE = 0x00;

    /**
     * Select C-APDU LC value for the short version of the command
     */
    public static final byte MIN_LC = 0x05;

    /**
     * Select C-APDU LC value for the long version of the command
     */
    public static final byte MAX_LC = 0x10;

    /**
     * The File Name (aka AID)
     */
    private final byte[] mFileName;

    /**
     * Initialize a Get Processing Options APDU based on the byte[] received from the NFC field
     */
    public SelectCommandApdu(final byte[] apdu) {
        super(apdu);
        if (this.getType() != Type.SELECT) {
            throw new InvalidCommandApdu("Expected a SELECT C-APDU, found: " + this.getType());
        }
        if (this.getP1() != EXPECTED_P1 || this.getP2() != EXPECTED_P2) {
            throw new InvalidP1P2("Invalid P1 or P2 value: " + this.getP1() + ", " + this.getP2());
        }
        if (apdu[apdu.length - 1] != EXPECTED_LE) {
            throw new InvalidCommandApdu("Invalid LE value for the SELECT C-APDU");
        }
        final int lc = (0x000000FF & apdu[Iso7816.LC_OFFSET]);  // We need to be careful that byte is signed
        if (lc < MIN_LC || lc > MAX_LC) {
            throw new InvalidLc("Invalid LC for a SELECT C-APDU: " + lc);
        }
        if (lc + 6 != apdu.length) {
            throw new InvalidLc("Invalid SELECT APDU length (does not match LC info)");
        }
        mFileName = new byte[lc];
        System.arraycopy(apdu, Iso7816.C_DATA_OFFSET, mFileName, 0, lc);
    }

    /**
     * Get the File Name (aka AID) as specified in the SELECT C-APDU
     *
     * @return the AID as byte[]
     * */
    final public byte[] getFileName() {
        return mFileName;
    }
}
