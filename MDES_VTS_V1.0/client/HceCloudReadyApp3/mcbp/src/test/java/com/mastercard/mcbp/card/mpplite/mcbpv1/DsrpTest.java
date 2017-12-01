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

package com.mastercard.mcbp.card.mpplite.mcbpv1;


import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.mobilekernel.CryptogramType;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpOutputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mobilekernel.MobileKernel;
import com.mastercard.mcbp.card.mobilekernel.RemotePaymentResultCode;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;
import com.mastercard.mcbp.utils.UnitTestMcbpLoggerFactory;
import com.mastercard.mcbp.utils.UnitTestPinValidator;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Date;
import com.mastercard.mobile_api.utils.Tlv;
import com.mastercard.mobile_api.utils.Utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DsrpTest {
    /**
     * MPP Lite to be used for the execution of tests
     */
    private MppLite mMppLite;

    /**
     * DSRP Input data (common values shared among test cases)
     */
    private DsrpInputData mDsrpInputData;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Initialize factories: LoggerFactory and McbpCryptoFactory
        McbpLoggerFactory.setInstance(new UnitTestMcbpLoggerFactory(), null);
    }

    @Before
    public void setUp() throws Exception {
        // Code executed before each test
        final String cardName = "MasterCard_MCBP_PersoProfile_1";
        String testAtc = "0001";  // We always use the first key (ATC = 0001)
        final String pinString = "31323334";

        final DigitizedCardProfile profile = RegToolTestUtils.getProfileByName(cardName);

        // Get Card Profile, SUK, PIN
        final int atc = Integer.valueOf(testAtc, 16);

        final SingleUseKey suk = RegToolTestUtils.getSingleUseKey(
                profile.getDigitizedCardId().toHexString(),
                pinString,
                atc).toSingleUseKey();

        final UnitTestPinValidator validator =
                new UnitTestPinValidator(ByteArray.of(pinString), true);

        final TransactionCredentials credentials = new TransactionCredentials(
                validator.authenticate(suk.getContent().getSukContactlessUmd()),
                suk.getContent().getSessionKeyContactlessMd(),
                suk.getContent().getAtc(),
                suk.getContent().getIdn());

        mMppLite = MppLiteMcbpV1Factory.buildV1(profile.getMppLiteModule(),
                                                getTransactionCredentialsManager(credentials),
                                                validator,
                                                getConsentManager());

        mMppLite.startRemotePayment();

        mDsrpInputData = new DsrpInputData();
        mDsrpInputData.setCountryCode((char) 250);
        mDsrpInputData.setCurrencyCode((char) 978);
        mDsrpInputData.setOtherAmount(0);
        mDsrpInputData.setTransactionAmount(0);
        mDsrpInputData.setTransactionDate(new Date(2015, 6, 13));
        mDsrpInputData.setUnpredictableNumber(1);
        mDsrpInputData.setCryptogramType(CryptogramType.UCAF);
        mDsrpInputData.setTransactionType((byte)0x00);
    }

    @Test
    public void testLudoError() throws Exception {
        // Code executed before each test
        final String cardName = "mcbp_card_dsrp_test_1";
        String testAtc = "0001";  // We always use the first key (ATC = 0001)
        final String pinString = "31323334";

        final DigitizedCardProfile profile = RegToolTestUtils.getProfileByName(cardName);

        // Get Card Profile, SUK, PIN
        final int atc = Integer.valueOf(testAtc, 16);

        final SingleUseKey suk = RegToolTestUtils.getSingleUseKey(
                profile.getDigitizedCardId().toHexString(),
                pinString,
                atc).toSingleUseKey();

        final UnitTestPinValidator validator =
                new UnitTestPinValidator(ByteArray.of(pinString), true);

        final TransactionCredentials credentials = new TransactionCredentials(
                validator.authenticate(suk.getContent().getSukContactlessUmd()),
                suk.getContent().getSessionKeyContactlessMd(),
                suk.getContent().getAtc(),
                suk.getContent().getIdn());

        mMppLite = MppLiteMcbpV1Factory.buildV1(profile.getMppLiteModule(),
                                                getTransactionCredentialsManager(credentials),
                                                validator,
                                                getConsentManager());

        mMppLite.startRemotePayment();

        mDsrpInputData = new DsrpInputData();
        mDsrpInputData.setCountryCode((char) 250);
        mDsrpInputData.setCurrencyCode((char) 978);
        mDsrpInputData.setOtherAmount(0);
        mDsrpInputData.setTransactionAmount(0);
        mDsrpInputData.setTransactionDate(new Date(2015, 6, 13));
        mDsrpInputData.setUnpredictableNumber(1);

        mDsrpInputData.setCryptogramType(CryptogramType.DE55);
        mDsrpInputData.setTransactionAmount(1599);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.DECLINED, result.getCode());
        assertEquals(null, result.getData());
    }

    @Test
    public void testLudoError2() throws Exception {
        // Code executed before each test
        final String cardName = "mcbp_card_dsrp_test_wrong_psn";
        String testAtc = "0001";  // We always use the first key (ATC = 0001)
        final String pinString = "31323334";

        final DigitizedCardProfile profile = RegToolTestUtils.getProfileByName(cardName);

        // Get Card Profile, SUK, PIN
        final int atc = Integer.valueOf(testAtc, 16);

        final SingleUseKey suk = RegToolTestUtils.getSingleUseKey(
                profile.getDigitizedCardId().toHexString(),
                pinString,
                atc).toSingleUseKey();

        final UnitTestPinValidator validator =
                new UnitTestPinValidator(ByteArray.of(pinString), true);

        final TransactionCredentials credentials = new TransactionCredentials(
                validator.authenticate(suk.getContent().getSukContactlessUmd()),
                suk.getContent().getSessionKeyContactlessMd(),
                suk.getContent().getAtc(),
                suk.getContent().getIdn());

        mMppLite = MppLiteMcbpV1Factory.buildV1(profile.getMppLiteModule(),
                                                getTransactionCredentialsManager(credentials),
                                                validator,
                                                getConsentManager());

        mMppLite.startRemotePayment();

        mDsrpInputData = new DsrpInputData();
        mDsrpInputData.setCountryCode((char) 250);
        mDsrpInputData.setCurrencyCode((char) 978);
        mDsrpInputData.setOtherAmount(0);
        mDsrpInputData.setTransactionAmount(0);
        mDsrpInputData.setTransactionDate(new Date(2015, 6, 13));
        mDsrpInputData.setUnpredictableNumber(1);

        mDsrpInputData.setCryptogramType(CryptogramType.UCAF);
        mDsrpInputData.setTransactionAmount(1599);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());
        assertEquals(null, result.getData());
    }

    @Test
    public void ucafTest() throws Exception {

        mDsrpInputData.setCryptogramType(CryptogramType.UCAF);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

        final DsrpOutputData dsrpOutputData = result.getData();
        final String expectedUcafAuthorizationMessage =
                "0000006D110070040700098180001054133390000015130000000000000000001512453030313031"
                + "4530333334360000010030303030303030303030343800003032303031313031205434331C4146"
                + "2F50635A5647485259584141454141414142476F414246413D3D0978";
        assertEquals(expectedUcafAuthorizationMessage, buildAuthorizationMessage(dsrpOutputData));
    }

    @Test
    public void invalidDateInTransactionDateTest() throws Exception {
        mDsrpInputData.setTransactionDate(new Date(2016,11,42));

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void invalidMonthInTransactionDateTest() throws Exception {
        mDsrpInputData.setTransactionDate(new Date(2016,13,22));

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void pastYearInTransactionDateTest() throws Exception {
        mDsrpInputData.setTransactionDate(new Date(1857,12,22));

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void futureYearInTransactionDateTest() throws Exception {
        mDsrpInputData.setTransactionDate(new Date(3057,12,22));

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void zeroUnpredictableNumberTest() throws Exception {
        mDsrpInputData.setUnpredictableNumber(0);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void maxLongUnpredictableNumberTest() throws Exception {
        mDsrpInputData.setUnpredictableNumber(999999999999999999L);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }


    @Test
    public void ucafCryptogramTypeTest() throws Exception {
        mDsrpInputData.setCryptogramType(CryptogramType.UCAF);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    /*@Test
    public void invalidCryptogramType() throws Exception {
        mDsrpInputData.setCryptogramType(CryptogramType.fromString("abcd"));

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }*/

    @Test
    public void de55CryptogramTypeTest() throws Exception {
        mDsrpInputData.setCryptogramType(CryptogramType.DE55);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void negativeTransactionTypeTest() throws Exception {
        mDsrpInputData.setTransactionType((byte)-1);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void zeroTransactionTypeTest() throws Exception {
        mDsrpInputData.setTransactionType((byte)0);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void maxAllowedTransactionTypeTest() throws Exception {
        mDsrpInputData.setTransactionType((byte)99);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void greaterThanMaxAllowedTransactionTypeTest() throws Exception {
        mDsrpInputData.setTransactionType((byte) 100);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());
    }

    @Test
    public void negativeCountryCodeTest() throws Exception {
        mDsrpInputData.setCountryCode((char)-1);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void zeroCountryCodeTest() throws Exception {
        mDsrpInputData.setCountryCode((char)0);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void validCountryCodeTest() throws Exception {
        mDsrpInputData.setCountryCode((char)37);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void maxAllowedCountryCodeTest() throws Exception {
        mDsrpInputData.setCountryCode((char)999);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void greaterThanMaxAllowedCountryCodeTest() throws Exception {
        mDsrpInputData.setCountryCode((char)1000);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());
    }


    @Test
    public void negativeCurrencyCodeTest() throws Exception {
        mDsrpInputData.setCurrencyCode((char)-1);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void zeroCurrencyCodeTest() throws Exception {
        mDsrpInputData.setCurrencyCode((char)0);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void validCurrencyCodeTest() throws Exception {
        mDsrpInputData.setCurrencyCode((char)37);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void maxAllowedCurrencyCodeTest() throws Exception {
        mDsrpInputData.setCurrencyCode((char)999);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void greaterThanMaxAllowedCurrencyCodeTest() throws Exception {
        mDsrpInputData.setCurrencyCode((char)1000);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());
    }

    @Test
    public void negativeOtherAmountTest() throws Exception {
        mDsrpInputData.setOtherAmount(-1);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void zeroOtherAmountTest() throws Exception {
        mDsrpInputData.setOtherAmount(0);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void validOtherAmountTest() throws Exception {
        mDsrpInputData.setOtherAmount(11);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void maxAllowedOtherAmountTest() throws Exception {
        mDsrpInputData.setOtherAmount(999999999999L);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void greaterThanMaxAllowedOtherAmountTest() throws Exception {
        mDsrpInputData.setOtherAmount(1000000000000L);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());
    }

    @Test
    public void negativeTransactionAmountTest() throws Exception {
        mDsrpInputData.setTransactionAmount(-1);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());

    }

    @Test
    public void zeroTransactionAmountTest() throws Exception {
        mDsrpInputData.setTransactionAmount(0);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void validTransactionAmountTest() throws Exception {
        mDsrpInputData.setTransactionAmount(11);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void maxAllowedTransactionAmountTest() throws Exception {
        mDsrpInputData.setTransactionAmount(999999999999L);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

    }

    @Test
    public void greaterThanMaxAllowedTransactionAmountTest() throws Exception {
        mDsrpInputData.setTransactionAmount(1000000000000L);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.ERROR_INVALID_INPUT, result.getCode());
    }

    @Test
    public void de55Test() throws Exception {

        mDsrpInputData.setCryptogramType(CryptogramType.DE55);
        mDsrpInputData.setTransactionAmount(1599);

        final DsrpResult result = getTransactionRecord(mDsrpInputData);
        assertEquals(RemotePaymentResultCode.OK, result.getCode());

        final DsrpOutputData dsrpOutputData = result.getData();
        final String expectedUcafAuthorizationMessage =
                "000000D41100700407000980820010541333900000151300000000000000063F1512453030313031"
                + "45303333343600000100303030303030303030303438000030323030313130310978799F2608A8"
                + "38C835BF87D4239F10120114A50000000000000000377FE34EAF00FF9F36020001950500000000"
                + "009F2701809F34030100029F3704000000019F02060000000015999F03060000000000005F2A02"
                + "09789A031506139C01005A0854133390000015135F3401005F24031512319F1A02025082021A80";
        assertEquals(expectedUcafAuthorizationMessage, buildAuthorizationMessage(dsrpOutputData));
    }

    @After
    public void tearDown() throws Exception {
        // Code executed after each test
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // Code executed after the last test method
    }

    private DsrpResult getTransactionRecord(final DsrpInputData dsrpInputData) throws Exception {
        return MobileKernel.generateDsrpData(dsrpInputData, mMppLite);
    }

    private static String buildAuthorizationMessage(final DsrpOutputData outputData) {
        final String messageType = "1100";
        final CryptogramType cryptogramType = outputData.getCryptogramType();

        final String pan = ByteArray.of((byte) outputData.getPan().length()).toHexString() +
                           outputData.getPan();  // Very complex way of converting the length
        final String processingCode = "000000";
        final String amount =
                Utils.longToBcdByteArray(outputData.getTransactionAmount(), 6).toHexString();

        final String expiryYear =
                String.format(Locale.ENGLISH, "%02d", outputData.getExpiryDate().getYear());
        final String expiryMonth =
                String.format(Locale.ENGLISH, "%02d", outputData.getExpiryDate().getMonth());
        final String expiryDate = expiryYear + expiryMonth;
        final String posEntryMode = "453030313031453033333436";
        final String cardSequenceNumber =
                String.format(Locale.ENGLISH, "%04d", outputData.getPanSequenceNumber());
        final String functionCode = "0100";
        final String retrievalReferenceNumber = "303030303030303030303438";
        final String serviceCode = "0000";
        final String terminalId = "3032303031313031";
        final String currencyTransaction =
                String.format(Locale.ENGLISH, "%04d", outputData.getCurrencyCode());

        final String messageLength;
        final String primaryBitmap;

        if (cryptogramType == CryptogramType.DE55) {
            messageLength = "000000D4";
            primaryBitmap = "7004070009808200";
            final String de55 = outputData.getTransactionCryptogramData().toHexString();
            final String de55Length = ByteArray.of((byte) (de55.length() / 2)).toHexString();
            final String additionalDataPrivate = de55Length + de55;

            return messageLength
                   + messageType
                   + primaryBitmap
                   + pan
                   + processingCode
                   + amount
                   + expiryDate
                   + posEntryMode
                   + cardSequenceNumber
                   + functionCode
                   + retrievalReferenceNumber
                   + serviceCode
                   + terminalId
                   + currencyTransaction
                   + additionalDataPrivate;

        } else if (cryptogramType == CryptogramType.UCAF) {
            messageLength = "0000006D";
            primaryBitmap = "7004070009818000";
            final ByteArray ucaf = outputData.getTransactionCryptogramData();
            final String de48Tlv = Tlv.create("543433", ucaf.toHexString());
            final String de48TlvLength = ByteArray.of((byte) (de48Tlv.length() / 2)).toHexString();
            final String additionalDataPrivate = de48TlvLength + de48Tlv;
            return messageLength
                   + messageType
                   + primaryBitmap
                   + pan
                   + processingCode
                   + amount
                   + expiryDate
                   + posEntryMode
                   + cardSequenceNumber
                   + functionCode
                   + retrievalReferenceNumber
                   + serviceCode
                   + terminalId
                   + additionalDataPrivate
                   + currencyTransaction;
        } else {
            throw new IllegalArgumentException("Invalid Cryptogram Type");
        }
    }

    private TransactionCredentialsManager getTransactionCredentialsManager(
            final TransactionCredentials credentials) {
        return new TransactionCredentialsManager() {
            @Override
            public TransactionCredentials getValidUmdAndMdCredentialsFor(
                    final Scope scope) {
                switch (scope) {
                    case REMOTE_PAYMENT:
                        return credentials;
                    case CONTACTLESS:
                    default:
                        return null;
                }
            }

            @Override
            public TransactionCredentials getValidMdCredentialsFor(final Scope scope) {
                return null;
            }

            @Override
            public boolean areUmdCredentialsSubjectToCvmFor(final TransactionRange transactionRange,
                                                            final Scope scope) {
                return false;
            }

            @Override
            public boolean hasValidCredentialsFor(final Scope scope) {
                switch (scope) {
                    case REMOTE_PAYMENT:
                        return true;
                    case CONTACTLESS:
                    default:
                        return false;
                }
            }

            @Override
            public byte[] getAtcForCancelPayment(final Scope scope) {
                switch (scope) {
                    case REMOTE_PAYMENT:
                        return credentials.getAtc().getBytes();
                    case CONTACTLESS:
                    default:
                        return null;
                }
            }

            @Override
            public TransactionCredentials getRandomCredentials() {
                return new TransactionCredentials();
            }
        };
    }

    private ConsentManager getConsentManager() {
        return new ConsentManager() {
            @Override
            public boolean isConsentGiven() {
                return true;
            }
        };
    }
}