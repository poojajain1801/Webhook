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

package com.mastercard.mcbp.utils.lde;

/**
 * Utility class for LDE
 */
public class Utils {

    private static final int PAN_BEGIN_INDEX = 0;
    private static final int PAN_END_INDEX = 19;

    /**
     * PAN is initial 10 bytes of digitized card id padded with 'F'<br>
     * Note : Please refer following details for how we parse DC_ID to retrieve PAN<br>
     * The DC_ID (17 bytes) is a unique identifier of a Digitized Card.
     * It is the concatenation of the following information:
     * - Tokenize PAN (10 bytes):The value is left justified and padded with trailing
     * hexadecimal 'F's. <br>
     * - PAN Sequence Number (PSN) (1 byte)<br>
     * - Provisioning Date (6 bytes) : The value is a timestamp equals to YYMMDDHHMMSS<br>
     * Reference : MCBP MPA functional Specification V1
     *
     * @param digitizedCardId Digitized card identifier
     * @return String last 4 digits of the PAN for the wallet to display
     */
    public static String getLastFourDigitOfPAN(String digitizedCardId) {
        String actualPan = retrievePanFromDigitizedCardId(digitizedCardId);
        String pan = removePaddingFromPAN(actualPan);
        return pan.substring(pan.length() - 4);
    }

    /**
     * Utility API to read PAN form digitized card Id and remove 'F' padding form PAN
     *
     * @param panWithPadding Digitized card identifier with padding data
     * @return Data without padding.
     */
    public static String removePaddingFromPAN(String panWithPadding) {
        return panWithPadding.replaceAll("F", "");
    }

    /**
     * Retrieve PAN from digitized card id
     * @param digitizedCardId Digitize card id.
     * @return PAN number form given card id.
     */
    public static String retrievePanFromDigitizedCardId(String digitizedCardId) {
        return digitizedCardId.substring(PAN_BEGIN_INDEX, PAN_END_INDEX);
    }
}
