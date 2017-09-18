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

#include <utils/byte_array.h>
#include <utils/cardholder_validator.h>

#ifndef SRC_UTILS_PIN_VALIDATOR_H_  // NOLINT
#define SRC_UTILS_PIN_VALIDATOR_H_  // NOLINT

namespace mcbp_core {

/**
 *
 */
/**
 *  \brief     PIN Cardholder Validator class
 *  \details   Unlock UMD keys using the PIN value
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class PinValidator: public CardholderValidator {
 public:
  /**
   * Create an instance of the PinValidator. Once used the PIN is wiped.
   * @input pin The PIN
   */
  explicit PinValidator(ByteArray* pin);

  /**
   * Unlock the UMD key using the PIN (UMD -> SUK)
   * @param The UMD key to be unlocked
   */
  virtual void unlock_umd_key(ByteArray* keys);

  /**
   * Wipe the PIN
   */
  virtual void wipe();

 protected:
  // None

 private:
  ByteArray* pin_;
};

// Inline definitions

inline PinValidator::PinValidator(ByteArray* pin): pin_(pin) { }

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_CARDHOLDER_VALIDATOR_H_)  // NOLINT
