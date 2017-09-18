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

#include <wrappers/unit_test_wrapper.h>
#include <utils/utilities.h>
#include <core/card_utilities.h>
#include <core/mcbp_card.h>
#include <utils/pin_validator.h>

#include <exception>
#include <mutex>

namespace mcbp_unit_test {

UnitTestWrapper* UnitTestWrapper::instance_ = nullptr;
mcbp_core::McbpCard *UnitTestWrapper::card_ = nullptr;

std::once_flag flag_mcbp_core_mcbpcore_wrapper;

void CreateUnitTestWrapper() {
  if (UnitTestWrapper::instance_ != nullptr) return;
  UnitTestWrapper::instance_ = new UnitTestWrapper();
}

UnitTestWrapper* UnitTestWrapper::instance() {
  if (instance_ == nullptr)
    std::call_once(flag_mcbp_core_mcbpcore_wrapper, CreateUnitTestWrapper);
  return instance_;
}

void UnitTestWrapper::initialize(const mcbp_core::CardProfileData& profile) {
  if (card_ != nullptr) throw std::exception();

  auto* mcm_card_profile = mcbp_core::create_mcm_card_profile(profile);

  card_ = new mcbp_core::McbpCard();
  card_->initialize(mcm_card_profile);
}

void UnitTestWrapper::start_contactless(const mcbp_core::KeysData& keys,
                                        ByteArray pin, const int64_t& amount,
                                        const int32_t& currency,
                                        const bool exact_amount) {
  auto *suks = new mcbp_core::TransactionKeys(keys);
  mcbp_core::PinValidator pin_validator(&pin);

  if (card_ == nullptr) throw("MCBP Card: Not initialized");

  card_->start_contactless(suks, &pin_validator, instance_, 0, 0, false);
}

ByteArray UnitTestWrapper::transceive(const ByteArray& c_apdu) {
  return card_->transceive(c_apdu);
}

void UnitTestWrapper::activate_remote(const mcbp_core::KeysData& keys,
                                      ByteArray pin) {
  auto *suks = new mcbp_core::TransactionKeys(keys);
  mcbp_core::PinValidator pin_validator(&pin);

  if (card_ == nullptr) throw("MCBP Card: Not initialized");

  card_->activate_remote(suks, &pin_validator);
}

void UnitTestWrapper::deactivate() {
  if (card_ == nullptr) return;
  card_->deactivate();
  delete card_;
  card_ = nullptr;
}

ByteArray UnitTestWrapper::remote_payment(
    const mcbp_core::DsrpInputData& input) {
  auto output = card_->transaction_record(input);
  if (input.cryptogram_type == mcbp_core::DsrpTransactionType::DE55)
    return De55Response(output);

  if (input.cryptogram_type == mcbp_core::DsrpTransactionType::UCAF)
    return UcafResponse(output);

  // This should not happen
  return ByteArray();
}

UnitTestWrapper::~UnitTestWrapper() {
  deactivate();
}

void UnitTestWrapper::on_event(
    const mcbp_core::ContactlessTransactionContext& context) {
  // Forward the event to the wrapper class. The wrapper will take care of
  // translating the even into the right format

  // Currently it is not passed forward to the unit test.
  // A different implementation may be possible in the future.
}

}  // namespace mcbp_unit_test
