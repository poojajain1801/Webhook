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
#include <core/mcm/alternate_contactless_payment_data.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>

namespace mcbp_core {

AlternateContactlessPaymentData::AlternateContactlessPaymentData(
    const ByteArray& aid,
    const ByteArray& payment_fci,
    const ByteArray& gpo_response,
    const ByteArray& ciac_decline,
    const ByteArray& cvr_mask):
      aid_(aid),
      payment_fci_(payment_fci),
      gpo_response_(gpo_response),
      ciac_decline_(ciac_decline),
      cvr_mask_(cvr_mask) {
  if (aid_.empty() || aid_.size() > 16)
    throw InvalidInput("Alternate AID");
  if (payment_fci_.empty() || payment_fci_.size() > 255)
    throw InvalidInput("Alternate Payment FCI");
  if (gpo_response_.empty() || gpo_response_.size() > 127)
    throw InvalidInput("Alternate GPO Response");
  if (ciac_decline_.size() != 3)
    throw InvalidInput("Alternate CIAC Decline");
  if (cvr_mask_.size() != 6)
    throw InvalidInput("Alternate CVR Mask");
}

void AlternateContactlessPaymentData::wipe() {
  Wipe(&aid_);
  Wipe(&payment_fci_);
  Wipe(&gpo_response_);
  Wipe(&ciac_decline_);
  Wipe(&cvr_mask_);
}

AlternateContactlessPaymentData::~AlternateContactlessPaymentData() {
  wipe();
}

}  // namespace mcbp_core
