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
package com.mastercard.mobile_api.payment.cld;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.tlv.ParsingException;
import com.mastercard.mobile_api.utils.tlv.TlvParser;

import org.apache.commons.codec.binary.Hex;

public class Cld {
    public final static byte VERSION_TAG = (byte) 0x11;
    public final static byte FORM_FACTOR_TAG = (byte) 0x12;
    public final static byte FRONT_SIDE_TAG = (byte) 0x01;
    public final static byte BACK_SIDE_TAG = (byte) 0x02;
    public final static byte DEFAULT_VERSION = (byte) 0x01;
    /**
     * The version
     */
    private byte version = DEFAULT_VERSION;
    // Form factor possible values
    public final static byte ID1_FORMAT = (byte) 0x01;

    /**
     * The FrontSide
     */
    private CardSide frontSide = null;
    /**
     * The BackSide
     */
    private CardSide backSide = null;

    /**
     * Instantiates a new Cld.
     *
     * @param cardLayoutDescription the cld data
     */
    public Cld(final ByteArray cardLayoutDescription) {
        try {
            init(cardLayoutDescription.getBytes(), 0, cardLayoutDescription.getLength());
        } catch (ParsingException e) {
            // Something went wrong, let's use one of the known CLD values
            ByteArray cld = ByteArray.of(getDefaultCldWithCardArt(3));
            try {
                init(cld.getBytes(), 0, cld.getLength());
            } catch (ParsingException e1) {
                // This should not happen as we go back to a default and hopefully well tested CLD
                throw new RuntimeException("Something is wrong with the CLD");
            }
        }
    }

    private void init(byte[] cldData, int cldOffset, int cldLength) throws ParsingException {

        final CldTlvHandler tlvHandler = new CldTlvHandler();
        tlvHandler.setVersionToParse(true);
        tlvHandler.setFrontSideToParse(true);

        TlvParser.parseTlv(cldData, cldOffset, cldLength, tlvHandler);

        if (tlvHandler.isVersionToParse()) {
            throw new ParsingException();
        } else {
            version = tlvHandler.getVersion();
        }

        if (tlvHandler.isFrontSideToParse()) {
            throw new ParsingException();
        } else {
            frontSide = tlvHandler.getFrontSide();
        }

        backSide = tlvHandler.getBackSide();

        // clearing cld data buffer
        Utils.clearByteArray(cldData);
    }

    public byte getVersion() {
        return version;
    }

    public CardSide getFrontSide() {
        return frontSide;
    }

    public CardSide getBackSide() {
        return backSide;
    }

    public void clear() {
        // clears cld from memory
        if (frontSide != null) {
            frontSide.clear();
        }
        if (backSide != null) {
            backSide.clear();
        }
    }

    /**
     * Get the default CLD value with a Random Card Art
     * */
    private static String getDefaultCldWithCardArt(final int cardArtIndex) {
        final String CLD_HEADER = "1101011201010137130A04";
        final String CLD_TRAILER =
                "160F1A10020003FFFFFF4578706972657316180807010003" +
                "FFFFFF4D5220412E2043415244484F4C4445520216131004" +
                "6261636B5F6261636B67726F756E64150" +
                "20300012C161B0817030004FFFFFF2A2A2A2A202A2A2A2A2" +
                "02A2A2A2A202A2A2A2A160D3110010003" +
                "FFFFFF2A2A2F2A2A020D160B3E1E0540030000002A2A2A";

        final String card = new String(Hex.encodeHex(("TVK_" + cardArtIndex +".png").getBytes()));

        return CLD_HEADER + card + CLD_TRAILER;
    }
}
