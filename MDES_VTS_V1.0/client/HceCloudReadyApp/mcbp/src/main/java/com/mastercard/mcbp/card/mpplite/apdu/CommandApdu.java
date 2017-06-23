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
package com.mastercard.mcbp.card.mpplite.apdu;

import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidCla;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidCommandApdu;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidIns;

/**
 * The Class Command Apdu.
 */
public class CommandApdu {
    /**
     * Define the APDU types supported by this implementation
     * */
    public enum Type {
        UNDEFINED,
        SELECT,
        GET_PROCESSING_OPTIONS,
        READ_RECORD,
        GENERATE_AC,
        COMPUTE_CRYPTOGRAPHIC_CHECKSUM
    }

    /**
     * The value of the Command APDU as it has been received from the NFC field.
     */
    private final byte[] mValue;

    /**
     * The Command APDU type
     * */
    final Type mType;

    /**
     * Default Basic Constructor.
     */
    private CommandApdu() {
        // Intentionally no-op
        this.mValue = null;
        this.mType = Type.UNDEFINED;
    }

    /**
     * Build a new C-APDU from a byte[]. An APDU is built only if its type is supported.
     * This method can be called only from sub-classes
     *
     * @param apdu The Command APDU as byte[]
     *
     * @throws InvalidCla If the Class is not supported
     * @throws InvalidIns If the Instruction Code is not supported
     * @throws InvalidCommandApdu if too short
     *
     * */
    protected CommandApdu(final byte[] apdu) {
        mType = determineApduType(apdu);
        this.mValue = apdu;
    }

    public static Type determineApduType(final byte[] apdu) {
        if (apdu == null || apdu.length < 4) {
            throw new InvalidCommandApdu("Invalid C-APDU");
        }
        byte cla = apdu[Iso7816.CLA_OFFSET];
        switch(apdu[Iso7816.INS_OFFSET]) {
            case (byte)0xA4:
                if (cla == 0x00) {
                    return Type.SELECT;
                } else {
                    throw new InvalidCla("C-APDU - Class not supported");
                }
            case (byte)0xA8:
                if (cla == (byte)0x80) {
                    return Type.GET_PROCESSING_OPTIONS;
                } else {
                    throw new InvalidCla("C-APDU - Class not supported");
                }
            case (byte)0xB2:
                if (cla == 0x00) {
                    return Type.READ_RECORD;
                } else {
                    throw new InvalidCla("C-APDU - Class not supported");
                }
            case (byte)0xAE:
                if (cla == (byte)0x80) {
                    return Type.GENERATE_AC;
                } else {
                    throw new InvalidCla("C-APDU - Class not supported");
                }
            case (byte)0x2A:
                if (cla == (byte)0x80) {
                    return Type.COMPUTE_CRYPTOGRAPHIC_CHECKSUM;
                } else {
                    throw new InvalidCla("C-APDU - Class not supported");
                }
            default:
                throw new InvalidIns("C-APDU - Instruction Code not supported");
        }
    }

    /**
     * Gets the cLA.
     *
     * @return the cLA
     */
    public final byte getCla() {
        return mValue[Iso7816.CLA_OFFSET];
    }

    /**
     * Gets the iNS.
     *
     * @return the iNS
     */
    public final byte getIns() {
        return mValue[Iso7816.INS_OFFSET];
    }

    /**
     * Gets the p1.
     *
     * @return the p1
     */
    public final byte getP1() {
        return mValue[Iso7816.P1_OFFSET];
    }

    /**
     * Gets the p2.
     *
     * @return the p2
     */
    public final byte getP2() {
        return mValue[Iso7816.P2_OFFSET];
    }

    /**
     * Get the Command APDU type. Only a few types are supported
     *
     * @return The type of the Command APDU
     * */
    public final Type getType() {
        return mType;
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    public final int getLength() {
        return mValue.length;
    }
}
