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

import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.tlv.ParsingException;
import com.mastercard.mobile_api.utils.tlv.TlvHandler;

import java.util.ArrayList;
import java.util.List;

public class CardSideTlvHandler extends TlvHandler {

    private Background cardSideBackground;
    private short cardElements;
    private List pictureList = new ArrayList();
    private List textList  = new ArrayList();
    private boolean isBackgroundToParse;
    private boolean isCardElementsToParse;
    private boolean isTextToParse;
    private boolean isPictureToParse;
    
    public CardSideTlvHandler() {

    }

    public void parseTag(byte tag, int length, byte[] data, int offset) throws ParsingException {

        switch (tag) {
            case CardSide.BACKGROUND_TAG:
                setBackgroundToParse(false);
                cardSideBackground = new Background(data, offset, length);
                break;
            case CardSide.CARD_ELEMENTS_TAG:
                setCardElementsToParse(false);
                cardElements = Utils.readShort(data, offset);
                break;
            case CardSide.PICTURE_TAG:
                setPictureToParse(false);
                pictureList.add(new Picture(data, offset, length));
                break;
            case CardSide.ALWAYS_TEXT_TAG:
            case CardSide.PIN_TEXT_TAG:
            case CardSide.NO_PIN_TEXT_TAG:
                setTextToParse(false);
                textList.add(new Text(tag, data, offset, length));
                break;
            default:
                throw new ParsingException();
        }
    }

    public void parseTag(short tag, int length, byte[] data, int offset) throws ParsingException {

    }

    public Background getCardSideBackground() {
        return cardSideBackground;
    }

    public short getCardElements() {
        return cardElements;
    }

    public List getPictureList() {
        return pictureList;
    }

    public void setPictureList(List pictureList) {
        this.pictureList = pictureList;
    }

    public List getTextList() {
        return textList;
    }

    public void setTextList(List textList) {
        this.textList = textList;
    }

    public boolean isBackgroundToParse() {
        return isBackgroundToParse;
    }

    public void setBackgroundToParse(boolean isBackgroundToParse) {
        this.isBackgroundToParse = isBackgroundToParse;
    }

    public boolean isCardElementsToParse() {
        return isCardElementsToParse;
    }

    public void setCardElementsToParse(boolean isCardElementToParse) {
        this.isCardElementsToParse = isCardElementToParse;
    }

    public void setPictureToParse(boolean isPictureToParse) {
        this.isPictureToParse = isPictureToParse;
    }

    public void setTextToParse(boolean isTextToParse) {
        this.isTextToParse = isTextToParse;
    }
}
