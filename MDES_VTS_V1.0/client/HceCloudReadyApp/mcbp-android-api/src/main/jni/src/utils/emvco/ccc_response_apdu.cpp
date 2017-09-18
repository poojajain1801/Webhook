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

#include <utils/emvco/ccc_response_apdu.h>
#include <utils/utilities.h>
#include <core/constants.h>

namespace mcbp_core {

const ByteArray CCCResponseApdu::TAG_CVC3_TRACK2 = {0x9F, 0x61};
const ByteArray CCCResponseApdu::TAG_CVC3_TRACK1 = {0x9F, 0x60};
const ByteArray CCCResponseApdu::TAG_ATC         = {0x9F, 0x36};
const ByteArray CCCResponseApdu::TAG_POSCII      = {0xDF, 0x4B};

CCCResponseApdu::CCCResponseApdu(const ByteArray& CC) {
  ResponseApdu::set_TLV_response_and_success(ByteArray(1, kTagFormat2), CC);
}

CCCResponseApdu::CCCResponseApdu(const ByteArray& cvc3,
                                 const ByteArray& crypto_atc,
                                 const ByteArray& poscii) {
  const ByteArray tlv_cvc3_track2 = Tlv(TAG_CVC3_TRACK2, cvc3);
  const ByteArray tlv_cvc3_track1 = Tlv(TAG_CVC3_TRACK1, cvc3);
  const ByteArray tlv_crypto_atc  = Tlv(TAG_ATC, crypto_atc);

  ByteArray tlv_poscii;

  if (!poscii.empty()) tlv_poscii = Tlv(TAG_POSCII, poscii);

  ByteArray resp;  /* Response - 15 or 20 bytes depending on poscii presence */
  resp.reserve(tlv_cvc3_track2.size() + tlv_cvc3_track1.size() +
               crypto_atc.size() + poscii.size());

  resp = tlv_cvc3_track2;

  Append(tlv_cvc3_track1, &resp);
  Append(tlv_crypto_atc, &resp);
  Append(tlv_poscii, &resp);

  ResponseApdu::set_TLV_response_and_success(ByteArray(1, kTagFormat2), resp);
}

CCCResponseApdu::CCCResponseApdu(const ByteArray& atc,
                                 const ByteArray& poscii) {
  const ByteArray tlv_atc    = Tlv(TAG_ATC, atc);
  const ByteArray tlv_poscii = Tlv(TAG_POSCII, poscii);

  ByteArray resp; /* Response 11 bytes */
  resp.reserve(11);
  resp = tlv_atc;
  Append(tlv_poscii, &resp);
  ResponseApdu::set_TLV_response_and_success(ByteArray(1, kTagFormat2), resp);
}

}  // namespace mcbp_core
