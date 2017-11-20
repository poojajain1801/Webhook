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

public class Background {

    public final static byte RGB_TYPE = (byte) 0x01;
    public final static byte JPEG_TYPE = (byte) 0x02;
    public final static byte URL_TYPE = (byte) 0x03;
    public final static byte REFERENCE_TYPE = (byte) 0x04;
    private final static byte[] BLANK_VALUE = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    private byte backgroundType;
    private byte[] backgroundValue;

    public Background() {
        backgroundType = RGB_TYPE;
        backgroundValue = BLANK_VALUE;
    }

    public Background(byte[] backgroundData, int offset, int length) {
        backgroundType = backgroundData[offset];
        backgroundValue = new byte[length - 1];
        System.arraycopy(backgroundData, offset + 1, backgroundValue, 0, backgroundValue.length);
    }

    public byte getBackgroundType() {
        return backgroundType;
    }

    public byte[] getBackgroundValue() {
        return backgroundValue;
    }
}
