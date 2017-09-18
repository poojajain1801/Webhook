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
#include <utils/emvco/gpo_apdu.h>
#include <utils/mcbp_core_exception.h>
#include <core/constants.h>

namespace mcbp_core {

GpoApdu::GpoApdu(const ByteArray& gpo) : CommandApdu(gpo) {
  if (gpo[0] != kClaGetGpo)
    throw InvalidCla("Invalid Cla for Get Processing Options C-APDU");

  if (gpo[1] != kInsGetGpo)
    throw InvalidIns("Invalid Ins for Get Processing Options C-APDU");

  const Byte lc = static_cast<Byte>(CommandApdu::lc());

  if (lc != kLcGetGpo2 && lc != kLcGetGpo1)
    throw InvalidLc("Invalid Get Processing Options C-APDU LC");

  if (gpo.size() != lc + 6)
    throw InvalidInput("Invalid Get Processing Options C-APDU length");

  command_template_ = gpo[5];
  command_template_length_ = gpo[6];

  if (lc == 0x03) {
    pdol_related_data_ = {gpo[7]};
    terminal_type_ = gpo[7];

  } else {
    pdol_related_data_     = {gpo.begin() +  7, gpo.begin() + 5 + lc};
    risk_management_data_  = {gpo.begin() +  7, gpo.begin() + 15};
    terminal_country_code_ = {gpo.begin() + 15, gpo.begin() + 17};
    terminal_type_ = gpo[17];
  }
}

const ByteArray& GpoApdu::risk_management_data() const {
  if ( CommandApdu::lc() != kLcGetGpo1)
    throw Exception("GPO C-APDU: Risk Management Data field not present");
  return risk_management_data_;
}

const ByteArray& GpoApdu::terminal_country_code() const {
  if ( CommandApdu::lc() != kLcGetGpo1)
    throw Exception("GPO C-APDU: Terminal Country Code field not present");
  return terminal_country_code_;
}

}  // namespace mcbp_core
