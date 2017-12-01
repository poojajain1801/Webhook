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

// Project includes
#include <core/mcm/card_risk_management_data.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>

namespace mcbp_core {

CardRiskManagementData::CardRiskManagementData(const CardProfileData& card) :
      crm_country_code_(card.crm_country_code),
      additional_check_table_(card.additional_check_table) {
  if (!additional_check_table_.empty() && additional_check_table_.size() != 18)
    throw InvalidInput("Additional Check Table");
  if (crm_country_code_.size() != 2) throw InvalidInput("CRM Country Code");
}

void CardRiskManagementData::wipe() {
  Wipe(&crm_country_code_);
  Wipe(&additional_check_table_);
}

CardRiskManagementData::~CardRiskManagementData() {
  wipe();
}

}  // namespace mcbp_core
