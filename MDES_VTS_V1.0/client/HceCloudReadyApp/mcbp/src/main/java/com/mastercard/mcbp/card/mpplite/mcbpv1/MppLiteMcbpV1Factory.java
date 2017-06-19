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

import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.transactiondecisionmanager.AdviceManager;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;

import java.util.List;

/**
 * Factory to build MCBP V1 MPP Lite objects
 */
public enum MppLiteMcbpV1Factory {
    INSTANCE;

    /**
     * Build an MPP Lite for MCBP V1.0+ using Java implementation
     *
     * @param mppLiteModule       The MPP Lite profile data
     * @param credentialsManager  The callback interface to the Credentials Manager
     * @param cardholderValidator The CVM Manager to be used by the MPP Lite
     * @param consentManager      The Consent Manager to retrieve information about user consent
     * @param adviceManager       The Advice Manager to get the Wallet advice on the transaction
     * @param additionalPdolList  List of additional PDOLs to be requested by the MPP Lite to the
     *                            POS
     * @param additionalUdolList  List of additional UDOLs to be requested by the MPP Lite to the
     *                            POS
     * @since 1.0.7
     */
    private static MppLite buildV1Plus(final MppLiteModule mppLiteModule,
                                      final TransactionCredentialsManager credentialsManager,
                                      final ChValidator cardholderValidator,
                                      final ConsentManager consentManager,
                                      final AdviceManager adviceManager,
                                      final List<DolRequestList.DolItem> additionalPdolList,
                                      final List<DolRequestList.DolItem> additionalUdolList) {
        return MppLiteImpl.buildMppLiteForMcbp1Plus(mppLiteModule,
                                                    credentialsManager,
                                                    cardholderValidator,
                                                    consentManager,
                                                    adviceManager,
                                                    additionalPdolList,
                                                    additionalUdolList,
                                                    true /* Mask M-CHIP in AIP for US */);
    }

    /**
     * Build an MPP Lite for MCBP V1.0 using Java implementation with implicit support for transit
     *
     * @param mppLiteModule       The MPP Lite profile data
     * @param credentialsManager  The callback interface to the Credentials Manager
     * @param cardholderValidator The CVM Manager to be used by the MPP Lite
     * @param consentManager      The Consent Manager to retrieve information about user consent
     * @since 1.0.6a
     */
    public static MppLite buildV1(final MppLiteModule mppLiteModule,
                                  final TransactionCredentialsManager credentialsManager,
                                  final ChValidator cardholderValidator,
                                  final ConsentManager consentManager) {
        return MppLiteImpl.buildMppLiteV1(mppLiteModule,
                                          credentialsManager,
                                          cardholderValidator,
                                          consentManager);
    }
}
