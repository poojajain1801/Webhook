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

package com.mastercard.mcbp.card.mpplite.mcbpv1.state;

import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GetProcessingOptionsCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ReadRecordCommandApdu;

/**
 * Interface for the Contactless Ready Sub States
 */
interface ContactlessReadySubState {

    /***
     * Perform the Process Compute Cryptographic Checksum
     * @param apdu The Compute CC Command APDU
     * @return The Compute CC Response APDU that can be sent back to the NFC reader
     * @throws com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException if something goes wrong
     */
    byte[] processComputeCc(final ComputeCcCommandApdu apdu);

    /***
     * Parse the Command APDU and return the corresponding Record
     * @param apdu The Read Record Command APDU
     * @return The Read Record Response APDU that can be sent back to the NFC reader
     * @throws com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException if something goes wrong
     */
    byte[] processReadRecord(final ReadRecordCommandApdu apdu);

    /***
     * Compute the Generate AC Response APDU
     * @param apdu The Generate AC Command APDU
     * @return The Generate AC Response APDU that can be sent back to the NFC reader
     * @throws com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException if something goes wrong
     */
    byte[] processGenerateAc(final GenerateAcCommandApdu apdu);

    /***
     *
     * @param apdu The Get Processing Options Command APDU
     * @return The GPO Response that can be sent back to NFC reader
     * @throws com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException if something goes wrong
     */
    byte[] processGpo(final GetProcessingOptionsCommandApdu apdu);

    /***
     * Cancel payment
     */
    void cancelPayment();
}
