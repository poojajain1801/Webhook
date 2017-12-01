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

#include <utils/emvco/command_apdu.h>

#ifndef SRC_UTILS_EMVCO_GENERATE_AC_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_GENERATE_AC_APDU_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Generate AC C-APDU
 *  \details   Provide simple access to the relevant fields of the C-APDU, such
 *             as CDOL1 Related Data fields
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class GenerateACApdu : public CommandApdu {
 public:
  /**
  * Instantiates a new generate ac apdu.
  * @param gen_ac_apud The Generate AC C-APDU as received from the terminal
  */
  explicit GenerateACApdu(const ByteArray& gen_ac_apdu);

  /**
   * Get the CDOL 1 Related data
   * @return a constant reference to the CDOL1 Related Data
   */
  const ByteArray& cdol() const NOEXCEPT;

  /**
   * Get the authorized amount.
   * @return a constant reference to the authorized amount
   */
  const ByteArray& authorized_amount() const NOEXCEPT;

  /**
   * Get the other amount.
   * @return a constant reference to the other amount
   */
  const ByteArray& get_other_amount() const NOEXCEPT;

  /**
   * Get the terminal country code.
   * @return a constant reference to the terminal country code
   */
  const ByteArray& terminal_country_code() const NOEXCEPT;

  /**
   * Get the terminal verification results.
   * @return a constant reference to the terminal verification results
   */
  const ByteArray& terminal_verification_results() const NOEXCEPT;

  /**
   * Get the transaction currency code.
   * @return a constant reference to the transaction currency code
   */
  const ByteArray& transaction_currency_code() const NOEXCEPT;

  /**
  * Get the transaction date.
  * @return a constant reference to the transaction date
  */
  const ByteArray& transaction_date() const NOEXCEPT;

  /**
   * Get the transaction type.
   * @return a constant reference to the transaction type
   */
  const Byte& transaction_type() const NOEXCEPT;

  /**
   * Get the unpredictable number.
   * @return a constant reference to the unpredictable number
   */
  const ByteArray& unpredictable_number() const NOEXCEPT;

  /**
   * Get the terminal type.
   * @return a constant reference to the terminal type
   */
  const Byte& terminal_type() const NOEXCEPT;

  /**
   * Get the data authentication code.
   * @return a constant reference to the data authentication code
   */
  const ByteArray& data_authentication_code() const NOEXCEPT;

  /**
   * Get the icc dynamic number.
   * @return a constant reference to the icc dynamic number
   */
  const ByteArray& icc_dynamic_number() const NOEXCEPT;

  /**
  * Get the cvm results.
  * @return a constant reference to the cvm results
  */
  const ByteArray& cvm_results() const NOEXCEPT;

  /**
  * Get the Merchant Category Code
  * @return a constant reference to the Merchant Category Code
  */
  const ByteArray& merchant_category_code() const NOEXCEPT;

 protected:
  // None

 private:
  /**
   * The cdol.
   */
  ByteArray cdol_;

  /**
   * The authorized amount.
   */
  ByteArray authorized_amount_;

  /**
   * The other amount.
   */
  ByteArray other_amount_;

  /**
   * The terminal country code.
   */
  ByteArray terminal_country_code_;

  /**
   * The terminal verification results.
   */
  ByteArray terminal_verification_results_;

  /**
   * The transaction currency code.
   */
  ByteArray transaction_currency_code_;

  /**
   * The transaction date.
   */
  ByteArray transaction_date_;

  /**
   * The transaction type.
   */
  Byte transaction_type_;

  /**
   * The unpredictable number.
   */
  ByteArray unpredictable_number_;

  /**
   * The terminal type.
   */
  Byte terminal_type_;

  /**
   * The data authentication code.
   */
  ByteArray data_authentication_code_;

  /**
   * The icc dynamic number.
   */
  ByteArray icc_dynamic_number_;

  /**
   * The cvm results.
   */
  ByteArray cvm_results_;

  /**
   * The cvm results.
   */
  ByteArray merchant_category_code_;

  /**
   * Parses the.
   *
   * @param genAC the gen ac
   */
  void parse(const ByteArray& genAC);
};

// Inline function definitions

inline const ByteArray& GenerateACApdu::cdol() const NOEXCEPT {
  return cdol_;
}
inline const ByteArray& GenerateACApdu::authorized_amount() const NOEXCEPT {
  return authorized_amount_;
}
inline const ByteArray& GenerateACApdu::get_other_amount() const NOEXCEPT {
  return other_amount_;
}
inline const ByteArray& GenerateACApdu::terminal_country_code() const NOEXCEPT {
  return terminal_country_code_;
}
inline
const ByteArray& GenerateACApdu::terminal_verification_results() const
    NOEXCEPT {
  return terminal_verification_results_;
}
inline
const ByteArray& GenerateACApdu::transaction_currency_code() const NOEXCEPT {
  return transaction_currency_code_;
}
inline const ByteArray& GenerateACApdu::transaction_date() const NOEXCEPT {
  return transaction_date_;
}
inline const Byte& GenerateACApdu::transaction_type() const NOEXCEPT {
  return transaction_type_;
}
inline const ByteArray& GenerateACApdu::unpredictable_number() const NOEXCEPT {
  return unpredictable_number_;
}
inline const Byte& GenerateACApdu::terminal_type() const NOEXCEPT {
  return terminal_type_;
}
inline
const ByteArray& GenerateACApdu::data_authentication_code() const NOEXCEPT {
  return data_authentication_code_;
}
inline const ByteArray& GenerateACApdu::icc_dynamic_number() const NOEXCEPT {
  return icc_dynamic_number_;
}
inline const ByteArray& GenerateACApdu::cvm_results() const NOEXCEPT {
  return cvm_results_;
}
inline
const ByteArray& GenerateACApdu::merchant_category_code() const NOEXCEPT {
  return merchant_category_code_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_GENERATE_AC_APDU_H_)  // NOLINT
