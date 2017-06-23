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

// C++ Libraries

// Project Libraries
#include <utils/emvco/command_apdu.h>

#ifndef SRC_UTILS_EMVCO_COMPUTE_CC_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_COMPUTE_CC_APDU_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Compute Crypto Checksum AC C-APDU
 *  \details   Provide simple access to the relevant fields of the C-APDU, such
 *             as UDOL Related Data fields
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class ComputeCCApdu : public CommandApdu {
 public:
  /**
   * Instantiates a new Compute Crypto Checksum C-APDU.
   * @param ccc the Compute Crypto Checksum C-APDU as received from the reader
   */
  explicit ComputeCCApdu(const ByteArray& ccc);

  /**
    * Gets the udol.
    * @return the udol
    */
  const ByteArray& udol() const;

  /**
   * Gets the unpredictable number.
   * @return the unpredictable number
   */
  const ByteArray& unpredictable_number() const;

  /**
   * Gets the mobile support indicator.
   * @return the mobile support indicator
   */
  const Byte& mobile_support_indicator() const;

  /**
   * Gets the authorized amount.
   * @return the authorized amount
   */
  const ByteArray& authorized_amount() const;

  /**
   * Gets the transaction currency code.
   * @return the transaction currency code
   */
  const ByteArray& transaction_currency_code() const;

  /**
   * Gets the terminal country code.
   * @return the terminal country code
   */
  const ByteArray& terminal_country_code() const;

  /**
   * Gets the transaction type
   * @return the transaction type
   */
  const Byte& transaction_type() const;

  /**
   * Gets the transaction date.
   * @return the transaction date
   */
  const ByteArray& transaction_date() const;

  /**
   * Gets the merchant category code.
   * @return the merchant category code
   */
  const ByteArray& merchant_category_code() const;

  /**
   * Gets the terminal type.
   * @return the terminal type
   */
  const Byte& terminal_type() const;

 private:
  /**
    * The udol.
    */
  ByteArray udol_;

  /**
    * The unpredictable number.
    */
  ByteArray unpredictable_number_;

  /**
   * The mobile support indicator.
   */
  Byte mobile_support_indicator_;

  /**
   * The authorized amount.
   */
  ByteArray authorized_amount_;

  /**
   * The transaction currency code.
   */
  ByteArray transaction_currency_code_;

  /**
   * The terminal country code.
   */
  ByteArray terminal_country_code_;

  /**
   * The transaction type
   */
  Byte transaction_type_;

  /**
   * The transaction date
   */
  ByteArray transaction_date_;

  /**
   * The Merchant Category Code
   */
  ByteArray merchant_category_code_;

  /**
   * The terminal type
   */
  Byte terminal_type_;

  /**
  * Parses the.
  *
  * @param computeCC the compute cc
  */
  void parse(const ByteArray& computeCC);
};

// Inline functions definition

inline const ByteArray& ComputeCCApdu::udol() const {
  return udol_;
}
inline const ByteArray& ComputeCCApdu::unpredictable_number() const {
  return unpredictable_number_;
}
inline const Byte& ComputeCCApdu::mobile_support_indicator() const {
  return mobile_support_indicator_;
}
inline const ByteArray& ComputeCCApdu::authorized_amount() const {
  return authorized_amount_;
}
inline const ByteArray& ComputeCCApdu::transaction_currency_code() const {
  return transaction_currency_code_;
}
inline const ByteArray& ComputeCCApdu::terminal_country_code() const {
  return terminal_country_code_;
}
inline const Byte& ComputeCCApdu::transaction_type() const {
  return transaction_type_;
}
inline const ByteArray& ComputeCCApdu::transaction_date() const {
  return transaction_date_;
}
inline const ByteArray& ComputeCCApdu::merchant_category_code() const {
  return merchant_category_code_;
}
inline const Byte& ComputeCCApdu::terminal_type() const {
  return terminal_type_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_COMPUTE_CC_APDU_H_)  // NOLINT
