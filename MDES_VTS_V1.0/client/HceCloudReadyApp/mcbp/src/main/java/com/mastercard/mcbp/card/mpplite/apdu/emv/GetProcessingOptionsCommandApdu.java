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
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.ConditionsOfUseNotSatisfied;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidCommandApdu;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidLc;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidP1P2;
import com.mastercard.mobile_api.utils.Utils;

/**
 * Support object to parse and retrieve data from the Command APDU Get Processing Options
 * <p/>
 * MCBP v1 implementation supports only two possible GPO C-APDU lengths / types.
 */
public class GetProcessingOptionsCommandApdu extends CommandApdu {
    /**
     * Expected P1 value
     */
    public static final byte EXPECTED_P1 = 0x00;

    /**
     * Expected P1 value
     */
    public static final byte EXPECTED_P2 = 0x00;

    /**
     * Expected P1 value
     */
    public static final byte EXPECTED_LE = 0x00;

    /**
     * GPO C-APDU Command Template Tag value (as part of the C-Data)
     */
    public static final byte COMMAND_TEMPLATE_TAG = (byte) 0x83;

    /**
     * The LC field of the GPO C-APDU
     */
    private final short mLc;

    /**
     * The Terminal Type, tag 9F35
     */
    private final byte mTerminalType;

    /**
     * The Terminal Risk Management Data (valid only for type 2 GPO C-APDU) 9F1D
     */
    private final byte[] mTerminalRiskManagementData;

    /**
     * The Terminal Type (valid only for type 2 GPO C-APDU)  tag 9F1A
     */
    private final byte[] mTerminalCountryCode;

    /**
     * The PDOL
     */
    private final byte[] mPdol;

    private static final int ONE_BYTE_OF_83_TAG = 1;

    private final DolValues mPdolData;

    /**
     * Initialize a Get Processing Options APDU based on the byte[] received from the NFC field
     */
    public GetProcessingOptionsCommandApdu(final byte[] apdu, final DolRequestList pdolList) {
        super(apdu);
        // Check the basic APDU fields
        validateApdu(apdu);

        this.mLc = (short) (apdu[Iso7816.LC_OFFSET] & 0XFF);
        if (mLc + 6 != apdu.length) {
            throw new InvalidLc("Invalid ComputeCC C-APDU length (does not match LC info)");
        }

        byte[] cData = new byte[mLc];
        System.arraycopy(apdu, Iso7816.C_DATA_OFFSET, cData, 0, mLc);

        if (cData[0] != COMMAND_TEMPLATE_TAG) {
            throw new ConditionsOfUseNotSatisfied("GPO C-APDU: Invalid C-DATA Tag");
        }

        final byte pdolDataTagLength = getPdolDataTagLength(apdu);
        final byte pdolDataOffset = (byte) (pdolDataTagLength + 1);
        final short pdolDataLength = (short) (cData[pdolDataTagLength] & 0XFF);

        // Value of pdol data length should always equals to the difference between command
        // length and pdol data tag length plus 1 byte of '83' tag
        if ((mLc - pdolDataLength) != (pdolDataTagLength + ONE_BYTE_OF_83_TAG)) {
            throw new InvalidLc("GPO C-APDU: Invalid C-DATA Length");
        }

        mPdol = new byte[pdolDataLength];
        System.arraycopy(cData, pdolDataOffset, mPdol, 0, pdolDataLength);

        mPdolData = DolValues.of(pdolList, mPdol);

        // Check if we can retrieve information on the PDOL length
        if (pdolList == null) {
            throw new InvalidLc("GPO C-APDU: Invalid PDOL list length");
        }

        // Check that the PDOL List we have received in the C-APDU matches the expected length
        // of what was requested. If not, we throw an exception.
        if (pdolList.getExpectedDolLength() != (cData[pdolDataTagLength] & 0XFF)) {
            throw new InvalidLc("GPO C-APDU: Invalid PDOL list length");
        }

        final byte[] terminalTypeValue = mPdolData.getValueByTag("9F35");

        mTerminalType = (terminalTypeValue == null ||
                         terminalTypeValue.length == 0) ? 0x00 : terminalTypeValue[0];
        mTerminalRiskManagementData = mPdolData.getValueByTag("9F1D");

        mTerminalCountryCode = mPdolData.getValueByTag("9F1A");

        // GPO.2.2
        if (Utils.isTerminalOffline(getTerminalType())) {
            throw new ConditionsOfUseNotSatisfied("Terminal is OffLine only");
        }
    }

    /**
     * Calculates the length of Tag present for PDOL data in command APDU.
     */
    private byte getPdolDataTagLength(final byte[] commandApdu) {
        if ((commandApdu[Iso7816.C_DATA_OFFSET + 1] & 0XFF) == 0X81) {
            return 2;
        } else if ((commandApdu[Iso7816.C_DATA_OFFSET + 1] & 0XFF) < 0X81) {
            return 1;
        }
        //The length in command APDU is invalid. We should never be here.
        throw new InvalidLc("GPO C-APDU: Invalid C-DATA Length");
    }

    /**
     * Validate the basic APDU field for consistency
     *
     * @param apdu The Command APDU as byte[]
     */
    private void validateApdu(final byte[] apdu) {
        if (this.getType() != Type.GET_PROCESSING_OPTIONS) {
            throw new InvalidCommandApdu("Expected a GPO C-APDU, found: " + this.getType());
        }

        if (this.getP1() != EXPECTED_P1 || this.getP2() != EXPECTED_P2) {
            throw new InvalidP1P2("Invalid P1 or P2 value: " + this.getP1() + ", " + this.getP2());
        }
        if (apdu[apdu.length - 1] != EXPECTED_LE) {
            throw new InvalidCommandApdu("Invalid LE value for the GPO C-APDU");
        }
    }

    /**
     * Get the LC value of this APDU. Possible values are @see{LC_VALUE_1} or @see{LC_VALUE_2} only
     *
     * @return the GPO C-APDU LC Value.
     */
    final public short getLc() {
        return mLc;
    }

    /**
     * Get the Terminal Type (valid for all GPO C-APDU)
     *
     * @return the Terminal Type in the GPO C-APDU
     */
    final public byte getTerminalType() {
        return mTerminalType;
    }

    /**
     * Get the Terminal Risk Management Data (valid only for type 2 GPO C-APDU)
     *
     * @return the Terminal Risk Management Data in the GPO C-APDU
     */
    final public byte[] getTerminalRiskManagementData() {
        return mTerminalRiskManagementData;
    }

    /**
     * Get the Terminal Country Code (valid only for type 2 GPO C-APDU)
     *
     * @return the Terminal Country Code in the GPO C-APDU
     */
    final public byte[] getTerminalCountryCode() {
        return mTerminalCountryCode;
    }

    /**
     * Get the Processing options Data Object List
     *
     * @return The value of the pdol as byte[]
     */
    final public byte[] getPdol() {
        return mPdol;
    }
}
