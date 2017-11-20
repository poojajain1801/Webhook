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

package com.mastercard.mcbp.card.mpplite.mcbpv1.logic;

import java.util.Arrays;

/**
 * Single tone utility object to perform the Additional Check Table
 */
public enum AdditionalCheckTable {
    INSTANCE;

    public enum Result {
        MATCH_FOUND,
        MATCH_NOT_FOUND,
        NOT_PERFORMED
    }

    static final byte PROCESS_CHECK_TABLE_MASK = 0x03;

    /**
     * Process the Additional Check Table
     *
     * @param cdol The CDOL as received in the Command APDU
     */
    public static Result process(final byte[] cdol,
                                 final byte[] cvrMaskAnd,
                                 final byte[] ciacDecline,
                                 final byte[] additionalCheckTable) {

        if (!((cvrMaskAnd[5] & PROCESS_CHECK_TABLE_MASK) != 0x00 ||
              (ciacDecline[2] & PROCESS_CHECK_TABLE_MASK) != 0x00)) {
            // Conditions not met, we skip the additional check table
            return Result.NOT_PERFORMED;
        }

        final byte actPosition = additionalCheckTable[0];
        final byte actLength = additionalCheckTable[1];
        final byte actNumber = additionalCheckTable[2];

        if (actPosition == 0x00
            || actPosition + actLength - 1 > cdol.length
            || actNumber * actLength > 15
            || actLength == 0x00
            || actNumber < 2) {
            return Result.NOT_PERFORMED;
        }

        final byte[] masked = new byte[actLength];

        // prepare the cdol data masked in transient
        for (int i = 0; i < actLength; i++) {
            masked[i] = (byte) (additionalCheckTable[3 + i] & cdol[actPosition + i - 1]);
        }

        // compare the masked value with the entries until a match is found
        boolean matchFound = false;

        for (int i = 1; i < actNumber; ++i) {
            final byte[] subTable = new byte[actLength];
            final int start = i * actLength + 3;
            System.arraycopy(additionalCheckTable, start, subTable, 0, actLength);

            if (Arrays.equals(masked, subTable)) {
                matchFound = true;
                break;
            }
        }

        if (matchFound) {
            return Result.MATCH_FOUND;
        }
        return Result.MATCH_NOT_FOUND;
    }
}
