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

#ifndef SRC_UTILS_EMVCO_GPO_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_GPO_APDU_H_  // NOLINT

#include <utils/emvco/command_apdu.h>

namespace mcbp_core {

/**
 *  \brief     The Get Processing Options C-APDU
 *  \details   Get Processing Options C-APDU - Refer to Table 40 MPA Functional
 *             Description 1.0
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class GpoApdu : public CommandApdu {
 public:
  /**
   * Instantiates a new gets the data apdu.
   * @param GPO the gpo
   */
  explicit GpoApdu(const ByteArray& c_apdu);

  /**
   * Get the PDOL Related data
   * @return The PDOL Related data
   */
  const ByteArray& pdol_values() const NOEXCEPT;

   /**
   * Terminal type.
   * @return the Terminal Type
   */
  Byte terminal_type() const NOEXCEPT;

  /**
   * The Command Template tag
   * @return The command template tag
   */
  const Byte command_template() const NOEXCEPT;

  /**
   * The Command Template data length
   * @return The command template data length
   */
  const Byte command_template_length() const NOEXCEPT;

  /**
   * Get the Risk Management Data
   * @return A constant reference to the Risk Management Data
   */
  const ByteArray& risk_management_data() const;

  /**
   * Get the Terminal Country Code
   * @return A constant reference to the Terminal Country Code
   */
  const ByteArray& terminal_country_code() const;

 protected:
  // None

 private:
  // Terminal type
  Byte terminal_type_;

  // Risk Management Data
  ByteArray risk_management_data_;

  // Terminal Country Code
  ByteArray terminal_country_code_;

  // The pdol_related_data
  ByteArray pdol_related_data_;

  // The command template
  Byte command_template_;

  // The command template data length
  Byte command_template_length_;
};

// Inline definitions

inline Byte GpoApdu::terminal_type() const NOEXCEPT {
  return terminal_type_;
}
inline const ByteArray& GpoApdu::pdol_values() const NOEXCEPT {
  return pdol_related_data_;
}
inline const Byte GpoApdu::command_template() const NOEXCEPT {
  return command_template_;
}
inline const Byte GpoApdu::command_template_length() const NOEXCEPT {
  return command_template_length_;
}
}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_GPO_APDU_H_)  // NOLINT
