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

#ifndef SRC_UTILS_EMVCO_CCC_RESPONSE_ADPU_H_  // NOLINT
#define SRC_UTILS_EMVCO_CCC_RESPONSE_ADPU_H_  // NOLINT

#include <utils/emvco/response_apdu.h>

namespace mcbp_core {

/**
 *  \brief     The Compute Crypto Checksum R-APDU
 *  \details   It offers utility functions to create the CCC R-APDU
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class CCCResponseApdu : public ResponseApdu {
 public:
  /**
   * Instantiates a new Compute Crypto Checksum R-APDU
   * @param CC the cc
   */
  explicit CCCResponseApdu(const ByteArray& CC);

  /**
   * Create a Compute Crypto Checksum R-APDU for MCBP 1.0 (Approve)
   * @param cvc3        The CVC3 value calculated using UMD session key
   * @param crypto_atc  The Crypto ATC value calculated using MD session key
   * @param poscii      The POSCII vector (if empty, it is ignored)
   */
  CCCResponseApdu(const ByteArray& cvc3, const ByteArray& crypto_atc,
                  const ByteArray& poscii);

  /**
   * Create a Compute Crypto Checksum R-APDU for MCBP 1.0 (Decline)
   * @param atc         The ATC in credentials
   * @param poscii      The POSCII vector (if empty, it is ignored)
   */
  CCCResponseApdu(const ByteArray& atc, const ByteArray& poscii);

 private:
  static const ByteArray TAG_CVC3_TRACK2;
  static const ByteArray TAG_CVC3_TRACK1;
  static const ByteArray TAG_ATC;
  static const ByteArray TAG_POSCII;
};

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_CCC_RESPONSE_ADPU_H_)  // NOLINT
