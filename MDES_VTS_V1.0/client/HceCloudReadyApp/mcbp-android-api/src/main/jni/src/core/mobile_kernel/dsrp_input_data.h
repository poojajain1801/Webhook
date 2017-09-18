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

#ifndef SRC_CORE_MOBILE_KERNEL_DSRP_INPUT_DATA_H_  // NOLINT
#define SRC_CORE_MOBILE_KERNEL_DSRP_INPUT_DATA_H_  // NOLINT

namespace mcbp_core {

enum class DsrpTransactionType {
  UCAF,
  DE55,
  UNDEFINED
};

/**
 *  \brief     The DSRP Input Data.
 *  \details   Data structure which contains all the relevant data for a DSRP
 *             transaction (either UCAF or DE55) *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct DsrpInputData {
 public:
  int64_t             transaction_amount;
  int64_t             other_amount;
  uint16_t            currency_code;
  Byte                transaction_type;
  uint32_t            unpredictable_number;
  DsrpTransactionType cryptogram_type;
  uint8_t             day;
  uint8_t             month;
  uint16_t            year;
  uint16_t            country_code;

  DsrpInputData();

  /**
   * Denstructor. Securely zeroing data before de-allocating the object.
   */
  ~DsrpInputData();
};


// Inline definitions

inline DsrpInputData::DsrpInputData() :
  transaction_amount(0),
  other_amount(0),
  currency_code(0),
  transaction_type(0x00),
  unpredictable_number(0),
  cryptogram_type(DsrpTransactionType::UNDEFINED),
  day(0x00),
  month(0x00),
  year(0x00),
  country_code(0x00) { }

inline DsrpInputData::~DsrpInputData() {
  transaction_amount   = 0;
  other_amount         = 0;
  currency_code        = 0;
  transaction_type     = 0x00;
  unpredictable_number = 0;
  cryptogram_type      = DsrpTransactionType::UNDEFINED;
  day                  = 0x00;
  month                = 0x00;
  year                 = 0x00;
  country_code         = 0x00;
}

}  // namespace mcbp_core

#endif  // SRC_CORE_MOBILE_KERNEL_DSRP_INPUT_DATA_H_  // NOLINT
