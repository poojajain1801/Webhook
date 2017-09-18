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

#include <utils/emvco/generate_ac_response_apdu.h>
#include <utils/utilities.h>
#include <utils/mcbp_core_exception.h>
#include <core/constants.h>

namespace mcbp_core {

/* Response is 47 bytes (44 without POSCII)
 * Cryptogram Information Data       -  2 + 1 +  1 byte
 * Application Transaction Counter   -  2 + 1 +  2 bytes
 * Application Cryptogram            -  2 + 1 +  8 bytes
 * Issuer Application Data           -  2 + 1 + 18 bytes
 * POSCII (if not null)              -  2 + 1 +  3 bytes
 */
GenerateACResponseApdu::GenerateACResponseApdu(const bool& lda,
                                               const ByteArray& sdad_or_ac,
                                               const ByteArray& atc,
                                               const Byte& cid,
                                               const ByteArray& iad,
                                               const ByteArray& poscii) {
  ByteArray response;
  response.reserve(47);

  response = Tlv(kTagCid, cid);
  Append(Tlv(kTagAtc, atc), &response);
  if (lda) {
    Append(Tlv(kTagSdad, sdad_or_ac), &response);
  } else {
    Append(Tlv(kTagAc, sdad_or_ac), &response);
  }
  Append(Tlv(kTagIad, iad), &response);

  if (!poscii.empty()) Append(Tlv(kTagPoscii, poscii), &response);

  // Add the message tag and set response to success
  ResponseApdu::set_TLV_response_and_success(ByteArray(1, kTagFormat2),
                                             response);
}

}  // namespace mcbp_core
