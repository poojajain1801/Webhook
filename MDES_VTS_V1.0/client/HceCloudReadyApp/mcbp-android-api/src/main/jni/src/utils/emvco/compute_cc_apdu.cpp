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
#include <utils/emvco/compute_cc_apdu.h>
#include <utils/mcbp_core_exception.h>
#include <core/constants.h>

namespace mcbp_core {

ComputeCCApdu::ComputeCCApdu(const ByteArray& c_apdu) : CommandApdu(c_apdu) {
  // Check the C-APDU is correct
  if (kClaComputeCC != cla())
    throw InvalidCla("Invalid Cla in the ComputeCC C-APDU");
  if (kInsComputeCC != ins())
    throw InvalidIns("Invalid Ins in the ComputeCC C-APDU");
  if (kP1ComputeCC != p1())
    throw InvalidP1("Invalid P1 in the ComputeCC C-APDU");
  if (kP2ComputeCC != p2())
    throw InvalidP2("Invalid P2 in the ComputeCC C-APDU");
  if (kLcComputeCC  != lc())
    throw InvalidLc("Invalid Lc in Compute CC C-APDU");
  if (c_apdu.size() != lc() + 6 || lc() != kUdol1Lenght)
    throw InvalidInput("Invalid Compute CC C-APDU length");

  parse(c_apdu);
}

void ComputeCCApdu::parse(const ByteArray& c_apdu) {
  udol_                      = {c_apdu.begin() + 5, c_apdu.begin() + 27};
  unpredictable_number_      = {c_apdu.begin() + 5, c_apdu.begin() + 9};
  mobile_support_indicator_  = c_apdu[9];
  authorized_amount_         = {c_apdu.begin() + 10, c_apdu.begin() + 16};
  transaction_currency_code_ = {c_apdu.begin() + 16, c_apdu.begin() + 18};
  terminal_country_code_     = {c_apdu.begin() + 18, c_apdu.begin() + 20};
  transaction_type_          = c_apdu[20];
  transaction_date_          = {c_apdu.begin() + 21, c_apdu.begin() + 24};
  merchant_category_code_    = {c_apdu.begin() + 24, c_apdu.begin() + 26};
  terminal_type_             = c_apdu[26];
}

}  // namespace mcbp_core
