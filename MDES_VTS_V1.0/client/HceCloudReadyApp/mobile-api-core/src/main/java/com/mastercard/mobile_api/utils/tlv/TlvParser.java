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

import com.mastercard.mobile_api.utils.Utils;

/**
 * The Class TlvParser.
 */
public class TlvParser {
    /**
     * Parses the tlv.
     *
     * @param data    the data
     * @param offset  the offset
     * @param length  the length
     * @param handler the handler
     */
    public static void parseTlv(final byte[] data, int offset, final int length,
                                final TlvHandler handler) throws ParsingException {

        final int lastOffset = offset + length;
        try {
            while(offset < lastOffset) {

                // parsing tag
                final byte byteTag = data[offset];
                short shortTag = 0;
                if (((byte) (byteTag & 0x1f)) == (byte) 0x1f) {
                    shortTag = Utils.readShort(data, offset);
                    offset += 2;
                } else {
                    offset++;
                }

                final int tagLength = BerTlvUtils.getTlvLength(data, offset);
                offset += BerTlvUtils.getTlvLengthByte(data, offset);

                // parse tag
                if (shortTag == 0) {
                    handler.parseTag(byteTag, tagLength, data, offset);
                } else {
                    handler.parseTag(shortTag, tagLength, data, offset);
                }
                offset += tagLength;
            }
        } catch (final NullPointerException | ArrayIndexOutOfBoundsException e) {
            throw new ParsingException();
        }
    }
}
