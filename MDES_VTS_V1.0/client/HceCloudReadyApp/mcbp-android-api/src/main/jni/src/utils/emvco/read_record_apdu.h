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
#include <utils/emvco/command_apdu.h>

#ifndef SRC_UTILS_EMVCO_READ_RECORD_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_READ_RECORD_APDU_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Read Record C-APDU
 *  \details   Read Record C-APDU - Refer to Table 42 MPA Functional Description
 *             1.0
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class ReadRecordApdu : public CommandApdu {
 public:
  /**
   * Instantiates a new read record apdu.
   *
   * @param read_record the read record
   */
  explicit ReadRecordApdu(const ByteArray& read_record);

  /**
   * Gets the record number.
   *
   * @return the record number
   */
  Byte record_number() const NOEXCEPT;

  /**
   * Gets the sfi number.
   *
   * @return the sfi number
   */
  Byte sfi_number() const NOEXCEPT;

 protected:
  // None

 private:
  // None
};

// Inline function definitions
inline Byte ReadRecordApdu::record_number() const NOEXCEPT {
  return CommandApdu::p1();
}
inline Byte ReadRecordApdu::sfi_number() const NOEXCEPT {
  return static_cast<Byte>(CommandApdu::p2() >> 3);
}

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_READ_RECORD_APDU_H_)  // NOLINT
