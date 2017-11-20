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
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for Magstripe dynamic Track 1 and Track 2 data
 */
public class DynamicTrackDataTest {

    private String mTrack1 = "42353235383637393738333639373335315E202F5E323130313232363030303030"
                             + "3030303030303030";
    private String mTrack2 = "5258679783697351D21012260000000000000F";

    private String mTrack1Cvc3Bitmap;
    private String mTrack1UnAtcBitmap;
    private String mTrack1NoAtcDigits;
    private String mTrack2Cvc3Bitmap;
    private String mTrack2UnAtcBitmap;
    private String mTrack2NoAtcDigits;

    private String mCryptoAtc;
    private String mCvc3;
    private String mUnpredictableNumber;
    private boolean mIsCdCvmSupported;

    @Before
    public void setUp() {
        // initialize all the parameters. Tests may override some parameters as needed
        mTrack1Cvc3Bitmap = "0000000000F0";
        mTrack1UnAtcBitmap = "000000000F0E";
        mTrack1NoAtcDigits = "04";
        mTrack2Cvc3Bitmap = "00F0";
        mTrack2UnAtcBitmap = "0F0E";
        mTrack2NoAtcDigits = "04";

        mCryptoAtc = "0B77";
        mCvc3 = "0345";
        mUnpredictableNumber = "00000819";
        mIsCdCvmSupported = true;
    }

    @Test
    public void testGetDynamicTrack1() throws Exception {
        mCryptoAtc = "0B77";
        mCvc3 = "0345";
        mUnpredictableNumber = "00000819";
        mIsCdCvmSupported = true;

        DynamicTrackData dynamicTrackData = new DynamicTrackData(getContactlessPaymentData(),
                                                                 getCryptoAtc(),
                                                                 getCvc3(),
                                                                 getUnpredictableNumber(),
                                                                 mIsCdCvmSupported);

        final String expectedData = "42353235383637393738333639373335315E202F5E3231303132323630"
                                    + "323933353038333738313938";
        final String actualData = dynamicTrackData.getDynamicTrack1().toHexString();

        assertEquals(expectedData, actualData);

    }

    @Test
    public void testGetDynamicTrack2() throws Exception {
        mCryptoAtc = "0B77";
        mCvc3 = "0345";
        mUnpredictableNumber = "00000819";
        mIsCdCvmSupported = true;

        DynamicTrackData dynamicTrackData = new DynamicTrackData(getContactlessPaymentData(),
                                                                 getCryptoAtc(),
                                                                 getCvc3(),
                                                                 getUnpredictableNumber(),
                                                                 mIsCdCvmSupported);

        final String expectedData = "5258679783697351D21012260293508378198F";
        final String actualData = dynamicTrackData.getDynamicTrack2().toHexString();

        assertEquals(expectedData, actualData);
    }

    private ByteArray getCryptoAtc() {
        return ByteArray.of(mCryptoAtc);
    }

    private ByteArray getCvc3() {
        return ByteArray.of(mCvc3);
    }

    private ByteArray getUnpredictableNumber() {
        return ByteArray.of(mUnpredictableNumber);
    }

    private ContactlessPaymentData getContactlessPaymentData() {
        final String recordValue = "70"
                                   + "8186"
                                   + "9F6C020001"
                                   + "9F6206" + mTrack1Cvc3Bitmap
                                   + "9F6306" + mTrack1UnAtcBitmap
                                   + "5629"   + mTrack1
                                   + "9F6401" + mTrack1NoAtcDigits
                                   + "9F6502" + mTrack2Cvc3Bitmap
                                   + "9F6602" + mTrack2UnAtcBitmap
                                   + "9F6B13" + mTrack2
                                   + "9F6701" + mTrack2NoAtcDigits
                                   // UDOL
                                   + "9F69199F6A049F7E019F02065F2A029F1A029C019A039F15029F3501";


        // Build the contactless payment data with the record information we just created
        final ContactlessPaymentData contactlessPaymentData = new ContactlessPaymentData();
        Record record = new Record();
        record.setRecordNumber((byte)0x01);
        record.setSfi((byte)0x01);
        record.setRecordValue(ByteArray.of(recordValue));
        Record[] records = new Record[1];
        records[0] = record;
        contactlessPaymentData.setRecords(records);
        return contactlessPaymentData;
    }
}