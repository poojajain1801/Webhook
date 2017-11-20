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
import com.mastercard.mobile_api.utils.tlv.TlvHandler;

/**
 * The Class CldTlvHandler parses all the information of a Card Layout
 * Description
 */
public class CldTlvHandler extends TlvHandler {

    private byte version;
    private byte formFactor = Cld.ID1_FORMAT;
    private CardSide frontSide = null;
    private CardSide backSide = null;

    private boolean versionToParse;
    private boolean formFactorToParse;
    private boolean frontSideToParse;
    private boolean backSideToParse;

    public void parseTag(byte tag, int length, byte[] data, int offset) throws ParsingException {

        switch (tag) {

            case Cld.VERSION_TAG:
                setVersionToParse(false);
                version = data[offset];
                break;
            case Cld.FORM_FACTOR_TAG:
                setFormFactorToParse(false);
                formFactor = data[offset];
                break;
            case Cld.FRONT_SIDE_TAG:
                setFrontSideToParse(false);
                if (frontSide == null) {
                    frontSide = new CardSide(tag, data, offset, length);
                } else {
                    frontSide.updateCardSideContent(data, offset, length);
                }
                break;
            case Cld.BACK_SIDE_TAG:
                setBackSideToParse(false);
                if (backSide == null) {
                    backSide = new CardSide(tag, data, offset, length);
                } else {
                    backSide.updateCardSideContent(data, offset, length);
                }
                break;
            default:
                throw new ParsingException();
        }
    }

    public void parseTag(short tag, int length, byte[] data, int offset) throws ParsingException {
    }

    public byte getVersion() {
        return version;
    }

    public byte getFormFactor() {
        return formFactor;
    }

    public CardSide getFrontSide() {
        return frontSide;
    }

    public CardSide getBackSide() {
        return backSide;
    }

    public boolean isVersionToParse() {
        return versionToParse;
    }

    public void setVersionToParse(boolean versionToParse) {
        this.versionToParse = versionToParse;
    }

    public boolean isFormFactorToParse() {
        return formFactorToParse;
    }

    public void setFormFactorToParse(boolean formFactorToParse) {
        this.formFactorToParse = formFactorToParse;
    }

    public boolean isFrontSideToParse() {
        return frontSideToParse;
    }

    public void setFrontSideToParse(boolean frontSideToParse) {
        this.frontSideToParse = frontSideToParse;
    }

    public boolean isBackSideToParse() {
        return backSideToParse;
    }

    public void setBackSideToParse(boolean backsideToParse) {
        this.backSideToParse = backsideToParse;
    }
}
