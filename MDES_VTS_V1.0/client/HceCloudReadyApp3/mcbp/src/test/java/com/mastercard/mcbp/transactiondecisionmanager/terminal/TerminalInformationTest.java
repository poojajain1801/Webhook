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

package com.mastercard.mcbp.transactiondecisionmanager.terminal;

import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import static com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalTechnology
        .CONTACTLESS_EMV;
import static com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalTechnology
        .CONTACTLESS_MAGSTRIPE;
import static com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalTechnology
        .REMOTE_DSRP_EMV;
import static com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalTechnology
        .REMOTE_DSRP_UCAF;
import static com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalType
        .CARDHOLDER_OPERATED;
import static com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalType
        .MERCHANT_ATTENDED;
import static org.junit.Assert.*;

/**
 * Test class for Terminal Information
 */
public class TerminalInformationTest {

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
    public void testForMchip() throws Exception {
        mAuthorisedAmount = "000000000500";
        mOtherAmount = "000000000000";
        mMerchantCategoryCode = "0000";
        mCvmResults = "1F0002";
        mMobileSupportIndicator = "03";

        mTerminalRiskManagementData = "0400000000000000";
        mTerminalCapabilities = "000000";



        mTerminalInformation = buildMchipTerminalInformation();

        verifyExpected(MERCHANT_ATTENDED,
                       CONTACTLESS_EMV,
                       PersistentTransactionContext.YES,
                       CdCvmSupport.YES);
    }

    @Test
    public void testForMagstripe() throws Exception {
        mMobileSupportIndicator = "01";
        mAuthorisedAmount = "000000000500";
        mTransactionType = "00";
        mMerchantCategoryCode = "0000";
        mTerminalType = "22";

        mTerminalRiskManagementData = "0400000000000000";
        mTerminalCapabilities = "000000";

        mTerminalInformation = buildMagstripeTerminalInformation();

        verifyExpected(MERCHANT_ATTENDED,
                       CONTACTLESS_MAGSTRIPE,
                       PersistentTransactionContext.YES,
                       CdCvmSupport.YES);

        // Another test

        mMobileSupportIndicator = "01";
        mAuthorisedAmount = "000000000500";
        mTransactionType = "00";
        mMerchantCategoryCode = "0000";
        mTerminalType = "22";

        mTerminalRiskManagementData = "0400000000000000";
        mTerminalCapabilities = "000000";

        mTerminalInformation = buildMagstripeTerminalInformation();

        verifyExpected(MERCHANT_ATTENDED,
                       CONTACTLESS_MAGSTRIPE,
                       PersistentTransactionContext.YES,
                       CdCvmSupport.YES);
    }

    private void verifyExpected(final TerminalType terminalType,
                                final TerminalTechnology terminalTechnology,
                                final PersistentTransactionContext persistentTransactionContext,
                                final CdCvmSupport cdCvmSupport) {
        assertEquals(terminalType, mTerminalInformation.getTerminalType());
        assertEquals(terminalTechnology, mTerminalInformation.getTerminalTechnology());
        assertEquals(persistentTransactionContext,
                     mTerminalInformation.getPersistentTransactionContext());
        assertEquals(cdCvmSupport, mTerminalInformation.getCdCvmSupport());
    }

    @Test
    public void testForRemotePayment() throws Exception {
        final CryptogramInput cryptogramInput1 =
                CryptogramInput.forUcaf(ByteArray.of(new byte[4]));

        mTerminalInformation = TerminalInformation.forRemotePayment(cryptogramInput1);

        verifyExpected(CARDHOLDER_OPERATED,
                       REMOTE_DSRP_UCAF,
                       PersistentTransactionContext.UNKNOWN,
                       CdCvmSupport.UNKNOWN);

        final CryptogramInput cryptogramInput2 = CryptogramInput.forDe55(ByteArray.of(new byte[6]),
                                                                         ByteArray.of(new byte[6]),
                                                                         ByteArray.of(new byte[2]),
                                                                         ByteArray.of(new byte[2]),
                                                                         ByteArray.of(new byte[2]),
                                                                         ByteArray.of(new byte[4]),
                                                                         (byte) 0x00);

        mTerminalInformation = TerminalInformation.forRemotePayment(cryptogramInput2);

        verifyExpected(CARDHOLDER_OPERATED,
                       REMOTE_DSRP_EMV,
                       PersistentTransactionContext.UNKNOWN,
                       CdCvmSupport.UNKNOWN);
    }

    private GenerateAcCommandApdu buildGenerateAcApdu() {
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
        return new GenerateAcCommandApdu(ByteArray.of(apdu).getBytes());
    }


    private TerminalInformation buildMchipTerminalInformation() {
        final GenerateAcCommandApdu generateAcCommandApdu = buildGenerateAcApdu();

        final DolRequestList pdolList = DolRequestList.of(ByteArray.of("9F1D089F7E01").getBytes());
        final byte[] pdolData =
                ByteArray.of(mTerminalRiskManagementData + mMobileSupportIndicator).getBytes();
        final DolValues pdolValues = DolValues.of(pdolList, pdolData);

        return TerminalInformation.forMchip(generateAcCommandApdu, pdolValues, buildSampleDol());
    }

    private TerminalInformation buildMagstripeTerminalInformation() {
        final ComputeCcCommandApdu computeCcCommandApdu = buildComputeCcApdu();

        final DolRequestList pdolList = DolRequestList.of(ByteArray.of("9F1D089F3303").getBytes());
        final byte[] pdolData =
                ByteArray.of(mTerminalRiskManagementData + mTerminalCapabilities).getBytes();
        final DolValues pdolValues = DolValues.of(pdolList, pdolData);

        return TerminalInformation.forMagstripe(computeCcCommandApdu, pdolValues, buildSampleDol());
    }

    private DolValues buildSampleDol() {
        // Just used a random 9F11 tag for the test as the value does not really matter
        final DolRequestList cdolRequestList = DolRequestList.of(ByteArray.of("9F1108").getBytes());
        final byte[] cdolData = ByteArray.of(mTerminalRiskManagementData).getBytes();
        return DolValues.of(cdolRequestList, cdolData);
    }

    private ComputeCcCommandApdu buildComputeCcApdu() {
        final String apdu = "802A8E801E" +
                            "00000413" + // Unpredictable Number
                            mMobileSupportIndicator +
                            mAuthorisedAmount +
                            "0978" + // Transaction Currency Code
                            "0056" + // Terminal Country Code
                            mTransactionType +
                            "150216" + // Transaction Date
                            mMerchantCategoryCode +
                            mTerminalType +
                            mTerminalRiskManagementData +
                            "00";
        final String defaultUdolList = "9F6A049F7E019F02065F2A029F1A029C019A039F15029F35019F1D08";
        final DolRequestList udolList = DolRequestList.of(ByteArray.of(defaultUdolList).getBytes());

        return new ComputeCcCommandApdu(ByteArray.of(apdu).getBytes(),
                                        udolList);
    }
}