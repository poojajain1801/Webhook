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

public class Picture {

    private byte pictureHorizontalPosition;
    private byte pictureVerticalPosition;
    private byte pictureScale;
    private byte pictureType;
    private byte[] pictureValue;

    public Picture(byte[] pictureData, int offset, int length) throws ParsingException {

        if (length < 4) {
            throw new ParsingException();
        }

        pictureHorizontalPosition = pictureData[offset];
        if ((pictureHorizontalPosition < 0) || (pictureHorizontalPosition > 100)) {
            throw new ParsingException();
        }

        pictureVerticalPosition = pictureData[offset + 1];
        if ((pictureVerticalPosition < 0) || (pictureVerticalPosition > 100)) {
            throw new ParsingException();
        }

        pictureScale = pictureData[offset + 2];
        if ((pictureScale < 0) || (pictureScale > 100)) {
            throw new ParsingException();
        }

        pictureType = pictureData[offset + 3];

        pictureValue = new byte[length - 4];
        System.arraycopy(pictureData, offset + 4, pictureValue, 0, pictureValue.length);
    }

    public byte getPictureHorizontalPosition() {
        return pictureHorizontalPosition;
    }

    public byte getPictureVerticalPosition() {
        return pictureVerticalPosition;
    }

    public byte getPictureScale() {
        return pictureScale;
    }

    public byte getPictureType() {
        return pictureType;
    }

    public byte[] getPictureValue() {
        return pictureValue;
    }

    public void setPictureParams(byte type, byte[] value, byte horizontalPosition,
                                 byte verticalPosition, byte scale) {
        pictureType = type;
        pictureValue = value;
        pictureHorizontalPosition = horizontalPosition;
        pictureVerticalPosition = verticalPosition;
        pictureScale = scale;
    }
}
