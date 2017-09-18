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
#include <wrappers/card_profile_data.h>

#ifndef SRC_CORE_MCM_CARD_RISK_MANAGEMENT_DATA_H_  // NOLINT
#define SRC_CORE_MCM_CARD_RISK_MANAGEMENT_DATA_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Card Risk Management data
 *  \details   This object stores the information related to the Card Risk
 *             Management data as defined in the MCBP Perso profiles
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class CardRiskManagementData {
 public:
  /*
   * Default constructor
   */
  CardRiskManagementData() { /* Do nothing */ }

  /**
   * Constructor.
   * The relevant fields for the Card Risk Management data are automatically
   * taken by the constructor implementation.
   * @param card_profile A wrapper for all the card profile fields
   */
  explicit CardRiskManagementData(const CardProfileData& profile);

  /**
   * Get the additional check table data
   */
  const ByteArray& additional_check_table() const;

  /**
   * Get the CRM Country Code
   */
  const ByteArray& crm_country_code() const;

  /**
   * Wipe the content of the data structure
   */
  void wipe();

  /**
   * Default denstructors. It calls the wipe function to zeroes memory.
   */
  ~CardRiskManagementData();

 protected:
  // None

 private:
  ByteArray crm_country_code_;        /* 2  bytes       */
  ByteArray additional_check_table_;  /* 18 bytes [opt] */
};

// Inline functions' definition

inline const ByteArray& CardRiskManagementData::additional_check_table() const {
  return additional_check_table_;
}
inline const ByteArray& CardRiskManagementData::crm_country_code() const {
  return crm_country_code_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CARD_RISK_MANAGEMENT_DATA_H_)  // NOLINT
