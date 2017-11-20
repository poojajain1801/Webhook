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

#ifndef SRC_UTILS_EMVCO_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_APDU_H_  // NOLINT

#include <utils/byte_array.h>
#include <utils/mcbp_core_exception.h>

namespace mcbp_core {

/**
 *  \brief     The Parent class of all APDUs
 *  \details   It provides generic functions that are used by all the derived
 *             classes (e.g. value())
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class Apdu {
 public:
  /**
   * Instantiates a new empty apdu.
   */
  Apdu() { }

  /**
   * Instantiates a new apdu.
   *
   * @param apdu the apdu
   */
  explicit Apdu(const ByteArray& apdu) : value_(apdu) { }

  /**
   * Get the R-APDU message (all bytes).
   *
   * @return the entire R-APDU message
   */
  const ByteArray& value() const NOEXCEPT;

  /**
   * Set the value of the APDU.
   *
   * @param value the value of the APDU
   */
  void set_value(const ByteArray& value) NOEXCEPT;

  /**
   * Denstructor. Securily wipe the content of the APDU
   */
  ~Apdu();

 protected:
  // Nothing

 private:
  ByteArray value_;
};

// Inline functions' definition
inline void Apdu::set_value(const ByteArray& value) NOEXCEPT { value_ = value; }

inline const ByteArray& Apdu::value() const NOEXCEPT { return value_; }

}  // namespace mcbp_core

#endif // defined(SRC_UTILS_EMVCO_APDU_H_)  // NOLINT
