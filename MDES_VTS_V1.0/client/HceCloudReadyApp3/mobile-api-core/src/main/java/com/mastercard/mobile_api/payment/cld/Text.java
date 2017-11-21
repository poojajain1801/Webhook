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

public class Text {

    // Font types
    public final static byte ISO_1073_1_OCR_A = (byte) 0x01;
    public final static byte ISO_1073_1_OCR_B = (byte) 0x02;
    public final static byte COURIER_NEW = (byte) 0x04;
    public final static byte TIMES_NEW_ROMAN = (byte) 0x05;

    // Font modes
    public final static byte ITALIC = (byte) 0x80;
    public final static byte BOLD = (byte) 0x40;
    public final static byte UNDERLINE = (byte) 0x20;
    byte[] textBytes;
    private byte textType;
    private byte textHorizontalPosition;
    private byte textVerticalPosition;
    private byte font;
    private byte textMode;
    private byte textSize;
    private int textColor;

    public Text(final byte type, final byte[] textData, final int textOffset, final int textLength)
            throws ParsingException {
        textType = type;

        if (textLength < 8) {
            throw new ParsingException();
        }

        textVerticalPosition = textData[textOffset];
        if ((textVerticalPosition < 0) || (textVerticalPosition > 100)) {
            throw new ParsingException();
        }

        textHorizontalPosition = textData[textOffset + 1];
        if ((textHorizontalPosition < 0) || (textHorizontalPosition > 100)) {
            throw new ParsingException();
        }

        font = textData[textOffset + 2];

        textMode = textData[textOffset + 3];

        textSize = textData[textOffset + 4];

        if ((textSize < (byte) 0) || (textSize > (byte) 100)) {
            throw new ParsingException();
        }

        textColor = ((textData[textOffset + 5] & 0xff) << 16)
                | ((textData[textOffset + 5 + 1] & 0xff) << 8)
                | (textData[textOffset + 5 + 2] & 0xff);

        textBytes = new byte[textLength - 8];
        System.arraycopy(textData, textOffset + 8, textBytes, 0, textLength - 8);
    }

    public Text() {

    }

    public byte getTextHorizontalPosition() {
        return textHorizontalPosition;
    }

    public byte getTextVerticalPosition() {
        return textVerticalPosition;
    }

    public byte getFont() {
        return font;
    }

    public boolean isItalic() {
        return ((textMode & ITALIC) != 0);
    }

    public boolean isBold() {
        return ((textMode & BOLD) != 0);
    }

    public boolean isUnderline() {
        return ((textMode & UNDERLINE) != 0);
    }

    public byte getTextSize() {
        return textSize;
    }

    public void setTextSize(final byte textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(final int textColor) {
        this.textColor = textColor;
    }

    public String getTextValue() {
        return new String(textBytes);
    }

    public void clear() {
        Utils.clearByteArray(textBytes);
    }

}
