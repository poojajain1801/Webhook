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

#include <core/mcbp_card.h>
#include <utils/random_transaction_keys.h>
#include <utils/utilities.h>

namespace mcbp_core {

McbpCard::McbpCard() {
  mobile_kernel_ = nullptr;
  mcm_lite_      = nullptr;
  rp_activated_  = false;
  cl_activated_  = false;
}

void McbpCard::initialize(const McmCardProfile* card_profile) {
  if (mcm_lite_ != nullptr) throw InvalidState("MCBP Card: MCM Lite exists");

  mcm_lite_ = new McmLite();
  mcm_lite_->initialize(card_profile);
}

void McbpCard::start_contactless(TransactionKeys* keys,
                                 CardholderValidator* validator,
                                 McmLiteListener *const listener,
                                 const int64_t& amount,
                                 const int32_t& currency,
                                 const bool exact_amount,
                                 const bool cvm_entered,
                                 const bool cvm_required,
                                 const bool online_allowed) {
  if (mcm_lite_ == nullptr) throw InvalidState("MCM Lite does not exist");
  if (cl_activated_) throw InvalidState("MCBP Card: CL mode is active");
  if (rp_activated_) throw InvalidState("MCBP Card: RP already active");

  if (keys == nullptr) {
    keys = new mcbp_core::RandomTransactionKeys();
  } else if (validator != nullptr) {
    keys->unlock(validator);
  }

  mcm_lite_->start_contactless_payment(keys, listener, amount, currency,
                                       exact_amount, cvm_entered,
                                       cvm_required, online_allowed);
  cl_activated_ = true;
}

void McbpCard::stop_contactless() {
  try {
    mcm_lite_->cancel_payment();
  }
  catch (const InvalidState& e) {
    Log::instance()->d("Invalid state for cancel payment.");
    return;
  }
}

void McbpCard::activate_remote(TransactionKeys* keys,
                               CardholderValidator* validator) {
  if (cl_activated_) throw InvalidState("MCBP Card: CL mode is already active");
  if (rp_activated_) throw InvalidState("MCBP Card: RP already active");
  if (keys == nullptr) throw InvalidInput("No available keys");

  bool cvm_entered = (validator == nullptr ? false: true);

  if (cvm_entered) keys->unlock(validator);

  if (mcm_lite_ == nullptr)
    throw InvalidState("MCBP Card: MCM Lite does not exist");

  mcm_lite_->start_remote_payment(keys, true);
  mobile_kernel_ = new MobileKernel(mcm_lite_);

  rp_activated_ = true;
}

const ByteArray McbpCard::transceive(const ByteArray& c_apdu) {
  try {
    if (mcm_lite_ == nullptr)
      throw Exception("MCM Lite has not been created", Iso7816::kSwUnknown);

    return mcm_lite_->process_apdu(c_apdu);
  }
  catch (const mcbp_core::Exception& e) {
    Log::instance()->d("mcbp_core::Exception: %s", e.what());
    return ResponseApdu(e.error_code()).value();
  }
  // catch any possible exception and die gracefully (SW_UNKNOWN)
  catch (...) {
    Log::instance()->d("Generic Exception");
    return ResponseApdu(Iso7816::kSwUnknown).value();
  }
}

DsrpOutputData McbpCard::transaction_record(const DsrpInputData& trx_data) {
  DsrpOutputData output;
  ByteArray application_cryptogram;
  mobile_kernel_->generate_dsrp_data(trx_data, &output,
                                     &application_cryptogram);
  return output;
}

void McbpCard::deactivate() {
  if (mobile_kernel_ != nullptr) {
    delete mobile_kernel_;
    mobile_kernel_ = nullptr;
  }
  if (mcm_lite_ != nullptr) {
    mcm_lite_->stop();
    delete mcm_lite_;
    mcm_lite_ = nullptr;
  }
  cl_activated_ = false;
  rp_activated_ = false;
}

McbpCard::~McbpCard() {
  deactivate();
}

}  // namespace mcbp_core
