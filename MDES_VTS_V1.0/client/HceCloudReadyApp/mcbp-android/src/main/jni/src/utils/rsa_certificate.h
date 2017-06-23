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

#ifndef SRC_UTILS_RSA_CERTIFICATE_H_  // NOLINT
#define SRC_UTILS_RSA_CERTIFICATE_H_  // NOLINT

#ifdef _WIN32
#define EXPORT
#else
// Export Macro - Need to ensure functions are visible although
// the use of -fvisibility=hidden flag
#define EXPORT __attribute__((__visibility__("default")))
#endif

// Project includes
#include <utils/byte_array.h>

// Cryptopp includes
#include <cryptopp562/randpool.h>  // PRNG
#include <cryptopp562/rsa.h>

// Libraries includes
#include <string>

namespace mcbp_core {

using std::string;

/**
 * Utility class to store public and private keys of a certain card profile
 */
class RsaCertificate {
 public:
  EXPORT RsaCertificate();

  EXPORT RsaCertificate(const RsaCertificate& certificate);

  EXPORT RsaCertificate& operator=(const RsaCertificate& certificate);

  EXPORT ByteArray calculate_inverse(CryptoPP::RandomNumberGenerator* rng,
                                     const ByteArray& data) const;

  EXPORT bool validate_private_key(const int level) const;

  /**
   * Initialize the Private Key
   * @param prime_p           The Prime P
   * @param prime_q           The Prime Q
   * @param prime_exponent_p  The Prime Exponent P
   * @param prime_exponent_q  The Prime Exponent Q
   * @param crt_coefficient   The CRT Coefficient
   * @param validation_level  The validation level to be performed on the 
   *                          private key (0 - none, 3 - highest validation)
   *
   * NOTE: The validation has a huge impact on performances in those platform
   * where ASM is not supported with Crypto++ (e.g. Android or MacOS)
   */
  EXPORT bool init_private_key(const string& prime_p,
                               const string& prime_q,
                               const string& prime_exponent_p,
                               const string& prime_exponent_q,
                               const string& crt_coefficient,
                               const int validation_level = 0);

  EXPORT unsigned int key_length() const;

  // Securely erase private and public keys
  EXPORT void destroy();

  /**
   * Check whether the RSA certificate has been initialized
   * @return True if the RSA certificate has been initialized
   */
  bool initialized() const;

  /**
   * Securely erase sensitive data before deleting the object.
   */
  EXPORT ~RsaCertificate();

 protected:
  // None

 private:
  bool initialized_;

  string prime_p_;
  string prime_q_;
  string prime_exponent_p_;
  string prime_exponent_q_;
  string crt_coefficient_;

  unsigned int key_length_;

  // RSA Private Key
  CryptoPP::RSA::PrivateKey *rsa_priv_key_;
  // RSA public Key
  CryptoPP::RSA::PublicKey  *rsa_pub_key_;

  // Random number for RSA functionalities
  CryptoPP::RandomPool *random_;

  // Seed for RandomPool initialization
  static const std::size_t kSeedSize;

  // Copy utility
  void copy(const RsaCertificate& certificate);  // NOLINT
};

// Inline functions' definition

inline bool RsaCertificate::initialized() const {
  return initialized_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_RSA_CERTIFICATE_H_)  // NOLINT
