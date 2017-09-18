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
#include <core/mobile_kernel/dsrp_input_data.h>
#include <utils/byte_array.h>

// C++ libraries
#include <string>

#ifndef SRC_CORE_MOBILE_KERNEL_DSRP_OUTPUT_DATA_H_  // NOLINT
#define SRC_CORE_MOBILE_KERNEL_DSRP_OUTPUT_DATA_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The DSRP Output data.
 *  \details   This object contains the complete outcome of a DSRP transaction
 *             that is needed by the caller function to generate either UCAF or
 *             DE55 messages as well as the authorization message.
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct DsrpOutputData {
 public:
  std::string     pan;
  std::string     track_2_data;
  int32_t         pan_sequence_number;
  ByteArray       expiry_date;
  ByteArray       transaction_cryptogram_data;
  ByteArray       cryptogram;
  int32_t         ucaf_version;
  int64_t         transaction_amount;
  uint16_t        currency_code;
  uint16_t        atc;
  uint32_t        unpredictable_number;
  DsrpTransactionType cryptogram_type;

  DsrpOutputData();

  /**
   * Denstructor. Securely zeroing data before de-allocating the object.
   */
  ~DsrpOutputData();
};

// Inline definitions
inline DsrpOutputData::DsrpOutputData() :
  pan(""),
  track_2_data(""),
  pan_sequence_number(0),
  expiry_date(3, 0x00),
  transaction_cryptogram_data(),
  cryptogram(),
  ucaf_version(0),
  transaction_amount(0),
  currency_code(0),
  atc(0),
  unpredictable_number(0),
  cryptogram_type(DsrpTransactionType::UNDEFINED) {
}

inline DsrpOutputData::~DsrpOutputData() {
  for (std::size_t i = 0; i < pan.size(); i++) pan[i] = 0x00;
  for (std::size_t i = 0; i < track_2_data.size(); i++) track_2_data[i] = 0x00;
  for (std::size_t i = 0; i < expiry_date.size(); i++) expiry_date[i] = 0x00;
  for (std::size_t i = 0; i < transaction_cryptogram_data.size(); i++)
    transaction_cryptogram_data[i] = 0x00;
  for (std::size_t i = 0; i < cryptogram.size(); i++)
    cryptogram[i] = 0x00;

  pan_sequence_number  = 0;
  ucaf_version         = 0;
  transaction_amount   = 0;
  currency_code        = 0;
  atc                  = 0;
  unpredictable_number = 0;
  cryptogram_type      = DsrpTransactionType::UNDEFINED;
}

}  // namespace mcbp_core

#endif  // SRC_CORE_MOBILE_KERNEL_DSRP_OUTPUT_DATA_H_  // NOLINT
