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
#include <core/mcm/mcm_lite_services.h>
#include <core/constants.h>
#include <utils/utilities.h>
#include <utils/random_transaction_keys.h>
#include <utils/mcbp_core_exception.h>
#include <utils/crypto_factory.h>

#include <log/log.h>

// C++ Libraries
#include <map>
#include <sstream>
#include <string>

namespace mcbp_core {

McmLite::McmLite() : cvr_(6, 0x00) {
  card_profile_ = nullptr;
  credentials_  = nullptr;
  state_ = McmLiteState::STOPPED;
  cvm_entered_ = false;
}

void McmLite::initialize(const McmCardProfile* card_profile) {
  if (state_ != McmLiteState::STOPPED)
    throw InvalidState("McmLite::initialize");

  if (card_profile == nullptr) throw InvalidInput("McmLite::initialize");

  card_profile_ = card_profile;
  state_ = McmLiteState::INITIALIZED;
}

void McmLite::start_remote_payment(TransactionKeys* keys,
                                   const bool cvm_entered) {
  if (state_ != McmLiteState::INITIALIZED)
    throw InvalidState("McmLite::start_remote_payment");

  if ( !card_profile_->remote_payment() )
    throw DsrpIncompatibleProfileError("McmLite::start_remote_payment");

  // We do not check umd and md keys as they are assumed to be good, if the
  // pointer is not null
  if (keys == nullptr)
    throw DsrpCredentialsError("Invalid DSRP Credentials");

  credentials_ = keys;
  cvm_entered_ = cvm_entered;

  // Prepare the CVR
  const RemotePaymentData& rp_data = card_profile_->remote_payment_data();
  cvr_ = {rp_data.issuer_application_data().begin() + 2,
          rp_data.issuer_application_data().begin() + 8};

  state_ = McmLiteState::RP_READY;
}

void McmLite::start_contactless_payment(TransactionKeys* credentials,
                                        McmLiteListener *const listener,
                                        const int64_t& amount,
                                        const int32_t& currency,
                                        const bool exact_amount,
                                        const bool cvm_entered,
                                        const bool cvm_required,
                                        const bool online_allowed) {
  if (state_ != McmLiteState::INITIALIZED) {
    throw InvalidState("McmLite::start_contactless_payment");
  }

  if ( !card_profile_->contactless() ) {
    throw InvalidDigitizedCardCp("McmLite::start_contactless_payment");
  }

  if (cvm_entered == true || cvm_required == false) {
    if (credentials == nullptr) {
      throw ClCredentialsError("Invalid Contacless Credentials");
    }
  }

  if (currency < 0 || currency > kMaxCurrencyValue) {
    std::ostringstream ostr;
    ostr << currency;
    throw InvalidInput("Invalid currency value: " + ostr.str());
  }

  if (amount < 0 || amount > kMaxTransactionValue) {
    std::ostringstream ostr;
    ostr << amount;
    throw InvalidInput("Invalid Amount: " + ostr.str());
  }

  if ( listener == nullptr )
    throw InvalidInput("McmLite::start_contactless_payment - listener");

  credentials_ = credentials;
  cl_context_.set_listener(listener);

  if (amount >= 0) {
    cl_context_.set_bl_amount(Int64ToBcd(amount, 6));
    cl_context_.set_bl_currency(Int64ToBcd(currency, 2));

  } else {
    // Set as empty ByteArrays
    cl_context_.set_bl_amount(ByteArray());
    cl_context_.set_bl_currency(ByteArray());
  }

  cl_context_.set_bl_exact_amount(exact_amount);
  cvm_entered_ = cvm_entered;
  cl_context_.set_cvm_required(cvm_required);
  cl_context_.set_online_allowed(online_allowed);

  const auto& cl_data = card_profile_->contactless_payment_data();

  ByteArray issuer_data_in_cvr;

  if (cl_data.issuer_application_data().empty()) {
    issuer_data_in_cvr = ByteArray(6, 0x00);
  } else {
    issuer_data_in_cvr.insert(issuer_data_in_cvr.end(),
                              cl_data.issuer_application_data().begin() + 2,
                              cl_data.issuer_application_data().begin() + 8);
  }

  // Prepare the CVR
  cvr_.clear();
  cvr_.insert(cvr_.end(), issuer_data_in_cvr.begin(), issuer_data_in_cvr.end());

  cvr_.at(0) &= 0x00;
  cvr_.at(1) &= 0x03;
  cvr_.at(2) &= 0x00;
  cvr_.at(3) &= 0x00;
  cvr_.at(4) &= 0x00;
  cvr_.at(5) &= 0x00;

  if (credentials_ == nullptr)  // Use Random credentials
    credentials_ = new RandomTransactionKeys();

  state_ = McmLiteState::CL_NOT_SELECTED;
}

void McmLite::stop() {
  if (card_profile_ != nullptr) {
    delete card_profile_;
    card_profile_ = nullptr;
  }
  if (state_ != McmLiteState::STOPPED && state_ != McmLiteState::INITIALIZED)
    cancel_payment();

  state_ = McmLiteState::STOPPED;
}

void McmLite::cancel_payment() {
  if (state_ != McmLiteState::CL_SELECTED     &&
      state_ != McmLiteState::CL_NOT_SELECTED &&
      state_ != McmLiteState::CL_INITIATED    &&
      state_ != McmLiteState::RP_READY          )
    throw InvalidState("McmLite::cancel_payment");

  if (credentials_ != nullptr) {
    credentials_->wipe();
    delete credentials_;
    credentials_ = nullptr;
  }
  cl_context_.wipe();

  // The card profile is still valid. Thus, go to initialized.
  state_ = McmLiteState::INITIALIZED;
}

const ByteArray McmLite::process_apdu(const ByteArray& c_apdu) {
  if ((state_ == McmLiteState::STOPPED || state_ == McmLiteState::INITIALIZED ||
       state_ == McmLiteState::RP_READY))
    return ResponseApdu(Iso7816::kSwConditionsNotSatisfied).value();

  const Byte cla = c_apdu.at(0);
  const Byte ins = c_apdu.at(1);
  try {
    switch (ins) {
      case kInsSelect:
        if (cla == kClaSelect) return select(SelectApdu(c_apdu)).value();
        break;
      case kInsGetGpo:
        if (cla == kClaGetGpo)
          return (processing_options(GpoApdu(c_apdu))).value();
        break;
      case kInsReadRecord:
        if (cla == kClaReadRecord)
          return (read_record(ReadRecordApdu(c_apdu))).value();
        break;
      case kInsGenerateAC:
        if (cla == kClaGenerateAC)
          return (generate_ac(GenerateACApdu(c_apdu))).value();
        break;
      case kInsComputeCC:
        if (cla == kClaComputeCC)
          return (compute_cc(ComputeCCApdu(c_apdu))).value();
        break;
      default:
        return ResponseApdu(Iso7816::kSwInsNotSupported).value();
    }
  }
  catch (const InvalidInput& e) {
    Log::instance()->d("Invalid Input: %s", e.what());
    return ResponseApdu(e.error_code()).value();
  }
  return ResponseApdu(Iso7816::kSwUnknown).value();
}

ResponseApdu McmLite::select(const SelectApdu& c_apdu) {
  const Byte p1 = c_apdu.p1();
  const Byte p2 = c_apdu.p2();

  // (SEL.1)
  if (p1 != kP1Select || p2 != kP2Select)
    return ResponseApdu(Iso7816::kSWIncorrectP1P2);

  const auto& cl_data = card_profile_->contactless_payment_data();  // (SEL.2)
  if (c_apdu.aid() == kPpseAid) {  // (SEL.3)
    state_ = McmLiteState::CL_NOT_SELECTED;
    return ResponseApdu(cl_data.ppse_fci(), Iso7816::kSuccessWord);
  }

  if (c_apdu.aid() == cl_data.aid()) {  // (SEL.4)
    cl_context_.set_alternate_aid(false);  // (SEL.5)
    state_ = McmLiteState::CL_SELECTED;    // (SEL.12 and SEL.6)
    return ResponseApdu(cl_data.payment_fci(), Iso7816::kSuccessWord);
  }

  if (cl_data.alternate_profile()) {  // (SEL.7)
    if (c_apdu.aid() == cl_data.alternate().aid()) {  // (SEL. 8)
      cl_context_.set_alternate_aid(true);  // (SEL.10)
      state_ = McmLiteState::CL_SELECTED;   // (SEL.12 and SEL.11)
      return ResponseApdu(cl_data.alternate().payment_fci(),
                          Iso7816::kSuccessWord);
    }
  }
  // (SEL. 9)
  state_ = McmLiteState::CL_NOT_SELECTED;
  return ResponseApdu(Iso7816::kSwFileNotFound);
}

void McmLite::validate_gpo_apdu(const GpoApdu& c_apdu) const {
  if (state_ != McmLiteState::CL_SELECTED)  // (GPO.1.1)
    throw Exception("", Iso7816::kSwConditionsNotSatisfied);

  // (GPO.1.2)
  if (c_apdu.p1() != kP1GetGpo) throw InvalidP1("Invalid P1");
  if (c_apdu.p2() != kP2GetGpo) throw InvalidP2("Invalid P2");

  // (GPO.1.3)
  const Byte lc = c_apdu.lc();
  if (lc != kLcGetGpo2 && lc != kLcGetGpo1) throw InvalidLc("Invalid LC value");

  const Byte data_tag    = c_apdu.command_template();
  const Byte data_length = c_apdu.command_template_length();

  if (data_tag != kGetGpoDataTag)
    throw Exception("Invalid Data Tag", Iso7816::kSwConditionsNotSatisfied);

  if (lc == kLcGetGpo2 && data_length != kGetGpoDataLength2) {  // (GPO.2.5)
    throw InvalidInput("Invalid GPO Type 2 APDU Length",
                       Iso7816::kSwConditionsNotSatisfied);
    // (GPO.2.3)
  } else if (lc == kLcGetGpo1 && data_length != kGetGpoDataLength1) {
    throw InvalidInput("Invalid GPO Type 1 APDU Length",
                       Iso7816::kSwConditionsNotSatisfied);
  }

  if (offline_terminal(c_apdu.terminal_type()))  // (GPO.2.6 and GPO.2.2)
      throw InvalidInput("Invalid Terminal Type",
      Iso7816::kSwConditionsNotSatisfied);
}

ResponseApdu McmLite::processing_options(const GpoApdu& c_apdu) {
  // (GPO.1.1 to GPO.2.2 or GPO.2.6)
  validate_gpo_apdu(c_apdu);

  // (GPO.2.3 and GPO.2.7)
  gpo_set_aip(c_apdu);

  // (GPO.2.4 and GPO.2.8)
  cl_context_.set_pdol_values(c_apdu.pdol_values());

  // (GPO.2.9)
  ByteArray response;
  if (cl_context_.alternate_aid()) {
    const auto& alt = card_profile_->contactless_payment_data().alternate();
    response = alt.gpo_response();
  } else {
    response = card_profile_->contactless_payment_data().gpo_response();
  }
  // Set the AIP based on the outcome of GPO.2.3 or GPO.2.7
  response[4] = cl_context_.aip()[0];
  response[5] = cl_context_.aip()[1];

  // (GPO.2.10)
  state_ = McmLiteState::CL_INITIATED;

  return ResponseApdu(response, Iso7816::kSuccessWord);
}

ResponseApdu McmLite::read_record(const ReadRecordApdu& c_apdu) const {
  // (RRC.1.1)
  if (state_ != McmLiteState::CL_SELECTED &&
      state_ != McmLiteState::CL_INITIATED) {
    throw InvalidState("Invalid State for Read Record C-APDU");
  }

  // (RRC.1.2 and RRC.1.3)
  if (c_apdu.p1() == 0x00) throw InvalidP1("Invalid P1 (0x00)");
  if ((c_apdu.p2() & 0x07) != 0x04 ) throw InvalidP2("Invalid P2");

  // (RRC.1.4)
  const auto& cl_data = card_profile_->contactless_payment_data();

  uint16_t record_id = RecordId(c_apdu.sfi_number(), c_apdu.record_number());

  std::map <uint16_t, ByteArray>::const_iterator it;
  it = cl_data.records().find(record_id);

  if ( it == cl_data.records().end() )
    throw Exception("SW Record not found", Iso7816::kSwRecordNotFound);

  // (RRC.1.5)
  return ResponseApdu(it->second, Iso7816::kSuccessWord);
}

//------------------------------------------------------------------------------
// Utility functions
//------------------------------------------------------------------------------

bool McmLite::offline_terminal(const Byte terminal_type) {
  const Byte type = terminal_type & 0x0F;
  return type == 0x03 || type == 0x06 ? true: false;
}

void McmLite::process_pin_info() {
  cvm_entered_ ? cvr_[0] |= 0x05 : cvr_[3] |= 0x20;
}

bool McmLite::is_transit(const ByteArray& amount,
                         const ByteArray& merc_cat_code) {
  if (!Zeroes(amount)) return false;

  if (merc_cat_code == kMerchantCodeTransit1 ||
      merc_cat_code == kMerchantCodeTransit2 ||
      merc_cat_code == kMerchantCodeTransit3 ||
      merc_cat_code == kMerchantCodeTransit4 )
    return true;

  return false;
}

bool McmLite::context_conflict() const {
  // Check whether the business logic has enforced any rule
  if (cl_context_.bl_amount().empty()) return false;

  const auto& context = cl_context_.contactless_transaction_context();
  const auto& bl_currency = cl_context_.bl_currency();

  if (!Zeroes(bl_currency) && (bl_currency != context.currency_code)) {
    return true;
  }

  if (cl_context_.bl_exact_amount()) {
    return cl_context_.bl_amount() != context.amount;
  }

  // In C++ we cannot have the amount as nullptr. Thus, in case of non exact
  // amount we assume that zero represent the null value
  if (Zeroes(cl_context_.bl_amount())) return false;

  return cl_context_.bl_amount() < context.amount;
}

//------------------------------------------------------------------------------
// Compute Crypto Checksum functions
//------------------------------------------------------------------------------

void McmLite::validate_computecc_apdu(const ComputeCCApdu& c_apdu) {
  const auto& cl_data = card_profile_->contactless_payment_data();
  // (CCC.1.1 and CCC.1.2)
  if (state_ != McmLiteState::CL_INITIATED   ||
      cl_data.pin_iv_cvc3_track2().empty()   ||
      cl_data.ciac_decline_on_ppms().empty()   ) {
    // (CCC.1.3)
    cancel_payment();
    throw InvalidState("Invalid state for ComputeCC");
  }
  // (CCC.1.4)
  if (c_apdu.p1() != kP1ComputeCC || c_apdu.p2() != kP2ComputeCC) {
    // (CCC.1.5)
    cancel_payment();
    throw InvalidP1("Invalid P1 or P2");
  }
  // (CCC.1.6)
  if (c_apdu.lc() != kLcComputeCC) {
    // (CCC.1.7)
    cancel_payment();
    throw InvalidLc("Invalid LC");
  }
  // (CCC.1.8)
  if (offline_terminal(c_apdu.terminal_type())) {
    // (CCC.1.9)
    cancel_payment();
    throw Exception("Invalid Terminaly Type (offline)",
                    Iso7816::kSwConditionsNotSatisfied);
  }
}

ResponseApdu McmLite::compute_cc(const ComputeCCApdu& c_apdu) {
  // (CCC.1.1 to CCC.1.9)
  validate_computecc_apdu(c_apdu);

  // (CCC.1.10)
  initialize_context(c_apdu);

  // (CCC.1.11)
  if (cvm_entered_) {
    cl_context_.set_poscii(kPosciiCvmEntered);
  } else {
    cl_context_.set_poscii(kPosciiZeroes);
  }

  // Context Check (CCC.2)
  ResponseApdu response;
  if (approve_ccc(c_apdu)) {
    response = ccc_online(c_apdu);
  } else {
    response = ccc_decline(c_apdu);
  }

  const auto& trx_context = cl_context_.contactless_transaction_context();
  cl_context_.listener()->on_event(trx_context);

  // Clear data structures
  cancel_payment();
  return response;
}

void McmLite::initialize_context(const ComputeCCApdu& c_apdu) {
  auto& context = cl_context_.contactless_transaction_context();
  context.atc                  = credentials_->atc();
  context.amount               = c_apdu.authorized_amount();
  context.currency_code        = c_apdu.transaction_currency_code();
  context.transaction_date     = c_apdu.transaction_date();
  context.transaction_type     = c_apdu.transaction_type();
  context.unpredictable_number = c_apdu.unpredictable_number();
}

bool McmLite::approve_ccc(const ComputeCCApdu& c_apdu) {
  const auto& card_data = card_profile_->contactless_payment_data();
  auto& context = cl_context_.contactless_transaction_context();

  const ByteArray& amount = c_apdu.authorized_amount();
  const ByteArray& merchant_cat_code = c_apdu.merchant_category_code();

  // (CCC.2.1) and (CCC.2.2)
  if (!card_data.transit() && is_transit(amount, merchant_cat_code)) {
    context.result = ContextType::UNSUPPORTED_TRANSIT;  // (CCC.2.3)
    return false;
  }

  // (CCC.2.4), (CCC.2.5), (CCC.2.6)
  if (context_conflict()) {
    // (CCC.2.7)
    context.result = ContextType::CONTEXT_CONFLICT;
    cl_context_.set_poscii(kPosciiConflictingContext);
    return false;
  }

  // (CCC.3.1)
  if (!ms_domestic_international(c_apdu.terminal_country_code())) {
    // We set the DECLINE status as well (not in the original specs)
    context.result = ContextType::MAGSTRIPE_DECLINED;  // (CCC.3.2)
    return false;
  }

  // (CCC.3.3, CCC.3.4, CCC.3.5)
  if (ccc_verify_cmv(c_apdu.mobile_support_indicator())) {
    context.result = ContextType::MAGSTRIPE_COMPLETED;  // (CCC.3.7)
    return cl_context_.online_allowed() ? true: false;  // (CCC.3.8)
  }

  // (CCC.3.6)
  cl_context_.set_poscii(kPosciiPinRequired);
  context.result = ContextType::MAGSTRIPE_FIRST_TAP;
  return false;
}

bool McmLite::ms_domestic_international(
    const ByteArray& terminal_country_code) const {

  const auto& risk_data = card_profile_->card_risk_management_data();
  const auto& cl_data = card_profile_->contactless_payment_data();

  if (terminal_country_code == risk_data.crm_country_code())
    return (cl_data.ciac_decline_on_ppms()[0] & 0x02) == 0x02 ? false : true;

  return (cl_data.ciac_decline_on_ppms()[0] & 0x04) == 0x04 ? false : true;
}

bool McmLite::ccc_verify_cmv(const Byte mobile_support_indicator) const {
  if (cvm_entered_ == true) return true;  // (CCC.3.3)

  if ((mobile_support_indicator & 0x02) == 0x02) return false;  // (CCC.3.4)

  return cl_context_.cvm_required() ? false: true;  // (CCC.3.5)
}

ResponseApdu McmLite::ccc_online(const ComputeCCApdu& c_apdu) {
  const auto& card_data = card_profile_->contactless_payment_data();
  auto& context = cl_context_.contactless_transaction_context();

  context.cid = kOnlineDecision;  // (CCC.4.1)

  // (CCC.4.2) - Build input to Message Authentication Code
  const ByteArray& pin_ivcvc3_track2 = card_data.pin_iv_cvc3_track2();
  const ByteArray& un = c_apdu.unpredictable_number();
  const ByteArray& atc = credentials_->atc();

  ByteArray input;
  input.reserve(pin_ivcvc3_track2.size() + un.size() + atc.size());
  input.insert(input.end(), pin_ivcvc3_track2.begin(), pin_ivcvc3_track2.end());
  input.insert(input.end(), un.begin(), un.end());
  input.insert(input.end(), atc.begin(), atc.end());

  // (CCC.4.3)
  const CryptoFactory* const crypto = CryptoFactory::instance();
  const ByteArray& umd_key          = credentials_->sk_cl_umd();
  const ByteArray& md_key           = credentials_->sk_cl_md();

  const ByteArray des_umd  = crypto->des_3(input, umd_key, true);
  const ByteArray des_md   = crypto->des_3(input,  md_key, true);

  // Security - wipe the single use keys as they are not needed any longer
  credentials_->wipe_keys();

  // (CCC.4.4)
  uint16_t a = WordToUnsigned16({ des_md[0],  des_md[1]}) % 1000;
  uint16_t b = WordToUnsigned16({des_umd[6], des_umd[7]}) % 1000;
  uint16_t c = WordToUnsigned16(atc) % 100;

  a += 1000 * (c / 10);
  b += 1000 * (c % 10);

  ByteArray crypto_atc = Unsigned16ToWord(a);
  ByteArray cvc3       = Unsigned16ToWord(b);

  context.application_cryptogram = {cvc3[0], cvc3[1], crypto_atc[0],
                                    crypto_atc[1], 0x00, 0x00, 0x00, 0x00};

  // (CCC.4.5)
  if ((c_apdu.mobile_support_indicator() & 0x01) == 0x01)
    return CCCResponseApdu(cvc3, crypto_atc, cl_context_.poscii());

  return CCCResponseApdu(cvc3, crypto_atc, ByteArray());  // poscii is empty
}

ResponseApdu McmLite::ccc_decline(const ComputeCCApdu& c_apdu) {
  auto& context = cl_context_.contactless_transaction_context();

  context.cid = kDeclineDecision;  // (CCC.5.1)

  if (!((c_apdu.mobile_support_indicator() & 0x01 ) == 0x01))  // (CCC.5.2)
    throw Exception("Mobile support indicator not set",  // (CCC.5.3)
                    Iso7816::kSwConditionsNotSatisfied);

  const ByteArray atc = credentials_->atc();

  // Delete credentials as they are no longer needed.
  credentials_->wipe();

  // (CCC.5.4)
  return static_cast<ResponseApdu>(CCCResponseApdu(atc, cl_context_.poscii()));
}

//------------------------------------------------------------------------------
// Generate AC functions
//------------------------------------------------------------------------------

void McmLite::initialize_context(const GenerateACApdu& c_apdu) {
  auto& context = cl_context_.contactless_transaction_context();
  context.atc                  = credentials_->atc();
  context.amount               = c_apdu.authorized_amount();
  context.currency_code        = c_apdu.transaction_currency_code();
  context.transaction_date     = c_apdu.transaction_date();
  context.transaction_type     = c_apdu.transaction_type();
  context.unpredictable_number = c_apdu.unpredictable_number();
}

void McmLite::validate_generateac_apdu(
    const mcbp_core::GenerateACApdu &c_apdu) {
  // (GAC.1.1 and GAC.1.2)
  const auto& data = card_profile_->contactless_payment_data();
  if (state_ != McmLiteState::CL_INITIATED)
    throw InvalidState("McmLite is not in CL_INITIATED");

  if (data.cdol1_related_data_length() == 0  || /* CDOL is not empty          */
      data.issuer_application_data().empty() || /* Issuer application data    */
      !data.rsa_certificate().initialized()  || /* RSA is initialized         */

      (!data.alternate_profile() &&
        (data.ciac_decline().empty() ||         /* Ciac Decline - Primary AID */
         data.cvr_mask().empty() )              /* CVR Mask - Primary AID     */
      )                                      ||

      (data.alternate_profile() &&              /* If Alternate Profile       */
        (data.alternate().ciac_decline().empty() || /* Check related values   */
         data.alternate().cvr_mask().empty()) )
      ) {
    // (GAC.1.3)
    cancel_payment();
    throw UnexpectedDataError("Error while processing card data");
  }

  // (GAC.1.4)
  if ((c_apdu.p1() & kRcpRfu) == kRcpRfu || c_apdu.p2() != 0) {
    // (GAC.1.5)
    cancel_payment();
    throw InvalidP1("Invalid P1 or P2 data");
  }
  // (GAC.1.6)
  const Byte& lc = c_apdu.lc();
  if (lc < kMinimumCdol1Lenght || lc != data.cdol1_related_data_length()) {
    // (GAC.1.7)
    cancel_payment();
    std::string lc  = std::to_string(c_apdu.lc());
    std::string min = std::to_string(kMinimumCdol1Lenght);
    std::string dat = std::to_string(data.cdol1_related_data_length());
    throw InvalidLc("Invalid CDOL data length:" + lc + "," + min + "," + dat);
  }
  // (GAC.1.8)
  if (offline_terminal(c_apdu.terminal_type())) {
    // (GAC.1.9)
    cancel_payment();
    throw Exception("Invalid Terminal Type (offline)",
                    Iso7816::kSwConditionsNotSatisfied);
  }
}

ResponseApdu McmLite::generate_ac(const GenerateACApdu& c_apdu) {
  ResponseApdu response;

  // (GAC.1.1 to GAC.1.9)
  validate_generateac_apdu(c_apdu);

  initialize_context(c_apdu);  // (GAC.1.10)
  gac_initialize_crypto_output();  // (GAC.2.1)
  mc_domestic_international(c_apdu.terminal_country_code());  // (GAC.2.2)
  gac_process_additional_check_table(c_apdu.cdol());  // (GAC.2.5) and (GAC.2.6)
  process_pin_info();  // (GAC.2.7)
  // Poscii is already set to null - no need for (GAC.2.8)

  // (GAC.3 and GAC.4)
  if (gac_approve_online(c_apdu)) {
    gac_arqc(c_apdu);  // (GAC.5)
  } else {
    gac_aac(c_apdu);  // (GAC.6)
  }
  // (GAC.7 and GAC.8)
  gac_ac(c_apdu, &response);

  const auto& trx_context = cl_context_.contactless_transaction_context();
  cl_context_.listener()->on_event(trx_context);  // (GAC.7.7) or (GAC.8.5)

  cancel_payment();  // (GAC.7.8) or (GAC.8.6)
  return response;
}

void McmLite::gac_process_additional_check_table(
    const ByteArray& cdol1_rel_data) {
  Byte cvr_mask     = 0x00;
  Byte ciac_decline = 0x00;

  if (!cl_context_.alternate_aid()) {
    const auto& cp_data = card_profile_->contactless_payment_data();
    cvr_mask     = cp_data.cvr_mask().at(5);
    ciac_decline = cp_data.ciac_decline().at(2);

  } else { /* Use Alternate Contacless card */
    const auto& cp_data = card_profile_->contactless_payment_data().alternate();
    cvr_mask     = cp_data.cvr_mask().at(5);
    ciac_decline = cp_data.ciac_decline().at(2);
  }

  // (GAC.2.5)
  if (!( (cvr_mask & 0x03) != 0x00 || (ciac_decline & 0x03) != 0x00)) return;

  // (GAC.2.6)
  const auto& risk_data = card_profile_->card_risk_management_data();
  const auto& cl_data   = card_profile_->contactless_payment_data();

  const ByteArray& additional_check_table = risk_data.additional_check_table();

  const Byte cdol1_related_data_length = cl_data.cdol1_related_data_length();
  const Byte act_position              = additional_check_table.at(0);
  const Byte act_length                = additional_check_table.at(1);
  const Byte act_number                = additional_check_table.at(2);

  if (act_position == 0x00                                       ||
      act_length   == 0x00                                       ||
      act_number   <  2                                          ||
      act_position +  act_length - 1 > cdol1_related_data_length ||
      act_length * act_number > 15                                 ) {
    return;
  }

  ByteArray masked_value(act_length, 0x00);

  for (std::size_t i = 0; i < act_length; i++) {
    masked_value.at(i) = cdol1_rel_data.at(act_position + i - 1) &
                         additional_check_table.at(3 + i);
  }

  for (std::size_t i = 1; i < act_number; i++) {
    ByteArray entry = {additional_check_table.begin() + (3 + i * act_length),
                   additional_check_table.begin() + (3 + (i + 1) * act_length)};
    if (entry == masked_value) {
      // Indicate 'Match found in Additional Check Table' in Card Verification
      // Result
      cvr_[5] |= 0x02;
      return;
    }
  }

  // Match not found
  cvr_[5] |= 0x01;
}

bool McmLite::gac_context_check(const GenerateACApdu& c_apdu) {
  auto& context = cl_context_.contactless_transaction_context();

  // (GAC.3.1) and (GAC.3.2)
  if (is_transit(context.amount, c_apdu.merchant_category_code())) {
    // (GAC.3.3)
    context.result = ContextType::UNSUPPORTED_TRANSIT;
    cl_context_.set_poscii(kPosciiZeroes);
    return false;
  }
  // GAC.3.4, GAC.3.5, GAC.3.6
  if (context_conflict()) {
    context.result = ContextType::CONTEXT_CONFLICT;
    cl_context_.set_poscii(kPosciiConflictingContext);
    return false;
  }
  return true;
}

bool McmLite::gac_crm(const GenerateACApdu& c_apdu) {
  auto& context = cl_context_.contactless_transaction_context();

  // (GAC.4.1 and GAC.4.3)
  if (((c_apdu.p1() & 0xC0) == 0x00) || gac_match_cvr_ciac_decline()) {
    // (GAC.4.2)
    context.result = ContextType::MCHIP_DECLINED;
    cl_context_.set_poscii(kPosciiZeroes);
    return false;
  }
  if (!gac_verify_cvm(c_apdu)) {  // (GAC.4.4, GAC.4.5, GAC.4.6, and GAC.4.7)
    // (GAC.4.8)
    context.result = ContextType::MCHIP_FIRST_TAP;
    cl_context_.set_poscii(kPosciiPinRequired);
    cvr_[5] |= 0x08;
    return false;
  }
  // (GAC.4.9)
  context.result = ContextType::MCHIP_COMPLETED;
  // (GAC.4.10)
  if (!cl_context_.online_allowed()) {
    // (GAC.4.11)
    cl_context_.set_poscii(kPosciiZeroes);
    return false;
  }
  // We can go to ARQC
  return true;
}

bool McmLite::gac_verify_cvm(const GenerateACApdu& c_apdu) {
  if (cvm_entered_) return true;  // (GAC.4.4)

  const ByteArray& cvm_results = c_apdu.cvm_results();
  const Byte cvm_results_6_1 = cvm_results[0] & 0x3F;

  const bool cvm_offline = (cvm_results_6_1 == 0x01 || cvm_results_6_1 == 0x04)
                            && cvm_results[2] == 0x02;  // (GAC.4.5 and GAC.4.7)

  if ( !cvm_offline && !cl_context_.cvm_required()) return true;  // (GAC.4.7)

  if (cvm_offline) cvr_[3] |= 0x01;  // (GAC.4.6)

  return false;
}

void McmLite::gac_arqc(const GenerateACApdu& c_apdu) {
  // (GAC.5.1)
  // Indicate 'ARQC Returned in First Generated AC' and 'AC Not Requested' in
  // Second Generated AC in CVR
  cvr_.at(0) |= 0xA0;

  // (GAC.5.2) Indicate ARQC in Cryptogram Information Data
  cl_context_.cryptogram_output().cid = kOnlineDecision;
  cl_context_.contactless_transaction_context().cid = kOnlineDecision;

  // (GAC.5.3)
  if ((c_apdu.p1() & 0x10) == 0x10) {
    // (GAC.5.4) - Indicate 'Combined DDA/AC Generation Returned in First
    //                       Generate AC' in Card Verification Results
    cvr_.at(1) |= 0x40;
  }
}

void McmLite::gac_aac(const GenerateACApdu& c_apdu) {
  // (GAC.6.1)
  // Indicate 'AAC Returned in First Generated AC' and 'AC Not Requested' in
  // Second Generated AC in CVR
  cvr_.at(0) |= 0x80;

  // (GAC.6.2) Indicate ARQC in Cryptogram Information Data
  cl_context_.cryptogram_output().cid = kDeclineDecision;
  cl_context_.contactless_transaction_context().cid = kDeclineDecision;

  // (GAC.6.3) Check whether AAC and CDA(DDA/AC) have been requested
  if (c_apdu.p1() == 0x10) {
    // (GAC.6.4)
    if (cvm_entered_ || !cl_context_.cvm_required()) {
      // (GAC.6.5) - Indicate 'Combined DDA/AC Generation Returned in First
      //             Generate AC' in Card Validation Results
      cvr_.at(1) |= 0x40;
    }
  }
}

void McmLite::gac_ac(const GenerateACApdu& c_apdu, ResponseApdu* const r_apdu) {
  // (GAC.7.1)
  if (!cl_context_.alternate_aid()) {
    const auto& cl_data = card_profile_->contactless_payment_data();
    const ByteArray& cvr_mask_and = cl_data.cvr_mask();

    for (std::size_t i = 0; i < cvr_.size(); i++)
      cvr_[i] &= cvr_mask_and[i];

  } else {  // Alternate AID
    const auto& cl_data = card_profile_->contactless_payment_data().alternate();
    const ByteArray& cvr_mask_and = cl_data.cvr_mask();

    for (std::size_t i = 0; i < cvr_.size(); i++) {
      cvr_[i] &= cvr_mask_and[i];
    }
  }

  // (GAC.7.2) - Build Input for the Application Cryptogram
  const ByteArray& cdol1_rel_data = c_apdu.cdol();
  const int input_size = 29 + static_cast<int>(cl_context_.aip().size())   +
                              static_cast<int>(credentials_->atc().size()) +
                              static_cast<int>(cvr_.size());

  const ByteArray& aip = cl_context_.aip();
  const ByteArray& atc = credentials_->atc();

  ByteArray ac_input;
  ac_input.reserve(input_size);
  ac_input = {cdol1_rel_data.begin(), cdol1_rel_data.begin() + 29};
  Append(aip, &ac_input);
  Append(atc, &ac_input);
  Append(cvr_, &ac_input);

  // (GAC.7.3) - Compute 2 values of Application Cryptogram
  const ByteArray& sk_cl_context_umd = credentials_->sk_cl_umd();
  const ByteArray& sk_cl_context_md  = credentials_->sk_cl_md();

  const CryptoFactory* const crypto = CryptoFactory::instance();

  ByteArray ac_umd = crypto->mac(ac_input, sk_cl_context_umd);
  ByteArray ac_md  = crypto->mac(ac_input, sk_cl_context_md);

  cl_context_.cryptogram_output().application_cryptogram = ac_umd;
  cl_context_.contactless_transaction_context().application_cryptogram = ac_umd;

  // (GAC.7.4) - Build Issuer Application Data
  for (std::size_t i = 0; i < cvr_.size(); i++) {
    cl_context_.cryptogram_output().issuer_application_data[2 + i] = cvr_[i];
  }

  // Set field DAC/IDN in bytes 9:10 of the Issuer Application Data
  auto& cryptogram_output = cl_context_.cryptogram_output();
  auto& issuer_app_data = cryptogram_output.issuer_application_data;

  if (Zeroes(c_apdu.icc_dynamic_number())) {
    issuer_app_data[8] = cdol1_rel_data[30];
    issuer_app_data[9] = cdol1_rel_data[31];
  } else {
    issuer_app_data[8] = cdol1_rel_data[32];
    issuer_app_data[9] = cdol1_rel_data[33];
  }

  // Store the first 5 bytes of the AC (MD) into Issuer Application Data
  for (std::size_t i = 0; i < 5; i++) {
    issuer_app_data[11 + i] = ac_md[i];
  }

  // (GAC.7.5)
  if ((cvr_[1] & 0x40) == 0x40) {
    gac_cda(c_apdu, r_apdu);
    return;
  }

  // (GAC.7.6)
  const CryptogramOutput& output = cl_context_.cryptogram_output();
  *r_apdu = static_cast<ResponseApdu>(
                GenerateACResponseApdu(false, output.application_cryptogram,
                                       output.atc, output.cid,
                                       output.issuer_application_data,
                                       cl_context_.poscii()));
}

void McmLite::gac_cda(const GenerateACApdu& c_apdu,
                      ResponseApdu* const r_apdu) {
  const ByteArray& cdol = c_apdu.cdol();
  const ByteArray& pdol = cl_context_.pdol_values();
  CryptogramOutput& output = cl_context_.cryptogram_output();
  const RsaCertificate& rsa_certificate =
                    card_profile_->contactless_payment_data().rsa_certificate();

  // (GAC.8.1)
  ByteArray hash_input;
  /**
   * PDOL (if not null)                             - up to 11 bytes
   * CDOL                                           -       Lc bytes
   * Cryptogram Information Data                    -       4 (2 + 1 +  1) bytes
   * Application Transaction Counter                -       5 (2 + 1 +  2) bytes
   * Issuer Application Data                        -      21 (2 + 1 + 18) bytes
   * POS Cardholder Interaction info (if not null)  -       6 (2 + 1 +  3) bytes
   */
  hash_input.reserve(pdol.size() + cdol.size() + 4 + 5 + 21 + 6);
  Append(pdol, &hash_input);
  Append(cdol, &hash_input);
  Append(Tlv(kTagCid, output.cid), &hash_input);
  Append(Tlv(kTagAtc, output.atc), &hash_input);
  Append(Tlv(kTagIad, output.issuer_application_data), &hash_input);
  if (!cl_context_.poscii().empty())
    Append(Tlv(kTagPoscii, cl_context_.poscii()), &hash_input);

  // (GAC.8.2)

  // Get an instance of the Crypto Factory
  const CryptoFactory* const crypto = CryptoFactory::instance();
  ByteArray hash_result = crypto->sha_1(hash_input);

  /**
   * Signed Data Format         1 byte  - '05'
   * Hash Algorithm Indicator   1 byte  - '01'
   * Length of ICC Dynamic Data 1 byte  - '26'
   * ICC Dynamic Number         8 bytes - 'credentials.IDN[1:8]
   * CID                        1 byte  - 'crypto.output.cid'
   * Application Cryptogram     8 bytes - 'crypto.output.application_cryptogram'
   * Hash Result               20 bytes - 'hash_result'
   * Padding         ICC-key - 63 bytes - 'BB...BB'
   * Unpredictable Number       4 bytes - 'Unpredictable Number'
   */
  ByteArray dynamic_application_data;
  int padding_size = rsa_certificate.key_length() - 63;
  // This check should never be necessary as nobody will use 32 bits keys :-)
  padding_size < 0 ? padding_size = 0: padding_size;

  ByteArray padding(padding_size, 0xBB);
  dynamic_application_data.reserve(padding_size + 44);

  const ByteArray& idn = credentials_->idn();
  const Byte idn_len = static_cast<Byte>(credentials_->idn().size());

  dynamic_application_data = {0x05, 0x01, 0x26, idn_len};
  Append(idn, &dynamic_application_data);
  Append(output.cid, &dynamic_application_data);
  Append(output.application_cryptogram, &dynamic_application_data);
  Append(hash_result, &dynamic_application_data);
  Append(padding, &dynamic_application_data);
  Append({cdol.begin() + 25, cdol.begin() + 29}, &dynamic_application_data);

  // GPO.8.3 - Compute Signed Dynamic Application Data
  ByteArray hash_dyn_app_data = crypto->sha_1(dynamic_application_data);

  /**
   * The data to be returned is in the Generate AC response APDU is 
   * equal to the RSA keylength
   * -2 is to account for HEADER and TRAILER
   */
  const int dyn_app_data_ins_len = rsa_certificate.key_length() -
                               static_cast<int>(hash_dyn_app_data.size()) - 2;

  // Now add Header and store only the relevant part in the output
  ByteArray cdad;

  cdad.reserve(sizeof(kDdaHeader) + dyn_app_data_ins_len +
               hash_dyn_app_data.size() + sizeof(kDdaTrailer));

  cdad = {kDdaHeader};
  Append({dynamic_application_data.begin(),
          dynamic_application_data.begin() + dyn_app_data_ins_len}, &cdad);
  Append(hash_dyn_app_data, &cdad);
  Append(kDdaTrailer, &cdad);

  output.signed_dynamic_application_data = crypto->rsa(cdad, rsa_certificate);

  *r_apdu = static_cast<ResponseApdu>(
                GenerateACResponseApdu(true,
                                       output.signed_dynamic_application_data,
                                       output.atc, output.cid,
                                       output.issuer_application_data,
                                       cl_context_.poscii()));
}

void McmLite::gac_initialize_crypto_output() {
  const auto& data = card_profile_->contactless_payment_data();
  auto& cryptogram_output = cl_context_.cryptogram_output();
  cryptogram_output.atc = credentials_->atc();
  cryptogram_output.issuer_application_data = data.issuer_application_data();
}

bool McmLite::gac_approve_online(const GenerateACApdu& c_apdu) {
  // (GAC.3)
  if (!gac_context_check(c_apdu)) return false;

  // (GAC.4)
  if (!gac_crm(c_apdu)) return false;

  // We can go to ARQC
  return true;
}

void McmLite::mc_domestic_international(const ByteArray& term_country_code) {
  const CardRiskManagementData& c = card_profile_->card_risk_management_data();
  const ByteArray& card_country = c.crm_country_code();
  if (term_country_code.empty() || term_country_code != card_country) {
    cvr_[3] |= 0x04;
  } else {
    cvr_[3] |= 0x02;
  }
}

bool McmLite::gac_match_cvr_ciac_decline() const {
  const auto& cl_data = card_profile_->contactless_payment_data();
  const ByteArray& cvr = {cvr_.begin() + 3, cvr_.begin() + 6};
  const ByteArray *ciac_decline = nullptr;

  if (cl_context_.alternate_aid()) {
    ciac_decline = &cl_data.alternate().ciac_decline();
  } else {
    ciac_decline = &cl_data.ciac_decline();
  }

  for (std::size_t i = 0; i < cvr.size(); i++)
    if ((cvr[i] & (*ciac_decline)[i]) != 0x00) return true;
  return false;
}

//------------------------------------------------------------------------------
// Remote Payment functions
//------------------------------------------------------------------------------

void McmLite::create_remote_cryptogram(const CryptogramInput& input,
                                       TransactionOutput* const output) {
  // Useful variables
  const RemotePaymentData& rp_data = card_profile_->remote_payment_data();

  // (REM.1.1, 1.2, 1.3, 1.4)
  rp_check_state_input(output);
  // (REM.1.5)
  rp_initialize_output(output);
  // (REM.1.6, 1.7, 1.8)
  mc_domestic_international(input.terminal_country_code);
  // (REM.1.9)
  if (((rp_data.cvr_mask()[5]     & 0x03) != 0x00) ||
      ((rp_data.ciac_decline()[2] & 0x03) != 0x00)) {
    // (REM.1.10) - Build the CDOL1 value and process the additional check table
    ByteArray cdol1 = rp_build_cdol1(input);
    gac_process_additional_check_table(cdol1);
  }
  // (REM.2.1)
  process_pin_info();
  // (REM.2.2, 2.3, 2.4, 2.5, 2.6, 2.7)
  rp_decide_aac_or_arq(input, output);

  // Now generate AC

  // (REM.3.1)
  rp_apply_cvr_mask(rp_data.cvr_mask());

  // (REM.3.2) - Build the input for Application Cryptogram Generation
  ByteArray ac_input;
  ac_input.reserve(39);
  ByteArray cdol1 = rp_build_cdol1(input);
  const ByteArray& atc = output->cryptogram_output().atc;

  ac_input.insert(ac_input.end(), cdol1.begin(), cdol1.end());
  ac_input.insert(ac_input.end(), output->aip().begin(), output->aip().end());
  ac_input.insert(ac_input.end(), atc.begin(), atc.end());
  ac_input.insert(ac_input.end(), cvr_.begin(), cvr_.end());

  // (REM.3.3) - Compute 2 values of Application Cryptogram
  const ByteArray& sk_rp_umd = credentials_->sk_rp_umd();
  const ByteArray& sk_rp_md  = credentials_->sk_rp_md();

  const CryptoFactory *const crypto = CryptoFactory::instance();

  ByteArray ac_umd = crypto->mac(ac_input, sk_rp_umd);
  ByteArray ac_md  = crypto->mac(ac_input, sk_rp_md);;

  output->cryptogram_output().application_cryptogram = ac_umd;

  // (REM.3.4)
  for (std::size_t i = 0; i < cvr_.size(); i++)
    output->cryptogram_output().issuer_application_data[2 + i] = cvr_[i];
  // Set DAC/IDN
  output->cryptogram_output().issuer_application_data[8] = 0x00;
  output->cryptogram_output().issuer_application_data[9] = 0x00;
  // Byte 1:5 of the AC_MD
  for (std::size_t i = 0; i < 5; i++)
    output->cryptogram_output().issuer_application_data[11 + i] = ac_md[i];

  // (REM.3.5)
  cancel_payment();
}

void McmLite::rp_check_state_input(TransactionOutput * const output) {
  // (REM.1.1)
  if (state_ != McmLiteState::RP_READY) {
    // (REM.1.2)
    cancel_payment();
    throw InvalidState("create_remote_cryptogram");
  }
  // (REM.1.3)
  if (output == nullptr) {
    // (REM.1.4)
    cancel_payment();
    throw InvalidInput("create_remote_cryptogram");
  }
}

void McmLite::rp_initialize_output(TransactionOutput * const output) {
  // (REM.1.5)
  const RemotePaymentData& rp_data = card_profile_->remote_payment_data();
  // Initialize output
  output->set_track2_equivalent_data(rp_data.track2_equivalent_data());
  output->set_pan(rp_data.pan());
  output->set_pan_sequence_number(rp_data.pan_sequence_number());
  output->set_aip(rp_data.aip());
  output->set_application_expiry_date(rp_data.application_expiry_date());
  output->cryptogram_output().atc = credentials_->atc();
  output->set_cvm_entered(cvm_entered_);
  output->cryptogram_output().issuer_application_data =
                                          rp_data.issuer_application_data();
}

ByteArray McmLite::rp_build_cdol1(const mcbp_core::CryptogramInput& input) {
  ByteArray cdol1;
  cdol1.reserve(29);
  ByteArray zero(6, 0x00);
  if (input.amount_authorized.empty()) {
    Append(zero, &cdol1);
  } else {
    Append(input.amount_authorized, &cdol1);
  }
  if (input.amount_other.empty()) {
    Append(zero, &cdol1);
  } else {
    Append(input.amount_other, &cdol1);
  }
  if (input.terminal_country_code.empty()) {
    Append({zero.begin(), zero.begin() + 2}, &cdol1);
  } else {
    Append(input.terminal_country_code, &cdol1);
  }
  if (input.tvr.empty()) {
    Append({zero.begin(), zero.begin() + 5}, &cdol1);
  } else {
    Append(input.tvr, &cdol1);
  }
  if (input.transaction_currency_code.empty()) {
    Append({zero.begin(), zero.begin() + 2}, &cdol1);
  } else {
    Append(input.transaction_currency_code, &cdol1);
  }
  if (input.transaction_date.empty()) {
    Append({zero.begin(), zero.begin() + 3}, &cdol1);
  } else {
    Append(input.transaction_date, &cdol1);
  }
  if (input.transaction_type == 0x00) {
    Append(0x00, &cdol1);
  } else {
    Append(input.transaction_type, &cdol1);
  }
  if (input.unpredictable_number.empty()) {
    Append({zero.begin(), zero.begin() + 4}, &cdol1);
  } else {
    Append(input.unpredictable_number, &cdol1);
  }
  return cdol1;
}

void McmLite::rp_decide_aac_or_arq(const CryptogramInput& input,
                                   TransactionOutput* const output) {
  const RemotePaymentData& rp_data = card_profile_->remote_payment_data();
  ByteArray tmp;
  // (REM.2.2, 2.3)
  tmp.resize(rp_data.ciac_decline().size());
  for (std::size_t i = 0; i < rp_data.ciac_decline().size(); i++) {
    tmp[i] = cvr_[3 + i] & rp_data.ciac_decline()[i];
  }
  if (input.online_allowed && Zeroes(tmp)) {
    // (REM.2.6, 2.7)
    cvr_[0] |= 0xA0;
    output->cryptogram_output().cid = kOnlineDecision;
  } else {
    // (REM.2.4, 2.5)
    cvr_[0] |= 0x80;
    output->cryptogram_output().cid = kDeclineDecision;
  }
}

void McmLite::rp_apply_cvr_mask(const ByteArray& mask) {
  if (cvr_.size() != mask.size())
    throw InvalidInput("Invalid mask size");
  for (std::size_t i = 0; i < cvr_.size(); i++)
    cvr_[i] &= mask[i];
}

//------------------------------------------------------------------------------
// GPO functions
//------------------------------------------------------------------------------

void McmLite::gpo_set_aip(const GpoApdu& c_apdu) {
  if (c_apdu.lc() != kLcGetGpo2 && c_apdu.lc() != kLcGetGpo1)
    throw InvalidInput("gpo_set_aip");

  if (cl_context_.alternate_aid()) {
    const auto& data = card_profile_->contactless_payment_data().alternate();
    const ByteArray& aip = {data.gpo_response()[4], data.gpo_response()[5]};
    cl_context_.set_aip(aip);

  } else {
    const auto& data = card_profile_->contactless_payment_data();
    const ByteArray& aip = {data.gpo_response()[4], data.gpo_response()[5]};
    cl_context_.set_aip(aip);
  }

  if (c_apdu.lc() == kLcGetGpo1) {
    // Terminal Risk Management data is PDOL Related Data [3:10]
    const ByteArray& risk_management_data = c_apdu.risk_management_data();
    const ByteArray& terminal_country_code = c_apdu.terminal_country_code();

    if (Zeroes(risk_management_data) &&
        (terminal_country_code == kTerminalCodeUS ||
         terminal_country_code == kTerminalCode0000)) {
      ByteArray aip = cl_context_.aip();
      // aip[0] &= 0xFF;  /* Removed as it has no effect */
      aip[1] &= 0x7F;
      cl_context_.set_aip(aip);
    }
  }
}

McmLite::~McmLite() {
  stop();
  if (credentials_ != nullptr) {
    credentials_->wipe();
    delete credentials_;
    credentials_ = nullptr;
  }
  cl_context_.wipe();
}

}  // namespace mcbp_core
