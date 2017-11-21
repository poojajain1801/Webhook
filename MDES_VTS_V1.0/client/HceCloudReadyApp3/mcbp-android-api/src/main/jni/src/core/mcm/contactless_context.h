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

// Project libraries
#include <utils/transaction_keys.h>
#include <utils/emvco/response_apdu.h>
#include <utils/byte_array.h>
#include <core/mcm/mcm_lite_listener.h>
#include <utils/mcbp_core_exception.h>
#include <core/mcm/contactless_transaction_context.h>
#include <core/mcm/cryptogram_output.h>

#ifndef SRC_CORE_MCM_CONTACTLESS_CONTEXT_H_  // NOLINT
#define SRC_CORE_MCM_CONTACTLESS_CONTEXT_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The contactless context
 *  \details   It stores all the information related to the contactless cards
 *             and related business logic values
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class ContactlessContext {
 public:
  /// Default constructor
  ContactlessContext();

  /// Set the bl_amount
  void set_bl_amount(const ByteArray& bl_amount);

  /// Get the bl amount
  const ByteArray& bl_amount() const;

  /// Set the bl amount
  void set_bl_currency(const ByteArray& bl_currency);

  /// Get the bl currency
  const ByteArray& bl_currency() const;

  /// Set the bl exact amount
  void set_bl_exact_amount(const bool bl_exact_amount);

  /// Get the bl exact amount
  const bool bl_exact_amount() const;

  /// Set the cvm required
  void set_cvm_required(const bool cvm_required);

  // Get the cvm required
  bool cvm_required() const;

  // Set the online allowed
  void set_online_allowed(const bool online_allowed);

  // Get the online allowed
  bool online_allowed() const;

  // Set the alternate aid flag
  void set_alternate_aid(const bool alternate_aid);

  // Get the alternate aid
  bool alternate_aid() const;

  // Set the aip
  void set_aip(const ByteArray& aip);

  // Get the aip
  const ByteArray& aip() const;

  // Set the poscii
  void set_poscii(const ByteArray& poscii);

  // Get the poscii
  const ByteArray& poscii() const;

  // Set the response apdu
  void set_response_apdu(const ResponseApdu& r_apdu);

  // Get the response apdu
  const ResponseApdu& response_apdu() const;

  // Set the pdol
  void set_pdol_values(const ByteArray& pdol);

  // Get the pdol
  const ByteArray& pdol_values() const;

  // Set the listener
  void set_listener(McmLiteListener * const listener);

  // Get the listener
  McmLiteListener* listener() const;

  // Get the Transaction Context
  ContactlessTransactionContext& contactless_transaction_context();

  // Get the Transaction Context (Read-only)
  const ContactlessTransactionContext& contactless_transaction_context() const;

  // Get the Cryptogram Output
  CryptogramOutput& cryptogram_output();

  /**
   * Securely delete the content of the data structure
   */
  void wipe();

  /**
   * Denstructor
   */
  ~ContactlessContext();

 protected:
  // None

 private:
  // Amount
  ByteArray bl_amount_;

  // Currency code
  ByteArray bl_currency_;

  // Flag reporting whether the amount specified is exact or not
  bool bl_exact_amount_;

  // Flag specifying whether a card holder verification method is required
  bool cvm_required_;

  // Flag specifying whether the transaction can be approved online
  bool online_allowed_;

  // Flag specifying whether the alternate AID has been selected
  bool alternate_aid_;

  /*
   * Application Interchange Profile
   * Tag: 82 - 2 Bytes - Format: b
   */
  ByteArray aip_;

  /**
   * POS Cardholder Interaction Information
   * Tag: DF4B - 3 Bytes - Format: b
   */
  ByteArray poscii_;

  /**
   * PDOL Related data (3 or 13 bytes)
   */
  ByteArray pdol_values_;

  /**
   * The response APDU. 
   * The length depends on the type of transaction 
   */
  ResponseApdu response_apdu_;

  /**
   * Contactless Transaction Context
   */
  ContactlessTransactionContext contactless_transaction_context_;

  /**
   * Contactless Cryptogram Output (supporting data structure)
   */
  CryptogramOutput cryptogram_output_;

  // Listener to the MppMcbp object
  McmLiteListener *listener_;
};

// Inline functions

inline ContactlessTransactionContext&
                         ContactlessContext::contactless_transaction_context() {
  return contactless_transaction_context_;
}
inline const ContactlessTransactionContext&
                   ContactlessContext::contactless_transaction_context() const {
  return contactless_transaction_context_;
}
inline CryptogramOutput& ContactlessContext::cryptogram_output() {
  return cryptogram_output_;
}
inline McmLiteListener* ContactlessContext::listener() const {
  return listener_;
}
inline void ContactlessContext::set_bl_amount(const ByteArray& bl_amount) {
  bl_amount_ = bl_amount;
}
inline const ByteArray& ContactlessContext::bl_amount() const {
  return bl_amount_;
}
inline void ContactlessContext::set_bl_currency(const ByteArray& bl_currency) {
  bl_currency_ = bl_currency;
}
inline const ByteArray& ContactlessContext::bl_currency() const {
  return bl_currency_;
}
inline
void ContactlessContext::set_bl_exact_amount(const bool bl_exact_amount) {
  bl_exact_amount_ = bl_exact_amount;
}
inline const bool ContactlessContext::bl_exact_amount() const {
  return bl_exact_amount_;
}
inline void ContactlessContext::set_cvm_required(const bool cvm_required) {
  cvm_required_ = cvm_required;
}
inline bool ContactlessContext::cvm_required() const {
  return cvm_required_;
}
inline void ContactlessContext::set_online_allowed(const bool online_allowed) {
  online_allowed_ = online_allowed;
}
inline bool ContactlessContext::online_allowed() const {
  return online_allowed_;
}
inline void ContactlessContext::set_alternate_aid(const bool alternate_aid) {
  alternate_aid_ = alternate_aid;
}
inline bool ContactlessContext::alternate_aid() const {
  return alternate_aid_;
}
inline void ContactlessContext::set_aip(const ByteArray& aip) {
  aip_ = aip;
}
inline const ByteArray& ContactlessContext::aip() const {
  return aip_;
}
inline void ContactlessContext::set_poscii(const ByteArray& poscii) {
  poscii_ = poscii;
}
inline const ByteArray& ContactlessContext::poscii() const {
  return poscii_;
}
inline void ContactlessContext::set_response_apdu(const ResponseApdu& r_apdu) {
  response_apdu_ = r_apdu;
}
inline const ResponseApdu& ContactlessContext::response_apdu() const {
  return response_apdu_;
}
inline void ContactlessContext::set_pdol_values(const ByteArray& pdol) {
  pdol_values_ = pdol;
}
inline const ByteArray& ContactlessContext::pdol_values() const {
  return pdol_values_;
}
inline void ContactlessContext::set_listener(McmLiteListener * const listener) {
  listener_ = listener;
}

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CONTACTLESS_CONTEXT_H_)  // NOLINT
