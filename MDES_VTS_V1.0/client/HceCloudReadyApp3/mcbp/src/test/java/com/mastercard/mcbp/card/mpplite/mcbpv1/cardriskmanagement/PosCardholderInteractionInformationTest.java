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

package com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement;

import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test case for POSCII class
 */
public class PosCardholderInteractionInformationTest {
    private PosCardholderInteractionInformation mPoscii;

    @Before
    public void setup() {
        mPoscii = new PosCardholderInteractionInformation();
    }

    @Test
    public void testIndicateOfflinePinVerificationSuccessful() throws Exception {
        mPoscii.indicateOfflinePinVerificationSuccessful();
        assertEquals("DF4B03001000", ByteArray.of(mPoscii.getTlv()).toHexString());
    }

    @Test
    public void testIndicateContextIsConflicting() throws Exception {
        mPoscii.indicateContextIsConflicting();
        assertEquals("DF4B03000800", ByteArray.of(mPoscii.getTlv()).toHexString());
    }

    @Test
    public void testIndicateOfflinePinChangeRequired() throws Exception {
        mPoscii.indicateOfflinePinChangeRequired();
        assertEquals("DF4B03000400", ByteArray.of(mPoscii.getTlv()).toHexString());
    }

    @Test
    public void testIndicateCdCvmRequired() throws Exception {
        mPoscii.indicateCdCvmRequired();
        assertEquals("DF4B03000100", ByteArray.of(mPoscii.getTlv()).toHexString());
    }

    @Test
    public void testIndicateConsentRequired() throws Exception {
        mPoscii.indicateConsentRequired();
        assertEquals("DF4B03000200", ByteArray.of(mPoscii.getTlv()).toHexString());
    }

    @Test
    public void testClear() throws Exception {
        mPoscii.clear();
        assertEquals("DF4B03000000", ByteArray.of(mPoscii.getTlv()).toHexString());
    }

    @Test
    public void testForApproveMagstripeWithCvm() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forApproveMagstripe(true, walletReasons);
        assertEquals("DF4B03001000", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForApproveMagstripeWithoutCvm() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forApproveMagstripe(false, walletReasons);
        assertEquals("DF4B03000100", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForAbortMagstripeWithCredentialsNotAccessible() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.CREDENTIALS_NOT_ACCESSIBLE_WITHOUT_CVM);
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAbortMagstripe(false, walletReasons);
        assertEquals("DF4B03000100", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForAbortMagstripeWithMissingCdCvm() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAbortMagstripe(false, walletReasons);
        assertEquals("DF4B03000100", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForAbortMagstripeWithMissingConsent() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.MISSING_CONSENT);
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAbortMagstripe(false, walletReasons);
        assertEquals("DF4B03000300", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForDeclineMagstripeWithCredentialsNotAvailable() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.CREDENTIALS_NOT_AVAILABLE);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forDeclineMagstripe(true, walletReasons);
        assertEquals("DF4B03001000", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForDeclineMagstripeWithTransactionConditionNotAllowed() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.TRANSACTION_CONDITIONS_NOT_ALLOWED);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forDeclineMagstripe(true, walletReasons);
        assertEquals("DF4B03001000", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForErrorMagstripeWithTransactionConditionNotAllowed() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.TRANSACTION_CONDITIONS_NOT_ALLOWED);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forErrorMagstripe(true, walletReasons);
        assertEquals("DF4B03001000", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForApproveMchip() throws Exception {
        assertEquals(null, PosCardholderInteractionInformation.forApproveMchip());
    }

    @Test
    public void testForAbortMchipWithCredentialsNotAccessible() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.CREDENTIALS_NOT_ACCESSIBLE_WITHOUT_CVM);
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAbortMchip(walletReasons);
        assertEquals("DF4B03000100", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForAbortMchipWithMissingCdCvm() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAbortMchip(walletReasons);
        assertEquals("DF4B03000100", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForAbortMchipWithMissingConsent() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.MISSING_CONSENT);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAbortMchip(walletReasons);
        assertEquals("DF4B03000200", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForDeclineMchipWithCredentialsNotAvailable() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.CREDENTIALS_NOT_AVAILABLE);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forDeclineMchip();
        assertEquals("DF4B03000000", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForDeclineMchipWithTransactionConditionNotAllowed() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.TRANSACTION_CONDITIONS_NOT_ALLOWED);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forDeclineMchip();
        assertEquals("DF4B03000000", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForErrorMchipWithTransactionConditionNotAllowed() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        walletReasons.add(Reason.TRANSACTION_CONDITIONS_NOT_ALLOWED);
        walletReasons.add(Reason.MISSING_CD_CVM);
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forErrorMchip(walletReasons);
        assertEquals("DF4B03000100", ByteArray.of(poscii.getTlv()).toHexString());
    }

    @Test
    public void testForAuthenticateMchip() throws Exception {
        List<Reason> walletReasons = new ArrayList<>();
        final PosCardholderInteractionInformation poscii =
                PosCardholderInteractionInformation.forAuthenticateMchip();
        assertEquals("DF4B03000000", ByteArray.of(poscii.getTlv()).toHexString());
    }
}