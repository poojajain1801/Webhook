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

// Project Libraries
#include <utils/crypto_factory.h>
#include <utils/mcbp_core_exception.h>
#include <utils/utilities.h>
#include <utils/rsa_certificate.h>

// Crypto++ Libraries
#include <cryptopp562/nbtheory.h>
#include <cryptopp562/cryptlib.h>
#include <cryptopp562/hex.h>
#include <cryptopp562/filters.h>
#include <cryptopp562/des.h>
#include <cryptopp562/modes.h>
#include <cryptopp562/cbcmac.h>
#include <cryptopp562/secblock.h>
#include <cryptopp562/sha.h>
#include <cryptopp562/aes.h>
#include <cryptopp562/hmac.h>
#include <cryptopp562/integer.h>
#include <cryptopp562/rsa.h>

// C++ Libraries
#include <string>
#include <sstream>
#include <iostream>
#include <mutex>

using CryptoPP::StringSink;
using CryptoPP::ArraySink;
using CryptoPP::StringSource;
using CryptoPP::StreamTransformationFilter;
using CryptoPP::HexEncoder;
using CryptoPP::HexDecoder;
using CryptoPP::DES;
using CryptoPP::DES_EDE2;
using CryptoPP::CBC_Mode;
using CryptoPP::CBC_MAC;
using CryptoPP::ECB_Mode;
using CryptoPP::SecByteBlock;
using CryptoPP::ArraySource;
using CryptoPP::CBC_Mode_ExternalCipher;
using CryptoPP::ECB_Mode_ExternalCipher;
using std::string;

namespace mcbp_core {

// Utility Typedef
typedef CryptoPP::AES::Encryption AesEncryption;
typedef CryptoPP::AES::Decryption AesDecryption;
typedef CryptoPP::ECB_Mode_ExternalCipher::Encryption EcbEncryption;
typedef CryptoPP::ECB_Mode_ExternalCipher::Decryption EcbDecryption;
typedef CryptoPP::CBC_Mode_ExternalCipher::Encryption CbcEncryption;
typedef CryptoPP::CBC_Mode_ExternalCipher::Decryption CbcDecryption;
typedef CryptoPP::CTR_Mode_ExternalCipher::Encryption CtrEncryption;
typedef CryptoPP::CTR_Mode_ExternalCipher::Decryption CtrDecryption;

// Global static pointer used to ensure a single instance of the class.
const CryptoFactory* CryptoFactory::instance_ = nullptr;
std::once_flag flag_mcbp_core_crypto;

const int CryptoFactory::kSeedSize = 16;

// Create the Randpool object
CryptoPP::RandomPool CryptoFactory::random_;

void CreateCryptoFactory() {
  if (CryptoFactory::instance_ != nullptr) return;
  CryptoFactory::instance_ = new CryptoFactory();
  CryptoFactory::instance_->initialize();
}

const CryptoFactory* CryptoFactory::instance() {
  if (instance_ == nullptr)
    std::call_once(flag_mcbp_core_crypto, CreateCryptoFactory);
  return instance_;
}

void CryptoFactory::initialize() {
  // Must be at least 16 for RandomPool
  uint8_t pcb_seed[ kSeedSize ];
  for (std::size_t i = 0; i < kSeedSize; i++) pcb_seed[i] = 0x00;
  random_.IncorporateEntropy(pcb_seed, kSeedSize);
}

ByteArray CryptoFactory::generate_random(const std::size_t& block_size) {
  // Scratch Area
  ByteArray pcb_scratch(block_size, 0xFF);
  random_.GenerateBlock(&(pcb_scratch[0]), block_size);
  return pcb_scratch;
}

ByteArray CryptoFactory::mac(const ByteArray& data_to_mac,
                             const ByteArray& key) {
  ByteArray mac(data_to_mac);
  // Add mandatory padding 0x80
  mac.push_back(0x80);

  // Compute how many additional Padding bytes I need
  const std::size_t padding_bytes =
      (DES::BLOCKSIZE - (mac.size() % DES::BLOCKSIZE)) % DES::BLOCKSIZE;

  mac.reserve(mac.size() + padding_bytes);
  for (std::size_t i = 0; i < padding_bytes; i++)
    mac.push_back(0x00);

  ByteArray key_l(key.begin(), key.begin() + (key.size() / 2) );
  ByteArray key_r(key.begin() + (key.size() / 2), key.end());

  ByteArray d = des_cbc(mac, key_l, true);

  d = ByteArray(d.end() - 8, d.end());

  ByteArray b_mac = des(d, key_r, false);

  return des(b_mac, key_l, true);
}

ByteArray CryptoFactory::mac_sha_256(const ByteArray& data_to_mac,
                                     const ByteArray& key) {
  ByteArray mac_sha_256;
  try {
    string mac;
    CryptoPP::HMAC< CryptoPP::SHA256 > hmac(&key[0], key.size());
    ArraySource(&data_to_mac[0],
                data_to_mac.size(),
                true,
                new CryptoPP::HashFilter(hmac, new CryptoPP::StringSink(mac)));
    mac_sha_256 = StringToByteArray(mac);
  } catch(const CryptoPP::Exception& e) {
    handle_exception(e);
  }
  return mac_sha_256;
}

ByteArray CryptoFactory::des_cbc(const ByteArray& data,
                                 const ByteArray& b_key,
                                 const bool& encrypt) {
  if (DES::DEFAULT_KEYLENGTH != b_key.size())
    throw InvalidInput("Invalid Key Length");

  if (encrypt) {
    std::string cipher_text;
    try {
      DES::Encryption des_encryption(&b_key[0], b_key.size());
      ByteArray iv(DES::BLOCKSIZE, 0x00);
      CBC_Mode_ExternalCipher::Encryption cbc_enc(des_encryption, &iv[0]);
      StreamTransformationFilter stf_encryptor(
          cbc_enc,
          new StringSink(cipher_text),
          StreamTransformationFilter::NO_PADDING);

      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    } catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }
    return StringToByteArray(cipher_text);

  } else {  // decript;
    string decrypted_text;
    try {
      DES::Decryption des_decryption(&b_key[0], b_key.size());
      ByteArray iv(DES::BLOCKSIZE, 0x00);
      CBC_Mode_ExternalCipher::Decryption cbc_decr(des_decryption, &iv[0]);

      StreamTransformationFilter stf_decryptor(
          cbc_decr,
          new StringSink(decrypted_text),
          StreamTransformationFilter::NO_PADDING);

      stf_decryptor.Put(&data[0], data.size());
      stf_decryptor.MessageEnd();
    }
    catch(CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(decrypted_text);
  }
}

ByteArray CryptoFactory::des(const ByteArray& data,
                             const ByteArray& b_key,
                             const bool& encrypt) {
  if (encrypt) {
    std::string cipher_text;
    try {
      DES::Encryption des_encryption(&b_key[0], b_key.size());
      ECB_Mode_ExternalCipher::Encryption ecb_encryption(des_encryption);

      StreamTransformationFilter stf_encryptor(
          ecb_encryption,
          new StringSink(cipher_text),
          StreamTransformationFilter::NO_PADDING);

      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    }
    catch( CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(cipher_text);

  } else {  // decript;
    string decrypted_text;
    try {
      DES::Decryption des_decryption(&b_key[0], b_key.size());
      ECB_Mode_ExternalCipher::Decryption ecb_decr(des_decryption);
      StreamTransformationFilter stf_decryptor(
          ecb_decr,
          new StringSink(decrypted_text),
          StreamTransformationFilter::NO_PADDING);

      stf_decryptor.Put(&data[0], data.size());
      stf_decryptor.MessageEnd();
    }
    catch(CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(decrypted_text);
  }
}


ByteArray CryptoFactory::des_3(const ByteArray& data,
                              const ByteArray& b_key,
                              const bool& encrypt) {
  if (encrypt) {
    std::string cipher_text;
    try {
      CryptoPP::DES_EDE2::Encryption des_ede2_encr(&b_key[0], b_key.size());
      ByteArray iv(CryptoPP::DES_EDE2::BLOCKSIZE, 0x00);
      CBC_Mode_ExternalCipher::Encryption cbc_encr(des_ede2_encr, &iv[0]);

      CryptoPP::StreamTransformationFilter stf_encryptor(
          cbc_encr,
          new CryptoPP::StringSink(cipher_text),
          CryptoPP::StreamTransformationFilter::NO_PADDING);

      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    } catch(CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(cipher_text);

  } else {  // decript;
    string decrypted_text;
    try {
      CryptoPP::DES_EDE2::Decryption des_ede2_decr(&b_key[0], b_key.size());
      ByteArray iv(CryptoPP::DES_EDE2::BLOCKSIZE, 0x00);
      CBC_Mode_ExternalCipher::Decryption cbc_decr(des_ede2_decr, &iv[0]);

      CryptoPP::StreamTransformationFilter cbc_decryptor(
          cbc_decr,
          new CryptoPP::StringSink(decrypted_text),
          CryptoPP::StreamTransformationFilter::NO_PADDING);

      cbc_decryptor.Put(&data[0], data.size());
      cbc_decryptor.MessageEnd();
    }
    catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }
    return StringToByteArray(decrypted_text);
  }
}

ByteArray CryptoFactory::aes(const ByteArray& data,
                             const ByteArray& b_key,
                             const bool& encrypt) {
  if (encrypt) {
    std::string cipher_text;
    try {
      AesEncryption aes_encr(&b_key[0], b_key.size());
      EcbEncryption ecb_encryption(aes_encr);
      StreamTransformationFilter stf_encryptor(ecb_encryption,
                                               new StringSink(cipher_text));

      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    }
    catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }
    return StringToByteArray(cipher_text);

  } else {  // decript;
    string decrypted_text;
    try {
      AesDecryption aes_decryption(&b_key[0], b_key.size());
      EcbDecryption ecb_decryption(aes_decryption);

      StreamTransformationFilter stf_decryptor(ecb_decryption,
                                               new StringSink(decrypted_text));

      stf_decryptor.Put(&data[0], data.size());
      stf_decryptor.MessageEnd();
    }
    catch(CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(decrypted_text);
  }
}

ByteArray CryptoFactory::aes_ecb_nopadding(const ByteArray& data,
                                           const ByteArray& b_key,
                                           const bool& encrypt) {
  if (encrypt) {
    std::string cipher_text;
    try {
      AesEncryption aes_encr(&b_key[0], b_key.size());
      EcbEncryption ecb_encryption(aes_encr);
      StreamTransformationFilter stf_encryptor(ecb_encryption,
          new StringSink(cipher_text), StreamTransformationFilter::NO_PADDING);

      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    }
    catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }
    return StringToByteArray(cipher_text);

  } else {  // decript;
    string decrypted_text;
    try {
      AesDecryption aes_decryption(&b_key[0], b_key.size());
      EcbDecryption ecb_decryption(aes_decryption);

      StreamTransformationFilter stf_decryptor(ecb_decryption,
          new StringSink(decrypted_text),
          StreamTransformationFilter::NO_PADDING);

      stf_decryptor.Put(&data[0], data.size());
      stf_decryptor.MessageEnd();
    }
    catch(CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(decrypted_text);
  }
}

ByteArray CryptoFactory::aes_cbc_nopadding(const ByteArray& data,
                                           const ByteArray& b_key,
                                           const bool& encrypt) {
  const ByteArray iv(16, 0x00);
  if (encrypt) {
    std::string cipher_text;
    try {
      AesEncryption aes_encr(&b_key[0], b_key.size());
      CbcEncryption cbc_encryption(aes_encr, &iv[0]);
      StreamTransformationFilter stf_encryptor(
          cbc_encryption, new StringSink(cipher_text),
          StreamTransformationFilter::NO_PADDING);

      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    }
    catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }
    return StringToByteArray(cipher_text);

  } else {  // decript;
    string decrypted_text;
    try {
      AesDecryption aes_decryption(&b_key[0], b_key.size());
      CbcDecryption cbc_decryption(aes_decryption, &iv[0]);

      StreamTransformationFilter stf_decryptor(cbc_decryption,
          new CryptoPP::StringSink(decrypted_text),
          StreamTransformationFilter::NO_PADDING);

      stf_decryptor.Put(&data[0], data.size());
      stf_decryptor.MessageEnd();
    }
    catch(CryptoPP::Exception& e) {
      handle_exception(e);
    }
    return StringToByteArray(decrypted_text);
  }
}

ByteArray CryptoFactory::aes_ctr_nopadding(const ByteArray& data,
                                           const ByteArray& iv,
                                           const ByteArray& b_key,
                                           const bool encrypt) {
  if (encrypt) {
    AesEncryption aes_encr(&b_key[0], b_key.size());
    CtrEncryption ctr_encryption(aes_encr, &iv[0]);

    std::string cipher_text;

    StreamTransformationFilter stf_encryptor(ctr_encryption,
          new StringSink(cipher_text), StreamTransformationFilter::NO_PADDING);

    try {
      stf_encryptor.Put(&data[0], data.size());
      stf_encryptor.MessageEnd();
    } catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }

    const ByteArray result = StringToByteArray(cipher_text);
    mcbp_core::Wipe(&cipher_text);
    return result;
  }

  // AES MUST be in encryption mode according to Crypto++
  AesEncryption aes_decr(&b_key[0], b_key.size());
  CtrDecryption ctr_decryption(aes_decr, &iv[0]);

  std::string plain_text;

  StreamTransformationFilter stf_decryptor(ctr_decryption,
          new StringSink(plain_text), StreamTransformationFilter::NO_PADDING);

  try {
    stf_decryptor.Put(&data[0], data.size());
    stf_decryptor.MessageEnd();
  } catch( CryptoPP::Exception& e ) {
    handle_exception(e);
  }

  const ByteArray result = StringToByteArray(plain_text);
  mcbp_core::Wipe(&plain_text);
  return result;
}

ByteArray CryptoFactory::aes_cbc_mac(const ByteArray& data,
                                     const ByteArray& b_key) {
  ByteArray padded_data = add_iso_7816_padding(data);
  ByteArray intermediate(16, 0x00);
  const ByteArray iv(16, 0x00);

  for (std::size_t i = 0, j = 0; i < (padded_data.size() / 16); i++) {
    j = i * 16;

    ByteArray xor_data = do_xor(padded_data, j, intermediate, 0, 16);

    // Now encrypt
    std::string cipher_text;

    AesEncryption aes_encr(&b_key[0], b_key.size());
    CbcEncryption cbc_encryption(aes_encr, &iv[0]);
    try {
      StreamTransformationFilter stf_encryptor(cbc_encryption,
          new StringSink(cipher_text), StreamTransformationFilter::NO_PADDING);

      stf_encryptor.Put(&xor_data[0], xor_data.size());
      stf_encryptor.MessageEnd();
    }
    catch( CryptoPP::Exception& e ) {
      handle_exception(e);
    }
    intermediate = StringToByteArray(cipher_text);
    Wipe(&xor_data);
  }

  ByteArray mac(8, 0x00);
  for (int i = 0; i < 8; i++) {
    mac[i] = intermediate[i];
  }
  Wipe(&intermediate);  // Securely erase the old IV value
  return mac;
}

ByteArray CryptoFactory::sha_1(const ByteArray& data) {
  ByteArray digest(CryptoPP::SHA1::DIGESTSIZE, 0x00);
  CryptoPP::SHA1().CalculateDigest(&digest[0], &data[0], data.size());
  return digest;
}

ByteArray CryptoFactory::sha_256(const ByteArray& data) {
  ByteArray digest(CryptoPP::SHA256::DIGESTSIZE, 0x00);
  CryptoPP::SHA256().CalculateDigest(&digest[0], &data[0], data.size());
  return digest;
}

ByteArray CryptoFactory::rsa(const ByteArray& data,
                             const RsaCertificate& private_key) {
  ByteArray result;
  try {
    result = private_key.calculate_inverse(&random_, data);
  }
  catch( CryptoPP::Exception& e ) {
    handle_exception(e);
  }
  return result;
}

void CryptoFactory::handle_exception(const CryptoPP::Exception& e) {
    string error = "CryptoPP Exception: " + (string)(e.what());
    mcbp_core::Exception ex(error);
    throw ex;
}

std::string CryptoFactory::BigIntegerToDecimalString(
    const CryptoPP::Integer integer) {
  std::ostringstream oss;
  oss << std::dec << integer;    // Decimal output
  return oss.str();
}

ByteArray CryptoFactory::generate_pan_substitute_value(
    const ByteArray& app_instance_id) {
  using CryptoPP::Integer;
  ByteArray truncated_app_instance_id;

  const ByteArray hash = sha_1(app_instance_id);

  // Convert to Big Integer
  const std::string hex_value = "0x" + mcbp_core::ByteArrayToString(hash);
  const CryptoPP::Integer decimal_value(hex_value.c_str());
  const std::string decimal_string = BigIntegerToDecimalString(decimal_value);

  const size_t no_digits = decimal_string.size();

  // In the unlikely event we have less than 16 digits we fill the first with
  // zeroes
  const size_t offset = no_digits >= 16 ? 0: 16 - no_digits;
  ByteArray result(16, 0x00);
  // -1 avoids that we take the last digit which is a '.'
  for (size_t i = offset, j = no_digits - 16 - 1; i < 16; i++, j++) {
    result[i] = decimal_string[j];
  }
  return result;
}

ByteArray CryptoFactory::rsa_oaep_sha256_mgf1(const ByteArray& data,
                                              const ByteArray& key,
                                              const bool encryption) {
  if (encryption) {
    return rsa_oaep_sha256_mgf1_encrypt(data, key);
  }
  return rsa_oaep_sha256_mgf1_decrypt(data, key);
}

ByteArray CryptoFactory::rsa_oaep_sha256_mgf1_encrypt(const ByteArray& data,
                                                      const ByteArray& key) {
  typedef CryptoPP::RSAES<CryptoPP::OAEP<CryptoPP::SHA256> >::Encryptor
      RSA_OAEP_SHA256_Encryptor;

  CryptoPP::ByteQueue queue;
  queue.Put(&key[0], key.size());

  CryptoPP::RSA::PublicKey public_key;
  public_key.Load(queue);

  if (!public_key.Validate(random_, 3))
    throw InvalidInput("Rsa Public Key validation failed");

  RSA_OAEP_SHA256_Encryptor encryptor(public_key);

  SecByteBlock plain_text;
  plain_text.Assign(&data[0], data.size());
  size_t ecl = encryptor.CiphertextLength(plain_text.size());
  if (ecl == 0) {
    throw InvalidInput("RSA OAEP SHA256: Unable to encrypt data");
  }
  SecByteBlock cipher_text(ecl);

  // Paydirt
  encryptor.Encrypt(random_, plain_text, plain_text.size(), cipher_text);

  ByteArray result_array(cipher_text.size());
  for (int i = 0; i < result_array.size(); i++) {
    result_array[i] = cipher_text[i];
  }
  return result_array;
}

ByteArray CryptoFactory::rsa_oaep_sha256_mgf1_decrypt(const ByteArray& data,
                                                      const ByteArray& key) {
  typedef CryptoPP::RSAES<CryptoPP::OAEP<CryptoPP::SHA256> >::Decryptor
      RSA_OAEP_SHA256_Decryptor;

  CryptoPP::ByteQueue queue;
  queue.Put(&key[0], key.size());

  CryptoPP::RSA::PrivateKey private_key;
  private_key.Load(queue);

  if (!private_key.Validate(random_, 3))
    throw InvalidInput("Rsa Private Key validation failed");

  RSA_OAEP_SHA256_Decryptor decryptor(private_key);

  CryptoPP::SecByteBlock cipher_text;
  cipher_text.Assign(&data[0], data.size());

  // Now that there is a concrete object, we can validate
  assert(0 != decryptor.FixedCiphertextLength());
  assert(data.size() <= decryptor.FixedCiphertextLength());

  // Create recovered text space
  size_t dpl = decryptor.MaxPlaintextLength(cipher_text.size());
  assert(0 != dpl);
  SecByteBlock recovered(dpl);

  // Paydirt
  CryptoPP::DecodingResult result = decryptor.Decrypt(random_,
                                                      cipher_text,
                                                      cipher_text.size(),
                                                      recovered);
  // More sanity checks
  assert(result.isValidCoding);
  assert(result.messageLength <=
          decryptor.MaxPlaintextLength(cipher_text.size()));

  // At this point, we can set the size of the recovered
  //  data. Until decryption occurs (successfully), we
  //  only know its maximum size
  recovered.resize(result.messageLength);

  ByteArray result_array(recovered.size());
  for (int i = 0; i < recovered.size(); i++) {
    result_array[i] = recovered[i];
  }
  return result_array;
}

}  // namespace mcbp_core
