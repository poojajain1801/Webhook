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

// Project libraries
#include <utils/byte_array.h>

#ifndef SRC_CORE_MCM_CRYPTOGRAM_INPUT_H_  // NOLINT
#define SRC_CORE_MCM_CRYPTOGRAM_INPUT_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     Cryptogram Input
 *  \details   This object contains the data elements that McmLite needs to
 *             compute a Remote Payment transaction cryptogram
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct CryptogramInput {
 public:
  ByteArray amount_authorized;               /*  6 bytes     */
  ByteArray amount_other;                    /*  6 bytes     */
  ByteArray terminal_country_code;           /*  2 bytes     */
  ByteArray tvr;                             /*  5 bytes     */
  ByteArray transaction_currency_code;       /*  2 bytes     */
  ByteArray transaction_date;                /*  3 bytes     */
  Byte      transaction_type;                /*  1 byte      */
  ByteArray unpredictable_number;            /*  4 bytes     */

  bool online_allowed;

  CryptogramInput();
};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CRYPTOGRAM_INPUT_H_)  // NOLINT
