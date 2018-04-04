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

import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;
import com.mastercard.mcbp.utils.UnitTestMcbpLoggerFactory;
import com.mastercard.mcbp.utils.UnitTestPinValidator;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class to automate those Regtool test cases that require some form of additional input
 */
@RunWith(Parameterized.class)
public class RegTestToolManual {

    /**
     * Store the set of Command/Response APDUs that will be used by to perform tests
     */
    static private TestCommands sTestCommands;


    /**
     * Store the set of transaction credentials that will be used by all the tests
     */
    static private TestKeys testKeys;

    /**
     * Name of the card for which the current test is being executed
     */
    private final String mCardName;

    /**
     * Name of the current test being executed
     */
    private final String mTestName;

    /**
     * MPP Lite to be used for the execution of tests
     */
    private MppLite mMppLite;


    private SingleUseKey mSuk;

    /**
     * Test constructor
     *
     * @param cardName The current card being tested
     * @param testName The name of the test being executed for the current card
     */
    public RegTestToolManual(String cardName, String testName) {
        this.mCardName = cardName;
        this.mTestName = testName;
    }

    @Parameterized.Parameters(name = "{index}:{0},{1}")
    public static Collection<String[]> data() throws Exception {
        setUpClass();
        return sTestCommands.getApduTestNameList();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Code executed before the first test method
        ClassLoader classLoader = RegToolTest.class.getClassLoader();
        URI uriCommands = null;
        URI uriKeys = null;

        URL url;
        try {
            url = classLoader.getResource("./file/regtool_test_data_manual.xml");
            if (url != null) uriCommands = url.toURI();

            url = classLoader.getResource("./file/test_keys.xml");
            if (url != null) uriKeys = url.toURI();

        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
        // Initialize factories: LoggerFactory and McbpCryptoFactory
        McbpLoggerFactory.setInstance(new UnitTestMcbpLoggerFactory(), null);

        // Initialize and read test keys and test cards

        sTestCommands = new TestCommands(uriCommands);
        testKeys = new TestKeys(uriKeys);

    }

    @Before
    public void setUp() throws Exception {
        // Code executed before each test
        final DigitizedCardProfile profile = RegToolTestUtils.getProfileByName(mCardName);

        // Get Card Profile, SUK, PIN
        String testAtc = sTestCommands.getKeyAtc(mCardName, mTestName);
        final boolean isCvmEntered = sTestCommands.isCvmEntered(mCardName, mTestName);

        // Read suks from test_keys.xml
        mSuk = testKeys.getSuk(mTestName, testAtc);

        String pinString;

        // Generate session keys using suk builder if keys are not defined in test_keys.xml
        if (mSuk == null) {
            pinString = sTestCommands.getMobilePin(mCardName, mTestName);
            mSuk = RegToolTestUtils.getSingleUseKey(
                    profile.getDigitizedCardId().toHexString(), pinString,
                    Integer.parseInt(testAtc, 16)).toSingleUseKey();
        } else {
            // read mobile pin from test_keys.xml
            pinString = testKeys.getPin(mTestName, testAtc);
        }


        // Authenticate user
        final UnitTestPinValidator validator =
                new UnitTestPinValidator(ByteArray.of(pinString), isCvmEntered);


        final TransactionCredentials credentials = new TransactionCredentials(
                validator.authenticate(mSuk.getContent().getSukContactlessUmd()),
                mSuk.getContent().getSessionKeyContactlessMd(),
                mSuk.getContent().getAtc(),
                mSuk.getContent().getIdn());

        mMppLite = MppLiteMcbpV1Factory.buildV1(
                profile.getMppLiteModule(),
                getTransactionCredentialsManager(credentials),
                validator,
                getConsentManager());  // Constructor without additional PDOLs

        mMppLite.startContactLessPayment(getListener(), getTrxInfo());

    }

    @Test
    public void apduTest() throws Exception {

        final List<CommandResponseApdu> apduList = sTestCommands.getApduList(mCardName, mTestName);
        for (int i = 0; i < apduList.size(); i++) {
            final ByteArray commandApdu = ByteArray.of(apduList.get(i).getCommandApdu());
            final ByteArray expectedResponseApdu = ByteArray.of(apduList.get(i).getResponseApdu());
            final ByteArray actualResponse = mMppLite.processApdu(commandApdu);
            try {
                assertEquals(expectedResponseApdu.toHexString(), actualResponse.toHexString());
            } catch (final AssertionError e) {
                // Get some more debug of what has happened
                System.out.println("C-APDU: " + commandApdu.toHexString());
                System.out.println("R-APDU(Expected): " + expectedResponseApdu.toHexString());
                System.out.println("R-APDU(Actual):   " + actualResponse.toHexString());
                throw e;
            }
        }

    }

    @After
    public void tearDown() throws Exception {
        // Code executed after each test
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // Code executed after the last test method
    }

    private TransactionCredentialsManager getTransactionCredentialsManager(
            final TransactionCredentials credentials) {
        return new TransactionCredentialsManager() {
            @Override
            public TransactionCredentials getValidUmdAndMdCredentialsFor(
                    final Scope scope) {
                switch (scope) {
                    case CONTACTLESS:
                        return credentials;
                    case REMOTE_PAYMENT:
                    default:
                        return null;
                }
            }

            @Override
            public TransactionCredentials getValidMdCredentialsFor(
                    final Scope scope) {
                return null;
            }

            @Override
            public boolean areUmdCredentialsSubjectToCvmFor(final TransactionRange transactionRange,
                                                            final Scope scope) {
                return true;
            }

            @Override
            public boolean hasValidCredentialsFor(final Scope scope) {
                switch (scope) {
                    case CONTACTLESS:
                        return true;
                    case REMOTE_PAYMENT:
                    default:
                        return false;
                }
            }

            @Override
            public byte[] getAtcForCancelPayment(final Scope scope) {
                switch (scope) {
                    case CONTACTLESS:
                        return credentials.getAtc().getBytes();
                    case REMOTE_PAYMENT:
                    default:
                        return null;
                }
            }

            @Override
            public TransactionCredentials getRandomCredentials() {
                final SingleUseKeyContent sessionKey = mSuk.getContent();
                return new TransactionCredentials(sessionKey.getSukContactlessUmd(),
                                                  sessionKey.getSessionKeyContactlessMd(),
                                                  sessionKey.getAtc(),
                                                  sessionKey.getIdn());
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

    private ContactlessTransactionListener getListener() {
        return new ContactlessTransactionListener() {
            @Override
            public void onContactlessReady() {
                // Intentionally no-op
            }

            @Override
            public void onContactlessTransactionCompleted(ContactlessLog contactlessLog) {
                // Intentionally no-op
            }

            @Override
            public void onContactlessTransactionAbort(ContactlessLog contactlessLog) {
                // Intentionally no-op
            }
        };
    }

    private BusinessLogicTransactionInformation getTrxInfo() {
        final Long transactionAmount = sTestCommands.getAmount(mCardName, mTestName);
        final Integer transactionCurrencyCode = sTestCommands.getCurrencyCode(mCardName, mTestName);

        if (transactionAmount != null && transactionCurrencyCode != null) {
            return new BusinessLogicTransactionInformation(transactionAmount,
                                                           transactionCurrencyCode, true);
        }
        return new BusinessLogicTransactionInformation();
    }
}