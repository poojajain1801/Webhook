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

// Project includes
#include <utils/rsa_certificate.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>

// Crypto++ Library includes
#include <cryptopp562/integer.h>
#include <cryptopp562/nbtheory.h>
#include <cryptopp562/cryptlib.h>
#include <cryptopp562/hex.h>

// Libraries includes
#include <sstream>
#include <string>

namespace mcbp_core {

const std::size_t RsaCertificate::kSeedSize = 16;

RsaCertificate::RsaCertificate() {
  initialized_ = false;
  key_length_ = 0;

  rsa_priv_key_ = nullptr;
  rsa_pub_key_  = nullptr;

  // Initialize the random generator Randpool
  // Must be at least 16 for RandomPool
  uint8_t pcb_seed[kSeedSize];
  for (std::size_t i = 0; i < kSeedSize; i++)
    pcb_seed[i] = 0x00;

  random_ = new CryptoPP::RandomPool();
  random_->IncorporateEntropy(pcb_seed, kSeedSize);
}

RsaCertificate::RsaCertificate(const RsaCertificate& certificate) {
  copy(certificate);
}

RsaCertificate& RsaCertificate::operator=(const RsaCertificate& certificate) {
  if (this != &certificate) {
    copy(certificate);
  }
  return *this;
}

void RsaCertificate::copy(const RsaCertificate& certificate) {  // NOLINT
  /* The 4 values below are set by the init_private_key */
  initialized_ = false;
  key_length_ = 0;
  rsa_priv_key_ = nullptr;
  rsa_pub_key_  = nullptr;

  prime_p_ = certificate.prime_p_;
  prime_q_ = certificate.prime_q_;
  prime_exponent_p_ = certificate.prime_exponent_p_;
  prime_exponent_q_ = certificate.prime_exponent_q_;
  crt_coefficient_ = certificate.crt_coefficient_;

  uint8_t pcb_seed[kSeedSize];
  for (std::size_t i = 0; i < kSeedSize; i++)
    pcb_seed[i] = 0x00;
  random_->IncorporateEntropy(pcb_seed, kSeedSize);

  if (prime_p_ == "") return;

  // When copied the validation level of the private key is set to 0 as the key
  // is assumed to be good
  init_private_key(prime_p_, prime_q_, prime_exponent_p_, prime_exponent_q_,
                   crt_coefficient_, 0);
}

bool RsaCertificate::init_private_key(const string& prime_p,
                                      const string& prime_q,
                                      const string& prime_exponent_p,
                                      const string& prime_exponent_q,
                                      const string& crt_coefficient,
                                      const int validation_level) {
  if (initialized_) throw Exception("RSA Private Key already initialized");

  // Create the RSA::PrivateKey object
  rsa_priv_key_ = new CryptoPP::RSA::PrivateKey();

  try {
    using CryptoPP::Integer;
    Integer p(("0x" + prime_p).c_str());
    Integer q(("0x" + prime_q).c_str());
    Integer c(("0x" + prime_exponent_p).c_str());
    Integer f(("0x" + prime_exponent_q).c_str());
    Integer a(("0x" + crt_coefficient).c_str());
    Integer n = p * q;
    Integer big_one = CryptoPP::Integer::One();
    Integer e = c.InverseMod(p - big_one);
    Integer d = e.InverseMod(CryptoPP::LCM(p - big_one, q - big_one));

    rsa_priv_key_->Initialize(n, e, d, p, q, c, f, a);
    key_length_ = n.ByteCount();

    // 3 is the highest level of check according to Crypto++ documentation
    if (validation_level > 0)
      if (!rsa_priv_key_->Validate(*random_, validation_level))
        throw mcbp_core::Exception("Rsa private key validation failed");

    // Store the values used to generate the key
    // They are useful to the copy constructor
    prime_p_          = prime_p;
    prime_q_          = prime_q;
    prime_exponent_p_ = prime_exponent_p;
    prime_exponent_q_ = prime_exponent_q;
    crt_coefficient_  = crt_coefficient;

    // Wipe temporary data
    Wipe(p);
    Wipe(q);
    Wipe(c);
    Wipe(f);
    Wipe(a);
    Wipe(n);
    Wipe(big_one);
    Wipe(e);
    Wipe(d);
  } catch( CryptoPP::Exception& e ) {
    string error = "RSA Certificate - Crypto++: " + (string)(e.what());
    mcbp_core::Exception ex(error);
    throw ex;
  }
  initialized_ = true;
  return initialized_;
}

void RsaCertificate::destroy() {
  Wipe(&prime_p_);
  Wipe(&prime_q_);
  Wipe(&prime_exponent_p_);
  Wipe(&prime_exponent_q_);
  Wipe(&crt_coefficient_);

  if (!initialized_) return;

  if (rsa_priv_key_ != nullptr) {
    rsa_priv_key_->Wipe();
    delete rsa_priv_key_;
    rsa_priv_key_ = nullptr;
  }
  if (rsa_pub_key_ != nullptr) {
    delete rsa_pub_key_;
    rsa_pub_key_ = nullptr;
  }
  if (random_ != nullptr) {
    delete random_;
    random_ = nullptr;
  }
  initialized_ = false;
  key_length_ = 0;
}

bool RsaCertificate::validate_private_key(const int level) const {
    // Create a random object to validate the key
    CryptoPP::RandomPool rng;
    uint8_t pcb_seed[kSeedSize];

    for (std::size_t i = 0; i < kSeedSize; i++)
      pcb_seed[i] = 0x00;

    rng.IncorporateEntropy(pcb_seed, kSeedSize);
    // 3 is the highest level of check according to Crypto++ documentation
    if (!rsa_priv_key_->Validate(rng, level))
      throw mcbp_core::Exception("RSA validation failed");
    return true;
}

ByteArray RsaCertificate::calculate_inverse(
    CryptoPP::RandomNumberGenerator* rng,
    const ByteArray& data) const {
  if (!initialized_) throw InvalidState("RSA Private Key not initialized");

  using std::hex;
  using CryptoPP::Integer;

  // Create some temporary variables
  CryptoPP::Integer m, c;

  try {
    // Treat the message as a big endian array
    m = Integer((const byte *)&data[0], data.size());

    c = rsa_priv_key_->CalculateInverse(*rng, m);

    // Copy the CryptoPP::Integer to a string for later conversion to ByteArray
    std::ostringstream oss;
    oss << hex << c;    // use this for hex output
    string cipher_text(oss.str());

    // The cipher text should be 2 * data.size() + 1 (an h is added at the end)
    if ( cipher_text.size() != 2 * data.size() + 1 ) {
      // This happens when the first digit of the result is 0.
      // Extend the cipher_text with 0
      const std::size_t missing_zeros =
          2 * data.size() + 1 - cipher_text.size();

      for (std::size_t i = 0; i < missing_zeros; i++)
        cipher_text.insert(0, "0");
    }

    // Now convert to a ByteArray
    const int result_size = static_cast<int>(cipher_text.size() / 2);
    // The last digit should be ignored as it is an 'h' added by the
    // CryptoPP::Integer class
    cipher_text.pop_back();

    ByteArray result(result_size, 0x00);
    StringToHex(cipher_text, &result);

    return result;
  }
  catch( CryptoPP::Exception& e ) {
    string error = "RSA Certificate - Crypto++: " + (string)(e.what());
    mcbp_core::Exception ex(error);
    throw ex;
  }
}

unsigned int RsaCertificate::key_length() const {
  if (!initialized_) throw Exception("RSA Certificate not initialized");
  return key_length_;
}

RsaCertificate::~RsaCertificate() {
  destroy();
}

}  // namespace mcbp_core
