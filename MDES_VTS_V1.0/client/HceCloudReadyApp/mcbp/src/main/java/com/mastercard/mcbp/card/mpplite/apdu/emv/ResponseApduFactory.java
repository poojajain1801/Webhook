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

import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;

/**
 * Factory to generate Response APDUs
 */
public enum ResponseApduFactory {
    /**
     * Single tone instance of the factory
     * */
    INSTANCE;

    /**
     * Generated a response APDU comprised of the Status Word only (2 bytes)
     *
     * @param statusWord The value of the APDU
     *
     * @return The Response APDU as byte[]
     *
     * */
    public static byte[] of(char statusWord) {
        return charToByteArray(statusWord);
    }

    /**
     * Generate a response APDU with a specific value and status word
     *
     * @param value The value of the response APDU
     * @param statusWord The status word of this APDU
     *
     * @return The Response APDU as byte[]
     * */
    public static byte[] of(byte[] value, byte[] statusWord) throws InvalidInput {
        if (value == null || statusWord == null || statusWord.length != 2) {
            throw new InvalidInput("Invalid APDU value or statusWord");
        }

        // Concatenate value and status word to generate the response apdu
        byte[] apdu = new byte[value.length + statusWord.length];
        System.arraycopy(value, 0, apdu, 0, value.length);
        System.arraycopy(statusWord, 0, apdu, value.length, statusWord.length);

        return apdu;
    }

    /**
     * Generate a response APDU with a specific value and status word
     * */
    public static byte[] of(byte[] value, char statusWord) throws InvalidInput {
        return of(value, charToByteArray(statusWord));
    }

    /**
     * Generate a response of APDU with the status word of Wrong Data Length
     * @return the Wrong Data Length Status Word
     * */
    public static byte[] wrongDataLength() {
        return of(Iso7816.SW_WRONG_LENGTH);
    }

    /**
     * Generate a response of APDU with the status word of Security Status not satisfied
     * @return the Security Status not satisfied Status Word
     * */
    public static byte[] securityStatusNotSatisfied() {
        return of(Iso7816.SW_SECURITY_STATUS_NOT_SATISFIED);
    }

    /**
     * Generate a response of APDU with the status word of conditions of use not satisfied
     * @return the conditions of use not satisfied Status Word
     * */
    public static byte[] conditionsOfUseNotSatisfied() {
        return of(Iso7816.SW_CONDITIONS_NOT_SATISFIED);
    }

    /**
     * Generate a response of APDU with the status word of File not Found
     * @return the File not Found Status Word
     * */
    public static byte[] fileNotFound() {
        return of(Iso7816.SW_FILE_NOT_FOUND);
    }

    /**
     * Generate a response of APDU with the status word of Record not Found
     * @return the Record not Found Status Word
     * */
    public static byte[] recordNotFound() {
        return of(Iso7816.SW_RECORD_NOT_FOUND);
    }

    /**
     * Generate a response of APDU with the status word of Wrong Parameter P1-P2
     * @return the Wrong Parameter P1-P2 Status Word
     * */
    public static byte[] wrongParameterP1P2() {
        return of(Iso7816.SW_WRONG_P1P2);
    }

    /**
     * Generate a response of APDU with the status word of Instruction Code Not Supported
     * @return the Instruction Code Not Supported Status Word
     * */
    public static byte[] instructionCodeNotSupported() {
        return of(Iso7816.SW_INS_NOT_SUPPORTED);
    }

    /**
     * Generate a response of APDU with the status word of Class not Supported
     * @return the Class not Supported Status Word
     * */
    public static byte[] classNotSupported() {
        return of(Iso7816.SW_CLA_NOT_SUPPORTED);
    }

    /**
     * Generate a response of APDU with the status word of Successful Processing
     * @return the Successful Processing Status Word (status word only)
     * */
    public static byte[] successfulProcessing() {
        return of(Iso7816.SW_NO_ERROR);
    }

    /**
     * Generate a response of APDU with the status word of Wrong Data Length
     * @param value The value of the APDU
     * @return the APDU with the provided value and the success status word
     * */
    public static byte[] successfulProcessing(byte[] value) throws InvalidInput {
        return of(value, Iso7816.SW_NO_ERROR);
    }

    /**
     * Utility function to convert a Char into a byte[]
     * @param data The Java char to be converted to a byte[]
     * @return A byte[] of 2 elements containing the value of the char
     * */
    private static byte[] charToByteArray(char data) {
        byte[] result = new byte[2];
        result[0] = (byte)((data & 0xFF00) >> 8);
        result[1] = (byte)(data & 0x00FF);
        return result;
    }
}
