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

#include <utils/byte_array.h>
#include <string>
#include <map>

#ifndef SRC_WRAPPERS_CARD_PROFILE_DATA_H_  // NOLINT
#define SRC_WRAPPERS_CARD_PROFILE_DATA_H_  // NOLINT

namespace mcbp_core {

 /**
 *  \brief     Data structure for all the Card Profile attributes.
 *  \details   It is used to easily exchange data between the library and 
 *             platform specific wrappers assuming than in most cases the
 *             library may be compiled with hidden visibility flag on
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct CardProfileData {
 public:
  bool        cl_supported = false;
  bool        rp_supported = false;
  ByteArray   additional_check_table;
  ByteArray   crm_country_code;
  ByteArray   cl_aid;
  ByteArray   cl_ppse_fci;
  ByteArray   cl_payment_fci;
  ByteArray   cl_gpo_response;
  ByteArray   cl_cdol1_related_data_length;
  ByteArray   cl_ciac_decline;
  ByteArray   cl_cvr_mask_and;
  ByteArray   cl_issuer_application_data;
  std::string cl_icc_private_key_a;
  std::string cl_icc_private_key_p;
  std::string cl_icc_private_key_q;
  std::string cl_icc_private_key_dp;
  std::string cl_icc_private_key_dq;
  ByteArray   cl_pin_iv_cvc3_track2;
  ByteArray   cl_ciac_decline_on_ppms;
  ByteArray   alt_aid;
  ByteArray   alt_payment_fci;
  ByteArray   alt_gpo_response;
  ByteArray   alt_ciac_decline;
  ByteArray   alt_cvr_mask_and;
  std::map <uint16_t, ByteArray> records;
  ByteArray   rp_track2_equivalent_data;
  ByteArray   rp_pan;
  ByteArray   rp_pan_sequence_number;
  ByteArray   rp_application_expiry_date;
  ByteArray   rp_aip;
  ByteArray   rp_ciac_decline;
  ByteArray   rp_cvr_mask_and;
  ByteArray   rp_issuer_application_data;
  ByteArray   cvm_reset_timeout;
  ByteArray   dual_tap_reset_timeout;
  ByteArray   security_word;
  ByteArray   card_layout_description;
  ByteArray   application_lifecycle_data;
  ByteArray   mchip_issuer_options;
  ByteArray   magstripe_issuer_options;
  std::string cvm;

  /**
   * Zeroes all the fields
   */
  void wipe();

  /**
   * Securely destroy the object
   */
  ~CardProfileData();
};

// Inline functions definition

inline void CardProfileData::wipe() {
  cl_supported = false;
  rp_supported = false;
  for (std::size_t i = 0; i < additional_check_table.size(); i++)
    additional_check_table[i] = 0x00;
  for (std::size_t i = 0; i < crm_country_code.size(); i++)
    crm_country_code[i] = 0x00;
  for (std::size_t i = 0; i < cl_aid.size(); i++)
    cl_aid[i] = 0x00;
  for (std::size_t i = 0; i < cl_ppse_fci.size(); i++)
    cl_ppse_fci[i] = 0x00;
  for (std::size_t i = 0; i < cl_payment_fci.size(); i++)
    cl_payment_fci[i] = 0x00;
  for (std::size_t i = 0; i < cl_gpo_response.size(); i++)
    cl_gpo_response[i] = 0x00;
  for (std::size_t i = 0; i < cl_cdol1_related_data_length.size(); i++)
    cl_cdol1_related_data_length[i] = 0x00;
  for (std::size_t i = 0; i < cl_ciac_decline.size(); i++)
    cl_ciac_decline[i] = 0x00;
  for (std::size_t i = 0; i < cl_cvr_mask_and.size(); i++)
    cl_cvr_mask_and[i] = 0x00;
  for (std::size_t i = 0; i < cl_issuer_application_data.size(); i++)
    cl_issuer_application_data[i] = 0x00;
  for (std::size_t i = 0; i < cl_icc_private_key_a.size(); i++)
    cl_icc_private_key_a[i] = 0x00;
  for (std::size_t i = 0; i < cl_icc_private_key_p.size(); i++)
    cl_icc_private_key_p[i] = 0x00;
  for (std::size_t i = 0; i < cl_icc_private_key_q.size(); i++)
    cl_icc_private_key_q[i] = 0x00;
  for (std::size_t i = 0; i < cl_icc_private_key_dp.size(); i++)
    cl_icc_private_key_dp[i] = 0x00;
  for (std::size_t i = 0; i < cl_icc_private_key_dq.size(); i++)
    cl_icc_private_key_dq[i] = 0x00;
  for (std::size_t i = 0; i < cl_pin_iv_cvc3_track2.size(); i++)
    cl_pin_iv_cvc3_track2[i] = 0x00;
  for (std::size_t i = 0; i < cl_ciac_decline_on_ppms.size(); i++)
    cl_ciac_decline_on_ppms[i] = 0x00;
  for (std::size_t i = 0; i < alt_aid.size(); i++)
    alt_aid[i] = 0x00;
  for (std::size_t i = 0; i < alt_payment_fci.size(); i++)
    alt_payment_fci[i] = 0x00;
  for (std::size_t i = 0; i < alt_gpo_response.size(); i++)
    alt_gpo_response[i] = 0x00;
  for (std::size_t i = 0; i < alt_ciac_decline.size(); i++)
    alt_ciac_decline[i] = 0x00;
  for (std::size_t i = 0; i < alt_cvr_mask_and.size(); i++)
    alt_cvr_mask_and[i] = 0x00;
  for (std::size_t i = 0; i < rp_track2_equivalent_data.size(); i++)
    rp_track2_equivalent_data[i] = 0x00;
  for (std::size_t i = 0; i < rp_pan.size(); i++)
    rp_pan[i] = 0x00;
  for (std::size_t i = 0; i < rp_pan_sequence_number.size(); i++)
    rp_pan_sequence_number[i] = 0x00;
  for (std::size_t i = 0; i < rp_application_expiry_date.size(); i++)
    rp_application_expiry_date[i] = 0x00;
  for (std::size_t i = 0; i < rp_aip.size(); i++)
    rp_aip[i] = 0x00;
  for (std::size_t i = 0; i < rp_ciac_decline.size(); i++)
    rp_ciac_decline[i] = 0x00;
  for (std::size_t i = 0; i < rp_cvr_mask_and.size(); i++)
    rp_cvr_mask_and[i] = 0x00;
  for (std::size_t i = 0; i < rp_issuer_application_data.size(); i++)
    rp_issuer_application_data[i] = 0x00;
  for (std::size_t i = 0; i < cvm_reset_timeout.size(); i++)
    cvm_reset_timeout[i] = 0x00;
  for (std::size_t i = 0; i < dual_tap_reset_timeout.size(); i++)
    dual_tap_reset_timeout[i] = 0x00;
  for (std::size_t i = 0; i < security_word.size(); i++)
    security_word[i] = 0x00;
  for (std::size_t i = 0; i < card_layout_description.size(); i++)
    card_layout_description[i] = 0x00;
  for (std::size_t i = 0; i < application_lifecycle_data.size(); i++)
    application_lifecycle_data[i] = 0x00;
  for (std::size_t i = 0; i < mchip_issuer_options.size(); i++)
    mchip_issuer_options[i] = 0x00;
  for (std::size_t i = 0; i < magstripe_issuer_options.size(); i++)
    magstripe_issuer_options[i] = 0x00;
  for (std::size_t i = 0; i < cvm.size(); i++)
    cvm[i] = 0x00;

  typedef std::map <uint16_t, ByteArray>::iterator It;
  for (It it = records.begin(); it != records.end(); ++it)
    for (std::size_t i = 0; i < it->second.size(); i++) it->second[i] = 0x00;
}

inline CardProfileData::~CardProfileData() {
  wipe();
  additional_check_table.clear();
  crm_country_code.clear();
  cl_aid.clear();
  cl_ppse_fci.clear();
  cl_payment_fci.clear();
  cl_gpo_response.clear();
  cl_cdol1_related_data_length.clear();
  cl_ciac_decline.clear();
  cl_cvr_mask_and.clear();
  cl_issuer_application_data.clear();
  cl_icc_private_key_a.clear();
  cl_icc_private_key_p.clear();
  cl_icc_private_key_q.clear();
  cl_icc_private_key_dp.clear();
  cl_icc_private_key_dq.clear();
  cl_pin_iv_cvc3_track2.clear();
  cl_ciac_decline_on_ppms.clear();
  alt_aid.clear();
  alt_payment_fci.clear();
  alt_gpo_response.clear();
  alt_ciac_decline.clear();
  alt_cvr_mask_and.clear();
  records.clear();
  rp_track2_equivalent_data.clear();
  rp_pan.clear();
  rp_pan_sequence_number.clear();
  rp_application_expiry_date.clear();
  rp_aip.clear();
  rp_ciac_decline.clear();
  rp_cvr_mask_and.clear();
  rp_issuer_application_data.clear();
  cvm_reset_timeout.clear();
  dual_tap_reset_timeout.clear();
  security_word.clear();
  card_layout_description.clear();
  application_lifecycle_data.clear();
  mchip_issuer_options.clear();
  magstripe_issuer_options.clear();
  cvm.clear();
}

}  // namespace mcbp_core

#endif  // defined(SRC_WRAPPERS_CARD_PROFILE_DATA_H_)  // NOLINT
