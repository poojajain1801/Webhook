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

package com.mastercard.mcbp.card.transactionlogging;

import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for the transaction identifier
 */
public class TransactionIdentifierTest {

    CryptoService defaultCryptoService = null;

    @Before
    public void setUp() throws Exception {
        defaultCryptoService = CryptoServiceFactory.getDefaultCryptoService();
    }


    @Test(expected = InvalidInput.class)
    public void testVerifyMChipTransactionIdentifierWithNull() throws Exception {
        TransactionIdentifier.getMChip(null, null, null);
    }


    @Test(expected = InvalidInput.class)
    public void testVerifyMChipTransactionIdentifierWithNullPan() throws Exception {
        String atc = "0001";
        String applicationCryptogram = "1122334455667788";
        ByteArray atcByteArray = ByteArray.of(atc);
        ByteArray applicationCryptogramByteArray = ByteArray.of(applicationCryptogram);
        TransactionIdentifier.getMChip
                (null, atcByteArray, applicationCryptogramByteArray);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyMChipTransactionIdentifierWithNullAtc() throws Exception {
        String pan = "123456789012345";
        String applicationCryptogram = "1122334455667788";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray applicationCryptogramByteArray = ByteArray.of(applicationCryptogram);
        TransactionIdentifier.getMChip
                (panByteArray, null, applicationCryptogramByteArray);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyMChipTransactionIdentifierWithNullApplicationCryptogram() throws
            Exception {
        String pan = "123456789012345";
        String atc = "0001";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray atcByteArray = ByteArray.of(atc);
        TransactionIdentifier.getMChip
                (panByteArray, atcByteArray, null);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyMChipTransactionIdentifierWithInvalidPanLength1() throws Exception {
        String pan = "1234567890123458809908088908";
        String atc = "0001";
        String applicationCryptogram = "1122334455667788";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray atcByteArray = ByteArray.of(atc);
        ByteArray applicationCryptogramByteArray = ByteArray.of(applicationCryptogram);

        TransactionIdentifier.getMChip
                (panByteArray, atcByteArray, applicationCryptogramByteArray);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyMChipTransactionIdentifierWithInvalidPanLength2() throws Exception {
        String pan = "123456";
        String atc = "0001";
        String applicationCryptogram = "1122334455667788";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray atcByteArray = ByteArray.of(atc);
        ByteArray applicationCryptogramByteArray = ByteArray.of(applicationCryptogram);

        TransactionIdentifier.getMChip
                (panByteArray, atcByteArray, applicationCryptogramByteArray);
    }

    @Test
    public void testVerifyMChipTransactionIdentifierWithOddPan() throws Exception {
        String EXPECTED_DATA = "94e09c05c05aa8d183d14aeac628ebb7c0325e80881811f2ac53e81db86eb0b6";
        String pan = "123456789012345";
        String atc = "0001";
        String applicationCryptogram = "1122334455667788";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray atcByteArray = ByteArray.of(atc);
        ByteArray applicationCryptogramByteArray = ByteArray.of(applicationCryptogram);

        ByteArray result = TransactionIdentifier.getMChip
                (panByteArray, atcByteArray, applicationCryptogramByteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().toLowerCase());
    }


    @Test
    public void testVerifyMChipTransactionIdentifierWithEvenPan() throws Exception {
        String EXPECTED_DATA = "1989fa70a9f5fe16492d5a668b81e1a9088e2ad43741ef8b2d863f1712df9c5c";
        String pan = "1234567890123456";
        String atc = "0001";
        String applicationCryptogram = "1122334455667788";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray atcByteArray = ByteArray.of(atc);
        ByteArray applicationCryptogramByteArray = ByteArray.of(applicationCryptogram);

        ByteArray result = TransactionIdentifier.getMChip
                (panByteArray, atcByteArray, applicationCryptogramByteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().toLowerCase());
    }

    @Test
    public void testVerifyMagStripeTransactionIdentifierWithNull() throws Exception {
        Assert.assertEquals(null, TransactionIdentifier.getMagstripe(null, null));
    }

    @Test
    public void testVerifyMagStripeTransactionIdentifierWithTrack1Null() throws Exception {
        String EXPECTED_DATA = "00000000000000000000000000000000a334daeee4f7a991c5def7510ed4fb04";
        String track2Data = "1234987623458765=15091230000000000000";
        ByteArray track2ByteArray = encodeTrack2Data(ByteArray.of(track2Data.getBytes()));

        ByteArray result = TransactionIdentifier.getMagstripe(null, track2ByteArray);
        Assert.assertEquals(EXPECTED_DATA,
                            result == null ? "" : result.toHexString().toLowerCase());
    }

    @Test
    public void testVerifyMagStripeTransactionIdentifierWithTrack2Null() throws Exception {
        String EXPECTED_DATA = "ea6fe411a4307fcff5f10f039d1f74f500000000000000000000000000000000";
        String track1Data = "B1234987623458765^RULES/MDES  ^1509123000000000";
        ByteArray track1ByteArray = ByteArray.of(track1Data.getBytes());

        ByteArray result = TransactionIdentifier.getMagstripe(track1ByteArray, null);
        Assert.assertEquals(EXPECTED_DATA,
                            result == null ? "" : result.toHexString().toLowerCase());
    }

    @Test
    public void testVerifyMagStripeTransactionIdentifierWithOddTrack2() throws Exception {
        String EXPECTED_DATA = "ea6fe411a4307fcff5f10f039d1f74f5a334daeee4f7a991c5def7510ed4fb04";
        String track1Data = "B1234987623458765^RULES/MDES  ^1509123000000000";
        String track2Data = "1234987623458765=15091230000000000000";
        ByteArray track1ByteArray = ByteArray.of(track1Data.getBytes());
        ByteArray track2ByteArray = encodeTrack2Data(ByteArray.of(track2Data.getBytes()));

        ByteArray result = TransactionIdentifier.getMagstripe(track1ByteArray, track2ByteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().toLowerCase());
    }


    @Test
    public void testVerifyMagStripeTransactionIdentifierWithEvenTrack2() throws Exception {
        String EXPECTED_DATA = "ea6fe411a4307fcff5f10f039d1f74f5c25b11ec2fd70ba78e930325a5d37302";
        String track1Data = "B1234987623458765^RULES/MDES  ^1509123000000000";
        String track2Data = "1234987623458765=150912300000000000000";
        ByteArray track1ByteArray = ByteArray.of(track1Data.getBytes());
        ByteArray track2ByteArray = encodeTrack2Data(ByteArray.of(track2Data.getBytes()));

        ByteArray result = TransactionIdentifier.getMagstripe(track1ByteArray, track2ByteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().toLowerCase());
    }


    @Test(expected = InvalidInput.class)
    public void testVerifyDsrpWithUcafTransactionIdentifierWithNull() throws Exception {
        // FIXME: This test does nothing!
        TransactionIdentifier.generateDsrpWithUcafTransactionIdentifier(null, null);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyDsrpWithUcafTransactionIdentifierWithNullPan() throws Exception {
        String ucaf = "AHRbjBgsn2DeAAXsy27/AgBVFA==";
        ByteArray ucafByteArray = ByteArray.of(ucaf.getBytes());

        // FIXME: This test does nothing!
        TransactionIdentifier.generateDsrpWithUcafTransactionIdentifier(null, ucafByteArray);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyDsrpWithUcafTransactionIdentifierWithNullUcaf() throws Exception {
        String pan = "5413339000001513";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());

        // FIXME: This test does nothing!
        TransactionIdentifier.generateDsrpWithUcafTransactionIdentifier(panByteArray, null);
    }

    @Test(expected = InvalidInput.class)
    public void testVerifyDsrpWithUcafTransactionIdentifierWithInvalidPanLength1() throws
            Exception {
        String pan = "54133390";
        String ucaf = "AHRbjBgsn2DeAAXsy27/AgBVFA==";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray ucafByteArray = ByteArray.of(ucaf.getBytes());

        // FIXME: This test does nothing!
        TransactionIdentifier
                .generateDsrpWithUcafTransactionIdentifier(panByteArray, ucafByteArray);
    }


    @Test(expected = InvalidInput.class)
    public void testVerifyDsrpWithUcafTransactionIdentifierWithInvalidPanLength2() throws
            Exception {
        String pan = "5413339000001513123131233131";
        String ucaf = "AHRbjBgsn2DeAAXsy27/AgBVFA==";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray ucafByteArray = ByteArray.of(ucaf.getBytes());

        // FIXME: This test does nothing!
        TransactionIdentifier
                .generateDsrpWithUcafTransactionIdentifier(panByteArray, ucafByteArray);
    }

    @Test
    public void testVerifyDsrpWithUcafTransactionIdentifierWithEvenPan() throws Exception {
        String EXPECTED_DATA = "f2a491c260e132989522d3748dfcce9361d74e821ca40dda43b74d9ca470546a";
        String pan = "5413339000001513";
        String ucaf = "AHRbjBgsn2DeAAXsy27/AgBVFA==";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray ucafByteArray = ByteArray.of(ucaf.getBytes());

        final ByteArray result = TransactionIdentifier
                .generateDsrpWithUcafTransactionIdentifier(panByteArray, ucafByteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().toLowerCase());
    }

    @Test
    public void testVerifyDsrpWithUcafTransactionIdentifierWithOddPan() throws Exception {
        String EXPECTED_DATA = "7740df1b424dd9b83f16908b927c4b7b9af1f55361c05cbdf2ce6f21d425588e";
        String pan = "541333900000151";
        String ucaf = "AHRbjBgsn2DeAAXsy27/AgBVFA==";
        ByteArray panByteArray = ByteArray.of(pan.getBytes());
        ByteArray ucafByteArray = ByteArray.of(ucaf.getBytes());

        final ByteArray result = TransactionIdentifier
                .generateDsrpWithUcafTransactionIdentifier(panByteArray, ucafByteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().toLowerCase());
    }

    @Test
    public void testGetMagstripeWithNullInputs() throws Exception {
        ByteArray transactionIdentifier = TransactionIdentifier.getMagstripe(null, null);

        assertEquals(null, transactionIdentifier);

        transactionIdentifier = TransactionIdentifier.getMagstripe(null, ByteArray.of(""));
        assertEquals(null, transactionIdentifier);

        transactionIdentifier = TransactionIdentifier.getMagstripe(ByteArray.of(""), null);
        assertEquals(null, transactionIdentifier);

        transactionIdentifier = TransactionIdentifier.getMagstripe(ByteArray.of(""),
                                                                   ByteArray.of(""));
        assertEquals(null, transactionIdentifier);
    }

    @Test
    public void testRealDataExample() throws Exception {
        String EXPECTED_DATA = "0000000000000000000000000000000083a80002ee8b1a4807f0f8f3f671cdc6";
        ByteArray track2Data = ByteArray.of("5272323300946965D19042010997688070423F");

        ByteArray result = TransactionIdentifier.getMagstripe(null, track2Data);
        Assert.assertEquals(EXPECTED_DATA,
                            result == null ? "" : result.toHexString().toLowerCase());
    }

    /**
     * Calculate Hash of track2 data
     *
     * @param track2Data Track2 Data
     * @return Hash of Track2 data of 16 bytes
     */
    private static ByteArray encodeTrack2Data(final ByteArray track2Data) {

        final byte nibbleValue = 0X0F;
        final byte fieldSeparator = 0X3D;
        final byte replacedFieldSeparator = 0X0D;
        int count = 0;

        // Final byte array of track1Data
        byte[] track2ByteArray;

        boolean isPanOddLength = track2Data.getLength() % 2 != 0;

        if (isPanOddLength) {
            track2ByteArray = new byte[track2Data.getLength() / 2 + 1];
        } else {
            track2ByteArray = new byte[track2Data.getLength() / 2];
        }


        for (int i = 0; i < track2Data.getLength(); i = i + 2) {
            byte oneNibbleValue = track2Data.getByte(i);
            oneNibbleValue = (byte) ((oneNibbleValue << 4) & 0xF0);

            if (oneNibbleValue == fieldSeparator) {
                oneNibbleValue = replacedFieldSeparator;
            }
            if (isPanOddLength && (track2Data.getLength() - i) == 1) {
                oneNibbleValue = (byte) (oneNibbleValue | nibbleValue);
            } else {

                byte secondNibble = (byte) (track2Data.getByte(i + 1) & 0x0F);
                if (secondNibble == fieldSeparator) {
                    secondNibble = replacedFieldSeparator;
                }
                oneNibbleValue = (byte) (oneNibbleValue | secondNibble);
            }
            track2ByteArray[count] = oneNibbleValue;
            count++;
        }

        return ByteArray.of(track2ByteArray);
    }

    /**
     * To verify TransactionIdentifier.getMagstripe(final ByteArray track1DynamicData,final
     * ByteArray track2DynamicData)
     * with data give in MDES API spec example
     */
    @Test
    public void testGetMagstripe1() {

        // track1DynamicData and track2DynamicData given in example in MDES API 6.3.3.3 example spec
        final String track1DynamicData = "B1234987623458765^RULES/MDES  ^1509123000000000";
        final String track2DynamicData = "1234987623458765=15091230000000000000";

        try {
            ByteArray transactionIdentifier =
                    TransactionIdentifier.getMagstripe(
                            ByteArray.of(track1DynamicData.getBytes()),
                            encodeTrack2Data(ByteArray.of(track2DynamicData.getBytes())));
            Assert.assertEquals("ea6fe411a4307fcff5f10f039d1f74f5a334daeee4f7a991c5def7510ed4fb04",
                                transactionIdentifier.toHexString().toLowerCase());
        } catch (InvalidInput | McbpCryptoException invalidInput) {
            invalidInput.printStackTrace();
        }

    }

    /**
     * To verify SHA-256 of track1data of example given in MDES API spec (6.3.3.3)
     */
    @Test
    public void testComputeSha256OfTrack2() {
        // ASCII hex encoded
        final String track2Data = "1234987623458765D15091230000000000000F";
        byte[] dataByte = ByteArray.of(track2Data).getBytes();
        byte[] digest = new byte[0];
        try {
            digest = CryptoServiceFactory.getDefaultCryptoService().sha256(dataByte);
        } catch (McbpCryptoException e) {
            e.printStackTrace();
        }
        Assert.assertEquals
                ("b75c0ff874d16e55727b0a498b3306cea334daeee4f7a991c5def7510ed4fb04",
                 ByteArray.of(digest).toString().toLowerCase());

    }

    /**
     * To verify hash of track2data of example given in MDES API spec (6.3.3.3)
     */
    @Test
    public void testComputeSha256OfTrack1(
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // ASCII value
        final String track1Data = "B1234987623458765^RULES/MDES  ^1509123000000000";
        byte[] dataByte = track1Data.getBytes();
        byte[] digest = new byte[0];
        try {
            digest = CryptoServiceFactory.getDefaultCryptoService().sha256(dataByte);
        } catch (McbpCryptoException e) {
            e.printStackTrace();
        }
        Assert.assertEquals
                ("272d11c54b10af21bd1c2e93cfa7cfbbea6fe411a4307fcff5f10f039d1f74f5",
                 ByteArray.of(digest).toString().toLowerCase());
    }

    @Test
    public void testGetMagstripe2() {
        final String track1DynamicData = "B5329943634296135^  /^21122011004892020668";
        final String track2DynamicData = "5329943634296135=21122011004892020668";


        ByteArray transactionIdentifier = null;
        try {
            transactionIdentifier = TransactionIdentifier
                    .getMagstripe(ByteArray.of(track1DynamicData.getBytes()),
                                  encodeTrack2Data(ByteArray.of(track2DynamicData.getBytes())));
        } catch (InvalidInput | McbpCryptoException invalidInput) {
            invalidInput.printStackTrace();
        }
        Assert.assertEquals
                ("450ef089ea727311adba3aead036fd32cbd9963afa349853bd8ce8ce02934a7e",
                 transactionIdentifier.toHexString().toLowerCase());
    }
}