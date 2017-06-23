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

package com.mastercard.mcbp.card.mpplite.mcbpv1.output;

import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.Record;
import com.mastercard.mcbp.utils.exceptions.datamanagement.UnexpectedData;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.TlvEditor;

import java.util.Arrays;

/**
 * Utility class to build Track 1 and Track 2 dynamic data
 */
class DynamicTrackData {
    public final static byte[] EXPECTED_TRACK1_CVC3_BITMAP
            = ByteArray.of("0000000000F0").getBytes();
    public final static byte[] EXPECTED_TRACK1_UN_ATC_BITMAP
            = ByteArray.of("000000000F0E").getBytes();
    public final static byte[] EXPECTED_TRACK1_NO_ATC_DIGITS
            = ByteArray.of("04").getBytes();
    public final static byte[] EXPECTED_TRACK2_CVC3_BITMAP
            = ByteArray.of("00F0").getBytes();
    public final static byte[] EXPECTED_TRACK2_UN_ATC_BITMAP
            = ByteArray.of("0F0E").getBytes();
    public final static byte[] EXPECTED_TRACK2_NO_ATC_DIGITS
            = ByteArray.of("04").getBytes();

    private final ByteArray mDynamicTrack1Data;
    private final ByteArray mDynamicTrack2Data;

    private final boolean mIsCdCvmSupported;


    /**
     * Create an object using the information available in the Record 1 SFI 1 of the contactless
     * profile
     *
     * @param contactlessPaymentData The Contactless Payment Data in the profile
     */
    public DynamicTrackData(final ContactlessPaymentData contactlessPaymentData,
                            final ByteArray cryptoAtc,
                            final ByteArray cvc3,
                            final ByteArray unpredictableNumber,
                            final boolean isCdCvmSupported) throws UnexpectedData {
        // First let's initialize the boolean flag.
        mIsCdCvmSupported = isCdCvmSupported;

        final byte[] record11 = readRecord11(contactlessPaymentData);
        final TlvEditor tlvEditor = TlvEditor.of(record11);
        if (tlvEditor == null) {
            throw new UnexpectedData("Invalid profile data");
        }
        final TlvEditor recordContent = TlvEditor.of(tlvEditor.getValue("70"));

        if (!validateRecordData(recordContent)) {
            throw new UnexpectedData("Data in profile does not match standard MCBP 1.0 bitmaps");
        }

        final String discretionaryData =
                buildDiscretionaryData(cryptoAtc, cvc3, unpredictableNumber);

        final byte[] profileTrack1Data = recordContent.getValue("56");
        final byte[] profileTrack2Data = recordContent.getValue("9F6B");

        // Now let's build the dynamic Track 1 and Track 2 data.
        mDynamicTrack1Data = buildDynamicTrack1Data(discretionaryData, profileTrack1Data);
        mDynamicTrack2Data = buildDynamicTrack2Data(discretionaryData, profileTrack2Data);
    }


    public ByteArray getDynamicTrack1() {
        return mDynamicTrack1Data;
    }

    public ByteArray getDynamicTrack2() {
        return mDynamicTrack2Data;
    }

    private ByteArray buildDynamicTrack1Data(final String discretionaryData,
                                             final byte[] track1Data) {
        if (track1Data == null) {
            return ByteArray.of("");
        }
        final String track1 = new String(track1Data);
        int track1DataLength = track1.lastIndexOf("^") + 9;
        final byte[] dynamicTrack1 =
                (track1.substring(0, track1DataLength) + discretionaryData.substring(1, 13))
                        .getBytes();
        return ByteArray.of(dynamicTrack1, dynamicTrack1.length);
    }

    private ByteArray buildDynamicTrack2Data(final String discretionaryData,
                                             final byte[] track2Data) {
        if (track2Data == null) {
            return ByteArray.of("");
        }
        int track2DataLength = ByteArray.of(track2Data).toHexString().indexOf('D') + 8;
        final String dynamicTrack2 =
                ByteArray.of(track2Data).toHexString()
                         .substring(0, track2DataLength) + discretionaryData;

        return ByteArray.of(dynamicTrack2.length() % 2 != 0 ?
                            dynamicTrack2.substring(0, dynamicTrack2.length() - 1) : dynamicTrack2);
    }

    private String buildDiscretionaryData(final ByteArray atc,
                                          final ByteArray cvc3,
                                          final ByteArray un) {
        // The assumption is that with MCBP the basic value is 3. If the profile data is different,
        // the nUn will be different.
        final String nUn = mIsCdCvmSupported ? "8" : "3";
        return "0" +
               convertArrayTo2BytesBcdString(atc) +
               convertArrayTo2BytesBcdString(cvc3) +
               un.toHexString().substring(5, 8) +
               nUn + "F";
    }

    /**
     * Utility function to convert from Array to BCD encoding.
     * FIXME: Refactor as this works only for this very specific case
     */
    private String convertArrayTo2BytesBcdString(final ByteArray array) {
        if (array == null || array.getLength() < 2) {
            return "0000";
        }
        final Long decimalValue = Long.valueOf(array.toHexString(), 16);
        // We need to make sure we have always 4 digits.
        final String result = "0000" + decimalValue.toString();
        // Take only the last four
        return result.substring(result.length() - 4);
    }

    /**
     * At the moment we support only MCBP v1.0 profile configuration support for Magstripe
     * If such data is not available in the profile the generation of the transaction id for
     * Magstripe is aborted
     *
     * @return True if the data in the Record 1 1 of the profile matches the expected MCBP data
     */
    private boolean validateRecordData(final TlvEditor tlvEditor) {
        if (tlvEditor == null) {
            return false;
        }
        final byte[] track1Cvc3Bitmap = tlvEditor.getValue("9F62");
        final byte[] track1UnAtcBitmap = tlvEditor.getValue("9F63");
        final byte[] track1NoAtcDigits = tlvEditor.getValue("9F64");
        final byte[] track2Cvc3Bitmap = tlvEditor.getValue("9F65");
        final byte[] track2UnAtcBitmap = tlvEditor.getValue("9F66");
        final byte[] track2NoAtcDigits = tlvEditor.getValue("9F67");

        return Arrays.equals(track1Cvc3Bitmap, EXPECTED_TRACK1_CVC3_BITMAP) &&
               Arrays.equals(track1UnAtcBitmap, EXPECTED_TRACK1_UN_ATC_BITMAP) &&
               Arrays.equals(track1NoAtcDigits, EXPECTED_TRACK1_NO_ATC_DIGITS) &&
               Arrays.equals(track2Cvc3Bitmap, EXPECTED_TRACK2_CVC3_BITMAP) &&
               Arrays.equals(track2UnAtcBitmap, EXPECTED_TRACK2_UN_ATC_BITMAP) &&
               Arrays.equals(track2NoAtcDigits, EXPECTED_TRACK2_NO_ATC_DIGITS);
    }

    private byte[] readRecord11(final ContactlessPaymentData contactlessPaymentData)
            throws UnexpectedData {
        if (contactlessPaymentData == null || contactlessPaymentData.getRecords() == null) {
            throw new MppLiteException("Invalid profile information");
        }
        final Record[] records = contactlessPaymentData.getRecords();
        // Read Record 1 SFI 1
        for (Record record : records) {
            if (record.getRecordNumber() == 0x01 && record.getSfi() == 0x01) {
                final ByteArray value = record.getRecordValue();
                if (value != null) {
                    return value.getBytes();
                }
            }
        }
        // Record not found
        throw new UnexpectedData("Unable to retrieve Record 1 1 for transaction id");
    }

}
