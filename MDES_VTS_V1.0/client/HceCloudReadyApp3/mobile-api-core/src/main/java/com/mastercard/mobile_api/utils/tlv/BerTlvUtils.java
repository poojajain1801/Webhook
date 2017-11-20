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

package com.mastercard.mobile_api.utils.tlv;

public class BerTlvUtils {

    public static int getTlvLengthByte(final byte[] data, final int offset) {

        if (data[offset] == (byte) 0x81) {
            return 2;
        } else if (data[offset] == (byte) 0x82) {
            return 3;
        } else if (data[offset] == (byte) 0x83) {
            return 4;
        } else if (data[offset] == (byte) 0x84) {
            return 5;
        } else {
            return 1;
        }
    }

    public static int getTlvLength(final byte[] data, final int offset) {

        if (data[offset] > 0 && (data[offset] & 0xff) < 128) {
            return (data[offset] & 0xFF);
        } else if (data[offset] == (byte) 0x81) {
            return (data[offset + 1] & 0xFF);
        } else if (data[offset] == (byte) 0x82) {
            return (data[offset + 1] & 0xff) << 8 | (data[offset + 2] & 0xFF);
        } else if (data[offset] == (byte) 0x83) {
            return ((data[offset + 1] & 0xff) << 16) | ((data[offset + 2] & 0xff) << 8)
                    | (data[offset + 3] & 0xff);
        } else if (data[offset] == (byte) 0x84) {
            return (((data[offset] & 0xff) << 24) | ((data[offset + 1] & 0xff) << 16)
                    | ((data[offset + 2] & 0xff) << 8) | (data[offset + 3] & 0xff));
        } else {
            return data[offset] & 0xFF;
        }

    }

}
