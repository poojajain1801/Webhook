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

#include <utils/emvco/response_apdu.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>
#include <core/constants.h>

namespace mcbp_core {

ResponseApdu::ResponseApdu(const ByteArray& response,
                           const ByteArray& status_word) {
  ByteArray value(response);
  Append(status_word, &value);
  Apdu::set_value(value);
}

ResponseApdu::ResponseApdu(const ResponseApdu& r_apdu) :
  Apdu(r_apdu.Apdu::value()) { }

void ResponseApdu::set_TLV_response_and_success(const ByteArray& tag,
                                                const ByteArray& value) {
  set_TLV_response(tag, value, Iso7816::kSuccessWord);
}

ResponseApdu& ResponseApdu::operator=(const ResponseApdu& r_apdu) {
  if ( this == &r_apdu ) return *this;

  Apdu::set_value(r_apdu.value());
  return *this;
}

void ResponseApdu::set_TLV_response(const ByteArray& tag,
                                    const ByteArray& value,
                                    const ByteArray& status_word) {
  ByteArray response = Tlv(tag, value);
  Append(status_word, &response);
  Apdu::set_value(response);
}

}  // namespace mcbp_core
