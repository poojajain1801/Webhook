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

#ifndef SRC_UTILS_CARDHOLDER_VALIDATOR_H_  // NOLINT
#define SRC_UTILS_CARDHOLDER_VALIDATOR_H_  // NOLINT

namespace mcbp_core {

/**
 * Generic base class for Cardholder Validator objects
 */
class CardholderValidator {
 public:
  /**
   * The CardholderValidator shall provide a function to unlock the UMD key
   */
  virtual void unlock_umd_key(ByteArray* umd_key) = 0;

  /**
   * Wipe the content of the validator
   */
  virtual void wipe() = 0;
};

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_CARDHOLDER_VALIDATOR_H_)  // NOLINT
