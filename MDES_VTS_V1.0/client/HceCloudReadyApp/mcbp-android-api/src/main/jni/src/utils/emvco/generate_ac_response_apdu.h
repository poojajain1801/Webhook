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

#ifndef SRC_UTILS_EMVCO_GENERATE_AC_RESPONSE_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_GENERATE_AC_RESPONSE_APDU_H_  // NOLINT

#include <utils/emvco/response_apdu.h>

namespace mcbp_core {

/**
 *  \brief     The Generate AC R-APDU
 *  \details   It provides data structures and utility functions to compute
 *             the Generate AC R-APDU
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class GenerateACResponseApdu : public ResponseApdu {
 public:
 /**
  * Instantiates a new gen ac resp apdu.
  *
  * @param lda        the lda
  * @param sda_or_ac  the sda dor ac
  * @param atc        the atc
  * @param cid        the cid
  * @param iad        the iad
  * @param poscii     the poscii
  */
  GenerateACResponseApdu(const bool& lda,
                         const ByteArray& sdad_or_ac,
                         const ByteArray& atc,
                         const Byte& cid,
                         const ByteArray& iad,
                         const ByteArray& poscii);

 protected:
  // None

 private:
  // None
};

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_GENERATE_AC_RESPONSE_APDU_H_)  // NOLINT
