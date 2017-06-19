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

import com.mastercard.mobile_api.utils.tlv.ParsingException;
import com.mastercard.mobile_api.utils.tlv.TlvParser;

import java.util.List;

public class CardSide {

    public final static byte BACKGROUND_TAG = (byte) 0x13;
    public final static byte PICTURE_TAG = (byte) 0x14;
    public final static byte CARD_ELEMENTS_TAG = (byte) 0x15;
    public final static byte ALWAYS_TEXT_TAG = (byte) 0x16;
    public final static byte PIN_TEXT_TAG = (byte) 0x17;
    public final static byte NO_PIN_TEXT_TAG = (byte) 0x18;

    /**
     * The CardSide type
     */
    private byte cardSideType;
    /**
     * The cardBackground
     */
    private Background cardBackground = new Background();
    /**
     * The cardElements
     */
    private short cardElements = (short) 0x0000; // No information to be
    // displayed
    /**
     * The picture list
     */
    private List pictureList;
    /**
     * The text
     */
    private List textList;

    public CardSide(final byte type) {
        cardSideType = type;
    }

    public CardSide(final byte type, final byte[] cardSideData, final int cardSideOffset,
                    final int cardSideLength) throws ParsingException {
        cardSideType = type;
        final CardSideTlvHandler cardSideTlvHandler = new CardSideTlvHandler();
        cardSideTlvHandler.setBackgroundToParse(true);
        cardSideTlvHandler.setCardElementsToParse(true);

        TlvParser.parseTlv(cardSideData, cardSideOffset, cardSideLength, cardSideTlvHandler);

        if (!cardSideTlvHandler.isBackgroundToParse()) {
            cardBackground = cardSideTlvHandler.getCardSideBackground();
        }

        if (!cardSideTlvHandler.isCardElementsToParse()) {
            cardElements = cardSideTlvHandler.getCardElements();
        }

        pictureList = cardSideTlvHandler.getPictureList();

        textList = cardSideTlvHandler.getTextList();
    }

    public void updateCardSideContent(final byte[] cardSideData, final int cardSideOffset,
                                      final int cardSideLength) throws ParsingException {
        final CardSideTlvHandler cardSideTlvHandler = new CardSideTlvHandler();
        cardSideTlvHandler.setBackgroundToParse(true);
        cardSideTlvHandler.setCardElementsToParse(true);
        // any new picture or text will be added to the existing ones
        cardSideTlvHandler.setPictureList(this.pictureList);
        cardSideTlvHandler.setTextList(this.textList);

        TlvParser.parseTlv(cardSideData, cardSideOffset, cardSideLength, cardSideTlvHandler);

        if (!(cardSideTlvHandler.isBackgroundToParse())) {
            cardBackground = cardSideTlvHandler.getCardSideBackground();
        }

        if (!(cardSideTlvHandler.isCardElementsToParse())) {
            cardElements = cardSideTlvHandler.getCardElements();
        }

        pictureList = cardSideTlvHandler.getPictureList();

        textList = cardSideTlvHandler.getTextList();
    }

    public Background getCardBackground() {
        return cardBackground;
    }

    public short getCardElements() {
        return cardElements;
    }

    public List getPictureList() {
        return pictureList;
    }

    public List getText() {
        return textList;
    }

    public void clear() {
        // clearing all text fields
        for (int i = 0; i < textList.size(); i++) {
            final Text text = (Text) textList.get(i);
            text.clear();
        }
    }
}
