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
#include <core/mcm/mcm_card_profile.h>

namespace mcbp_core {

McmCardProfile::McmCardProfile(
    CardRiskManagementData *card_risk_management_data,
    ContactlessPaymentData *contactless_payment_data,
    RemotePaymentData      *remote_payment_data):
      card_risk_management_data_(card_risk_management_data),
      contactless_payment_data_(contactless_payment_data),
      remote_payment_data_(remote_payment_data) { }

void McmCardProfile::wipe() {
  if (card_risk_management_data_ != nullptr) {
    card_risk_management_data_->wipe();
    delete card_risk_management_data_;
    card_risk_management_data_ = nullptr;
  }
  if (contactless_payment_data_ != nullptr) {
    contactless_payment_data_->wipe();
    delete contactless_payment_data_;
    contactless_payment_data_ = nullptr;
  }
  if (remote_payment_data_ != nullptr) {
    remote_payment_data_->wipe();
    delete remote_payment_data_;
    remote_payment_data_ = nullptr;
  }
}

McmCardProfile::~McmCardProfile() {
  wipe();
}

}  // namespace mcbp_core
