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

// Crypto++ libraries
#include <cryptopp562/randpool.h>  // PRNG

// C++ Libraries
#include <string>

#ifndef SRC_UTILS_CRYPTO_FACTORY_H  // NOLINT
#define SRC_UTILS_CRYPTO_FACTORY_H  // NOLINT

#ifdef _WIN32
#define EXPORT
#else
// Export Macro - Need to ensure functions are visible although
// the use of -fvisibility=hidden flag
#define EXPORT __attribute__((__visibility__("default")))
#endif

namespace mcbp_core {

class RsaCertificate;

/**
 * CryptoFactory Singletone object
 * Provides basic cryptographic operations to perform common payment related
 * encryption/decryption operations. Some of the functions are specifically
 * design to work for MCBP 1.0 specifications.
 *
 * Note: the library may not be suitable for general purpose crypto operations
 */
class CryptoFactory {
 public:
  /** This function is called to create an instance of the class.
    * Calling the constructor publicly is not allowed. The constructor
    * is private and is only called by this Instance function.
  */
  EXPORT static const CryptoFactory* instance();

  /**
   * Generate a block_size bytes of random data
   * @param block_size The number of random bytes to be generated
   * @return a ByteArray of random generated bytes
   */
  EXPORT static ByteArray generate_random(const std::size_t& block_size);

  /**
   * Perform the MAC algorithm as desribed in [SECURITY]
   * @param data_to_mac The input Data (Data is padded with 0x80 and zeroes to 
   *                    match the block length
   * @param key The Key to be used
   * @return the MAC as ByteArray
   */
  EXPORT static ByteArray mac(const ByteArray& data_to_mac,
                              const ByteArray& key);

  /**
   * Perform the MAC SHA 256 algorithm
   * @param data_to_mac The input Data
   * @param key The Key to be used
   * @return the MAC SHA 256 as ByteArray
   */
  EXPORT static ByteArray mac_sha_256(const ByteArray& data_to_mac,
                                      const ByteArray& key);

  /**
   * Perform the DES CBC mode and no padding
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param encrypt A bool specifying whether it is an encryption (true) or 
   *                decryption (false) operation
   * @return the encrypted message
   */
  EXPORT static ByteArray des_cbc(const ByteArray& data, const ByteArray& b_key,
                                  const bool& encrypt);

  /**
   * Perform the DES EBC mode and no padding
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param encrypt A bool specifying whether it is an encryption (true) or 
   *                decryption (false) operation
   * @return the encrypted message
   */
  EXPORT static ByteArray des(const ByteArray& data, const ByteArray& b_key,
                              const bool& encrypt);

  /**
   * Perform the Triple DES with CBC mode and no padding
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param encrypt A bool specifying whether it is an encryption (true) or 
   *                decryption (false) operation
   * @return the encrypted message
   */
  EXPORT static ByteArray des_3(const ByteArray& data, const ByteArray& b_key,
                                const bool& encrypt);

  /**
   * Perform the AES encryption in ECB mode with default Crypto++ padding
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param encrypt A bool specifying whether it is an encryption (true) or 
   *                decryption (false) operation
   * @return the encrypted message
   */
  EXPORT static ByteArray aes(const ByteArray& data,
                              const ByteArray& b_key,
                              const bool& encrypt);

  /**
   * Perform the AES encryption in ECB mode without padding
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param encrypt A bool specifying whether it is an encryption (true) or 
   *                decryption (false) operation
   * @return the encrypted/decrypted message
   */
  EXPORT static ByteArray aes_ecb_nopadding(const ByteArray& data,
                                            const ByteArray& b_key,
                                            const bool& encrypt);

  /**
   * Perform the AES encryption in CBC mode with no padding
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param encrypt A bool specifying whether it is an encryption (true) or 
   *                decryption (false) operation
   * @return the encrypted/decrypted message
   */
  EXPORT static ByteArray aes_cbc_nopadding(const ByteArray& data,
                                            const ByteArray& b_key,
                                            const bool& encrypt);

  /**
   * Perform the AES CBC MAC algorithm
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @return The MAC of the message (8 bytes)
   */
  EXPORT static ByteArray aes_cbc_mac(const ByteArray& data,
                                      const ByteArray& b_key);

  /**
   * Perform the AES CTR with Counter encryption/decryption
   * @param data The input Data
   * @param b_key The encryption key to be used
   * @param iv the initialization vector
   * @return The MAC of the message (8 bytes)
   */
  EXPORT static ByteArray aes_ctr_nopadding(const ByteArray& data,
                                            const ByteArray& iv,
                                            const ByteArray& b_key,
                                            const bool encrypt);

  /**
   * Perform the SHA 1 hash function
   * @param data The input Data
   * @return The SHA 1 hash of the message
   */
  EXPORT static ByteArray sha_1(const ByteArray& data);

  /**
   * Perform the SHA 256 hash function
   * @param data The input Data
   * @return The SHA 256 hash of the message
   */
  EXPORT static ByteArray sha_256(const ByteArray& data);

  /**
   * Perform the RSA encryption using the private key that is given as input.
   * @param data The input Data
   * @param private_key The RSA private key
   * @return the encrypted message
   */
  EXPORT static ByteArray rsa(const ByteArray& data,
                              const RsaCertificate& private_key);

  /**
   * Generate the PAN substitute value based on the payment application instance
   * ID
   * @param data the PAN substitute value
   * @return the surrogate for the PAN
   */
  EXPORT static ByteArray generate_pan_substitute_value(
      const ByteArray& app_instance_id);

  /**
   * Encrypt/Decrypt using RSA with OAEP and SHA256 with MGF1 padding
   *
   * Please note that Encryption is expected to be performed with the Public Key
   * whereas Decryption is done using the Private Key
   *
   * @param data Input data to be encrypted
   * @param rsa_key the RSA public key to be used for encryption 
   *                or the RSA private key to be used for decryption.
   *                Note: The key is expected to be in Binary format!
   * @param encryption true for encryption, false for decryption
   *
   * @return the encrypted data
   */
  EXPORT static ByteArray rsa_oaep_sha256_mgf1(const ByteArray& data,
                                               const ByteArray& rsa_key,
                                               const bool encryption);

 protected:
  // None

 private:
  // Random number generator
  static CryptoPP::RandomPool random_;

  /**
   * Initialize the class
   */
  static void initialize();

  // Constructor and other operators are NOT available.
  CryptoFactory() { }
  CryptoFactory(CryptoFactory const&) { }
  CryptoFactory& operator=(CryptoFactory const&);

  // Pointer to the only existing instance of the CryptoFactory
  static const CryptoFactory *instance_;

  // Utility function to handle exceptions
  static void handle_exception(const CryptoPP::Exception& e);

  /**
   * Size of the seed for the random generator
   */
  static const int kSeedSize;

  // Friend function used for thread-safe initialization
  friend inline void CreateCryptoFactory();

  /**
   * Utility function to convert from BigInteger to Decimal String
   */
  static std::string BigIntegerToDecimalString(const CryptoPP::Integer integer);

  /**
   * Utility function to perform the RSA OAEP SHA256 encryption
   */
  static ByteArray rsa_oaep_sha256_mgf1_encrypt(const ByteArray& data,
                                                const ByteArray& public_key);

  /**
   * Utility function to perform the RSA OAEP SHA256 decryption
   */
  static ByteArray rsa_oaep_sha256_mgf1_decrypt(const ByteArray& data,
                                                const ByteArray& private_key);
};

}  // namespace mcbp_core

#endif  // defined(SRC_UTILS_CRYPTO_FACTORY_H)  // NOLINT