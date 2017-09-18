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
#include <utils/byte_array.h>
#include <core/mcm/contactless_payment_data.h>
#include <core/mcm/remote_payment_data.h>
#include <core/mcm/card_risk_management_data.h>

// C++ Libraries
#include <map>

#ifndef SRC_CORE_MCM_CARD_PROFILE_H_  // NOLINT
#define SRC_CORE_MCM_CARD_PROFILE_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The MCM Lite Card Profile
 *  \details   This object stores the MCBP Card Profile. It includes Card
 *             Risk Management data, Contactless Payment data, and Remote
 *             Payment data as defined in MCBP perso profiles.
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class McmCardProfile {
 public:
  /**
   * Create a Card Profile for both Contactless and Remote Payment transactions
   * @param card_risk_management_data  Pointer to the Card Risk Management data
   * @param contactless_payment_data   Pointer to the Contacless Payment data
   * @param remote_payment_data        Pointer to the Remote Payment data
   */
  McmCardProfile(CardRiskManagementData *card_risk_management_data,
                 ContactlessPaymentData *contactless_payment_data,
                 RemotePaymentData      *remote_payment_data);

  /**
   * Get the Card Risk Management Data
   */
  const CardRiskManagementData& card_risk_management_data() const;

  /**
   * Get the Card Risk Management Data
   */
  const ContactlessPaymentData& contactless_payment_data() const;

  /**
   * Get the Card Risk Management Data
   */
  const RemotePaymentData& remote_payment_data() const;


  /**
   * Check if the card support contactless
   * return True if the card support contactless, false otherwise
   */
  bool contactless() const;

  /**
   * Check if the card support remote payment
   * return True if the card support remote payment, false otherwise
   */
  bool remote_payment() const;

  /**
   * Securely erase all the data structures
   */
  void wipe();

  /**
   * Denscructor - Wipe data structure and delete memory allocated via pointers
   */
  ~McmCardProfile();

 protected:
  // None

 private:
  CardRiskManagementData    *card_risk_management_data_;
  ContactlessPaymentData    *contactless_payment_data_;
  RemotePaymentData         *remote_payment_data_;
};

// Inline functions' definition

inline const CardRiskManagementData&
                            McmCardProfile::card_risk_management_data() const {
  return *card_risk_management_data_;
}
inline const ContactlessPaymentData&
                            McmCardProfile::contactless_payment_data() const {
  return *contactless_payment_data_;
}
inline const RemotePaymentData& McmCardProfile::remote_payment_data() const {
  return *remote_payment_data_;
}
inline bool McmCardProfile::contactless() const {
  return contactless_payment_data_ == nullptr ? false : true;
}
inline bool McmCardProfile::remote_payment() const {
  return remote_payment_data_ == nullptr ? false : true;
}

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CARD_PROFILE_H_)  // NOLINT
