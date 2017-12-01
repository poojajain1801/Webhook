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

// Project libraries
#include <utils/byte_array.h>

#ifndef SRC_CORE_MCM_CRYPTOGRAM_OUTPUT_H_  // NOLINT
#define SRC_CORE_MCM_CRYPTOGRAM_OUTPUT_H_  // NOLINT

namespace mcbp_core {
/**
 *  \brief     Cryptogram Input
 *  \details   This object contains the data elements that McmLite needs to
 *             compute a Remote Payment transaction cryptogram
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct CryptogramOutput {
 public:
  ByteArray atc;                             /*  2 bytes     */
  ByteArray issuer_application_data;         /* 18 bytes     */
  Byte      cid;                             /*  1 byte      */
  ByteArray application_cryptogram;          /*  8 bytes     */

  // This field is present only if LDA is performed
  ByteArray signed_dynamic_application_data; /* RSA key length */

  /**
   * Default constructor. Reserve memory for each of data element
   */
  CryptogramOutput();

  /**
   * Constructor
   */
  CryptogramOutput(const ByteArray& atc,
                   const ByteArray& issuer_application_data);
};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_CRYPTOGRAM_OUTPUT_H_)  // NOLINT
