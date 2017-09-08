/*******************************************************************************
 * Copyright (c) 2015, MasterCard International Incorporated and/or its
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
*******************************************************************************/


/**
 * The following coding guidelines have been followed
 * http://google-styleguide.googlecode.com/svn/trunk/cppguide.xml
 *
 * MCBP = MasterCard Cloud-Based Payments
 */

// Project Libraries
#include <core/mobile_kernel/mobile_kernel.h>
#include <utils/utilities.h>
#include <core/constants.h>

namespace mcbp_core {

MobileKernel::MobileKernel(McmLite* const mcm_lite) :
  mcm_lite_(mcm_lite) { }

void MobileKernel::generate_dsrp_data(const DsrpInputData& dsrp_in,
                                      DsrpOutputData* dsrp_out,
                                      ByteArray* application_cryptogram) {
  // Check input data
  if (dsrp_in.transaction_amount    > 999999999999                   ||
      dsrp_in.transaction_amount    < 0                              ||
      dsrp_in.other_amount          > 999999999999                   ||
      dsrp_in.other_amount          < 0                              ||
      dsrp_in.currency_code         > 999                            ||
      dsrp_in.transaction_type      > 99                             ||
      dsrp_in.cryptogram_type      == DsrpTransactionType::UNDEFINED ||
      dsrp_in.country_code          > 999                            ||
      dsrp_in.unpredictable_number == 0                                ) {
    throw InvalidInput("Invalid DSRP Input Data");
  }
  // Create the Cryptogram Input
  CryptogramInput input;
  if (dsrp_in.cryptogram_type == DsrpTransactionType::DE55) {
    input.online_allowed = true;
    input.tvr = ByteArray(5, 0x00);
    input.amount_authorized = Int64ToBcd(dsrp_in.transaction_amount, 6);
    input.amount_other = Int64ToBcd(dsrp_in.other_amount, 6);
    input.terminal_country_code = Int64ToBcd(dsrp_in.country_code, 2);
    input.transaction_currency_code = Int64ToBcd(dsrp_in.currency_code, 2);
    input.transaction_date = DateToByteArray(dsrp_in.year, dsrp_in.month,
                                             dsrp_in.day);
    input.transaction_type = Int64ToBcd(dsrp_in.transaction_type, 1)[0];
  } else {
    input.online_allowed = true;
    input.tvr = ByteArray(5, 0x00);
    input.amount_authorized = ByteArray(6, 0x00);
    input.amount_other = ByteArray(6, 0x00);
    input.terminal_country_code = ByteArray(2, 0x00);
    input.transaction_currency_code = ByteArray(2, 0x00);
    input.transaction_date = ByteArray(3, 0x00);
    input.transaction_type = 0x00;
  }
  // The unpredictable number
  ByteArray& un = input.unpredictable_number;
  un.resize(4);
  un[0] = (dsrp_in.unpredictable_number >> 24 & 0x000000FF);
  un[1] = (dsrp_in.unpredictable_number >> 16 & 0x000000FF);
  un[2] = (dsrp_in.unpredictable_number >>  8 & 0x000000FF);
  un[3] = (dsrp_in.unpredictable_number       & 0x000000FF);

  // Generate the cryptogram
  TransactionOutput transaction_output;
  try {
    mcm_lite_->create_remote_cryptogram(input, &transaction_output);
  } catch (const Exception& e) {
    throw InternalError("generate_dsrp_data");
  }

  // Verify the output
  if (transaction_output.cryptogram_output().cid != 0x80)
    throw UnexpectedDataError("Invalid CID in the DSRP output");

  // Validate and assign the pan
  try {
    dsrp_out->pan = PanToString(transaction_output.pan());
    dsrp_out->track_2_data =
      ByteArrayToString(transaction_output.track2_equivalent_data());
  }
  catch (const InvalidInput& e) {
    throw UnexpectedDataError("Invalid PAN digit");
  }
  if (transaction_output.pan_sequence_number() > 9 &&
      dsrp_in.cryptogram_type == DsrpTransactionType::UCAF) {
    throw UnexpectedDataError("PAN Seq Num > 9 and UCAF");
  }

  if (!CheckDate(transaction_output.application_expiry_date())) {
    throw UnexpectedDataError("Invalid Date");
  }

  // Populate the output structure (PAN has already been assigned)
  dsrp_out->cryptogram_type = dsrp_in.cryptogram_type;
  dsrp_out->transaction_amount = dsrp_in.transaction_amount;
  dsrp_out->currency_code = dsrp_in.currency_code;
  dsrp_out->ucaf_version = 0;
  dsrp_out->unpredictable_number = dsrp_in.unpredictable_number;
  dsrp_out->expiry_date = transaction_output.application_expiry_date();
  dsrp_out->pan_sequence_number = transaction_output.pan_sequence_number();
  dsrp_out->cryptogram =
      transaction_output.cryptogram_output().application_cryptogram;

  const ByteArray& atc = transaction_output.cryptogram_output().atc;
  dsrp_out->atc = (atc[0] << 8) + atc[1];

  ByteArray& crypto_output = dsrp_out->transaction_cryptogram_data;

  if (dsrp_in.cryptogram_type == DsrpTransactionType::DE55) {
    // Build the DE55 message
    crypto_output.reserve(256);
    crypto_output.clear();
    ByteArray tmp;
    // Add the Application Cryptogram
    const auto& cryptogram_output = transaction_output.cryptogram_output();
    tmp = Tlv(kTagAc, cryptogram_output.application_cryptogram);
    Append(tmp, &crypto_output);
    // Add the AIP
    tmp = Tlv(kTagIad, cryptogram_output.issuer_application_data);
    Append(tmp, &crypto_output);
    // Application Transaction Counter
    tmp = Tlv(kTagAtc, cryptogram_output.atc);
    Append(tmp, &crypto_output);
    // Terminal Verification Results
    tmp = Tlv(kTagTvr, input.tvr);
    Append(tmp, &crypto_output);
    // CID
    tmp = Tlv(kTagCid, ByteArray(1, cryptogram_output.cid));
    Append(tmp, &crypto_output);
    // CVM Results
    ByteArray cvm_results(3, 0x00);
    if (transaction_output.cvm_entered()) {
      cvm_results = {0x01, 0x00, 0x02};
    } else {
      cvm_results = {0x3F, 0x00, 0x02};
    }
    tmp = Tlv(kTagCvmResults, cvm_results);
    Append(tmp, &crypto_output);
    // Unpredictable Number
    tmp = Tlv(kTagUn, input.unpredictable_number);
    Append(tmp, &crypto_output);
    // Amount, authorized
    tmp = Tlv(kTagAmountAuthor, input.amount_authorized);
    Append(tmp, &crypto_output);
    // Amount, other
    tmp = Tlv(kTagAmountOther, input.amount_other);
    Append(tmp, &crypto_output);
    // Transaction Currency Code
    tmp = Tlv(kTagTransactionCurrencyCode, input.transaction_currency_code);
    Append(tmp, &crypto_output);
    // Transaction Date
    tmp = Tlv(kTagTrxDate, input.transaction_date);
    Append(tmp, &crypto_output);
    // Transaction Type
    tmp = Tlv(kTagTrxType, ByteArray(1, input.transaction_type));
    Append(tmp, &crypto_output);
    // Application PAN
    tmp = Tlv(kTagPan, transaction_output.pan());
    Append(tmp, &crypto_output);
    // PAN Sequence Number
    tmp = Tlv(kTagPanSeqNum,
              ByteArray(1, transaction_output.pan_sequence_number()));
    Append(tmp, &crypto_output);
    // Application Expiration Date
    tmp = Tlv(kTagAed, transaction_output.application_expiry_date());
    Append(tmp, &crypto_output);
    // Application Terminal Country Code
    tmp = Tlv(kTagTerminalCountryCode, input.terminal_country_code);
    Append(tmp, &crypto_output);
    // AIP
    tmp = Tlv(kTagAip, transaction_output.aip());
    Append(tmp, &crypto_output);
  } else if (dsrp_in.cryptogram_type == DsrpTransactionType::UCAF) {
    // Build the UCAF message
    crypto_output.clear();
    crypto_output.reserve(19);
    crypto_output.push_back(0x0F & dsrp_out->pan_sequence_number);
    const auto& crypto_out = transaction_output.cryptogram_output();
    const ByteArray& truncated_md =
        {crypto_out.issuer_application_data.begin() + 11,
         crypto_out.issuer_application_data.begin() + 15};
    Append(truncated_md, &crypto_output);
    const ByteArray& truncated_umd =
        {crypto_out.application_cryptogram.begin() + 4,
         crypto_out.application_cryptogram.begin() + 8};
    Append(truncated_umd, &crypto_output);
    Append(crypto_out.atc, &crypto_output);
    Append(input.unpredictable_number, &crypto_output);
    Append(transaction_output.aip(), &crypto_output);
    Append(crypto_out.issuer_application_data[0], &crypto_output);
    Append(crypto_out.issuer_application_data[1], &crypto_output);
    dsrp_out->transaction_cryptogram_data = EncodeToBase64(crypto_output);
  }
}

}  // namespace mcbp_core
