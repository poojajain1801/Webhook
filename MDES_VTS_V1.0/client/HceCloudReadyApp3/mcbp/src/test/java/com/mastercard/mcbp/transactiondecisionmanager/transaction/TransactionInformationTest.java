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

package com.mastercard.mcbp.transactiondecisionmanager.transaction;

import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Transaction Information
 */
public class TransactionInformationTest {
    private String mMobileSupportIndicator;
    private String mAuthorisedAmount;
    private String mOtherAmount;
    private String mTransactionType;
    private String mMerchantCategoryCode;
    private String mTerminalType;
    private String mCvmResults;

    private String mTerminalRiskManagementData;
    private String mTerminalCapabilities;
    private TerminalInformation mTerminalInformation;

    @Before
    public void setUp() {
        mMobileSupportIndicator = null;
        mAuthorisedAmount = null;
        mOtherAmount = null;
        mTransactionType = null;
        mMerchantCategoryCode = null;
        mTerminalType = null;
        mCvmResults = null;

        mTerminalRiskManagementData = null;
        mTerminalCapabilities = null;
        mTerminalInformation = null;
    }
    @Test
    public void testForMchipLowValue() throws Exception {
        final byte[] applicationLabel = "mastercard".getBytes();

        mAuthorisedAmount = "000000000500";
        mOtherAmount = "000000000000";
        mMerchantCategoryCode = "0000";
        mCvmResults = "1F0002";
        mMobileSupportIndicator = "00";

        final byte[] apdu = buildGenerateAcApdu();

        final GenerateAcCommandApdu generateAcCommandApdu = new GenerateAcCommandApdu(apdu);

        final DolRequestList pdolList = DolRequestList.of(ByteArray.of("9F7E01").getBytes());
        final byte[] pdolData = ByteArray.of(mMobileSupportIndicator).getBytes();
        final DolValues pdolValues = DolValues.of(pdolList, pdolData);

        final TransactionInformation transactionInformation = TransactionInformation.forMchip(
                applicationLabel, generateAcCommandApdu, pdolValues);

        assertEquals(TransactionRange.LOW_VALUE,
                     transactionInformation.getTransactionRange());
        assertEquals(ExpectedUserActionOnPoi.NONE,
                     transactionInformation.getExpectedUserActionOnPoi());
    }

    @Test
    public void testForMchipTransit() throws Exception {
        final byte[] applicationLabel = "mastercard".getBytes();

        mAuthorisedAmount = "000000000000";
        mOtherAmount = "000000000000";
        mMerchantCategoryCode = "4111";
        mCvmResults = "1F0002";
        mMobileSupportIndicator = "00";

        final byte[] apdu = buildGenerateAcApdu();

        final GenerateAcCommandApdu generateAcCommandApdu = new GenerateAcCommandApdu(apdu);

        final DolRequestList pdolList = DolRequestList.of(ByteArray.of("9F7E01").getBytes());
        final byte[] pdolData = ByteArray.of(mMobileSupportIndicator).getBytes();
        final DolValues pdolValues = DolValues.of(pdolList, pdolData);

        final TransactionInformation transactionInformation = TransactionInformation.forMchip(
                applicationLabel, generateAcCommandApdu, pdolValues);

        assertEquals(TransactionRange.LOW_VALUE,
                     transactionInformation.getTransactionRange());
        assertEquals(TransactionType.TRANSIT,
                     transactionInformation.getTransactionType());
        assertEquals(ExpectedUserActionOnPoi.NONE,
                     transactionInformation.getExpectedUserActionOnPoi());
    }

    @Test
    public void testForMchipHighValueWithCdCvmSupport() throws Exception {
        final byte[] applicationLabel = "mastercard".getBytes();

        mAuthorisedAmount = "000000012582";
        mOtherAmount = "000000000000";
        mMerchantCategoryCode = "0000";
        mCvmResults = "020000";
        mMobileSupportIndicator = "00";

        final byte[] apdu = buildGenerateAcApdu();

        final GenerateAcCommandApdu generateAcCommandApdu = new GenerateAcCommandApdu(apdu);

        final DolRequestList pdolList = DolRequestList.of(ByteArray.of("9F7E01").getBytes());
        final byte[] pdolData = ByteArray.of(mMobileSupportIndicator).getBytes();
        final DolValues pdolValues = DolValues.of(pdolList, pdolData);

        final TransactionInformation transactionInformation = TransactionInformation.forMchip(
                applicationLabel, generateAcCommandApdu, pdolValues);

        assertEquals(TransactionRange.HIGH_VALUE,
                     transactionInformation.getTransactionRange());
        assertEquals(TransactionType.PURCHASE,
                     transactionInformation.getTransactionType());
        assertEquals(ExpectedUserActionOnPoi.ONLINE_PIN,
                     transactionInformation.getExpectedUserActionOnPoi());
        assertEquals(125.82, transactionInformation.getAuthorizedAmount(), 0.01);
    }

    @Test
    public void testForMagstripe() throws Exception {
        final byte[] applicationLabel = "mastercard".getBytes();
        mMobileSupportIndicator = "01";
        mAuthorisedAmount = "000000000500";
        mTransactionType = "00";
        mMerchantCategoryCode = "0000";
        mTerminalType = "22";
        mMobileSupportIndicator = "00";

        // FIXME: Add Terminal Risk Management Data
        final ComputeCcCommandApdu computeCcCommandApdu = buildComputeCcApdu();

        // TODO: Create a proper UDOL test
        final DolRequestList udolList = DolRequestList.of(ByteArray.of("9F7E01").getBytes());
        final byte[] udolData = ByteArray.of(mMobileSupportIndicator).getBytes();
        final DolValues udolValues = DolValues.of(udolList, udolData);

        final TransactionInformation transactionInformation = TransactionInformation.forMagstripe(
                applicationLabel, computeCcCommandApdu, udolValues);
    }

    @Test
    public void testForMagstripeWithMobileSupportIndicator() throws Exception {
        final byte[] applicationLabel = "mastercard".getBytes();
        mMobileSupportIndicator = "01";
        mAuthorisedAmount = "000000000500";
        mTransactionType = "00";
        mMerchantCategoryCode = "0000";
        mTerminalType = "22";
        mMobileSupportIndicator = "00";

        mTerminalCapabilities = "000800";

        // FIXME: Add Terminal Risk Management Data
        final ComputeCcCommandApdu computeCcCommandApdu = buildComputeCcApdu();

        // TODO: Create a proper UDOL test
        final DolRequestList udolList = DolRequestList.of(ByteArray.of("9F3303").getBytes());
        final byte[] udolData = ByteArray.of(mTerminalCapabilities).getBytes();
        final DolValues udolValues = DolValues.of(udolList, udolData);

        final TransactionInformation transactionInformation = TransactionInformation.forMagstripe(
                applicationLabel, computeCcCommandApdu, udolValues);
    }

    @Test
    public void testForMagstripeWithEmptyTerminalCapabilities() throws Exception {
        final byte[] applicationLabel = "mastercard".getBytes();
        mMobileSupportIndicator = "01";
        mAuthorisedAmount = "000000000500";
        mTransactionType = "00";
        mMerchantCategoryCode = "0000";
        mTerminalType = "22";
        mMobileSupportIndicator = "00";

        mTerminalCapabilities = "000000";

        final ComputeCcCommandApdu computeCcCommandApdu = buildComputeCcApdu();

        final DolRequestList udolList = DolRequestList.of(ByteArray.of("9F3303").getBytes());
        final byte[] udolData = ByteArray.of(mTerminalCapabilities).getBytes();
        final DolValues udolValues = DolValues.of(udolList, udolData);

        final TransactionInformation transactionInformation = TransactionInformation.forMagstripe(
                applicationLabel, computeCcCommandApdu, udolValues);
    }

    @Test
    public void testForRemotePayment() throws Exception {
        // TODO: To be done
    }

    private ComputeCcCommandApdu buildComputeCcApdu() {
        final String apdu = "802A8E8016" +
                            "00000413" + // Unpredictable Number
                            mMobileSupportIndicator +
                            mAuthorisedAmount +
                            "0978" + // Transaction Currency Code
                            "0056" + // Terminal Country Code
                            mTransactionType +
                            "150216" + // Transaction Date
                            mMerchantCategoryCode +
                            mTerminalType +
                            "00";
        final String defaultUdolList = "9F6A049F7E019F02065F2A029F1A029C019A039F15029F3501";
        final DolRequestList udolList = DolRequestList.of(ByteArray.of(defaultUdolList).getBytes());

        return new ComputeCcCommandApdu(ByteArray.of(apdu).getBytes(),
                                        udolList);
    }

    private byte[] buildGenerateAcApdu() {
        final String apdu = "80AE90002D" +
                            mAuthorisedAmount +
                            mOtherAmount +
                            "0056" + // Terminal Country Code
                            "0020000000" + // Terminal Verification Results
                            "0840" + // Transaction Currency Code
                            "160117" + // Transaction Date
                            "00" + // Transaction Type
                            "42645966" + // Unpredictable Number
                            "22" + // Terminal Type
                            "0000" + // Data Authentication Code
                            "0000000000000000" + // ICC Dynamic Number
                            mCvmResults +
                            mMerchantCategoryCode +
                            "00";
        return ByteArray.of(apdu).getBytes();
    }
}