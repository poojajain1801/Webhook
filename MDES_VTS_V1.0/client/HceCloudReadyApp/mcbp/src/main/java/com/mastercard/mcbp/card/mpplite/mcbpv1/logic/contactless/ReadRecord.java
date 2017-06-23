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

package com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless;

import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ReadRecordCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.Record;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.TlvEditor;

import java.util.List;

/**
 * Utility class to perform the Read Record operation
 */
public class ReadRecord {

    public final static String UDOL_TAG = "9F69";
    public final static String CDOL_TAG = "8C";
    public final static String RECORD_TAG = "70";

    private final byte mRecordNumber;
    private final byte mSfi;
    private final List<DolRequestList.DolItem> mAdditionalUdolList;
    private final byte[] mResponse;
    private ContactlessContext mContext;

    public static ReadRecord of(final ReadRecordCommandApdu apdu,
                                final ContactlessContext context) {
        return new ReadRecord(apdu, context);
    }

    private ReadRecord(final ReadRecordCommandApdu apdu,
                       final ContactlessContext context) {
        // RRC.1.4
        mContext = context;
        mRecordNumber = apdu.getRecordNumber();
        mSfi = apdu.getSfiNumber();
        mAdditionalUdolList = context.getAdditionalUdolList();
        mResponse = readRecordValue(context.getCardProfile().getContactlessPaymentData());
    }

    /**
     * @return The Read Record Response APDU
     */
    public byte[] getResponse() {
        return mResponse;
    }

    private byte[] readRecordValue(final ContactlessPaymentData contactlessPaymentData) {
        final Record[] records = contactlessPaymentData.getRecords();
        if (records == null) {
            return ResponseApduFactory.recordNotFound();
        }

        // RRC.1.5 Get the actual record content

        for (Record recordIterator : records) {
            if (recordIterator.getRecordNumber() == mRecordNumber &&
                recordIterator.getSfi() == mSfi) {
                try {
                    byte[] recordValue = recordIterator.getRecordValue().getBytes();
                    if (mRecordNumber == 0x01 && mSfi == 0x01) {
                        // We need to add the UDOLs
                        final byte[] updatedRecordValue = processAdditionalUdolList(recordValue);
                        return ResponseApduFactory.successfulProcessing(updatedRecordValue);
                    }

                    final DolRequestList cdolRequestList = readCdolListFromRecord(recordValue);
                    if (cdolRequestList != null) {
                        mContext.getTransactionContext().setCdolList(cdolRequestList);
                    }
                    return ResponseApduFactory.successfulProcessing(recordValue);


                } catch (InvalidInput e) {
                    throw new MppLiteException("Invalid Record format");
                }
            }
        }
        return ResponseApduFactory.recordNotFound();
    }

    private DolRequestList readCdolListFromRecord(final byte[] record) {
        final TlvEditor tlvEditor = TlvEditor.of(record);
        if (tlvEditor == null) {
            throw new MppLiteException("Invalid Record profile data");
        }
        final TlvEditor recordContent = TlvEditor.of(tlvEditor.getValue(RECORD_TAG));

        if (recordContent == null) {
            throw new MppLiteException("Invalid Record profile data");
        }

        final byte[] cdol = recordContent.getValue(CDOL_TAG);
        return cdol == null ? null: DolRequestList.of(cdol);
    }

    private byte[] processAdditionalUdolList(final byte[] record) {
        final TlvEditor tlvEditor = TlvEditor.of(record);
        if (tlvEditor == null) {
            throw new MppLiteException("Invalid Record 1 SFI 1 profile data");
        }
        final TlvEditor recordContent = TlvEditor.of(tlvEditor.getValue(RECORD_TAG));

        if (recordContent == null) {
            throw new MppLiteException("Invalid Record 1 SFI 1 profile data");
        }

        final byte[] udol = recordContent.getValue(UDOL_TAG);

        final DolRequestList udolRequestList = DolRequestList.of(udol);

        if (mAdditionalUdolList == null) {
            mContext.getTransactionContext().setUdolList(udolRequestList);
            return record;
        }

        // Add UDOLs here
        for (DolRequestList.DolItem item: mAdditionalUdolList) {
            udolRequestList.addTag(item.getTag(), item.getLength());
        }

        mContext.getTransactionContext().setUdolList(udolRequestList);

        recordContent.addTlv(ByteArray.of(UDOL_TAG).getBytes(), udolRequestList.getBytes());
        tlvEditor.addTlv(ByteArray.of(RECORD_TAG).getBytes(), recordContent.rebuildByteArray());
        return tlvEditor.rebuildByteArray();
    }
}
