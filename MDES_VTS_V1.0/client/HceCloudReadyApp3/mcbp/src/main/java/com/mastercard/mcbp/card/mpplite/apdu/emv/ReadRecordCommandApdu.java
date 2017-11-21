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

import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.CommandApdu;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidP1P2;

/**
 * Support object to parse and retrieve data from the Command APDU Read Record
 * */
public class ReadRecordCommandApdu extends CommandApdu {
    /**
     * Offset of the LE Field in a Get Processing Options APDU
     * */
    public static int LE_OFFSET = 4;

    /**
     * Command APDU constructor. It is called when a new command apdu is received
     * */
    public ReadRecordCommandApdu(byte[] apdu) {
        super(apdu);
        if (this.getType() != Type.READ_RECORD) {
            throw new InvalidCommandApdu("Expected a READ_RECORD APDU, found: " + this.getType());
        }
        if (this.getP1() == 0x00) {
            throw new InvalidP1P2("Invalid Record Number - invalid P1: " + this.getP1());
        }
        if ((this.getP2() & 0x07) != 0x04) {
            throw new InvalidP1P2("SFI value malformed - invalid P2: " + this.getP2());
        }
        if (apdu[LE_OFFSET] != 0x00) {
            throw new InvalidCommandApdu("Invalid LE field: " + apdu[LE_OFFSET]);
        }
    }

    /**
     * Gets the record number.
     *
     * @return the record number
     */
    public final byte getRecordNumber() {
        return this.getP1();
    }

    /**
     * Gets the sfi number.
     *
     * @return the sfi number
     */
    public final byte getSfiNumber() {
        return (byte) (this.getP2() >>> 3);
    }
}
