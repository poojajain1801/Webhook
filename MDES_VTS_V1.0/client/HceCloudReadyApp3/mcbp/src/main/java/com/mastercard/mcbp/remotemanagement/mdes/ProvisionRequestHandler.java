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

package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDProvisionRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDProvisionResponse;
import com.mastercard.mcbp.remotemanagement.mdes.profile.DigitizedCardProfileMdesContainer;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create provision request,execute it and process response
 */
public class ProvisionRequestHandler extends AbstractRequestHandler {

    public static final String PROVISION = BASE_REQUEST + "/" + "provision";

    public ProvisionRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                   SessionAwareAction sessionAwareAction, String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {

        CmsDProvisionRequestHolder cmsDProvisionRequestHolder =
                (CmsDProvisionRequestHolder) getCmsDRequestHolder();

        CmsDProvisionRequest cmsDProvisionRequest = new CmsDProvisionRequest();
        cmsDProvisionRequest.setTokenUniqueReference(cmsDProvisionRequestHolder.cardIdentifier);
        cmsDProvisionRequest.setRequestId(getRequestId());

        String provisionRequestAsJsonString = cmsDProvisionRequest.toJsonString();

        ByteArray response = communicate(provisionRequestAsJsonString, true);

        CmsDProvisionResponse cmsDProvisionResponse = CmsDProvisionResponse.valueOf(response);

        if (!cmsDProvisionResponse.isSuccess()) {
            throw new ServiceException(McbpErrorCode.SERVER_ERROR, cmsDProvisionResponse
                    .getErrorDescription());
        }
        //Update the response host so that next request in same session use this host
        mSessionContext.updateResponseHost(cmsDProvisionResponse.getResponseHost());

        //Insert card profile and token in DB
        final DigitizedCardProfileMdesContainer profileMdesContainer;

        String iccKek = cmsDProvisionResponse.getIccKek();

        // #MCBP_LOG_BEGIN
        mLogger.d("MCBP_PROTOCOL;PROVISION;ICC_KEK;SENDER:CMS;ENCRYPTED_ICC_KEK: ([" + iccKek +
                  "])");
        // #MCBP_LOG_END

        ByteArray decKey = null;
        ByteArray decryptedIccKek = null;
        try {
            decKey = getLdeRemoteManagementService().getDataEncryptionKey();

            // #MCBP_LOG_BEGIN
            mLogger.d("MCBP_PROTOCOL;PROVISION;DATA_ENCRYPTION_KEY: ([" + decKey + "])");
            // #MCBP_LOG_END

            decryptedIccKek = getCryptoService().decryptIccKey(ByteArray.of(iccKek), decKey);

            profileMdesContainer =
                    new DigitizedCardProfileMdesContainer(cmsDProvisionResponse.getCardProfile(),
                                                          decryptedIccKek);

            // #MCBP_LOG_BEGIN
            mLogger.d("MCBP_PROTOCOL;PROVISION;ICC_KEK;DECRYPTED_ICC_KEK: ([" + decryptedIccKek +
                      "])");
            // #MCBP_LOG_END

            DigitizedCardProfile digitizedCardProfile =
                    profileMdesContainer.toDigitizedCardProfile();

            getLdeRemoteManagementService().provisionDigitizedCardProfile(digitizedCardProfile);

            getLdeRemoteManagementService()
                    .insertTokenUniqueReference(cmsDProvisionRequestHolder.cardIdentifier,
                                                digitizedCardProfile.getCardId());

            return RemoteManagementResponseHolder
                    .generateSuccessResponse(getCmsDRequestHolder(), cmsDProvisionResponse);

        } catch (LdeNotInitialized | InvalidInput ex) {
            throw new ServiceException(McbpErrorCode.LDE_ERROR, ex.getMessage(), ex);
        } catch (McbpCryptoException ex) {
            throw new ServiceException(McbpErrorCode.CRYPTO_ERROR, ex.getMessage(), ex);
        } finally {
            Utils.clearByteArray(decKey);
            Utils.clearByteArray(decryptedIccKek);
        }
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + PROVISION;
    }
}
