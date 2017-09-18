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
#include <utils/emvco/generate_ac_apdu.h>
#include <utils/byte_array.h>
#include <utils/mcbp_core_exception.h>
#include <core/constants.h>

namespace mcbp_core {

GenerateACApdu::GenerateACApdu(const ByteArray& apdu) : CommandApdu(apdu) {
  if (kClaGenerateAC != cla())
    throw InvalidCla("Invalid Cla in GenerateAC C-APDU");

  if (kInsGenerateAC != ins())
    throw InvalidIns("Invalid Ins in GenerateAC C-APDU");

  if (CommandApdu::lc() < kMinimumCdol1Lenght)
    throw InvalidLc("Invalid GenerateAC C-APDU LC");

  if (apdu.size() != CommandApdu::lc() + 6)
    throw InvalidInput("Invalid GenerateAC C-APDU length");

  parse(apdu);
}

void GenerateACApdu::parse(const ByteArray& apdu) {
  // Table 44 - Page 206 MPA Functional Description 1.0
  cdol_                          = {apdu.begin() +  5, apdu.begin() + 5 + lc()};
  authorized_amount_             = {apdu.begin() +  5, apdu.begin() + 11};
  other_amount_                  = {apdu.begin() + 11, apdu.begin() + 17};
  terminal_country_code_         = {apdu.begin() + 17, apdu.begin() + 19};
  terminal_verification_results_ = {apdu.begin() + 19, apdu.begin() + 24};
  transaction_currency_code_     = {apdu.begin() + 24, apdu.begin() + 26};
  transaction_date_              = {apdu.begin() + 26, apdu.begin() + 29};
  transaction_type_              = apdu[29];
  unpredictable_number_          = {apdu.begin() + 30, apdu.begin() + 34};
  terminal_type_                 = apdu[34];
  data_authentication_code_      = {apdu.begin() + 35, apdu.begin() + 37};
  icc_dynamic_number_            = {apdu.begin() + 37, apdu.begin() + 45};
  cvm_results_                   = {apdu.begin() + 45, apdu.begin() + 48};
  merchant_category_code_        = {apdu.begin() + 48, apdu.begin() + 50};
}

}  // namespace mcbp_core
