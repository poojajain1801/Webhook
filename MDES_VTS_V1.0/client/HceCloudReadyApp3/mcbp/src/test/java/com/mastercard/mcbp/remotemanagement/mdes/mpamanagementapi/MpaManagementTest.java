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

package com.mastercard.mcbp.remotemanagement.mdes.mpamanagementapi;

import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.mdes.models.MobileKeys;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the MPA Management API (Pure Java layer)
 */
public class MpaManagementTest {
    LdeRemoteManagementService mLdeRemoteManagementService;
    MockWalletServer mWalletServer;
    MpaManagement mMpaManagement;
    CryptoService mCryptoService;

    @Before
    public void initialize() {
        mLdeRemoteManagementService = initializeMockLde();
        mCryptoService = CryptoServiceFactory.getDefaultCryptoService();
        mMpaManagement = new MpaManagement(mLdeRemoteManagementService, mCryptoService);
        mWalletServer = new MockWalletServer();
    }

    @Test
    public void testRegistrationWithMobilePin() throws Exception {
        final ByteArray mobilePin = ByteArray.of("3132333435363738");
        final String appInstanceId = mWalletServer.getPaymentInstanceId();
        ByteArray publicKey = ByteArray.of(mWalletServer.getPublicKey().getEncoded());

        // Get the public key from the Mock Up Wallet Server
        final EncryptedRegistrationRequestParameters parameters = mMpaManagement
                .getRegistrationRequestParameters(publicKey, mobilePin, appInstanceId);

        CmsRegisterResponse registerResponse =
                mWalletServer.processRegistrationParameters(parameters);

        // Now encrypt the mobile keys with the RGK

        MobileKeys mobileKeys = new MobileKeys(registerResponse.getEncryptedTransportKey(),
                                               registerResponse.getEncryptedDataEncryptionKey(),
                                               registerResponse.getEncryptedMacKey());
        final String mobileKeySetId = registerResponse.getMobileKeySetId();
        final String remoteManagementUrl = registerResponse.getRemoteManagementUrl();


        // Now call the register
        mMpaManagement.register(mobileKeySetId, mobileKeys, remoteManagementUrl);

        // Now verify that we have stored the same keys that were generated on the mock up server
        assertEquals(mWalletServer.getTransportKey().toHexString(),
                     mLdeRemoteManagementService.getTransportKey().toHexString());
        assertEquals(mWalletServer.getMacKey().toHexString(),
                     mLdeRemoteManagementService.getMacKey().toHexString());
        assertEquals(mWalletServer.getDataEncryptionKey().toHexString(),
                     mLdeRemoteManagementService.getDataEncryptionKey().toHexString());

        // Now verify that the right mobile PIN was encrypted
        assertEquals(mWalletServer.getReceivedMobilePin().toHexString(), mobilePin.toHexString());
    }

    @Test
    public void testRegistrationWithSeparateMobilePinSet() throws Exception {
        ByteArray publicKey = ByteArray.of(mWalletServer.getPublicKey().getEncoded());

        // Get the public key from the Mock Up Wallet Server
        final EncryptedRegistrationRequestParameters parameters = mMpaManagement
                .getRegistrationRequestParameters(publicKey);

        CmsRegisterResponse registerResponse =
                mWalletServer.processRegistrationParameters(parameters);

        // Now encrypt the mobile keys with the RGK

        MobileKeys mobileKeys = new MobileKeys(registerResponse.getEncryptedTransportKey(),
                                               registerResponse.getEncryptedDataEncryptionKey(),
                                               registerResponse.getEncryptedMacKey());
        final String mobileKeySetId = registerResponse.getMobileKeySetId();
        final String remoteManagementUrl = registerResponse.getRemoteManagementUrl();


        // Now call the register
        mMpaManagement.register(mobileKeySetId, mobileKeys, remoteManagementUrl);

        // Now verify that we have stored the same keys that were generated on the mock up server
        assertEquals(mWalletServer.getTransportKey().toHexString(),
                     mLdeRemoteManagementService.getTransportKey().toHexString());
        assertEquals(mWalletServer.getMacKey().toHexString(),
                     mLdeRemoteManagementService.getMacKey().toHexString());
        assertEquals(mWalletServer.getDataEncryptionKey().toHexString(),
                     mLdeRemoteManagementService.getDataEncryptionKey().toHexString());

        // Now verify that the right mobile PIN was encrypted
        assertEquals(mWalletServer.getReceivedMobilePin(), null);

        // Now set the PIN as separate API call
        final ByteArray mobilePin = ByteArray.of("3132333435363738");
        final String appInstanceId = mWalletServer.getPaymentInstanceId();

        final ByteArray encryptedPinBlock =
                mMpaManagement.getEncryptedPinBlockUsingDek(mobilePin, appInstanceId);
        mWalletServer.processSetPin(encryptedPinBlock);
        assertEquals(mobilePin.toHexString(), mWalletServer.getReceivedMobilePin().toHexString());
    }

    LdeRemoteManagementService initializeMockLde() {
        return new MockLdeRemoteManagementService();
    }
}