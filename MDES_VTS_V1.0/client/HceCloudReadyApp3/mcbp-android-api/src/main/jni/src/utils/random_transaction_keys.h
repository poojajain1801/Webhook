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

#ifndef SRC_UTILS_RANDOM_TRANSACTION_KEYS_H_  // NOLINT
#define SRC_UTILS_RANDOM_TRANSACTION_KEYS_H_  // NOLINT

#include <utils/transaction_keys.h>

namespace mcbp_core {

/**
 * The Class RandomTransactionKeys holds a new set of random keys,
 * to be used when the Mobile PIN wasn't entered.
 * The value of PVS is set to <code>0x00</code> by default and can't be
 * modified. The random keys are created during the object creation.
 */
class RandomTransactionKeys : public TransactionKeys {
 public:
  /**
   * Instantiates a new set of transaction keys with Random keys, to be
   * used when the Mobile PIN wasn't entered.
   * The value of PVS is set to <code>0x00</code> at creation.
   *
   * @param atc the Application Transaction Counter for the
   * transaction - 9F36
   */
  RandomTransactionKeys();

 protected:
  // None

 private:
  // None
};

}  // namespace mcbp_core

#endif  // defined (SRC_UTILS_RANDOM_TRANSACTION_KEYS_H_)  // NOLINT
