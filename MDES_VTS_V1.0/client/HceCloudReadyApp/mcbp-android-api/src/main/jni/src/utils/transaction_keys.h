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

#ifndef SRC_CORE_TRANSACTION_KEYS_H_  // NOLINT
#define SRC_CORE_TRANSACTION_KEYS_H_  // NOLINT

#include <utils/byte_array.h>
#include <utils/cardholder_validator.h>
#include <wrappers/keys_data.h>
#include <utils/mcbp_core_exception.h>

namespace mcbp_core {

/**
 * The Class AbstractSingleUseKey is the parent of both RandomSingleUseKey
 * and SingleUseKey which are passed to the MPPRemoteSE object after creation
 * and before each transaction.
 */
class TransactionKeys {
 public:
  /**
   * Size of the transaction keys in bytes
   */
  static const std::size_t kSizeOfTransactionKey;

  /**
   * Size of the ICC dynamic number in bytes
   */
  static const std::size_t kSizeOfIdn;

  /**
   * Size of the ICC dynamic number in bytes
   */
  static const std::size_t kSizeOfAtc;

  /**
   * Default constructor.
   */
  TransactionKeys();

  /**
   * Constructor
   */
  TransactionKeys(const KeysData& keys,
                  const Byte pvs = 0x05);

  /**
   * Gets the Session Key for ContactLess User & Mobile Device
   *
   * @return the Session Key for ContactLess User & Mobile Device
   */
  const ByteArray& sk_cl_umd() const NOEXCEPT;

  /**
   * Gets the Session Key for ContactLess Mobile Device
   *
   * @return the Session Key for ContactLess Mobile Device
   */
  const ByteArray& sk_cl_md() const NOEXCEPT;

  /**
   * Gets the Session Key for ContactLess - User & Mobile Device
   *
   * @return the Session Key for ContactLess - User & Mobile Device
   */
  const ByteArray& sk_rp_umd() const NOEXCEPT;

    /**
   * Gets the Session Key for Remote Payment - User & Mobile Device
   *
   * @return the Session Key for RemotePayment - & Mobile Device
   */
  const ByteArray& sk_rp_md() const NOEXCEPT;

  /**
   * Gets the PIN Verification Status (PVS). The value of the PVS is set by default in the implementation of AbstractSingleUseKey:
   * <ul>
   * <li>0x00 for RandomSingleUseKey which should be used when the Mobile PIN wasn't entered</li>
   * <li>0x05 for SingleUseKey which should be used when the Mobile Pin was entered</li>
   * </ul>
   *
   * @return the PIN Verification Status (PVS)
   */
  const Byte& pvs() const NOEXCEPT;

  /**
   * Clear the PIN Validation Status after use.
   */
  void clear_pvs() NOEXCEPT;

  /**
   * Gets the ICC Dynamic Number (IDN) - 9F4C.
   *
   * @return the ICC Dynamic Number
   */
  const ByteArray& idn() const NOEXCEPT;

  /**
   * Gets the Application Transaction Counter (ATC) - 9F36.
   *
   * @return the Application Transaction Counter
   */
  const ByteArray& atc() const NOEXCEPT;

  /**
   * Unlock the key for the next transaction
   */
  void unlock(CardholderValidator* validator) NOEXCEPT;

  /**
    * Wipes the sensitive information including ATC, IDN, and PVS
    */
  void wipe() NOEXCEPT;

  /**
   * Wipe Contactless and Remote Payment Keys.
   * It is recommended to call this function as soon as the keys are used
   */
  void wipe_keys() NOEXCEPT;

  /**
   * Securely erase sensitive data before denstructing the class.
   */
  ~TransactionKeys();

 protected:
  /**
   * The Session Key for ContactLess - User & Mobile Device
   */
  ByteArray sk_cl_umd_;

  /**
   * The Session Key for ContactLess - Mobile Device
   */
  ByteArray sk_cl_md_;

  /**
   * The Session Key for Remote Payment - User & Mobile Device
   */
  ByteArray sk_rp_umd_;

  /**
   * The Session Key for Remote Payment - Mobile Device
   */
  ByteArray sk_rp_md_;

  /**
   * The Application Transaction Counter (ATC) - 9F36.
   */
  ByteArray atc_;

  /**
   * The ICC Dynamic Number (IDN) - 9F4C.
   */
  ByteArray idn_;

  /**
   * The PIN Verification Status.
   */
  Byte pvs_;

 private:
  // Utility function
  void fn_xor(const ByteArray& pin) NOEXCEPT;
};

// Inline functions' definition

inline const ByteArray& TransactionKeys::sk_cl_umd() const NOEXCEPT {
  return sk_cl_umd_;
}
inline const ByteArray& TransactionKeys::sk_cl_md() const NOEXCEPT {
  return sk_cl_md_;
}
inline const ByteArray& TransactionKeys::sk_rp_umd() const NOEXCEPT {
  return sk_rp_umd_;
}
inline const ByteArray& TransactionKeys::sk_rp_md() const NOEXCEPT {
  return sk_rp_md_;
}
inline const Byte& TransactionKeys::pvs() const NOEXCEPT {
  return pvs_;
}
inline void TransactionKeys::clear_pvs() NOEXCEPT {
  pvs_ = 0x00;
}
inline const ByteArray& TransactionKeys::idn() const NOEXCEPT {
  return idn_;
}
inline const ByteArray& TransactionKeys::atc() const NOEXCEPT {
  return atc_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_TRANSACTION_KEYS_H_)  // NOLINT
