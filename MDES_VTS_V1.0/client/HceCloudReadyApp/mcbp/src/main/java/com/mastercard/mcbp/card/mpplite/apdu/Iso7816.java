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

public enum Iso7816 {
    INSTANCE;
    /**
     * Response status : Incorrect parameters (P1,P2) = 0x6A86
     */
    public static final char SW_WRONG_P1P2 = (char) 0x6A86;

    /**
     * Response status : Security condition not satisfied = 0x6982
     */
    public static final char SW_SECURITY_STATUS_NOT_SATISFIED = 0x6982;

    /**
     * Response status : CLA value not supported = 0x6E00
     */
    public static final char SW_CLA_NOT_SUPPORTED = 0x6E00;

    /**
     * Response status : Record not found = 0x6A83
     */
    public static final char SW_RECORD_NOT_FOUND = 0x6A83;

    /**
     * Response status : File not found = 0x6A82
     */
    public static final char SW_FILE_NOT_FOUND = 0x6A82;

    /**
     * Response status : No precise diagnosis = 0x6F00
     */
    public static final char SW_UNKNOWN = 0x6F00;

    /**
     * Response status : No Error = (char)0x9000
     */
    public static final char SW_NO_ERROR = 0x9000;

    /**
     * Response status : Conditions of use not satisfied = 0x6985
     */
    public static final char SW_CONDITIONS_NOT_SATISFIED = 0x6985;

    /**
     * Response status : INS value not supported = 0x6D00
     */
    public static final char SW_INS_NOT_SUPPORTED = 0x6D00;

    /**
     * Response status : Wrong length = 0x6700
     */
    public static final char SW_WRONG_LENGTH = 0x6700;

    /**
     * Offset of the CLA in C-APDU
     */
    public static int CLA_OFFSET = 0;

    /**
     * Offset of the INS in C-APDU
     */
    public static int INS_OFFSET = 1;

    /**
     * Offset of the P1 parameter in C-APDU
     */
    public static int P1_OFFSET = 2;

    /**
     * Offset of the P2 parameter in C-APDU
     */
    public static int P2_OFFSET = 3;

    /**
     * Offset of the LC field in C-APDU
     */
    public static final int LC_OFFSET = 4;

    /**
     * Offset of the C_DATA field in  C-APDU
     */
    public static final int C_DATA_OFFSET = 5;


}