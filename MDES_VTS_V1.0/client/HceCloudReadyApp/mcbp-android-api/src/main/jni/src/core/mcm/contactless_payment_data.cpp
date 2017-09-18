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
#include <core/mcm/contactless_payment_data.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>
#include <core/constants.h>

// C++ Libraries
#include <map>
#include <string>

namespace mcbp_core {

ContactlessPaymentData::ContactlessPaymentData(
    const CardProfileData& card) :
      aid_(card.cl_aid),
      ppse_fci_(card.cl_ppse_fci),
      payment_fci_(card.cl_payment_fci),
      gpo_response_(card.cl_gpo_response),
      cdol1_related_data_length_(card.cl_cdol1_related_data_length[0]),
      ciac_decline_(card.cl_ciac_decline),
      cvr_mask_(card.cl_cvr_mask_and),
      issuer_application_data_(card.cl_issuer_application_data),
      pin_iv_cvc3_track2_(card.cl_pin_iv_cvc3_track2),
      ciac_decline_on_ppms_(card.cl_ciac_decline_on_ppms),
      transit_(false),
      records_(card.records) {
  // Check that the input data is of the right size
  if (aid_.size() != 7 )
    throw InvalidInput("AID");
  if (ppse_fci_.empty() || ppse_fci().size() > 255)
    throw InvalidInput("PPSE FCI");
  if (payment_fci_.empty() || payment_fci().size() > 255)
    throw InvalidInput("Payment FCI");
  if (gpo_response_.empty() || gpo_response().size() > 127)
    throw InvalidInput("GPO Response");
  if (cdol1_related_data_length_ != 0x00 && cdol1_related_data_length_ < 45)
    throw InvalidInput("CDOL1 Length");
  if (ciac_decline_.size() != 3 && !ciac_decline_.empty())
    throw InvalidInput("CIAC Decline");
  if (cvr_mask_.size() != 6 && !cvr_mask_.empty())
    throw InvalidInput("CVR Mask");
  if (pin_iv_cvc3_track2_.size() != 2 && !pin_iv_cvc3_track2_.empty())
    throw InvalidInput("PIN IV CVC3 Track2");
  if (issuer_application_data_.size() != 18 &&
      !issuer_application_data_.empty())
    throw InvalidInput("Issuer Application Data");
  if (ciac_decline_on_ppms_.size() != 2 && !ciac_decline_on_ppms_.empty())
    throw InvalidInput("CIAC Decline on PPMS");

  // Set the private key (if data is not empty)
  if (!card.cl_icc_private_key_p.empty()                                   &&
       card.cl_icc_private_key_p.size()  <= 2 * kMaxRsaKeyParameterLength  &&
      !card.cl_icc_private_key_q.empty()                                   &&
       card.cl_icc_private_key_q.size()  <= 2 * kMaxRsaKeyParameterLength  &&
      !card.cl_icc_private_key_dp.empty()                                  &&
       card.cl_icc_private_key_dp.size() <= 2 * kMaxRsaKeyParameterLength  &&
      !card.cl_icc_private_key_dq.empty()                                  &&
       card.cl_icc_private_key_dq.size() <= 2 * kMaxRsaKeyParameterLength  &&
      !card.cl_icc_private_key_a.empty()                                   &&
       card.cl_icc_private_key_a.size()  <= 2 * kMaxRsaKeyParameterLength) {
    // Init the RSA certification
    // For performances reason the validation is not done (e.g. 0 level)
    // It is assumed that the RSA data is received correctly from the
    // provisioning system
    rsa_certificate_.init_private_key(card.cl_icc_private_key_p,
                                      card.cl_icc_private_key_q,
                                      card.cl_icc_private_key_dp,
                                      card.cl_icc_private_key_dq,
                                      card.cl_icc_private_key_a, 0);
  }

  alt_contactless_payment_data_ = nullptr;

  // Check whether Alternate Contactless data is present
  if (card.alt_aid.empty()) return;

  try {
    alt_contactless_payment_data_ =
      new AlternateContactlessPaymentData(card.alt_aid, card.alt_payment_fci,
                                          card.alt_gpo_response,
                                          card.alt_ciac_decline,
                                          card.alt_cvr_mask_and);
  }
  catch (const InvalidInput& e) {
    // Parameters for contactless alternate profile are wrong
    alt_contactless_payment_data_ = nullptr;
    throw;
  }
}

const AlternateContactlessPaymentData&
    ContactlessPaymentData::alternate() const {
  if (alt_contactless_payment_data_ == nullptr)
    throw Exception("Alternate Contacless does not exist - Have you checked?");
  return *alt_contactless_payment_data_;
}

void ContactlessPaymentData::wipe() {
  if (alt_contactless_payment_data_ != nullptr) {
    delete alt_contactless_payment_data_;
    alt_contactless_payment_data_ = nullptr;
  }
  Wipe(&aid_);
  Wipe(&ppse_fci_);
  Wipe(&payment_fci_);
  Wipe(&gpo_response_);
  cdol1_related_data_length_ = 0x00;
  Wipe(&ciac_decline_);
  Wipe(&cvr_mask_);
  Wipe(&issuer_application_data_);
  Wipe(&pin_iv_cvc3_track2_);
  Wipe(&ciac_decline_on_ppms_);
  transit_ = false;
}

ContactlessPaymentData::~ContactlessPaymentData() {
  wipe();
}

}  // namespace mcbp_core
