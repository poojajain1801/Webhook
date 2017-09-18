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

#ifndef SRC_WRAPPERS_KEYS_DATA_H_  // NOLINT
#define SRC_WRAPPERS_KEYS_DATA_H_  // NOLINT

namespace mcbp_core {

 /**
 *  \brief     Data structure for all the Transaction Keys attributes.
 *  \details   It is used to easily exchange data between the library and 
 *             platform specific wrappers assuming than in most cases the
 *             library may be compiled with hidden visibility flag on
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
struct KeysData {
 public:
  ByteArray sk_cl_umd;
  ByteArray sk_cl_md;
  ByteArray sk_rp_umd;
  ByteArray sk_rp_md;
  ByteArray atc;
  ByteArray idn;
  ByteArray pin;  // Used only by the unit test

  /**
   * Zeroes all the fields
   */
  void wipe();

  /**
   * Securely delete the object
   */
  ~KeysData();
};

// Inline functions definition

inline void KeysData::wipe() {
  for (std::size_t i = 0; i < sk_cl_umd.size(); i++) sk_cl_umd[i] = 0x00;
  for (std::size_t i = 0; i < sk_cl_md.size();  i++) sk_cl_md[i]  = 0x00;
  for (std::size_t i = 0; i < sk_rp_umd.size(); i++) sk_rp_umd[i] = 0x00;
  for (std::size_t i = 0; i < sk_rp_md.size();  i++) sk_rp_md[i]  = 0x00;
  for (std::size_t i = 0; i < atc.size();       i++) atc[i]       = 0x00;
  for (std::size_t i = 0; i < idn.size();       i++) idn[i]       = 0x00;
  for (std::size_t i = 0; i < pin.size();       i++) pin[i]       = 0x00;
}

inline KeysData::~KeysData() {
  wipe();
  sk_cl_umd.clear();
  sk_cl_md.clear();
  sk_rp_umd.clear();
  sk_rp_md.clear();
  atc.clear();
  idn.clear();
  pin.clear();
}

}  // namespace mcbp_core

#endif  // defined(SRC_WRAPPERS_KEYS_DATA_H_)  // NOLINT
