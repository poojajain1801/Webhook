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

package com.mastercard.mcbp.card.mpplite.apdu.emv;

import com.mastercard.mcbp.card.mpplite.apdu.RespApdu;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.TlvEditor;

import java.util.List;

/**
 * The Class SelectRespApdu.
 */
public class SelectResponseApdu extends RespApdu {

    public final static byte[] FCI_TEMPLATE_TAG = new byte[]{0x6F};
    public final static byte[] FCI_PROPRIETARY_TAG = new byte[]{(byte)0xA5};
    public final static byte[] PDOL_TAG = new byte[]{(byte)0x9F, (byte)0x38};
    public final static byte[] SUCCESS_WORD = new byte[]{(byte)0x90, 0x00};

    /**
     * PDOL List in the form of [TAG|LENGTH]
     */
    private final DolRequestList mPdolList;

    /**
     * Build a SELECT R-APDU from the value in the Profile and an additional list of PDOL parameters
     * that can be specified.
     * @param profileValue The SELECT R-APDU as in the Card Profile
     * @param additionalPdolEntries The list of additional PDOL values. A PDOL value is expected in
     *                              the form of TAG|LENGTH all within a single byte[]
     * @return The updated SELECT R-APDU, if any of the additional entries has been added.
     *         Otherwise, the profile response is returned.
     */
    public static SelectResponseApdu of(final byte[] profileValue,
                                        final List<DolRequestList.DolItem> additionalPdolEntries) {
        final int length = profileValue.length;
        if (length < 3) {
            return null;
        }
        // We assume the response is always present
        final byte[] data = new byte[length];

        // We have our response in TLV format
        System.arraycopy(profileValue, 0, data, 0, length);

        final TlvEditor selectResponseTlv = TlvEditor.of(data);
        if (selectResponseTlv == null) {
            // Something went wrong when parsing the data. Use what is in the profile
            return new SelectResponseApdu(profileValue);
        }

        if (additionalPdolEntries != null && !additionalPdolEntries.isEmpty()) {
            final TlvEditor fciTemplate =
                    TlvEditor.of(selectResponseTlv.getValue(FCI_TEMPLATE_TAG));
            if (fciTemplate == null) return new SelectResponseApdu(profileValue);

            final TlvEditor fciProprietary =
                    TlvEditor.of(fciTemplate.getValue(FCI_PROPRIETARY_TAG));
            if (fciProprietary == null) return new SelectResponseApdu(profileValue);

            final DolRequestList pdol = DolRequestList.of(fciProprietary.getValue(PDOL_TAG));

            // Add PDOLs here
            for (DolRequestList.DolItem item: additionalPdolEntries) {
                pdol.addTag(item.getTag(), item.getLength());
            }
            // Let's now add the newly created PDOL and all the other TLVs accordingly
            fciProprietary.addTlv(PDOL_TAG, pdol.getBytes());
            fciTemplate.addTlv(FCI_PROPRIETARY_TAG, fciProprietary.rebuildByteArray());
            selectResponseTlv.addTlv(FCI_TEMPLATE_TAG, fciTemplate.rebuildByteArray());
        }
        return new SelectResponseApdu(selectResponseTlv);
    }

    /**
     * Get the PDOL List in the format of concatenated [TAG|LENGTH] as byte[]
     * @return The PDOL List
     */
    public DolRequestList getPdolList() {
        return mPdolList;
    }

    /**
     * Instantiates a new gen ac resp APDU.
     *
     * @param value the Value of the Select Response APDU
     */
    private SelectResponseApdu(final byte[] value) {
        super(ByteArray.of(value));
        mPdolList = null;
    }

    /**
     * Private constructor used by the static factory method
     */
    private SelectResponseApdu(final TlvEditor responseContent) {
        super(ByteArray.of(responseContent.rebuildByteArray()), ByteArray.of(SUCCESS_WORD));
        mPdolList = getPdolList(responseContent);
    }

    /**
     * Utility function to extract the PDOL from the SELECT R-APDU
     * @param selectResponseTlv The Select Response APDU as TlvEditor object
     * @return The PDOL list as byte[]
     */
    private static DolRequestList getPdolList(final TlvEditor selectResponseTlv) {
        final TlvEditor fciTemplate =
                TlvEditor.of(selectResponseTlv.getValue(FCI_TEMPLATE_TAG));
        if (fciTemplate == null) return null;

        final TlvEditor fciProprietary =
                TlvEditor.of(fciTemplate.getValue(FCI_PROPRIETARY_TAG));
        if (fciProprietary == null) return null;

        return DolRequestList.of(fciProprietary.getValue(PDOL_TAG));
    }
}
