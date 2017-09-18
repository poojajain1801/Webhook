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
#include <utils/transaction_keys.h>
#include <utils/emvco/response_apdu.h>
#include <utils/byte_array.h>
#include <utils/mcbp_core_exception.h>

#ifndef SRC_CORE_MCM_CONTACTLESS_TRANSACTION_CONTEXT_H_  // NOLINT
#define SRC_CORE_MCM_CONTACTLESS_TRANSACTION_CONTEXT_H_  // NOLINT

namespace mcbp_core {

/**
 * Mcm transaction contexts
 */
enum class ContextType {
  /// M-CHIP first tap
  MCHIP_FIRST_TAP,
  /// M-CHIP transaction completed
  MCHIP_COMPLETED,
  /// Magstripe first tap
  MAGSTRIPE_FIRST_TAP,
  /// Magstripe transaction completed
  MAGSTRIPE_COMPLETED,
  /// Transaction Context Conflict detected
  CONTEXT_CONFLICT,
  /// Unsopported transit
  UNSUPPORTED_TRANSIT,
  /// Magstripe transaction completed (but declined by the MPP Lite)
  MAGSTRIPE_DECLINED,
  /// Magstripe transaction completed (but declined by the MPP Lite)
  MCHIP_DECLINED,
  // Not initialized
  NOT_INITIALIZED
};

/**
 *  \brief     The contactless context
 *  \details   Data structure used by the McmLite to keep track of information
 *             related to the ongoing contactless transaction
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct ContactlessTransactionContext {
 public:
  ByteArray   atc;                     /* 2 bytes     */
  ByteArray   amount;                  /* 6 bytes n12 */
  ByteArray   currency_code;           /* 2 bytes n2  */
  ByteArray   transaction_date;        /* 3 bytes n6  */
  Byte        transaction_type;        /* 1 byte  n2  */
  ByteArray   unpredictable_number;    /* 4 bytes     */
  Byte        cid;                     /* 1 byte      */
  ByteArray   application_cryptogram;  /* 8 bytes     */
  ContextType result;

  /**
   * Default constructor. Reserve memory for each of data element
   */
  ContactlessTransactionContext();
};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CONTACTLESS_TRANSACTION_CONTEXT_H_)  // NOLINT
