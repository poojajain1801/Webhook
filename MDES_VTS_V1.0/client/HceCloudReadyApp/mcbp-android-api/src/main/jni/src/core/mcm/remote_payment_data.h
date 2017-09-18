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

#ifndef SRC_CORE_MCM_REMOTE_PAYMENT_DATA_H_  // NOLINT
#define SRC_CORE_MCM_REMOTE_PAYMENT_DATA_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Remote Payment data
 *  \details   This object stores the information related to the Remote
 *             Payment data as defined in the MCBP Perso profiles
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct RemotePaymentData {
 public:
  /**
   * Default constructor.
   */
  RemotePaymentData() { /* Do Nothing */ }

  /**
   * Constructor.
   * The relevant fields for the Remote Payment profile are automatically
   * taken by the constructor implementation.
   * @param card_profile A wrapper for all the card profile fields
   */
  explicit RemotePaymentData(const CardProfileData& card_profile);

  /**
   * Get the Track2 Equivalent Data
   */
  const ByteArray& track2_equivalent_data() const;

  /**
   * Get the Primary Account Number (PAN)
   */
  const ByteArray& pan() const;

  /**
   * Get the PAN Sequence Number
   */
  const Byte& pan_sequence_number() const;

  /**
   * Get the Application Expiry Date
   */
  const ByteArray& application_expiry_date() const;

  /**
   * Get the Application Interchange Profile
   */
  const ByteArray& aip() const;

  /**
   * Get the Card Issuer Action Code - Decline
   */
  const ByteArray& ciac_decline() const;

  /**
   * Get the Cardholder Verification Results and Mask
   */
  const ByteArray& cvr_mask() const;

  /**
   * Get the Issuer Application Data
   */
  const ByteArray& issuer_application_data() const;

  /*
   * Wipe the content of the Remote Payment Data
   */
  void wipe();

  /**
   * Default denstructors. It calls the wipe function to zeroes memory.
   */
  ~RemotePaymentData();

 protected:
  // None

 private:
  ByteArray track2_equivalent_data_;   /* max 19 cn    */
  ByteArray pan_;                      /* max 10 cn    */
  Byte      pan_sequence_number_;      /*  1 byte  n2  */
  ByteArray application_expiry_date_;  /*  3 bytes n6  */
  ByteArray aip_;                      /*  2 bytes     */
  ByteArray ciac_decline_;             /*  3 bytes     */
  ByteArray cvr_mask_;                 /*  6 bytes     */
  ByteArray issuer_application_data_;  /* 18 bytes     */
};

// Inline functions' definition

inline const ByteArray& RemotePaymentData::track2_equivalent_data() const {
  return track2_equivalent_data_;
}
inline const ByteArray& RemotePaymentData::pan() const {
  return pan_;
}
inline const Byte& RemotePaymentData::pan_sequence_number() const {
  return pan_sequence_number_;
}
inline const ByteArray& RemotePaymentData::application_expiry_date() const {
  return application_expiry_date_;
}
inline const ByteArray& RemotePaymentData::aip() const {
  return aip_;
}
inline const ByteArray& RemotePaymentData::ciac_decline() const {
  return ciac_decline_;
}
inline const ByteArray& RemotePaymentData::cvr_mask() const {
  return cvr_mask_;
}
inline const ByteArray& RemotePaymentData::issuer_application_data() const {
  return issuer_application_data_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_REMOTE_PAYMENT_DATA_H_)  // NOLINT
