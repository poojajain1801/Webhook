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
#include <utils/mcbp_core_exception.h>

#ifndef SRC_CORE_MCM_ALTERNATE_CONTACTLESS_PAYMENT_DATA_H_  // NOLINT
#define SRC_CORE_MCM_ALTERNATE_CONTACTLESS_PAYMENT_DATA_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Alternate Contactless Payment data structure
 *  \details   This object contains information related to the Alternate
 *             Contactless payment data.
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class AlternateContactlessPaymentData {
 public:
  /**
   * Constructor
   * @param aid           The Application ID
   * @param payment_fci   The Payment File Control Information
   * @param gpo_response  The Get Processing Option response
   * @param ciac_decline  The Card Issuer Action Code - Decline
   * @param cvr_mask      The Cardholder Verification Result and Mask
   */
  AlternateContactlessPaymentData(const ByteArray& aid,
                                  const ByteArray& payment_fci,
                                  const ByteArray& gpo_response,
                                  const ByteArray& ciac_decline,
                                  const ByteArray& cvr_mask);

  /*
   * Get the Application ID
   */
  const ByteArray& aid() const NOEXCEPT;

  /*
   * Get the File Control Information template for this payment card
   */
  const ByteArray& payment_fci() const NOEXCEPT;

  /*
   * Get the Get Processing Options response 
   */
  const ByteArray& gpo_response() const NOEXCEPT;

  /*
   * Get the Card Issuer Action Code - Decline
   */
  const ByteArray& ciac_decline() const NOEXCEPT;

  /*
   * Get the Cardholder Verification Results and Mask
   */
  const ByteArray& cvr_mask() const NOEXCEPT;

  /*
   * Wipe the content of the Alternate Contactless Payment Data
   */
  void wipe();

  /**
   * Default denstructors. It calls the wipe function to zeroes memory.
   */
  ~AlternateContactlessPaymentData();

 protected:
  // None

 private:
  ByteArray aid_;                     /* 16 bytes      */
  ByteArray payment_fci_;             /* Max 255 bytes */
  ByteArray gpo_response_;            /* Max 127       */
  ByteArray ciac_decline_;            /* 3 bytes       */
  ByteArray cvr_mask_;                /* 6 bytes       */
};

// Inline functions' definition

inline const ByteArray& AlternateContactlessPaymentData::aid() const NOEXCEPT {
  return aid_;
}
inline
const ByteArray& AlternateContactlessPaymentData::payment_fci() const NOEXCEPT {
  return payment_fci_;
}
inline
const ByteArray& AlternateContactlessPaymentData::gpo_response() const
    NOEXCEPT {
  return gpo_response_;
}
inline
const ByteArray& AlternateContactlessPaymentData::ciac_decline() const
    NOEXCEPT {
  return ciac_decline_;
}
inline
const ByteArray& AlternateContactlessPaymentData::cvr_mask() const NOEXCEPT {
  return cvr_mask_;
}

}  // namespace mcbp_core

#endif // defined(SRC_CORE_MCM_ALTERNATE_CONTACTLESS_PAYMENT_DATA_H_)  // NOLINT
