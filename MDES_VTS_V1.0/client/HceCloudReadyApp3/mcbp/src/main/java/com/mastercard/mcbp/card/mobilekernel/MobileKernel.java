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

package com.mastercard.mcbp.card.mobilekernel;

import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.datamanagement.UnexpectedData;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Date;
import com.mastercard.mobile_api.utils.Tlv;
import com.mastercard.mobile_api.utils.Utils;

/***
 * Singleton utility object to generate a DSRP response
 */
public enum MobileKernel {
    INSTANCE;

    /**
     * Working variable - Reference to the MPP Lite used to generate the next DSRP cryptogram
     */
    static MppLite sMppLite = null;

    /**
     * generateDsrpData() is a static method on the Mobile Kernel class that
     * will return a formatted DSRP Transaction Cryptogram compatible with UCAF
     * version 0 or DE55.
     *
     * @param input Dsrp input data
     */
    public static synchronized DsrpResult generateDsrpData(final DsrpInputData input,
                                                           final MppLite mppLite)
            throws McbpCryptoException, UnexpectedData, InvalidInput {

        sMppLite = mppLite;

        if (!validateInput(input)) {
            sMppLite.cancelPayment();
            return new DsrpResult(RemotePaymentResultCode.ERROR_INVALID_INPUT, null);
        }

        // Prepare the input
        final ByteArray unpredictableNumber = ByteArray.of(new byte[4]);
        Utils.writeInt(unpredictableNumber, 0, input.getUnpredictableNumber());

        final CryptogramInput cryptogramInput;

        switch (input.getCryptogramType()) {
            case DE55:
                cryptogramInput = CryptogramInput.forDe55(
                        ByteArray.of(Utils.longToBcd(input.getTransactionAmount(), 6), 6),
                        Utils.longToBinaryByteArray(input.getOtherAmount(), 6),
                        ByteArray.of(Utils.longToBcd(input.getCountryCode(), 2), 2),
                        ByteArray.of(Utils.longToBcd(input.getCurrencyCode(), 2), 2),
                        getDateAsByteArray(input.getTransactionDate()),
                        unpredictableNumber,
                        input.getTransactionType());
                break;
            case UCAF:
                cryptogramInput = CryptogramInput.forUcaf(unpredictableNumber);
                break;
            default:
                sMppLite.cancelPayment();
                return new DsrpResult(RemotePaymentResultCode.ERROR_INVALID_INPUT, null);
        }

        final TransactionOutput output = sMppLite.createRemoteCryptogram(cryptogramInput);

        // Let's verify the output before continuing
        final RemotePaymentResultCode resultCode = verifyOutput(output, input.getCryptogramType());

        if (resultCode != RemotePaymentResultCode.OK) {
            return new DsrpResult(resultCode, null);
        }

        final DsrpOutputData outputData = getDsrpOutputData(input, cryptogramInput, output);

        return new DsrpResult(RemotePaymentResultCode.OK, outputData);

    }

    private static RemotePaymentResultCode verifyOutput(final TransactionOutput output,
                                                        final CryptogramType cryptogramType)
            throws UnexpectedData {
        if (output == null) {
            return RemotePaymentResultCode.ERROR_UNEXPECTED_DATA;
        }
        if (output.getCryptogramOutput().getCid() != (byte) 0x80) {
            return RemotePaymentResultCode.DECLINED;
        }
        final byte[] panSequenceNumber = output.getPanSequenceNumber().getBytes();

        if (panSequenceNumber[0] > 99 && cryptogramType == CryptogramType.DE55) {
            return RemotePaymentResultCode.ERROR_INVALID_INPUT;
        }
        if (panSequenceNumber[0] > 9 && cryptogramType == CryptogramType.UCAF) {
            return RemotePaymentResultCode.ERROR_INVALID_INPUT;
        }

        return verifyExpiryDate(output);
    }

    /**
     * Utility function to verify the Expiry Date
     *
     * @param output The Transaction Output
     */

    private static RemotePaymentResultCode verifyExpiryDate(final TransactionOutput output) {
        final ByteArray expiryDate = output.getExpiryDate();
        final int year =
                2000 + (((expiryDate.getByte(0) & 0x00F0) >> 4) * 10
                        + (expiryDate.getByte(0) & 0x000F));
        final int month =
                ((expiryDate.getByte(1) & 0x00F0) >> 4) * 10 + (expiryDate.getByte(1) & 0x000F);
        final int day =
                ((expiryDate.getByte(2) & 0x00F0) >> 4) * 10 + (expiryDate.getByte(2) & 0x000F);

        if (!(new Date(year, month, day).isValid())) {
            return RemotePaymentResultCode.ERROR_INVALID_INPUT;
        }
        return RemotePaymentResultCode.OK;
    }

    /**
     * Validate the DSRP Input Data
     *
     * @param input The DSRP Input Data
     * @return true if the input is according to specifications, false otherwise
     */
    private static boolean validateInput(final DsrpInputData input) {
        if (input == null || sMppLite == null) return false;

        // Pre-process and validate input DsrpInputData parameter:
        final long trxAmount = input.getTransactionAmount();
        if (trxAmount < 0 || trxAmount > BusinessLogicTransactionInformation.MAX_AMOUNT) {
            return false;
        }

        // Other Amount
        final long otherAmount = input.getOtherAmount();
        if (otherAmount < 0 || otherAmount > BusinessLogicTransactionInformation.MAX_AMOUNT) {
            return false;
        }

        final int currencyCode = input.getCurrencyCode();
        if (currencyCode < 0 || currencyCode > 999) return false;

        final byte transactionType = input.getTransactionType();
        if (transactionType < 0 || transactionType > 99) return false;

        final int countryCode = input.getCountryCode();
        if (countryCode < 0 || countryCode > 999) return false;

        if (input.getUnpredictableNumber() == 0) return false;

        if (input.getTransactionDate() == null) return false;

        // check date
        final Date date = input.getTransactionDate();
        return date.isValid();
    }

    private static DsrpOutputData getDsrpOutputData(final DsrpInputData input,
                                                    final CryptogramInput cryptoInput,
                                                    final TransactionOutput output) {
        // We remove any pad from the PAN and Track2Data as we may have added for a proper
        // conversion to HEX
        final String pan = output.getPan().toHexString().replaceAll("F", "");
        final String track2Data =
                output.getTrack2EquivalentData().toHexString().replaceAll("F", "");
        final ByteArray transactionCryptogramData = buildTransactionCryptogramData(input,
                                                                                   cryptoInput,
                                                                                   output);
        final int atc = Utils.readInt(output.getCryptogramOutput().getAtc().getBytes(), 0);

        // We now have all the inputs to generate the DSRP Output Data
        return new DsrpOutputData(pan,
                                  output.getPanSequenceNumber().getByte(0),
                                  extractExpiryDate(output),
                                  output.getCryptogramOutput().getCryptogram(),
                                  transactionCryptogramData,
                                  0,
                                  input.getTransactionAmount(),
                                  input.getCurrencyCode(),
                                  atc,
                                  input.getUnpredictableNumber(),
                                  input.getCryptogramType(),
                                  track2Data);
    }

    /**
     * buildDE55
     *
     * @param cryptogramInput CryptogramInput instance
     * @param output          TransactionOutput instance.
     * @return ByteArray
     */
    private static ByteArray buildDE55(final CryptogramInput cryptogramInput,
                                       final TransactionOutput output) {

        ByteArray res = Tlv.create(ByteArray.of((char) 0x9f26), output.getCryptogramOutput()
                                                                      .getCryptogram());

        res.append(Tlv.create(ByteArray.of((char) 0x9f10), output.getCryptogramOutput()
                                                                 .getIssuerApplicationData()));
        res.append(Tlv.create(ByteArray.of((char) 0x9f36), output.getCryptogramOutput().getAtc()));
        res.append(Tlv.create((byte) 0x95, cryptogramInput.getTvr()));

        ByteArray cid = ByteArray.get(1);
        cid.setByte(0, output.getCryptogramOutput().getCid());
        res.append(Tlv.create(ByteArray.of((char) 0x9f27), cid));

        ByteArray cvmResults = ByteArray.get(3);
        // If CryptogramInput.CVM_Entered then ‘010002’ else ‘3F0002’.
        cvmResults.setByte(2, (byte) 2);
        if (output.isCvmEntered()) {
            cvmResults.setByte(0, (byte) 0x01);
        } else {
            cvmResults.setByte(0, (byte) 0x3F);
        }
        res.append(Tlv.create(ByteArray.of((char) 0x9f34), cvmResults));
        // UN
        res.append(Tlv.create(ByteArray.of((char) 0x9f37),
                              cryptogramInput.getUnpredictableNumber()));
        // amount
        res.append(Tlv.create(ByteArray.of((char) 0x9f02), cryptogramInput.getAmountAuthorized()));
        // amount other
        res.append(Tlv.create(ByteArray.of((char) 0x9f03), cryptogramInput.getAmountOther()));
        // currency code
        res.append(Tlv.create(ByteArray.of((char) 0x5F2A),
                              cryptogramInput.getTransactionCurrencyCode()));
        // Date
        res.append(Tlv.create((byte) 0x9A, cryptogramInput.getTransactionDate()));
        // type
        res.append(Tlv.create((byte) 0x9C, cryptogramInput.getTransactionType()));
        // PAN
        res.append(Tlv.create((byte) 0x5A, output.getPan()));
        // PAN SEQUENCE NUMBER
        res.append(Tlv.create(ByteArray.of((char) 0x5F34), output.getPanSequenceNumber()));

        // Expiry date
        res.append(Tlv.create(ByteArray.of((char) 0x5F24), output.getExpiryDate()));
        // Terminal country code
        res.append(Tlv.create(ByteArray.of((char) 0x9F1A),
                              cryptogramInput.getTerminalCountryCode()));
        // AIP
        res.append(Tlv.create((byte) 0x82, output.getAip()));

        return res;
    }

    /**
     * Generate Ucaf data
     *
     * @param input  Dsrp input data
     * @param output TransactionOutput instance.
     */
    private static ByteArray buildUcaf(final DsrpInputData input, final TransactionOutput output) {

        final byte ucafPanSequenceNumber = (byte) (output.getPanSequenceNumber().getByte(0) & 0x0F);

        final ByteArray ucaf = ByteArray.of(ucafPanSequenceNumber);
        ucaf.append(output.getCryptogramOutput().getIssuerApplicationData().copyOfRange(11, 15));
        ucaf.append(output.getCryptogramOutput().getCryptogram().copyOfRange(4, 8));
        ucaf.append(output.getCryptogramOutput().getAtc().copyOfRange(0, 2));

        final ByteArray un = ByteArray.get(4);
        Utils.writeInt(un, 0, input.getUnpredictableNumber());
        ucaf.append(un);

        ucaf.append(output.getAip());
        final byte[] keyDerivationIndex = new byte[2];
        keyDerivationIndex[0] = output.getCryptogramOutput().getIssuerApplicationData().getByte(0);
        keyDerivationIndex[1] = output.getCryptogramOutput().getIssuerApplicationData().getByte(1);

        ucaf.append(ByteArray.of(keyDerivationIndex));

        // Encode the data as Base64 and return a Byte Array out of it
        return ByteArray.of(ucaf.toBase64String().getBytes());

    }

    /**
     * Get Data value in byte array
     *
     * @param date Date instance.
     */
    public static ByteArray getDateAsByteArray(final Date date) {
        String sDate = "";
        if (date.getYear() < 2010) {
            sDate += "0";
        }
        sDate += (date.getYear() % 2000);

        if (date.getMonth() < 10) {
            sDate += "0";
        }
        sDate += "" + date.getMonth();
        if (date.getDay() < 10) {
            sDate += "0";
        }
        sDate += date.getDay();

        return ByteArray.of(Utils.readHexString(sDate), 3);
    }

    /**
     * Utility function to parse the Expiry Date from the Transaction Output
     *
     * @param output The Transaction Output
     * @return The Expiry Date as Date object
     */
    private static Date extractExpiryDate(final TransactionOutput output) {
        final ByteArray expiryDate = output.getExpiryDate();

        final int year = Utils.bcdByteToInt(expiryDate.getByte(0));
        final int month = Utils.bcdByteToInt(expiryDate.getByte(1));
        final int day;
        if (expiryDate.getLength() == 3) {
            day = Utils.bcdByteToInt(expiryDate.getByte(2));
        } else {
            day = 1;
        }
        return new Date(year, month, day);
    }

    private static ByteArray buildTransactionCryptogramData(final DsrpInputData input,
                                                            final CryptogramInput cryptoInput,
                                                            final TransactionOutput output) {
        if (input.getCryptogramType() == CryptogramType.UCAF) {
            return buildUcaf(input, output);
        }
        return buildDE55(cryptoInput, output);
    }
}
