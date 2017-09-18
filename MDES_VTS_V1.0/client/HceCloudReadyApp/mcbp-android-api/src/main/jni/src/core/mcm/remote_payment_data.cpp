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
#include <core/mcm/remote_payment_data.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>

namespace mcbp_core {

RemotePaymentData::RemotePaymentData(const CardProfileData& card) :
      track2_equivalent_data_(card.rp_track2_equivalent_data),
      pan_(card.rp_pan),
      pan_sequence_number_((card.rp_pan_sequence_number.empty() ?
                              0x00: card.rp_pan_sequence_number[0])),
      application_expiry_date_(card.rp_application_expiry_date),
      aip_(card.rp_aip),
      ciac_decline_(card.rp_ciac_decline),
      cvr_mask_(card.rp_cvr_mask_and),
      issuer_application_data_(card.rp_issuer_application_data) {
  // Check that the input data is of the right size
  if (track2_equivalent_data_.empty() || track2_equivalent_data_.size() > 19)
    throw InvalidInput("Track2 Equivalent Data");
  if (pan_.empty() || pan_.size() > 9)
    throw InvalidInput("PAN");
  if (application_expiry_date_.size() != 3)
    throw InvalidInput("Application Expiry Date");
  if (aip_.size() != 2)
    throw InvalidInput("AIP");
  if (ciac_decline_.size() != 3)
    throw InvalidInput("CIAC Decline");
  if (cvr_mask_.size() != 6)
    throw InvalidInput("CVR Mask");
  if (issuer_application_data_.size() != 18)
    throw InvalidInput("Issuer Application Data");
}

void RemotePaymentData::wipe() {
  Wipe(&track2_equivalent_data_);
  Wipe(&pan_);
  pan_sequence_number_ = 0x00;
  Wipe(&application_expiry_date_);
  Wipe(&aip_);
  Wipe(&ciac_decline_);
  Wipe(&cvr_mask_);
  Wipe(&issuer_application_data_);
}

RemotePaymentData::~RemotePaymentData() {
  wipe();
}

}  // namespace mcbp_core
