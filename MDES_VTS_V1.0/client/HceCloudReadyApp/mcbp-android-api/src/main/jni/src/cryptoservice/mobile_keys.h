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

#include <utils/byte_array.h>

#ifndef CRYPTOSERVICE_MOBILE_KEYS_H  //NOLINT
#define CRYPTOSERVICE_MOBILE_KEYS_H  //NOLINT

#ifdef _WIN32
#define EXPORT
#else
// Export Macro - Need to ensure functions are visible although
// the use of -fvisibility=hidden flag
#define EXPORT __attribute__((__visibility__("default")))
#endif

namespace mcbp_crypto_service {

/**
 * Utility data structure to store the content of the three Mobile Keys
 */
class MobileKeys {
 public:
  /**
   * Constructor. All the three key values must be provided
   */
  EXPORT MobileKeys(const ByteArray& transport_key,
                    const ByteArray& mac_key,
                    const ByteArray& data_encryption_key);
  /**
   * Get the Transport Key
   */
  EXPORT ByteArray get_transport_key() const;

  /**
   * Get the MAC Key
   */
  EXPORT ByteArray get_mac_key() const;

  /**
   * Get the Data Encryption Key
   */
  EXPORT ByteArray get_data_encryption_key() const;

  /**
   * Securely erase the content of the keys before deallocating the object
   */
  EXPORT ~MobileKeys();

 protected:
  // Intentionally empty

 private:
  /**
   * The value of the Transport key
   */
  ByteArray transport_key_;

  /**
   * The value of the MAC key
   */
  ByteArray mac_key_;

  /**
   * The value of the Data Encryption key
   */
  ByteArray data_encryption_key_;
};

}  // End of namespace mcbp_crypto_service

#endif  // defined(CRYPTOSERVICE_MOBILE_KEYS_H)  //NOLINT
