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

package com.mastercard.mcbp.lde;

import android.test.AndroidTestCase;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.utils.UnitTestMcbpLoggerFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.List;

public class AndroidBasicMcbpDatabseTest extends AndroidTestCase {
    private McbpDataBase defaultMcbpDatabase;

    @Override
    protected void setUp() throws Exception {
        McbpLoggerFactory.setInstance(new UnitTestMcbpLoggerFactory(), null);
        defaultMcbpDatabase =
                AndroidMcbpDataBaseFactory.getDefaultMcbpDatabase(getContext());
    }

    //************** McbpDataBase#getAllSingleUseKeys test cases **************//

    /**
     * Invalid input test
     */
    public void testGetAllSingleUseKeysWithExceptionAsNullInput() {
        try {
            defaultMcbpDatabase.getAllSingleUseKeys(null);
            assertTrue(false);
        } catch (InvalidInput e) {
            assertTrue(true);
        } catch (McbpCryptoException e) {
            assertTrue(false);
        }
    }

    /**
     * Invalid input test
     */
    public void testGetAllSingleUseKeysWithExceptionAsEmptyInput() {
        try {
            defaultMcbpDatabase.getAllSingleUseKeys("");
            assertTrue(false);
        } catch (InvalidInput e) {
            assertTrue(true);
        } catch (McbpCryptoException e) {
            assertTrue(false);
        }
    }

    public void testGetAllSingleUseKeys() {
        try {
            final String digitizedCardId = getDummyCardProfileId();
            final SingleUseKey dummySingleUseKey = getDummySingleUseKey(
                    digitizedCardId, "01");
            defaultMcbpDatabase
                    .provisionSingleUseKey(digitizedCardId,
                                           dummySingleUseKey);
            final List<SingleUseKey> allSingleUseKeys =
                    defaultMcbpDatabase.getAllSingleUseKeys(digitizedCardId);
            assertEquals(1, allSingleUseKeys.size());
            final SingleUseKey singleUseKey = allSingleUseKeys.get(0);
            assertEquals(dummySingleUseKey.getCardId(), singleUseKey.getCardId());
            assertEquals(dummySingleUseKey.getContent().getAtc().toHexString(),
                         singleUseKey.getContent().getAtc().toHexString());
            assertEquals(dummySingleUseKey.getContent().getHash().toHexString(),
                         singleUseKey.getContent().getHash().toHexString());
            assertEquals(dummySingleUseKey.getContent().getIdn().toHexString(),
                         singleUseKey.getContent().getIdn().toHexString());
            assertEquals(dummySingleUseKey.getContent().getInfo().toHexString(),
                         singleUseKey.getContent().getInfo().toHexString());
            assertNull(singleUseKey.getContent().getSessionKeyContactlessMd());
            assertNull(singleUseKey.getContent().getSukContactlessUmd());
            assertNull(singleUseKey.getContent().getSessionKeyRemotePaymentMd());
            assertNull(singleUseKey.getContent().getSukRemotePaymentUmd());
        } catch (InvalidInput | McbpCryptoException | LdeCheckedException e) {
            assertTrue(false);
        }
    }

    public void testDuplicateSingleUseKeys() {
        try {
            final String digitizedCardId = getDummyCardProfileId();
            final SingleUseKey dummySingleUseKey = getDummySingleUseKey(digitizedCardId, "01");
            defaultMcbpDatabase.provisionSingleUseKey(digitizedCardId, dummySingleUseKey);
            defaultMcbpDatabase.provisionSingleUseKey(digitizedCardId, dummySingleUseKey);
        } catch (InvalidInput | McbpCryptoException | LdeCheckedException e) {
            assertTrue(false);
        }
    }

    public void testGetAllSingleUseKeysWithWrongCardId() {
        try {
            final String originalDigitizedCardId = getDummyCardProfileId();
            final String newDigitizedCardId = "5480981500100002FFFF01150305163348";
            final SingleUseKey dummySingleUseKey = getDummySingleUseKey(
                    originalDigitizedCardId, "01");
            defaultMcbpDatabase
                    .provisionSingleUseKey(originalDigitizedCardId,
                                           dummySingleUseKey);
            final List<SingleUseKey> allSingleUseKeys =
                    defaultMcbpDatabase.getAllSingleUseKeys(newDigitizedCardId);
            assertEquals(0, allSingleUseKeys.size());
        } catch (InvalidInput | McbpCryptoException | LdeCheckedException e) {
            assertTrue(false);
        }
    }

    //*************************************************************************//

    //************** McbpDataBase#getTokenUniqueReferenceFromCardId test cases **************//
    public void testGetTokenUniqueReferenceFromCardIdWithExceptionAsNullInput() {
        try {
            defaultMcbpDatabase.getTokenUniqueReferenceFromCardId(null);
            assertTrue(true);
        } catch (InvalidInput invalidInput) {
            assertTrue(true);
        }
    }

    public void testGetTokenUniqueReferenceFromCardIdWithExceptionAsEmptyInput() {
        try {
            defaultMcbpDatabase.getTokenUniqueReferenceFromCardId("");
            assertTrue(true);
        } catch (InvalidInput invalidInput) {
            assertTrue(true);
        }
    }

    public void testGetTokenUniqueReferenceFromCardId() {
        try {
            final String dummyCardId = getDummyCardProfileId();
            final String dummyTokenUniqueRefereance = "dummyTokenUniqueReference";
            defaultMcbpDatabase.insertTokenUniqueReference(dummyTokenUniqueRefereance, dummyCardId);
            final String tokenUniqueReferenceFromCardId =
                    defaultMcbpDatabase.getTokenUniqueReferenceFromCardId(dummyCardId);
            assertEquals(dummyTokenUniqueRefereance, tokenUniqueReferenceFromCardId);
        } catch (InvalidInput invalidInput) {
            invalidInput.printStackTrace();
        }
    }

    public void testGetTokenUniqueReferenceFromCardIdWithWrongCardId() {
        try {
            final String originalDummyCardId = getDummyCardProfileId();
            final String newDummyCardId = "newCardId";
            final String dummyTokenUniqueReference = "dummyTokenUniqueReference";
            defaultMcbpDatabase
                    .insertTokenUniqueReference(dummyTokenUniqueReference, originalDummyCardId);
            final String tokenUniqueReferenceFromCardId =
                    defaultMcbpDatabase.getTokenUniqueReferenceFromCardId(newDummyCardId);
            assertNull(tokenUniqueReferenceFromCardId);
        } catch (InvalidInput invalidInput) {
            invalidInput.printStackTrace();
        }
    }
    //*************************************************************************//

    //************** McbpDataBase#getLdeState test cases **************//
    public void testLdeStateAtInitialize() {
        final LdeState ldeState = defaultMcbpDatabase.getLdeState();
        assertEquals(ldeState, LdeState.UNINITIALIZED);
    }

    public void testLdeStateAfterInitialize() {
        LdeInitParams ldeInitParams =
                new LdeInitParams(ByteArray.of("7456154146516515916123143545"),
                                  ByteArray.of("5465156156414505414514513037887496"));
        ldeInitParams.setRnsMpaId(ByteArray.of("1414415613418741541145a1d451a451544514"));
        ldeInitParams.setUrlRemoteManagement("http://dummyurl");
        try {
            LdeState ldeState = defaultMcbpDatabase.getLdeState();
            assertEquals(ldeState, LdeState.UNINITIALIZED);
            defaultMcbpDatabase.initializeLde(ldeInitParams);
            ldeState = defaultMcbpDatabase.getLdeState();
            assertEquals(ldeState, LdeState.INITIALIZED);
        } catch (McbpCryptoException | InvalidInput e) {
            assertTrue(false);
        }
    }
    //*************************************************************************//

    //************** McbpDataBase#initializeLde test cases **************//
    public void testInitializeLdeWithNullInputs() {
        LdeInitParams ldeInitParams = new LdeInitParams(null, null);
        try {
            defaultMcbpDatabase.initializeLde(ldeInitParams);
            assertTrue(false);
        } catch (InvalidInput e) {
            assertTrue(true);
        } catch (McbpCryptoException e) {
            assertTrue(false);
        }
    }

    public void testInitializeLdeWithEmptyInputs() {
        LdeInitParams ldeInitParams = new LdeInitParams(ByteArray.of(""), ByteArray.of(""));
        try {
            defaultMcbpDatabase.initializeLde(ldeInitParams);
            assertTrue(false);
        } catch (InvalidInput e) {
            assertTrue(true);
        } catch (McbpCryptoException e) {
            assertTrue(false);
        }
    }

    public void testInitializeLde() {
        final String orignalCmsMpaId = "515156132165032651032510";
        final String orignalDfp = "5151515613515316135485";
        LdeInitParams ldeInitParams = new LdeInitParams(ByteArray.of(orignalCmsMpaId),
                                                        ByteArray.of(orignalDfp));
        final String urlRemoteManagement1 = "http://172.60.10.169";
        ldeInitParams.setUrlRemoteManagement(urlRemoteManagement1);
        try {
            defaultMcbpDatabase.initializeLde(ldeInitParams);
            final String cmsMpaId = defaultMcbpDatabase.getCmsMpaId();
            assertEquals(orignalCmsMpaId, cmsMpaId);
            final String dfp = defaultMcbpDatabase.getMpaFingerPrint().toHexString();
            assertEquals(orignalDfp, dfp);
            final String urlRemoteManagement = defaultMcbpDatabase.getUrlRemoteManagement();
            assertEquals(urlRemoteManagement1, urlRemoteManagement);
        } catch (InvalidInput | McbpCryptoException e) {
            assertTrue(false);
        }
    }

    //*************************************************************************//
    //************** McbpDataBase#updateWalletState test cases **************//
    public void testUpdateWalletStateWithNullInputs() {
        try {
            defaultMcbpDatabase.updateWalletState(null, null);
            assertTrue(false);
        } catch (InvalidInput invalidInput) {
            assertTrue(true);
        }
    }

    public void testUpdateWalletStateWithEmptyInputs() {
        try {
            defaultMcbpDatabase.updateWalletState("", WalletState.NOTREGISTER);
            assertTrue(false);
        } catch (InvalidInput invalidInput) {
            assertTrue(true);
        }
    }

    public void testUpdateWalletState() {
        final String orignalCmsMpaId = "515156132165032651032510";
        final String orignalDfp = "5151515613515316135485";
        LdeInitParams ldeInitParams = new LdeInitParams(ByteArray.of(orignalCmsMpaId),
                                                        ByteArray.of(orignalDfp));
        final String urlRemoteManagement1 = "http://172.60.10.169";
        ldeInitParams.setUrlRemoteManagement(urlRemoteManagement1);
        try {
            defaultMcbpDatabase.initializeLde(ldeInitParams);
            WalletState walletState = defaultMcbpDatabase.getWalletState();
            assertEquals(WalletState.NOTREGISTER, walletState);
            defaultMcbpDatabase.updateWalletState(orignalCmsMpaId, WalletState.REGISTER);
            walletState = defaultMcbpDatabase.getWalletState();
            assertEquals(WalletState.REGISTER, walletState);
        } catch (InvalidInput | McbpCryptoException invalidInput) {
            assertTrue(false);
        }
    }
    //*************************************************************************//

    @Override
    protected void tearDown() throws Exception {
        ((AndroidBasicMcbpDataBase) defaultMcbpDatabase).clearAllDataFromDb();
    }

    private SingleUseKey getDummySingleUseKey(String cardId, String atcAsHex) {
        SingleUseKeyContent singleUseKeyContent = new SingleUseKeyContent();
        singleUseKeyContent.setAtc(ByteArray.of(atcAsHex));
        singleUseKeyContent.setHash(ByteArray.of(getDummyCardProfileHash()));
        singleUseKeyContent.setIdn(ByteArray.of(getDummyIdn()));
        singleUseKeyContent.setInfo(ByteArray.of(getDummySukInfo()));
        singleUseKeyContent.setSessionKeyContactlessMd(ByteArray.of(getDummyClMd()));
        singleUseKeyContent.setSessionKeyRemotePaymentMd(ByteArray.of(getDummyRPMd()));
        singleUseKeyContent.setSukContactlessUmd(ByteArray.of(getDummyClUmd()));
        singleUseKeyContent.setSukRemotePaymentUmd(ByteArray.of(getDummyRPUmd()));
        SingleUseKey singleUseKey = new SingleUseKey();
        singleUseKey.setDigitizedCardId(ByteArray.of(cardId));
        ByteArray sukId = ByteArray.of(cardId)
                                   .append(ByteArray.of(singleUseKeyContent.getAtc()));
        singleUseKey.setId(sukId);
        singleUseKey.setContent(singleUseKeyContent);
        return singleUseKey;
    }

    private static String getDummyCardProfileId() {
        return "5480981500100002FFFF01150305163347";
    }

    private static String getDummyCardProfileHash() {
        return "5155252525151516";
    }

    private static String getDummyIdn() {
        return "4E4F54205245414C2049444E";
    }

    private static String getDummySukInfo() {
        return "515525fff515aaa6";
    }

    private static String getDummyClMd() {
        return "4E4F54205245414C2044535250204D4420534B";
    }

    private static String getDummyClUmd() {
        return "4E4F54205245414C204E464320554D442053554B";
    }

    private static String getDummyRPUmd() {
        return "4E4F54205245414C204453525020554D442053554B";
    }

    private static String getDummyRPMd() {
        return "4E4F54205245414C2044535250204D4420534B";
    }

}
