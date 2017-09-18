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

#include <utils/emvco/apdu.h>
#include <core/constants.h>

#ifndef SRC_UTILS_EMVCO_COMMAND_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_COMMAND_APDU_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The C-APDU
 *  \details   The parent class of all the C-APDU
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class CommandApdu : public Apdu {
 public:
  /**
  * Instantiates a new apdu.
  *
  * @param apdu the apdu
  */
  explicit CommandApdu(const ByteArray& apdu);

  /**
  * Gets the CLA C-APDU field
  * @return the CLA C-APDU field
  */
  const Byte& cla() const;

  /**
  * Gets the INS C-APDU field
  * @return the INS C-APDU field
  */
  const Byte& ins() const;

  /**
  * Gets the P1 C-APDU field
  * @return the P1 C-APDU field
  */
  const Byte& p1() const;
  /**
   * Gets the P2 C-APDU field
   * @return the P2 C-APDU field
   */
  const Byte& p2() const;

  /**
  * Gets the LC C-APDU field
  * @return the LC C-APDU field
  */
  Byte lc() const;

  /**
   * Get the length of the C-APDU
   * @return the length of the C-APDU
   */
  unsigned int length() const;

  /**
  * Gets the byte array representing the C-APDU.
  * @return a constant reference to the C-APDU
  */
  const ByteArray& value() const;

 protected:
  // None

 private:
  // None
};

// Inline functions' definition

inline const ByteArray& CommandApdu::value() const {
  return Apdu::value();
}
inline unsigned int CommandApdu::length() const {
  return static_cast<unsigned int>(Apdu::value().size());
}
inline const Byte& CommandApdu::cla() const {
  return Apdu::value()[kCommandApduClaOffset];
}
inline const Byte& CommandApdu::ins() const {
  return Apdu::value()[kCommandApduInsOffset];
}
inline const Byte& CommandApdu::p1() const {
  return Apdu::value()[kCommandApduP1Offset];
}
inline const Byte& CommandApdu::p2() const {
  return Apdu::value()[kCommandApduP2Offset];
}
inline Byte CommandApdu::lc() const {
  return Apdu::value().size() < kCommandApduLcOffset ?
                                        0: Apdu::value()[kCommandApduLcOffset];
}

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_COMMAND_APDU_H_)  // NOLINT
