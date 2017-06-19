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

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Mock LDE Remote Management Service for Unit test
 */
public class MockLdeRemoteManagementService implements LdeRemoteManagementService {
    public WalletState mWalletState;
    private ByteArray mTransportKey;
    private ByteArray mMacKey;
    private ByteArray mDataEncryptionKey;
    private String mMobileKeySetId;
    private String mRemoteManagementUrl;

    @Override
    public boolean isLdeInitialized() {
        return true;
    }

    @Override
    public String getUrlRemoteManagement() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public ByteArray getMpaFingerPrint() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public ByteArray getCmsMpaId() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void provisionDigitizedCardProfile(final McbpDigitizedCardProfileWrapper cardProfile)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void provisionSingleUseKey(final SingleUseKey singleUseKey)
            throws LdeCheckedException, InvalidInput, McbpCryptoException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void wipeDigitizedCard(final ByteArray digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void resetMpaToInstalledState() throws LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void activateProfile(final String digitizedCardId)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void remoteWipeWallet() throws LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public List<String> getListOfAvailableCardId() throws LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public List<TransactionLog> getTransactionLogs(final String digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void wipeDcSuk(final ByteArray digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void wipeDcSuk(final String digitizedCardId, final String singleUseKeyId)
            throws LdeNotInitialized, InvalidInput {
        throw new RuntimeException("Not Implemented");

    }

    @Override
    public int getSingleUseKeyCount(final String digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public ByteArray getAvailableATCs(final String digitizedCardId)
            throws InvalidInput, LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public TransactionCredentialStatus[] getAllTransactionCredentialStatus(
            final String tokenUniqueReference) throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public ByteArray getMobileKeySetIdAsByteArray() {
        return mMobileKeySetId != null ?
                ByteArray.of(mMobileKeySetId.getBytes(Charset.defaultCharset())) : null;
    }

    @Override
    public ByteArray getTransportKey()
            throws LdeNotInitialized, McbpCryptoException, InvalidInput {
        return mTransportKey;
    }

    @Override
    public ByteArray getMacKey()
            throws LdeNotInitialized, McbpCryptoException, InvalidInput {
        return mMacKey;
    }

    @Override
    public ByteArray getDataEncryptionKey()
            throws LdeNotInitialized, McbpCryptoException, InvalidInput {
        return mDataEncryptionKey;
    }

    @Override
    public ByteArray getConfidentialityKey()
            throws LdeNotInitialized, McbpCryptoException, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void insertMobileKeySetId(final String mobileKeySetId)
            throws McbpCryptoException, InvalidInput {
        mMobileKeySetId = mobileKeySetId;
    }

    @Override
    public void insertTransportKey(final ByteArray transportKey)
            throws McbpCryptoException, InvalidInput {
        // Need to make a copy to simulate storage into LDE memory and avoid zeroing
        mTransportKey = ByteArray.of(transportKey);
    }

    @Override
    public void insertMacKey(final ByteArray macKey)
            throws McbpCryptoException, InvalidInput {
        // Need to make a copy to simulate storage into LDE memory and avoid zeroing
        mMacKey = ByteArray.of(macKey);
    }

    @Override
    public void insertDataEncryptionKey(final ByteArray dataEncryptionKey)
            throws McbpCryptoException, InvalidInput {
        // Need to make a copy to simulate storage into LDE memory and avoid zeroing
        mDataEncryptionKey = ByteArray.of(dataEncryptionKey);
    }

    @Override
    public void insertConfidentialityKey(final ByteArray confidentialityKey)
            throws McbpCryptoException, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void insertTokenUniqueReference(final String tokenUniqueReference,
                                           final String digitizedCardId)
            throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String getCardIdFromTokenUniqueReference(final String tokenUniqueReference)
            throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String getTokenUniqueReferenceFromCardId(final String digitizedCardId)
            throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void insertOrUpdateTransactionCredentialStatus(final String digitizedCardId,
                                                          final ByteArray atc,
                                                          final TransactionCredentialStatus.Status status)
            throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void deleteTransactionCredentialStatusOtherThanActive(
            final String digitizedCardId) throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void deleteAllTransactionCredentialStatus(final String digitizedCardId)
            throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void updateRemoteManagementUrl(final String url) throws InvalidInput {
        mRemoteManagementUrl = url;
    }

    @Override
    public void deleteTokenUniqueReference(final String digitizeCardId)
            throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void suspendCard(final String cardIdentifier) throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public ProfileState getCardState(final String cardIdentifier) throws InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void wipeAllSuks() throws LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void wipeAllTransactionCredentialStatus() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void updateDigitizedCardTemplate() throws McbpCryptoException, InvalidInput {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isCardProfileAlreadyProvision(final String cardIdentifier)
            throws LdeNotInitialized {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void updateWalletState(final WalletState walletState) {
        mWalletState = walletState;
    }

    @Override
    public WalletState getWalletState() {
        return mWalletState;
    }

    @Override
    public void unregister() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public long getNumberOfCardsProvisioned() {
        throw new RuntimeException("Not Implemented");
    }
}
