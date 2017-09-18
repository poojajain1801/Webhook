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
#include <utils/rsa_certificate.h>
#include <wrappers/card_profile_data.h>
#include <core/mcm/alternate_contactless_payment_data.h>

// C++ Libraries
#include <string>
#include <map>

#ifndef SRC_CORE_MCM_CONTACTLESS_PAYMENT_DATA_H_  // NOLINT
#define SRC_CORE_MCM_CONTACTLESS_PAYMENT_DATA_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Contactless Payment data
 *  \details   This object stores the information related to the Contactless
 *             Payment data as defined in the MCBP Perso profiles
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class ContactlessPaymentData {
 public:
  /**
   * Constructor.
   * The relevant fields for the Contactless profile are automatically
   * taken by the constructor implementation.
   * @param card_profile A wrapper for all the card profile fields
   * @param alt Pointer to the alternate contactless profile (if not specified,
   *            it is assumed to be nullptr 
   */
  explicit ContactlessPaymentData(const CardProfileData& card_profile);

  /*
   * Get the Application ID
   */
  const ByteArray& aid() const;

  /*
   * Get the File Control Information template for the PPSE
   */
  const ByteArray& ppse_fci() const;

  /*
   * Get the File Control Information template for this payment card
   */
  const ByteArray& payment_fci() const;

  /*
   * Get the Get Processing Options response 
   */
  const ByteArray& gpo_response() const;

  /*
   * Get the length of the CDOL 1 related data
   */
  const Byte& cdol1_related_data_length() const;

  /*
   * Get the Card Issuer Action Code - Decline
   */
  const ByteArray& ciac_decline() const;

  /*
   * Get the Cardholder Verification Results and Mask
   */
  const ByteArray& cvr_mask() const;

  /*
   * Get the Issuer Application Data
   */
  const ByteArray& issuer_application_data() const;

  /*
   * Get the PIN IV CVC3 Track2
   */
  const ByteArray& pin_iv_cvc3_track2() const;

  /*
   * Get the Card Issuer Action Code - Decline on PPMS (Magstripe mode)
   */
  const ByteArray& ciac_decline_on_ppms() const;

  /**
   * Check whether the card support transit (Experimental - Not in MCBP 1.0)
   */
  bool transit() const;

  /*
   * Get the Card Records
   * Each record is stored using a key which is sfi value | record number
   * For example, Record 2 3 will be identified by the key 0010 0011
   * @return a constant reference to the records data structure
   */
  const std::map <uint16_t, ByteArray>& records() const;


  /**
   * Check whether an alternate profile is available
   * @return True if an alternate profile has been provided, false otherwise
   */
  bool alternate_profile() const;

  /**
   * Get the alternate profile. It raises an exception if the profile does not
   * exist. This function should be called only if the alternate_profile()
   * returns true
   */
  const AlternateContactlessPaymentData& alternate() const;

  /**
   * Get the RSA Certificate
   * @return the RSA certificate
   */
  const RsaCertificate& rsa_certificate() const;

  /*
   * Wipe the content of the Contactless Payment Data
   */
  void wipe();

  /**
   * Default denstructors. It calls the wipe function to zeroes memory.
   */
  ~ContactlessPaymentData();

 protected:
  // None

 private:
  ByteArray aid_;                       /* 16 bytes                    */
  ByteArray ppse_fci_;                  /* max 255 bytes               */
  ByteArray payment_fci_;               /* max 255 bytes               */
  ByteArray gpo_response_;              /* max 127 bytes               */
  Byte      cdol1_related_data_length_; /*  1 byte  [cond, MC]         */
  ByteArray ciac_decline_;              /*  3 bytes [cond, MC]         */
  ByteArray cvr_mask_;                  /*  6 bytes [cond, MC]         */
  ByteArray issuer_application_data_;   /* 18 bytes [cond, MC]         */
  ByteArray pin_iv_cvc3_track2_;        /*  2 bytes [cond, MS]         */
  ByteArray ciac_decline_on_ppms_;      /*  2 bytes [cond, MS]         */

  // True if transit is supported
  bool      transit_;                   /*  1 byte  [Experimental]     */

  // Each record is stored using a key which is sfi value | record number
  std::map <uint16_t, ByteArray> records_;

  RsaCertificate rsa_certificate_;

  const AlternateContactlessPaymentData *alt_contactless_payment_data_;

  // Copy constructor is not available
  ContactlessPaymentData(const ContactlessPaymentData& data);
};

// Inline functions' definition

inline const ByteArray& ContactlessPaymentData::aid() const {
  return aid_;
}
inline const ByteArray& ContactlessPaymentData::ppse_fci() const {
  return ppse_fci_;
}
inline const ByteArray& ContactlessPaymentData::payment_fci() const {
  return payment_fci_;
}
inline const ByteArray& ContactlessPaymentData::gpo_response() const {
  return gpo_response_;
}
inline const Byte& ContactlessPaymentData::cdol1_related_data_length() const {
  return cdol1_related_data_length_;
}
inline const ByteArray& ContactlessPaymentData::ciac_decline() const {
  return ciac_decline_;
}
inline const ByteArray& ContactlessPaymentData::cvr_mask() const {
  return cvr_mask_;
}
inline
const ByteArray& ContactlessPaymentData::issuer_application_data() const {
  return issuer_application_data_;
}
inline const ByteArray& ContactlessPaymentData::pin_iv_cvc3_track2() const {
  return pin_iv_cvc3_track2_;
}
inline const ByteArray& ContactlessPaymentData::ciac_decline_on_ppms() const {
  return ciac_decline_on_ppms_;
}
inline bool ContactlessPaymentData::transit() const {
  return transit_;
}
inline bool ContactlessPaymentData::alternate_profile() const {
  return alt_contactless_payment_data_ != nullptr ? true: false;
}
inline const std::map <uint16_t, ByteArray>&
                                      ContactlessPaymentData::records() const {
  return records_;
}
inline const RsaCertificate& ContactlessPaymentData::rsa_certificate() const {
  return rsa_certificate_;
}


}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CONTACTLESS_PAYMENT_DATA_H_)  // NOLINT
