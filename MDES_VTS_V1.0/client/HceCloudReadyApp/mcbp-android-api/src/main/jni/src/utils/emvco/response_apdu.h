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

#ifndef SRC_UTILS_EMVCO_RESPONSE_APDU_H_  // NOLINT
#define SRC_UTILS_EMVCO_RESPONSE_APDU_H_  // NOLINT

#include <utils/emvco/apdu.h>

namespace mcbp_core {

/**
 *  \brief     The parent class of all the Response APDU
 *  \details   This class is used to derive R-APDUs classes
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class ResponseApdu : public Apdu {
 public:
  /**
   * Default Basic Constructor.
   */
  ResponseApdu() { }

 /**
   * Instantiates a new R-APDU.
   *
   * @param response      the data of the response
   * @param status_word   the Status Word
   *
   */
  ResponseApdu(const ByteArray& response, const ByteArray& status_word);

  /**
   * Instantiates a new R-APDU without data.
   *
   * @param sw the Status Word
   */
  explicit ResponseApdu(const ByteArray& status_word) : Apdu(status_word) { }

  /**
   * Instantiates a new R-APDU by copying another one.
   *
   * @param r_apdu
   */
  ResponseApdu(const ResponseApdu& r_apdu);

  /**
   * Copy operator
   * 
   * @param r_apdu
   */
  ResponseApdu& operator=(const ResponseApdu& r_apdu);

  /**
   * Get the R-APDU message (all bytes).
   *
   * @return the entire R-APDU message
   */
  const ByteArray& value() const;

  /**
   * Set the value of the APDU encoded as TLV.
   *
   * @param tag             the tag of the TLV
   * @param value           the value of the APDU
   */
  void set_TLV_response_and_success(const ByteArray& tag,
                                    const ByteArray& value);

  /**
   * Set the value of the APDU encoded as TLV and add the response.
   *
   * @param tag             the tag of the TLV
   * @param value           the value of the APDU
   * @param response_word   the response_word
   */
  void set_TLV_response(const ByteArray& tag,
                        const ByteArray& value,
                        const ByteArray& response_word);

 protected:
  // None

 private:
  // None added
};

// Inline functions' definition

inline const ByteArray& ResponseApdu::value() const { return Apdu::value(); }

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_EMVCO_RESPONSE_APDU_H_)  // NOLINT

