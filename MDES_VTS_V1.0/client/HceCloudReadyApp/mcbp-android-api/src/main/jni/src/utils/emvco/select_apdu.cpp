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

#include <utils/emvco/select_apdu.h>
#include <utils/mcbp_core_exception.h>
#include <core/constants.h>

namespace mcbp_core {

const Byte SelectApdu::kCla        = 0x00;
const Byte SelectApdu::kIns        = 0xA4;
const Byte SelectApdu::kDataOffset = 0x05;

SelectApdu::SelectApdu(const ByteArray& select) : CommandApdu(select), aid_() {
  if (cla() != kClaSelect)
    throw InvalidCla("Invalid Cla in the Select C-APDU");

  if (ins() != kInsSelect)
    throw InvalidIns("Invalid Ins in the Select C-APDU");

  const std::size_t end = kDataOffset + lc();

  if (select.size() < kDataOffset ||
      select.size() != 6 + select[kDataOffset - 1])
    throw InvalidInput("Invalid SELECT APDU");

  // End is used to avoid taking the last byte of the APDU, which is '00'
  aid_ = {select.begin() + kDataOffset, select.begin() + end};
}

}  // namespace mcbp_core
